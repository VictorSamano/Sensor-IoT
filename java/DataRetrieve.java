package com.example.mkt_project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.DownloadManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

public class DataRetrieve extends AppCompatActivity {
    private LinearLayout FieldC,wv_data,Infodata;
    private static final int PERMISSION_STORAGE_CODE = 1000;
    private Switch DeDa;
    private Spinner SpFi, SpFormat;
    WebView RetData;
    private String Id;
    private MenuItem IBack, IShared;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_retrieve);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.ic_launcher_foreground);
        /*FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText( view.getContext(), "hola", Toast.LENGTH_SHORT).show();
            }
        });*/
        FieldC= findViewById(R.id.Field);
        FieldC.setVisibility(View.INVISIBLE);
        DeDa= findViewById(R.id.desireddata);
        DeDa.setChecked(false);
        DeDa.setText("Todos los campos");
        SpFi= findViewById(R.id.Spin_Field);
        SpFormat= findViewById(R.id.Sp_Format);
        wv_data= findViewById(R.id.WVdata);
        Infodata= findViewById(R.id.InfoData);
        wv_data.setVisibility(View.INVISIBLE);
        RetData= findViewById(R.id.wvDatos);
        RetData.setWebChromeClient(new WebChromeClient());
        RetData.getSettings().setJavaScriptEnabled(true);
        String [] Opc= getIntent().getStringArrayExtra("Opciones");
        Id= getIntent().getStringExtra("ID");
        String [] OpcFT = {"csv", "xml", "json"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_item_unit, Opc);
        ArrayAdapter<String> adapter1 = new ArrayAdapter<>(this, R.layout.spinner_item_unit, OpcFT);
        SpFi.setAdapter(adapter);
        SpFormat.setAdapter(adapter1);
    }

    public void RecuperarDatos(View view){
        String RField= String.valueOf(SpFi.getSelectedItemPosition()+1);
        String RFormat=SpFormat.getSelectedItem().toString();
        if (!DeDa.isChecked()){
            RetData.loadUrl("https://thingspeak.com/channels/"+Id+"/feed."+ RFormat.toLowerCase());
        }else {
            RetData.loadUrl("https://thingspeak.com/channels/"+Id+"/field/"+RField+"."+RFormat.toLowerCase());
        }
        if (RFormat=="csv"){
            if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
                if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_DENIED){
                    String [] permissions= {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                    requestPermissions(permissions, PERMISSION_STORAGE_CODE);
                }else {
                    startDownloading();
                }
            }
            IBack.setVisible(false);
            IShared.setVisible(false);
        }else {
            wv_data.setVisibility(View.VISIBLE);
            IBack.setVisible(true);
            IShared.setVisible(true);
            Infodata.setVisibility(View.INVISIBLE);
            getSupportActionBar().setTitle(RFormat);
        }
    }

    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.sharesction, menu);
        IBack = menu.findItem(R.id.Back);
        IShared = menu.findItem(R.id.Share);
        IBack.setVisible(false);
        IShared.setVisible(false);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item){
        int id= item.getItemId();
        if (id== R.id.Share){
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Url", RetData.getUrl());
            clipboard.setPrimaryClip(clip);
            Toast.makeText(this, "Url copiado al portapapeles", Toast.LENGTH_SHORT).show();
            return true;
        }
        else if (id== R.id.Back){
            Infodata.setVisibility(View.VISIBLE);
            wv_data.setVisibility(View.INVISIBLE);
            IBack.setVisible(false);
            IShared.setVisible(false);
            getSupportActionBar().setTitle(getTitle());
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /*public void Regresar(View view){
        Infodata.setVisibility(View.VISIBLE);
        wv_data.setVisibility(View.INVISIBLE);
    }*/

    public void AllData(View view){
        if (DeDa.isChecked()){
            FieldC.setVisibility(View.VISIBLE);
            DeDa.setText("Un solo campo");
        }else {
            FieldC.setVisibility(View.INVISIBLE);
            DeDa.setText("Todos los campos");
        }
    }

    /*public void CopyUrl(View view){
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Url", RetData.getUrl());
        clipboard.setPrimaryClip(clip);
        Toast.makeText(this, "Url copiado al portapapeles", Toast.LENGTH_SHORT).show();
    }*/

    private void startDownloading(){
        String url = RetData.getUrl();
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
        request.setTitle("Download file "+ System.currentTimeMillis()+".csv");
        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, String.valueOf(+ System.currentTimeMillis()));
        DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        manager.enqueue(request);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case PERMISSION_STORAGE_CODE:{
                if(grantResults.length>0 && grantResults[0]== PackageManager.PERMISSION_GRANTED){
                    startDownloading();
                }else {
                    Toast.makeText(this, "Permiso denegado", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
