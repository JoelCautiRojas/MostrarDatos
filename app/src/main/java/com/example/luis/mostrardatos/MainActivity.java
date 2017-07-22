package com.example.luis.mostrardatos;

import android.graphics.Rect;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import com.github.snowdream.android.widget.SmartImageView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import org.json.JSONArray;
import org.json.JSONException;
import cz.msebera.android.httpclient.Header;
public class MainActivity extends AppCompatActivity {
    TextView t1,t2,t3;
    SmartImageView sm1,sm2,sm3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
}