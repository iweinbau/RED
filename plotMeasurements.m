data = fileread('measurements_2_box.txt');
data = strrep(data, ',', '.');
FID = fopen('measurements_2_box.txt', 'w');
fwrite(FID, data, 'char');
fclose(FID);

%%
data_box = importdata('measurements_2_box.txt');

N = data_box(:,1);
timings = data_box(:,2:end);
meanTiming = mean(timings,2);
meanTiming = meanTiming - meanTiming(1);

A = N;
a = A\meanTiming;

hold on
error = std(timings,0,2);
errorbar(N,meanTiming,error);
%plot(min(N):max(N),a(1)*(min(N):max(N)))

data_sphere = importdata('measurements_2_sphere.txt');

N = data_sphere(:,1);
timings = data_sphere(:,2:end);
meanTiming = mean(timings,2);
meanTiming = meanTiming;

A = N;
a = A\meanTiming;

error = std(timings,0,2);
errorbar(N,meanTiming,error);
%plot(min(N):max(N),a(1)*(min(N):max(N)))
hold off


%%
NN = 100;
data_lights = importdata('measurements_lights_2.txt');

N_lights = data_lights(:,1);
light_timings = data_lights(:,2:end);
light_meanTiming = mean(light_timings,2);
light_meanTiming = light_meanTiming - light_meanTiming(1);

A = N_lights;
a = A\light_meanTiming;

hold on
error = std(light_timings,0,2);
errorbar(N_lights,light_meanTiming,error);
plot(min(N):max(N), a(1)*(min(N_lights):max(N_lights)))
hold off



