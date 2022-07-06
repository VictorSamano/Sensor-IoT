package com.example.mkt_project;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.macroyau.thingspeakandroid.ThingSpeakChannel;
import com.macroyau.thingspeakandroid.ThingSpeakLineChart;
import com.macroyau.thingspeakandroid.model.ChannelFeed;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import lecho.lib.hellocharts.formatter.SimpleAxisValueFormatter;
import lecho.lib.hellocharts.formatter.SimpleLineChartValueFormatter;
import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.view.LineChartView;

public class ShowChannelData extends AppCompatActivity {

    private EditText NOD;
    private TextView Prom;
    private ThingSpeakChannel tsChannel;
    private ThingSpeakLineChart tsChart;
    private LineChartView chartView;
    private Spinner Unidad, Graph;
    private int Unit,field, MaxData;
    WebView ChartSen;
    private String PCh, APIK, IDSave;
    private String [] Op;
    private Button RData;
    SimpleLineChartValueFormatter lineFormatter = new SimpleLineChartValueFormatter();
    SimpleAxisValueFormatter axisValueFormatter = new SimpleAxisValueFormatter();

    int channelWeb = 1772665;
    int channelEnergia = 1772682;
    double last_value = 0;


    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_channel_data);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.ic_launcher_foreground);
        /*Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);*/
        Unidad = (Spinner) findViewById(R.id.S_Unit);
        Graph = (Spinner) findViewById(R.id.S_Graph);
        NOD = (EditText) findViewById(R.id.txt_Num);
        Prom = (TextView) findViewById(R.id.Average);
        RData= findViewById(R.id.DD);
        ChartSen = findViewById(R.id.WV);
        ChartSen.setVisibility(View.INVISIBLE);
        ChartSen.setWebViewClient(new WebViewClient());
        ChartSen.getSettings().setJavaScriptEnabled(true);
        ChartSen.loadUrl(getResources().getString(R.string.URLSamano));
        Unit = getIntent().getIntExtra("U", 15);//60);
        field = getIntent().getIntExtra("Fd", 1);

        String[] Opc = {"Select an Option", "Samples"};
        String[] OPC = {"", "", "", "", "", "", "", ""};
        String[] opciones = {"Time Frame", "Samples", "Minutes", "Hours", "Days"};
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, "canales", null, 1);
        SQLiteDatabase BaseDeDatos = admin.getReadableDatabase();
        IDSave = getIntent().getStringExtra("ID");
        lineFormatter.setDecimalDigitsNumber(4);
        axisValueFormatter.setDecimalDigitsNumber(2);
        Cursor fila = BaseDeDatos.rawQuery("select * from TS where ID =" + IDSave, null);
        if (fila.moveToFirst()) {
            if (fila.getInt(3)==1) {
                PCh = "TRUE";
                APIK = fila.getString(2);
            } else {
                PCh = "FALSE";
            }
        }
        if (PCh=="TRUE"){
            RData.setVisibility(View.INVISIBLE);
        }else {
            RData.setVisibility(View.VISIBLE);
        }
        ArrayAdapter<CharSequence> adapter;
        if (Integer.parseInt(IDSave)!=channelWeb){
            adapter = ArrayAdapter.createFromResource(this, R.array.combo_Samples, R.layout.spinner_item_unit);
            //adapter = new ArrayAdapter<>(this, R.layout.spinner_item_unit, Opc);

        }else {
            adapter = ArrayAdapter.createFromResource(this, R.array.combo_Opciones, R.layout.spinner_item_unit);
            //adapter = new ArrayAdapter<>(this, R.layout.spinner_item_unit, opciones);
        }
        Unidad.setAdapter(adapter);
        if (PCh=="FALSE")
            tsChannel = new ThingSpeakChannel(Long.parseLong(IDSave));
        else
            tsChannel = new ThingSpeakChannel(Long.parseLong(IDSave), APIK);
        // Set listener for Channel feed update events
        tsChannel.setChannelFeedUpdateListener(new ThingSpeakChannel.ChannelFeedUpdateListener() {
            @Override
            public void onChannelFeedUpdated(long channelId, String channelName, ChannelFeed channelFeed) {
                Date lastUpdate = channelFeed.getChannel().getUpdatedAt();
                Toast.makeText(ShowChannelData.this, lastUpdate.toString(), Toast.LENGTH_LONG).show();

                // contar numeros de campos utilizados del canal
                int Nofields=0;

                for (int i= 1; i<=8; i++){
                    if (channelFeed.getChannel().getFieldName(i)==null)
                        break;
                    OPC[i-1]=channelFeed.getChannel().getFieldName(i);
                    Nofields++;
                }
                int j= Nofields;
                if (channelId==channelWeb){
                    Nofields++;
                }
                if (channelId==channelEnergia){
                    //Nofields+=4;
                }
                Op= new String[Nofields];
                // Copiar nombres de canales
                for (int i =0; i<j; i++){
                    Op[i]=OPC[i];
                }
                if (channelId==channelWeb){
                    Op[j]=getResources().getString(R.string.Visualizacion);
                }
                ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(ShowChannelData.this, R.layout.spinner_item_unit, Op);
                Graph.setAdapter(adapter1);
                Graph.setSelection(field-1);
                for (int i=  1; i<9;i++){
                    if(!channelFeed.getChannel().getFieldName(i).isEmpty()){
                        break;
                    }
                }
                //Toast.makeText(ShowChannelData.this, FNames., Toast.LENGTH_LONG).show();
            }
        });
        tsChannel.loadChannelFeed();
        // Create a Calendar object dated 5 minutes ago
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, -5);
        chartView = findViewById(R.id.chart);
        chartView.setZoomEnabled(true);
        chartView.setZoomType(ZoomType.HORIZONTAL_AND_VERTICAL);
        chartView.setValueSelectionEnabled(true);
        chartView.setBackgroundColor(Color.parseColor("#FFFFFF"));
        // Create a line chart from Field1 of ThinkSpeak Channel
        tsChart = new ThingSpeakLineChart(Long.parseLong(IDSave), field);
        //https://api.thingspeak.com/channels/266256/fields/2/last.json
        if (PCh=="TRUE"){
            tsChart.setReadApiKey(APIK);
        }
        //tsChart.setReadApiKey(APIK);
        // Get 200 entries at maximum
        tsChart.setNumberOfEntries(Unit);
        // Set value axis labels on 10-unit interval
        tsChart.setValueAxisLabelInterval((float) 0.05);
        // Set date axis labels on 5-minute interval
        tsChart.setDateAxisLabelInterval(5);
        // Show the line as a cubic spline
        tsChart.useSpline(false);
        // Set the line color
        tsChart.setLineColor(Color.parseColor("#D32F2F"));
        // Set the axis color
        tsChart.setAxisColor(Color.parseColor("#455a64"));
        // Set the starting date (5 minutes ago) for the default viewport of the chart
        tsChart.setChartStartDate(calendar.getTime());
        // Set listener for chart data update
        tsChart.setListener(new ThingSpeakLineChart.ChartDataUpdateListener() {
            @Override
            public void onChartDataUpdated(long channelId, int fieldId, String title, LineChartData lineChartData, Viewport maxViewport, Viewport initialViewport) {
                // Set chart data to the LineChartView\\\
                //LineChartValueFormatter formatter= new SimpleLineChartValueFormatter(3);
                List<Line> lines = new ArrayList<Line>();
                List<Line> Av = lineChartData.getLines();
                Line line = new Line(Av.get(0).getValues()).setColor(Color.GRAY).setHasLabelsOnlyForSelected(true).setFormatter(lineFormatter);
                lines.add(line);
                lineChartData.setLines(lines);
               // lineChartData.getLines().set(0, (Line) lineChartData.getLines()).setFormatter(formatter);
                Axis Axis = lineChartData.getAxisYLeft();
                Axis.setFormatter(axisValueFormatter).setHasLines(true).setMaxLabelChars(7);
                lineChartData.setAxisYLeft(Axis);
                chartView.setLineChartData(lineChartData);
                // Set scrolling bounds of the chart
                maxViewport.set(maxViewport.left,maxViewport.top,maxViewport.right-10,maxViewport.bottom);
                chartView.setMaximumViewport(maxViewport);
                // Set the initial chart bounds
                initialViewport.set(initialViewport.left,initialViewport.top,initialViewport.right-10,initialViewport.bottom);
                chartView.setCurrentViewport(initialViewport);
                //Toast.makeText(ShowChannelData.this, title, Toast.LENGTH_SHORT).show();
                double prom=0;
                for (int i=0; i<Unit; i++) {
                    prom = Av.get(0).getValues().get(i).getY() + prom;
                }
                Prom.setText(getResources().getString(R.string.Mean) + Math.round(prom/Unit*10000.0)/10000.0);



            }
        });
        // Load chart data asynchronously
        tsChart.loadChartData();
        Graph.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(ShowChannelData.this, ShowChannelData.class);
                if (field!=position+1) {
                    /*getIntent().putExtra("Fd", position + 1);
                    getIntent().putExtra("U", Unit);
                    recreate();*/
                    field=position+1;
                    //Toast.makeText(ShowChannelData.this, "Posicion:" + Integer.toString(position),Toast.LENGTH_SHORT).show();
                    if (Integer.parseInt(IDSave)==channelWeb){
                        if (Graph.getAdapter().getCount()==field){
                            chartView.setVisibility(View.INVISIBLE);
                            ChartSen.setVisibility(View.VISIBLE);
                            Prom.setVisibility(View.INVISIBLE);
                            return;
                        }else{
                            chartView.setVisibility(View.VISIBLE);
                            ChartSen.setVisibility(View.INVISIBLE);
                            Prom.setVisibility(View.VISIBLE);
                        }
                    }

                    tsChart = new ThingSpeakLineChart(Long.parseLong(IDSave), field);
                    if (PCh=="TRUE"){
                        tsChart.setReadApiKey(APIK);
                    }
                    //tsChart.setReadApiKey(APIK);
                    // Get 200 entries at maximum
                    tsChart.setNumberOfEntries(Unit);
                    // Set value axis labels on 10-unit interval
                    tsChart.setValueAxisLabelInterval((float) 0.05);
                    // Set date axis labels on 5-minute interval
                    tsChart.setDateAxisLabelInterval(5);
                    // Show the line as a cubic spline
                    tsChart.useSpline(false);
                    // Set the line color
                    tsChart.setLineColor(Color.parseColor("#D32F2F"));
                    // Set the axis color
                    tsChart.setAxisColor(Color.parseColor("#455a64"));
                    // Set the starting date (5 minutes ago) for the default viewport of the chart
                    tsChart.setChartStartDate(calendar.getTime());
                    // Set listener for chart data update
                    try {
                        tsChart.setListener(new ThingSpeakLineChart.ChartDataUpdateListener() {
                            @Override
                            public void onChartDataUpdated(long channelId, int fieldId, String title, LineChartData lineChartData, Viewport maxViewport, Viewport initialViewport) {
                                // Set chart data to the LineChartView
                                List<Line> lines = new ArrayList<Line>();
                                List<Line> Av = lineChartData.getLines();
                                Line line = new Line(Av.get(0).getValues()).setColor(Color.GRAY).setHasLabelsOnlyForSelected(true).setFormatter(lineFormatter);
                                lines.add(line);
                                lineChartData.setLines(lines);
                                Axis Axis = lineChartData.getAxisYLeft();
                                Axis.setFormatter(axisValueFormatter).setHasLines(true).setMaxLabelChars(7);
                                lineChartData.setAxisYLeft(Axis);
                                chartView.setLineChartData(lineChartData);
                                // Set scrolling bounds of the chart
                                chartView.setMaximumViewport(maxViewport);
                                // Set the initial chart bounds
                                chartView.setCurrentViewport(initialViewport);
                                //Toast.makeText(ShowChannelData.this, title, Toast.LENGTH_SHORT).show();
                                double prom=0, min=0, max=0;
                                for (int i=0; i<Av.get(0).getValues().size(); i++) {
                                    double j=Av.get(0).getValues().get(i).getY();
                                    prom =  j + prom;
                                }
                                Prom.setText(getResources().getString(R.string.Mean) + Math.round(prom/Av.get(0).getValues().size()*10000.0)/10000.0);
                                last_value = Av.get(0).getValues().get(Av.get(0).getValues().size()-1).getY();

                                if (Integer.parseInt(IDSave)==channelEnergia){
                                    Prom.setVisibility(View.INVISIBLE);
                                    String title2;
                                    String value;
                                    String unid;
                                    //last_value = Av.get(0).getValues().get(Av.get(0).getValues().size()-1).getY();
                                    switch(position){
                                        case 1:
                                            title2 = "Costo Generado";    value="5.244";   unid="$";
                                            break;
                                        case 2:
                                            title2 = "Costo Generado por Día";    value="0.3496";   unid="$";
                                            break;
                                        case 3:
                                            title2 = "Energía Consumida";    value="5.814";   unid="kWh";
                                            break;
                                        case 4:
                                            title2 = "Costo Diario Generado";    value="0.3130";   unid="$";
                                            break;
                                        case 5:
                                            title2 = "Energía Consumida por Día";    value="0.3876";   unid="kWh";
                                            break;
                                        default:
                                            title2 = "Energía Consumida en el Día";    value="0.3470";   unid="kWh";
                                            break;
                                    }
                                    value = String.format("%1.2f",last_value);
                                    mensaje(title2,value,unid);
                                }

                            }
                        });

                        tsChart.loadChartData();
                    }catch (IndexOutOfBoundsException e){
                        Toast.makeText(ShowChannelData.this,"El numero de datos solicitados excede los datos existentes", Toast.LENGTH_LONG).show();
                        //Unit=PrevUnit;
                    }



                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        fila.close();
        BaseDeDatos.close();
    }
    public void Act(View view){
        Integer Un;
        if (!NOD.getText().toString().isEmpty() && Unidad.getSelectedItemPosition()!=0){
            if (Unidad.getSelectedItemPosition()== 1) {
                Un = 1;
            }else if (Unidad.getSelectedItemPosition()== 2){
                Un=4;
            }else if (Unidad.getSelectedItemPosition()==3){
                Un=4*60;
            }else if (Unidad.getSelectedItemPosition()==4){
                Un=4*60*24;
            }else if (Unidad.getSelectedItemPosition()==5){
                Un= 4*60*24*7;
            }else
                Un= 4*60*24*7*30;
            int PrevUnit= Unit;
            Unit = Un * Integer.parseInt(NOD.getText().toString());
            if(Unit<=8000) {
                try {
                    tsChart.setNumberOfEntries(Unit);
                    tsChart.setListener(new ThingSpeakLineChart.ChartDataUpdateListener() {
                        @Override
                        public void onChartDataUpdated(long channelId, int fieldId, String title, LineChartData lineChartData, Viewport maxViewport, Viewport initialViewport) {
                            // Set chart data to the LineChartView
                            List<Line> lines = new ArrayList<Line>();
                            List<Line> Av = lineChartData.getLines();
                            Line line = new Line(Av.get(0).getValues()).setColor(Color.GRAY).setHasLabelsOnlyForSelected(true).setFormatter(lineFormatter);
                            lines.add(line);
                            lineChartData.setLines(lines);
                            Axis Axis = lineChartData.getAxisYLeft();
                            Axis.setFormatter(axisValueFormatter).setHasLines(true).setMaxLabelChars(7);
                            lineChartData.setAxisYLeft(Axis);
                            chartView.setLineChartData(lineChartData);
                            // Set scrolling bounds of the chart
                            chartView.setMaximumViewport(maxViewport);
                            // Set the initial chart bounds
                            chartView.setCurrentViewport(initialViewport);
                            //Toast.makeText(ShowChannelData.this, title, Toast.LENGTH_SHORT).show();
                            double prom = 0;
                            for (int i = 0; i < Av.get(0).getValues().size()-1; i++) {
                                prom = Av.get(0).getValues().get(i).getY() + prom;
                            }
                            Prom.setText(getResources().getString(R.string.Mean) + Math.round(prom / (Av.get(0).getValues().size()-1) * 10000.0) / 10000.0);
                            if (Unit!=Av.get(0).getValues().size()){
                                 NOD.setText(String.valueOf(Av.get(0).getValues().size()));
                                 Unidad.setSelection(1);
                                 Toast.makeText(ShowChannelData.this,"La cantidad solicitada ha sido ajustada a los valores existentes", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                    // Load chart data asynchronously
                    tsChart.loadChartData();
                } catch (IndexOutOfBoundsException e){
                    Toast.makeText(this,"El numero de datos solicitados excede los datos existentes", Toast.LENGTH_LONG).show();
                    Unit=PrevUnit;
                }

            }
            else
                Toast.makeText(this,"La cantidad excede el limite de datos a mostrar", Toast.LENGTH_LONG).show();
        }else {
            Toast.makeText(ShowChannelData.this, "Ingrese todos los valores requeridos",Toast.LENGTH_SHORT).show();
        }
    }
    public void RD(View view){
        Intent i = new Intent(this, DataRetrieve.class);
        i.putExtra("Opciones", Op);
        i.putExtra("ID", IDSave);
        startActivity(i);
    }

    public void mensaje(String title, String value, String unid){
        LayoutInflater layoutInflater = getLayoutInflater();

        View view = layoutInflater.inflate(R.layout.custom_toast, (ViewGroup) findViewById(R.id.visor));
        TextView titulo = view.findViewById(R.id.textView20);
        TextView valor = view.findViewById(R.id.textView21);
        TextView unidad = view.findViewById(R.id.textView22);

        titulo.setText(title);
        valor.setText(value);
        unidad.setText(unid);

        Toast toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.CENTER_VERTICAL,0,50);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(view);
        toast.show();
    }

}