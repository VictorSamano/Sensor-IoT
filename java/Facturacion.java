package com.example.mkt_project;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.Calendar;

public class Facturacion extends AppCompatActivity {

    TextView fecha;
    TextView costo;
    int anio;
    int mes;
    int dia;
    int channelCuota = 1772680;
    String WriteAPIKEYCuota = "31OSRK9QHVLJI0XN";

    String updateurl = "http://api.thingspeak.com/update?api_key=" + WriteAPIKEYCuota;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facturacion);
        fecha = findViewById(R.id.EditCorte);
        costo = findViewById(R.id.EditCosto);
    }

    public void AbrirCalendario(View view){
        Calendar cal =Calendar.getInstance();
        anio = cal.get(Calendar.YEAR);
        mes = cal.get(Calendar.MONTH);
        dia = cal.get(Calendar.MONTH);

        DatePickerDialog dpd = new DatePickerDialog(Facturacion.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month++;
                String msg = day + "/" + month + "/" + year;
                fecha.setText(msg);
            }
        }, anio, mes, dia);
        dpd.show();
    }

    public void sube_facturacion(View view){
        String msg = updateurl;
        msg += "&field1=" + Integer.toString(dia);
        msg += "&field2=" + Integer.toString(mes);
        msg += "&field3=" + Integer.toString(anio);
        msg += "&field4=" + costo.getText().toString();

        //Toast.makeText(ControlsActivity.this,controlsResults + "," + Integer.toString(decoder) + ", " + msg,Toast.LENGTH_LONG).show();
        RequestQueue queue = Volley.newRequestQueue(Facturacion.this);
        StringRequest stringRequest =new StringRequest(Request.Method.GET, msg,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(Facturacion.this, "DATOS DE FACTURACION ACTUALIZADOS", Toast.LENGTH_LONG).show();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(Facturacion.this, "FALLA DE COMUNICACION, INTENTE DE NUEVO", Toast.LENGTH_LONG).show();
            }
        });
        queue.add(stringRequest);
    }

}