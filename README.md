<style>
    .margen{
        margin-top:50px;
    }
</style>
<h2 align="center" style=>
Programas y documentación detallada del artículo "Electrical energy consumption monitoring system in the residential sector using IoT"
</h2>
<p class="margen" align="justify">
El archivo Sensor.ino es el programa de Arduino del sensor inteligente.
</p>
<p align="justify">
El archivo Diagrama.png es el diagrama eléctrico detallado del sensor inteligente.
</p>
<p align="justify">
El fichero thingspeak contiene dos archivos Energia.m y Defasamiento.m. El primero es una App de ThingSpeak del tipo Matlab Analysis para el cálculo de la energía y su costo. El segundo se trata de una App de ThingSpeak del tipo Matlab Visualization que genera la representación gráfica del desfasamiento entre el voltaje y la corriente.
</p>
<p align="justify">
El fichero java incluye 6 archivos Channels.java, AdminSQLiteOpenHelper.java, MainActivity.java, Facturacion.java, ShowChannelData.java, y DataRetrieve.java. Todos ellos en conjunto estructuran las funcionalidades de la aplicación móvil implementada. El primero se encarga de la administración y acceso a los datos alojados en el servidor, así como el acceso a la ventana de actualización de datos de facturación a través de las credenciales del servidor y el almacenamiento de credenciales en el dispositivo móvil a través de una base de datos SQLite.  El segundo es una clase encargado de la inicialización de la base de datos SQLite. El tercero es el encargado de almacenar un nuevo canal del servidor a la base de datos de la aplicación. El cuarto permite actualizar en el servidor los datos de facturación. El quinto es un modulo encargado de descargar los datos del canal y campo seleccionado para graficar la información en un Chart de la aplicación, así como permitir el acceso a las visualizaciones y ventanas emergentes relacionadas al consumo y costo de energía eléctrica. El sexto es un modulo encargado de descargar los históricos del canal y campo seleccionado en un archivo JSON, o CSV desde el servidor.
</p>
<p align="justify">
El fichero xml_layout incluye 8 archivos activity_channels.xml, activity_main.xml, activity_show_channel_data.xml, activity_facturacion.xml, activity_data_retrieve.xml, custom_toast.xml, spinner_item.xml y spinner_item_unit. Los primeros 5 archivos hacen referencia a la apariencia visual de cada ventana de la aplicación y referente a las funcionalidades de los ficheros java antes mencionados. El sexto es la apariencia de una ventana emergente que muestra la última medición de consumo de energía eléctrica y costo asociado. Los últimos 2 archivos hacen referencia al contenido de los menús desplegables de cada ventana.
</p>
<p align="justify">
El fichero xml_values incluye 5 archivos Language.xml, strings.xml, colors.xml, themes.xml, ic_launcher_background. Los primeros dos archivos contienen los nombres de etiquetas de cada uno de los campos de textos utilizados en la aplicación. El resto son referidos a las paletas de colores utilizadas en cada una de las ventanas de la aplicación.
</p>