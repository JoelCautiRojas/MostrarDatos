package com.example.luis.mostrardatos;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.github.snowdream.android.widget.SmartImageView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import org.json.JSONArray;
import org.json.JSONException;
import cz.msebera.android.httpclient.Header;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.Manifest.permission_group.CAMERA;

public class MainActivity extends AppCompatActivity {
    TextView t1,t2,t3;
    SmartImageView sm1,sm2,sm3;
    LinearLayout milayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(verificarPermisos())
        {
           Toast.makeText(getApplicationContext(),"Permisos correctos",Toast.LENGTH_SHORT).show();
        }
        else
        {
        }
        milayout = (LinearLayout) findViewById(R.id.milayout);
        t1 = (TextView) findViewById(R.id.nombre1);
        t2 = (TextView) findViewById(R.id.nombre2);
        t3 = (TextView) findViewById(R.id.nombre3);
        sm1 = (SmartImageView) findViewById(R.id.imagen1);
        sm2 = (SmartImageView) findViewById(R.id.imagen2);
        sm3 = (SmartImageView) findViewById(R.id.imagen3);
        AsyncHttpClient cliente = new AsyncHttpClient();
        RequestParams datos = new RequestParams();
        cliente.post("http://aktechnologysolutions.pe.hu/", datos, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if(statusCode==200)
                {
                    String respuesta = new String(responseBody);
                    try {
                        JSONArray respuesta_matriz = new JSONArray(respuesta);
                        t1.setText(respuesta_matriz.getJSONObject(0).getString("nombre"));
                        t2.setText(respuesta_matriz.getJSONObject(1).getString("nombre"));
                        t3.setText(respuesta_matriz.getJSONObject(2).getString("nombre"));
                        Rect rectangulo = new Rect(sm1.getLeft(),sm1.getTop(),sm1.getRight(),sm1.getBottom());
                        sm1.setImageUrl("http://aktechnologysolutions.pe.hu/imagenes/"+respuesta_matriz.getJSONObject(0).getString("imagen"),rectangulo);
                        Rect rectangulo2 = new Rect(sm2.getLeft(),sm2.getTop(),sm2.getRight(),sm2.getBottom());
                        sm2.setImageUrl("http://aktechnologysolutions.pe.hu/imagenes/"+respuesta_matriz.getJSONObject(1).getString("imagen"),rectangulo2);
                        Rect rectangulo3 = new Rect(sm3.getLeft(),sm3.getTop(),sm3.getRight(),sm3.getBottom());
                        sm3.setImageUrl("http://aktechnologysolutions.pe.hu/imagenes/"+respuesta_matriz.getJSONObject(2).getString("imagen"),rectangulo3);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(),"Error con formato de JSON",Toast.LENGTH_SHORT).show();
                    }
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getApplicationContext(),"No se pudo conectar al servidor",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean verificarPermisos() {
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
        {
            return true;
        }
        /*if((checkSelfPermission(WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED)&&(checkSelfPermission(CAMERA) == PackageManager.PERMISSION_GRANTED))
        {
            return true;
        }*/
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED ) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,Manifest.permission.WRITE_EXTERNAL_STORAGE)||ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,Manifest.permission.CAMERA)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.CAMERA},
                        100);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
        else
        {
            return true;
        }
        return false;
    }

    public void OnRequestPermissionResult(int requestCode, @NonNull String[] permisions, @NonNull int[] grantResults){
        super.onRequestPermissionsResult(requestCode, permisions, grantResults);
        switch (requestCode)
        {
            case 100:
                if((grantResults.length == 2) && (grantResults[0] == PackageManager.PERMISSION_GRANTED) && (grantResults[1] == PackageManager.PERMISSION_GRANTED))
                {
                    Toast.makeText(MainActivity.this, "Permisos Aceptados", Toast.LENGTH_SHORT).show();
                }
                else
                {
                   AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("Permisos Denegados");
                    builder.setMessage("Para usar las funciones de la app necesitas aceptar los permisos");
                    builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent = new Intent();
                            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            Uri uri = Uri.fromParts("package",getPackageName(),null);
                            intent.setData(uri);
                            startActivity(intent);
                        }
                    });
                    builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
                    builder.show();
                }
                break;
            default:
                break;
        }
    }

    public void siguiente(View view) {
        Intent sig = new Intent(this,Main2Activity.class);
        startActivity(sig);
    }

    public void mapa(View view) {
        Intent mapa = new Intent(this,MapsActivity.class);
        startActivity(mapa);
    }
}