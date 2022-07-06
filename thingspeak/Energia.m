% Se leen los datos de la última fecha de facturación y el costo de la energía por kWh
fc= thingSpeakRead(1772680,'Fields',[1,2,3,4],'NumPoints',1,'ReadKey','O2CMN9CCXMZ1QO3O');
%se leen los datos de potencia de un día
[pot,time] = thingSpeakRead(1772678,'Fields',1,'Numpoints',5400,'ReadKey','F16CDKTZ74DNKHZ2');
%El vector de tiempo se convierte en un vector que contiene valores en horas
time=datenum(time)*24;
%El vector de potencia se convierte en un vector que contiene valores en kW
pot=pot./1000;
%Se calcula la energía consumida durante un día
enerDia=0;
for i=1:1:length(pot)-1
    enerDia=enerDia+((pot(i)+pot(i+1))*0.5*(time(i+1)-time(i)));
end
%Se recuperan los datos de energía del día desde el corte hasta la fecha
enerAnt= thingSpeakRead(1772682,'Fields',1,'DateRange',[datetime(fc(3),fc(2),fc(1),12,0,0),datetime('now')],'ReadKey','061ET6DU41RMQEO0');

enerTotal=0;
for i=1:1:length(enerAnt)
    enerTotal=enerTotal+enerAnt(i);
end
% A la energía calculada hasta la fecha se le suma la energía del día
enerTotal=enerTotal+enerDia;

C_total=enerTotal*fc(4);
C_promedio=C_total/(length(enerAnt)+1);

thingSpeakWrite(1772682,'Fields',[1,2,3,4],'Values',[enerDia,C_total,C_promedio,enerTotal],'WriteKey','IUAV02MF5LHOH57I');