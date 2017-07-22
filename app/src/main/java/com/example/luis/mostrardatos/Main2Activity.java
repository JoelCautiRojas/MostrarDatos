package com.example.luis.mostrardatos;

import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import cz.msebera.android.httpclient.Header;

public class Main2Activity extends AppCompatActivity {
    EditText et1;
    ImageView im1;
    Button buscar,enviar;
    String ruta;
    File archivo;
    Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        et1 = (EditText) findViewById(R.id.editText);
        im1 = (ImageView) findViewById(R.id.imageView);
        buscar = (Button) findViewById(R.id.button);
        enviar = (Button) findViewById(R.id.button2);
        enviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AsyncHttpClient cliente = new AsyncHttpClient();
                RequestParams datos = new RequestParams();
                datos.put("nombre",et1.getText().toString());
                try {
                    datos.put("imagen",archivo);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                cliente.post("http://aktechnologysolutions.pe.hu/insertar.php", datos, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        Toast.makeText(getApplicationContext(),new String(responseBody),Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        Toast.makeText(getApplicationContext(),"Error sin conexion al servidor",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        buscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final CharSequence[] option = {"Tomar Foto","Cancelar"};
                final AlertDialog.Builder builder = new AlertDialog.Builder(Main2Activity.this);
                builder.setTitle("Elige una opcion");
                builder.setItems(option, new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which){
                        if(option[which]=="Tomar Foto")
                        {
                            File carpeta =  new File(Environment.getExternalStorageDirectory(), "MyCarpetaPrueba");
                            boolean isDirectoryCreated = carpeta.exists();
                            if(!isDirectoryCreated)
                            {
                                isDirectoryCreated = carpeta.mkdirs();
                            }
                            if(isDirectoryCreated)
                            {
                                Long timestamp = System.currentTimeMillis() / 1000;
                                String imageName = timestamp.toString()+".jpg";
                                ruta = Environment.getExternalStorageDirectory() + File.separator + "MyCarpetaPrueba" + File.separator + imageName;
                                archivo = new File(ruta);
                                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(archivo));
                                startActivityForResult(intent,100);
                            }
                        }
                        else
                        {
                            dialog.dismiss();
                        }
                    }
                });
                builder.show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK) {
            switch (requestCode) {
                case 100:
                    MediaScannerConnection.scanFile(this, new String[]{ruta}, null, new MediaScannerConnection.OnScanCompletedListener() {
                        @Override
                        public void onScanCompleted(String path, Uri uri) {

                        }
                    });
                    ContentResolver cr = this.getContentResolver();
                    try {
                        try {
                            bitmap = MediaStore.Images.Media.getBitmap(cr, Uri.fromFile(archivo));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        int rotate = 0;
                        ExifInterface exif = new ExifInterface(archivo.getAbsolutePath());
                        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                        switch (orientation) {
                            case ExifInterface.ORIENTATION_ROTATE_90:
                                rotate = 90;
                                break;
                            case ExifInterface.ORIENTATION_ROTATE_180:
                                rotate = 180;
                                break;
                            case ExifInterface.ORIENTATION_ROTATE_270:
                                rotate = 270;
                                break;
                        }
                        Matrix matriz = new Matrix();
                        matriz.postRotate(rotate);
                        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matriz, true);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    im1.setImageBitmap(bitmap);                    
                    break;
            }
        }
    }
}
