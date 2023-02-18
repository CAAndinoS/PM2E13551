package com.example.pm2e10435;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import android.Manifest;

import com.example.pm2e10435.configuracion.SQLiteConexion;
import com.example.pm2e10435.transacciones.Contactos;
import com.example.pm2e10435.transacciones.Transacciones;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class listacombo extends AppCompatActivity {
    static final  int REQUEST_CALL_PHONE_PERMISSION = 201;
    SQLiteConexion conexion;
    Spinner combopersonas;
    EditText txtnombres, txtpais, txtid, txttelefono,txtnota,txtbuscar;
    ImageView imgfoto;
    Button btneliminar,btnactualizar,btnllamar,btncompartir,btnbuscar;

    ArrayList<Contactos> listacontactos;
    ArrayList<String> Arreglocontactos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listacombo);
        conexion  = new SQLiteConexion(this, Transacciones.NameDatabase, null, 1);
        combopersonas = (Spinner) findViewById(R.id.combopersonas);
        txtnombres = (EditText) findViewById(R.id.txtcbnombre);
        txtpais = (EditText) findViewById(R.id.txtcbpais);
        txtid = (EditText) findViewById(R.id.txtcbid);
        txttelefono = (EditText) findViewById(R.id.txtcbtelefono);
        txtnota = (EditText) findViewById(R.id.txtcbnota);
        imgfoto = (ImageView) findViewById(R.id.imgfoto);
        btneliminar = (Button) findViewById(R.id.btneliminar);
        btnactualizar = (Button) findViewById(R.id.btnactualizar);
        btnllamar = (Button) findViewById(R.id.btnllamar);
        btncompartir = (Button) findViewById(R.id.btncompartir);
        //
        txtbuscar = (EditText) findViewById(R.id.txtbuscar);
        btnbuscar = (Button) findViewById(R.id.btnbuscar);

        btnbuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               BuscarContacto();
            }

        });

        //
        btnllamar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CuadroDialogo1();
            }

        });

        btncompartir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CompartirContacto();
            }

        });

        btneliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EliminarContacto();
            }

        });

        btnactualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String exe = txtnombres.getText().toString();
                String ext = txtpais.getText().toString();
                String exn = txttelefono.getText().toString();
                String exp = txtnota.getText().toString();
                if(exe.isEmpty() || ext.isEmpty() || exn.isEmpty() || exp.isEmpty()){
                    CuadroDialogo();
                } else  {
                    ActualizarContacto();
                }

            }

        });

        ObtenerListaPersonas();

        ArrayAdapter<CharSequence> adp = new ArrayAdapter(this, android.R.layout.simple_spinner_item, Arreglocontactos);
        combopersonas.setAdapter(adp);

        combopersonas.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int indice, long l)
            {
                try {

                    txtnombres.setText(listacontactos.get(indice).getNombre());
                    txtpais.setText(listacontactos.get(indice).getPais());
                    txtid.setText(listacontactos.get(indice).getId().toString());
                    txtnota.setText(listacontactos.get(indice).getNota());
                    txttelefono.setText(listacontactos.get(indice).getTelefono().toString());
                    byte[] imageBytes = listacontactos.get(indice).getFoto();
                    Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                    imgfoto.setImageBitmap(bitmap);

                }catch (Exception ex)
                {
                    ex.toString();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }
//
    private void BuscarContacto(){
        SQLiteConexion conexion = new SQLiteConexion(this, Transacciones.NameDatabase, null, 1);
        SQLiteDatabase db = conexion.getWritableDatabase();

        Cursor cursor = db.query(Transacciones.tablacontactos, null, "nombre LIKE ?", new String[]{"%" + txtbuscar.getText().toString() + "%"}, null, null, null);

        Contactos person = null;
        List<Contactos> listacontacto = new ArrayList<Contactos>();

        while(cursor.moveToNext())
        {
            person = new Contactos();
            person.setId(cursor.getInt(0));
            person.setNombre(cursor.getString(1));
            person.setPais(cursor.getString(2));
            person.setTelefono(cursor.getString(3));
            person.setNota(cursor.getString(4));
            byte[] byteArray = cursor.getBlob(5);
            Bitmap bitmap;
            person.setFoto(byteArray);

            listacontacto.add(person);
        }
        cursor.close();
        db.close();
        FillList2(listacontacto);
    }


    private void FillList2(List<Contactos> listacontactos)
    {
        // Limpia los campos de la interfaz antes de actualizarlos
        txtid.setText("");
        txtnombres.setText("");
        txtpais.setText("");
        txttelefono.setText("");
        txtnota.setText("");
        imgfoto.setImageResource(0);

        for(int i = 0; i < listacontactos.size(); i++)
        {
            // Actualiza los valores de los campos de la interfaz
            txtid.setText(String.valueOf(listacontactos.get(i).getId()));
            txtnombres.setText(listacontactos.get(i).getNombre());
            txtpais.setText(listacontactos.get(i).getPais());
            txttelefono.setText(listacontactos.get(i).getTelefono());
            txtnota.setText(listacontactos.get(i).getNota());
            byte[] byteArray = listacontactos.get(i).getFoto();
            Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
            imgfoto.setImageBitmap(bitmap);
        }
    }


    private void permisos() {

        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL_PHONE_PERMISSION);
        }else
        {
            dispatchTakePictureIntent();
        }
    }

    private void dispatchTakePictureIntent() {
        try {

            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:" + txttelefono.getText().toString()));
            startActivity(callIntent);
        }catch (Exception ex) {
            Toast.makeText(this, "No se pudo Llamar al Contactos", Toast.LENGTH_LONG).show();
        }
    }
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == REQUEST_CALL_PHONE_PERMISSION)
        {
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                dispatchTakePictureIntent();

            }
            else
            {
                Toast.makeText(getApplicationContext(), "se necesita el permiso del telefono",Toast.LENGTH_LONG).show();
            }
        }
    }
    private void CompartirContacto() {
        Bitmap bitmap = ((BitmapDrawable)imgfoto.getDrawable()).getBitmap();
        imgfoto.setImageBitmap(bitmap);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] byteArray = stream.toByteArray();

        Intent inten = new Intent(Intent.ACTION_SEND);
        inten.setType("image/jpeg");
        inten.putExtra(Intent.EXTRA_TEXT, txtnombres.getText().toString() + "," +
                txtpais.getText().toString() + "," +
                txttelefono.getText().toString() + "," +
                txtnota.getText().toString());
        inten.putExtra(Intent.EXTRA_STREAM, byteArray);
        startActivity(Intent.createChooser(inten, "Compartir Usando...."));
    }


    private void ActualizarContacto() {
        try {
        SQLiteConexion conexion = new SQLiteConexion(this, Transacciones.NameDatabase, null, 1);
        SQLiteDatabase db = conexion.getWritableDatabase();

        ContentValues valores = new ContentValues();
        valores.put("nombre", txtnombres.getText().toString());
        valores.put("pais", txtpais.getText().toString());
        valores.put("telefono", txttelefono.getText().toString());
        valores.put("nota", txtnota.getText().toString());

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        Bitmap bitmap = ((BitmapDrawable)imgfoto.getDrawable()).getBitmap();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        valores.put("foto", byteArray);

            int resultado = db.update(Transacciones.tablacontactos, valores, "id=?", new String[]{txtid.getText().toString()});
            if (resultado > 0) {
                Toast.makeText(this, "Se actualizó el registro con éxito", Toast.LENGTH_LONG).show();
                //LLamado para refrescar pantalla despues de eliminar el contacto
                FillList();
                ObtenerListaPersonas();
                ArrayAdapter<CharSequence> adp = new ArrayAdapter(this, android.R.layout.simple_spinner_item, Arreglocontactos);
                combopersonas.setAdapter(adp);
            } else {
                Toast.makeText(this, "No se pudo actualizar el registro", Toast.LENGTH_LONG).show();
            }
        } catch (Exception ex) {
            Toast.makeText(this, "No se pudo actualizar el dato", Toast.LENGTH_LONG).show();
        }
    }

    private void EliminarContacto() {
        try {
            SQLiteConexion conexion = new SQLiteConexion(this, Transacciones.NameDatabase, null, 1);
            SQLiteDatabase db = conexion.getWritableDatabase();
            Long Resultado = Long.valueOf(db.delete(Transacciones.tablacontactos, "id=?", new String[]{txtid.getText().toString()}));

            if (Resultado > 0) {
                Toast.makeText(this, "Se eliminó el registro con éxito", Toast.LENGTH_LONG).show();
                //LLamado para refrescar pantalla despues de eliminar el contacto
                FillList();
                ObtenerListaPersonas();
                ArrayAdapter<CharSequence> adp = new ArrayAdapter(this, android.R.layout.simple_spinner_item, Arreglocontactos);
                combopersonas.setAdapter(adp);
            } else {
                Toast.makeText(this, "No se pudo eliminar el registro", Toast.LENGTH_LONG).show();
            }
        } catch (Exception ex) {
            Toast.makeText(this, "No se pudo eliminar el dato", Toast.LENGTH_LONG).show();
        }
    }

    private void ObtenerListaPersonas()
    {
        SQLiteDatabase db = conexion.getReadableDatabase();
        Contactos person = null;
        listacontactos = new ArrayList<Contactos>();

        // Cursor
        Cursor cursor = db.rawQuery("SELECT * FROM contactos", null );

        while(cursor.moveToNext())
        {
            person = new Contactos();
            person.setId(cursor.getInt(0));
            person.setNombre(cursor.getString(1));
            person.setPais(cursor.getString(2));
            person.setTelefono(cursor.getString(3));
            person.setNota(cursor.getString(4));
            byte[] byteArray = cursor.getBlob(5);
            Bitmap bitmap;
            person.setFoto(byteArray);

            listacontactos.add(person);
        }

        cursor.close();
        FillList();
    }

    private void FillList()
    {
        Arreglocontactos = new ArrayList<String>();
        for(int i = 0; i < listacontactos.size(); i++)
        {
            Arreglocontactos.add(listacontactos.get(i).getId() + " | " +
                    listacontactos.get(i).getNombre() + " | " +
                    listacontactos.get(i).getPais() + " | " +
                    listacontactos.get(i).getTelefono() + " | " +
                    listacontactos.get(i).getNota() + " | " +
                    listacontactos.get(i).getNota() + " | ");
        }
    }
    private void CuadroDialogo(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        String exe = txtnombres.getText().toString();
        String ext = txtpais.getText().toString();
        String exn = txttelefono.getText().toString();
        String exp = txtnota.getText().toString();
        if(exe.isEmpty()){
            builder.setTitle("Error en Nombre");
            builder.setMessage("Ingresar Valor en Nombre");
        } else if( ext.isEmpty() ) {
            builder.setTitle("Error eb Pais");
            builder.setMessage("Ingresar Valor en Pais");
        }else if( exn.isEmpty()) {
            builder.setTitle("Error en Telefono");
            builder.setMessage("Ingresar Valor en Telefono");
        }else if( exp.isEmpty()) {
            builder.setTitle("Error en Nota");
            builder.setMessage("Ingresar Valor en Nota");
        }
        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Acción a realizar cuando se hace clic en el botón Aceptar
            }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Acción a realizar cuando se hace clic en el botón Cancelar
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void CuadroDialogo1(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setTitle("Accion");
            builder.setMessage("Desea llamar a"+ txtnombres.getText().toString());

        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Acción a realizar cuando se hace clic en el botón Aceptar
                permisos();
            }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Acción a realizar cuando se hace clic en el botón Cancelar
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}