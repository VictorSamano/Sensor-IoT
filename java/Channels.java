package com.example.mkt_project;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class Channels extends AppCompatActivity {

    private Spinner canal;
    ArrayList<String> listacanales;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_channels);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.ic_launcher_foreground);
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, "canales", null, 1);
        SQLiteDatabase BaseDeDatos = admin.getReadableDatabase();
        canal = (Spinner)findViewById(R.id.ID_sel);
        Cursor cursor = BaseDeDatos.rawQuery("select * from TS", null);
        int i= 0;
        while (cursor.moveToNext()){
            i++;
        }
        cursor.close();
        cursor = BaseDeDatos.rawQuery("select * from TS", null);
        String canales[]= new String[i];
        i= 0;
        while (cursor.moveToNext()){
            canales[i] = cursor.getString(0);
            i++;
        }
        listacanales = new ArrayList<String>();
        listacanales.add(getResources().getString(R.string.ChannelID));
        for (int j= 0; j<=i-1; j++){
            if (canales[j].startsWith("0")) {
                listacanales.add("0" + canales[j]);
                Toast.makeText(this, "Tengo un 0",Toast.LENGTH_SHORT).show();
            }
            else
                listacanales.add(canales[j]);
        }
        ArrayAdapter<CharSequence> adaptador = new ArrayAdapter(this, R.layout.spinner_item,listacanales);
        canal.setAdapter(adaptador);
        cursor.close();
        BaseDeDatos.close();
    }

    public void NewChannel(View view){
        Intent i = new Intent(this,MainActivity.class);
        startActivity(i);
    }

    public void ShowChannel(View view){
        if (canal.getSelectedItemPosition()==0){
            Toast.makeText(this, "Seleccione una opción valida", Toast.LENGTH_SHORT).show();
        }else {
            Intent i = new Intent(this, ShowChannelData.class);
            i.putExtra("ID", canal.getSelectedItem().toString());
            startActivity(i);
        }
    }
    public void  DelChannel(View view){
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, "canales", null, 1);
        SQLiteDatabase BaseDeDatos = admin.getReadableDatabase();
        String ID = canal.getSelectedItem().toString();
        if (canal.getSelectedItemPosition() != 0){
            int cantidad = BaseDeDatos.delete("TS", "ID=" + ID, null);
            BaseDeDatos.close();
            if (cantidad == 1){
                Toast.makeText(this, "Canal eliminado exitosamente", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this, "El canal no existe", Toast.LENGTH_SHORT).show();
            }
            listacanales.remove(canal.getSelectedItemPosition());
            ArrayAdapter<CharSequence> adaptador = new ArrayAdapter(this, R.layout.spinner_item,listacanales);
            canal.setAdapter(adaptador);
        }else {
            Toast.makeText(this, "Debes introducir una opción valida",Toast.LENGTH_SHORT).show();
        }
    }

    public void ShowPay(View view){
            Intent i = new Intent(this, Facturacion.class);
            startActivity(i);

    }
}