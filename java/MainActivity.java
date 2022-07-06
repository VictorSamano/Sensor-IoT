package com.example.mkt_project;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private EditText et1, et2, et3;
    private Switch PC;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.ic_launcher_foreground);
        et1 = (EditText)findViewById(R.id.txt_URL);
        et2 = (EditText)findViewById(R.id.txt_ID);
        et3 = (EditText)findViewById(R.id.txt_API);
        PC= findViewById(R.id.PrCh);
        et3.setVisibility(View.INVISIBLE);
        et3.setText("-1");
        PC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (PC.isChecked()){
                    et3.setVisibility(View.VISIBLE);
                    et3.setText("");
                }else {
                    et3.setVisibility(View.INVISIBLE);
                    et3.setText("-1");
                }
            }
        });
    }

    public void Agregar(View view){
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this,"canales", null, 1);
        SQLiteDatabase BaseDeDatos = admin.getWritableDatabase();
        String URL = et1.getText().toString();
        String ID = et2.getText().toString();
        if (ID.startsWith("0")){
            Toast.makeText(this, "Si lo guarda", Toast.LENGTH_SHORT).show();
        }
        String APIK = et3.getText().toString();
        if (!URL.isEmpty() && !ID.isEmpty() && !APIK.isEmpty()){
            ContentValues registro = new ContentValues();
            registro.put("ID", ID);
            registro.put("URL", URL);
            registro.put("API", APIK);
            if (PC.isChecked()){
                registro.put("Priv", 1);
            }else {
                registro.put("Priv", 0);
            }
            BaseDeDatos.insert("TS", null, registro);
            BaseDeDatos.close();
            Toast.makeText(this, "Registro exitoso", Toast.LENGTH_SHORT).show();
            Intent i = new Intent(this, Channels.class);
            startActivity(i);
        }else{
            Toast.makeText(this,"Debes llenar todos los campos", Toast.LENGTH_SHORT).show();
        }
    }
}