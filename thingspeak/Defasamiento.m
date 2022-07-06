% Enter your MATLAB code below
readChannelID =  1772665;
readAPIKEY = "RKM61062UODD9LK7";

data = thingSpeakRead( readChannelID, 'ReadKey', readAPIKEY);
T=1/60;
t=0:T/12:T;
t_cur = 1:length(t);
Varray = 1:length(t);
Carray = 1:length(t);

for c=1:length(t)
    Varray(c) = data(1)*sin(2*pi*60*t(c));
end
fase= data(4);
t_desfase = fase*T/360;
for c=1:length(t)
    Carray(c) = data(2)*sin(2*pi*60*t(c)-fase*pi/180);
end

hold on
yyaxis left
ylabel('Voltaje (V)')
xlabel('Tiempo (s)')
plot(t,Varray, 'b')
yyaxis right
ylabel('Corriente (A)')
plot(t,Carray)
title('Desfase entre señales de Voltaje y Corriente')
grid on
grid minor;
hold off