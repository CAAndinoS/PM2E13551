package com.example.pm2e10435;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.DialogFragment;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import android.Manifest;
import com.example.pm2e10435.configuracion.SQLiteConexion;
import com.example.pm2e10435.transacciones.Transacciones;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    static final  int REQUEST_IMAGE = 101;
    static final  int PETICION_ACCESS_CAM = 201;

    ImageView imageView;
    String currentPhotoPath;

    // Global
    EditText  nombre, telefono, nota;
    Spinner pais;
    Button btnguardar, btnlista;
    ImageButton btnfoto;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Spinner spinnerPais = findViewById(R.id.spPais);
        ArrayAdapter<CharSequence> adapter=ArrayAdapter.createFromResource(this, R.array.languages, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        spinnerPais.setAdapter(adapter);




        pais = (Spinner) findViewById(R.id.spPais);

        nombre = (EditText) findViewById(R.id.etNombre);
        telefono = (EditText) findViewById(R.id.etPhone);
        nota = (EditText) findViewById(R.id.etNota);

        btnguardar = (Button) findViewById(R.id.btnGuardar);
        btnlista = (Button) findViewById(R.id.btnLista);

        imageView = (ImageView) findViewById(R.id.imageViewuth);
        btnfoto = (ImageButton) findViewById(R.id.btnFoto);

        btnfoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                permisos();
            }
        });

        btnguardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String ext = nombre.getText().toString();
                String exl = telefono.getText().toString();
                String exp = nota.getText().toString();
                if(ext.isEmpty() || exl.isEmpty() || exp.isEmpty()){
                    CuadroDialogo();
                } else  {
                    AgregarPersona();
                }
            }

        });
        btnlista.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(getApplicationContext(), listacombo.class);
                startActivity(intent);
            }
        });


        pais.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Obtiene el elemento seleccionado del Spinner
                String elementoSeleccionado = parent.getItemAtPosition(position).toString();
                // Realiza cualquier acción necesaria con el elemento seleccionado
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // No se hace nada si no se selecciona ningún elemento
}
        });
    }
    private void permisos() {
        // Metodo para obtener los permisos requeridos de la aplicacion
        if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},PETICION_ACCESS_CAM);
        }
        else
        {
            dispatchTakePictureIntent();

        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == PETICION_ACCESS_CAM)
        {
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                dispatchTakePictureIntent();

            }
            else
            {
                Toast.makeText(getApplicationContext(), "se necesita el permiso de la camara",Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_IMAGE && resultCode == RESULT_OK)
        {

            if (currentPhotoPath != null) {
            try {
                File foto = new File(currentPhotoPath);
                Bitmap bitmap = BitmapFactory.decodeFile(foto.getAbsolutePath());
                imageView.setImageBitmap(bitmap);
                //imageView.setImageURI(Uri.fromFile(foto));
            }
            catch (Exception ex)
            {
                ex.toString();
            }
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                ex.toString();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.pm2e10435.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE);
            }
        }
    }
    private void AgregarPersona()
    {
        try
        {
            SQLiteConexion conexion = new SQLiteConexion(this, Transacciones.NameDatabase, null,1);
            SQLiteDatabase db = conexion.getWritableDatabase();

            File imageFile = createImageFile();
            //Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
            Bitmap bitmap = ((BitmapDrawable)imageView.getDrawable()).getBitmap();
            imageView.setImageBitmap(bitmap);

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] byteArray = stream.toByteArray();



            ContentValues valores = new ContentValues();
            valores.put("nombre", nombre.getText().toString());
            valores.put("pais", pais.getItemAtPosition(pais.getSelectedItemPosition()).toString());
            valores.put("telefono", telefono.getText().toString());
            valores.put("nota", nota.getText().toString());
            valores.put("foto", byteArray);

            Long Resultado = db.insert(Transacciones.tablacontactos, "id", valores);
            Toast.makeText(this, Resultado.toString(), Toast.LENGTH_SHORT).show();

            ClearScreen();
        }
        catch (Exception ex)
        {
            Toast.makeText(this,"No se pudo insertar el dato", Toast.LENGTH_LONG).show();
        }
    }

    private void ClearScreen()
    {
        nombre.setText(Transacciones.Empty);
        telefono.setText(Transacciones.Empty);
        nota.setText(Transacciones.Empty);

    }

    private void MostrarCliente()
    {
        String mensaje = nombre.getText().toString() +
                " | " + telefono.getText().toString() +
                " | " + nota.getText().toString() +
                " | " + pais.getItemAtPosition(pais.getSelectedItemPosition()).toString();

        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show();
    }
    private void CuadroDialogo(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        String ext = nombre.getText().toString();
        String exl = telefono.getText().toString();
        String exp = nota.getText().toString();
        if(ext.isEmpty()){
            builder.setTitle("Error en Nombre");
            builder.setMessage("Ingresar Valor en Nombre");
        } else if( exl.isEmpty() ) {
            builder.setTitle("Error eb Telefono");
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

}