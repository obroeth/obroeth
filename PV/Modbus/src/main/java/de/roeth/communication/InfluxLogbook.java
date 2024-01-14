package de.roeth.communication;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;
import de.roeth.model.Device;
import de.roeth.model.input.DefaultDeviceProperty;
import de.roeth.model.input.DeviceProperty;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class InfluxLogbook {

    private static long logTimestamp;

    public static void startNewLog() {
        logTimestamp = System.currentTimeMillis();
    }

    public static void log(Device device, DeviceProperty prop) {
        try {
            File file = getLogFile(device);
            CSVWriter csvWriter = new CSVWriter(new FileWriter(file, true));
            csvWriter.writeNext(addTimestamp(prop.ToCsv()));
            csvWriter.close();
        } catch (IOException e) {
            System.out.println("Can not write Influx log!");
            e.printStackTrace();
        }
    }

    private static String[] addTimestamp(String[] strings) {
        String[] csv = Arrays.copyOf(strings, strings.length + 1);
        csv[csv.length - 1] = String.valueOf(logTimestamp);
        return csv;
    }

    private static File getLogFile(Device device) throws IOException {
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String formattedDate = currentDate.format(dateFormatter);

        File file = new File("influxlog/" + formattedDate + "/" + device.name + ".csv");
        if (!file.exists()) {
            Path path = FileSystems.getDefault().getPath(file.getParent());
            Files.createDirectories(path);

            // Erzeuge die Datei
            Files.createFile(path.resolve(file.getName()));
        }
        return file;
    }

    public static void recoverFromLog(long start, long end) throws IOException, CsvValidationException {
        File dir = new File("influxlog/");
        if (dir.exists() && dir.isDirectory()) {
            for (File subdir : dir.listFiles()) {
                if (subdir.isDirectory()) {
                    System.out.println("===> Start to recover: " + subdir.getName());
                    for (File device : subdir.listFiles()) {
                        List<DeviceProperty> props = new ArrayList<>();
                        System.out.println("======> Start to recover device: " + device.getName().replace(".csv", ""));
                        CSVReader csvs = new CSVReader(new FileReader(device));
                        String[] csv;
                        long lastTimestamp = 0;
                        long measurementCounter = 0;
                        while ((csv = csvs.readNext()) != null) {
                            if (lastTimestamp == 0 || lastTimestamp == Long.parseLong(csv[csv.length - 1])) {
                                props.add(DefaultDeviceProperty.fromCsv(csv));
                            } else {
                                if (lastTimestamp >= start && lastTimestamp <= end) {
                                    InfluxIO.recover(device.getName().replace(".csv", ""), props, lastTimestamp);
                                    measurementCounter++;
                                }
                                props.clear();
                                props.add(DefaultDeviceProperty.fromCsv(csv));
                            }
                            lastTimestamp = Long.parseLong(csv[csv.length - 1]);
                        }
                        if (lastTimestamp != 0 && lastTimestamp >= start && lastTimestamp <= end) {
                            InfluxIO.recover(device.getName().replace(".csv", ""), props, lastTimestamp);
                            measurementCounter++;
                        }
                        System.out.println("<====== Finished to recover device: " + device.getName().replace(".csv", "") + " with <" + measurementCounter + "> measurements.");
                    }
                }
            }
        }
    }

    public static void main(String[] args) throws CsvValidationException, IOException {
        long start = 0;
        long end = 1704996904000L;

        recoverFromLog(start, end);
    }

}
