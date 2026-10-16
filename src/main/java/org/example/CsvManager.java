/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.example;





import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author dorgu
 */
public class CsvManager {

    protected File csvFile;

    private CSVReader cr;
    private CSVWriter cw;
    protected List<String[]> csvData;
    protected String[] actualRow;
    protected char separator;
    private final DateTimeFormatter df;
    private final DateTimeFormatter bf;
    private LocalDate lastDate;

    protected List<Integer> columnsToRemove;
  
    protected List<Integer> rowsToRemove;

    private String[] lastWeatherRow;

    public CsvManager(File csvFile, char separator) throws FileNotFoundException, IOException {
        this.csvFile = csvFile;
        this.separator = separator;
        lastDate = LocalDate.MIN;
   
        columnsToRemove = new LinkedList<>();

        df = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        bf = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    }

  

    

 

    public LocalDate getDate(String date) {
        return LocalDate.parse(date, df);
    }

    

 

    public void updateTime(int index) throws IOException, CsvException {

        loadCsvData();
        for (int row = 1; row < csvData.size(); row++) {
            updateColumn(row, index, getDaytime(getData(row, index)));
        }
        writeData();
    }

    public List<String[]> updateTime(int index, List<String[]> list) throws IOException, CsvException {
        String[] rowToUpdate;
        loadCsvData();
        for (int row = 0; row < list.size(); row++) {
            rowToUpdate = list.get(row);
            rowToUpdate[index] = getDaytime(rowToUpdate[index]);
            list.set(index, rowToUpdate);
        }
        return list;
    }

    public List<String[]> updateTimeList(int index, List<String[]> list) throws IOException, CsvException {
        String[] rowToUpdate;

        for (int row = 0; row < list.size(); row++) {
            rowToUpdate = list.get(row);
            rowToUpdate[index] = getDaytime(rowToUpdate[index]);
            list.set(row, rowToUpdate);
        }
        return list;
    }

    public String getDaytime(String value) {

        int hodina = Integer.parseInt(value);

        if (hodina > -1 && hodina < 6 || hodina > 20 && hodina < 24) {
            return "noc";
        } else if (hodina > 5 && hodina < 10) {
            return "rano";
        } else if (hodina > 9 && hodina < 12) {
            return "dopoledne";
        } else if (hodina > 11 && hodina < 17) {
            return "odpoledne";
        } else {
            return "vecer";
        }
    }

  

   

    private void removeRows(List<Integer> rowsToRemove) {
        for (int i = rowsToRemove.size() - 1; i >= 0; --i) {
            csvData.remove(rowsToRemove.get(i).intValue());
        }
    }

    public String getTrafficDensity(String roadName, int roadNameColumnIndex, int carCountColumnIndex) throws IOException, CsvException {
        List<String[]> trafficDensityData = loadCsvData(new File("hustotaJizniCechy.csv"), ';');

        int count = 0;
        int total = 0;

        for (int row = 1; row < trafficDensityData.size(); row++) {
            if (trafficDensityData.get(row)[roadNameColumnIndex].equals(roadName)) {

                total += Integer.parseInt(trafficDensityData.get(row)[carCountColumnIndex]);
                count++;
            }
        }

        if (count != 0) {
            return String.valueOf(total / count);
        }
        return "null";
    }

    public boolean addColumnValues(List<String> data) throws IOException, CsvException {

        for (int i = 0; i < data.size(); i++) {
            addValue(i, data.get(i));
        }
        writeData();

        return csvData.size() - 1 == data.size();
    }

    public boolean updateColumnValues(List<String> data, int column) throws IOException, CsvException {
        loadCsvData();
        for (int row = 0; row < data.size(); row++) {
            updateColumn(row, column, data.get(row));
        }
        writeData();

        return csvData.size() - 1 == data.size();
    }

    public String getData(int row, int column) throws IOException, CsvException {
        return csvData.get(row)[column];
    }

    public void addDays() throws IOException, CsvException {
        loadCsvData();
        for (int i = 1; i < csvData.size(); i++) {
            addValue(i, String.valueOf(LocalDate.parse(getData(i, 1), df).getDayOfWeek().getValue()));

        }
        writeData();

    }

   
    public String[] getWeatherDataRow(File csvFile, char delimiter, int dateColumnIndex, LocalDate requestedDate) throws IOException, CsvException {

        List<String[]> weatherData = loadCsvData(csvFile, delimiter);

        for (int row = 1; row < weatherData.size(); row++) {
            String[] weatherRow = weatherData.get(row);
            LocalDate weatherDate = LocalDate.parse(weatherRow[dateColumnIndex], df);

            if (weatherDate.isEqual(requestedDate)) {
                return weatherRow;
            }
        }
        return null;

    }

    public void addId() throws IOException, CsvException {
        loadCsvData();

        for (int row = 1; row < csvData.size(); row++) {
            addValue(row, String.valueOf(row));
        }

        writeData();
    }

    public void addValue(int row, String value) throws IOException, CsvException {

        csvData.set(row, getNewRow(csvData.get(row), value));
    }

    public void addValues(int row, String[] values) throws IOException, CsvException {
        csvData.set(row, getNewRow(csvData.get(row), values));
    }

    public void updateColumn(int row, int column, String value) throws IOException, CsvException {
        csvData.get(row)[column] = value;
    }

    public void loadCsvData() throws IOException, CsvException {
        resetCsvReader();
        csvData = cr.readAll();
        cr.close();
    }

    public List<String[]> loadCsvData(File file, char delimiter) throws IOException, CsvException {
        CSVReader cr = new CSVReader(new FileReader(file), delimiter);
        List<String[]> csvData = cr.readAll();
        cr.close();
        return csvData;
    }

    public void writeData() throws IOException {
        resetCsvWriter();
        cw.writeAll(csvData, false);
        closeCsvWriter();
    }

    public static String[] getNewRow(String[] originalRow, String[] values) {
        String[] newRow = new String[originalRow.length + values.length];

        System.arraycopy(originalRow, 0, newRow, 0, originalRow.length);
        System.arraycopy(values, 0, newRow, originalRow.length, values.length);

        return newRow;

    }

    public static String[] getNewRow(String[] originalRow, String value) {
        String[] newRow = new String[originalRow.length + 1];
        System.arraycopy(originalRow, 0, newRow, 0, originalRow.length);
        newRow[newRow.length - 1] = value;
        return newRow;

    }

    private LocalDate parseDate(String date, boolean base) {
        if (base) {
            return LocalDate.parse(date, df);
        }

        return LocalDate.parse(date, bf);

    }

    private void resetCsvReader() throws FileNotFoundException {
        cr = new CSVReader(new FileReader(csvFile), separator);
    }

    private void resetCsvWriter() throws FileNotFoundException, IOException {
        cw = new CSVWriter(new FileWriter(csvFile), separator);
    }

    private void closeCsvWriter() throws IOException {
        cw.flush();
        cw.close();
    }

    private void getCarData(File carsFile, char delimiter, int searchedRow, String id) throws IOException, CsvException {
        List<String[]> carsData = loadCsvData(carsFile, delimiter);

        for (int row = 1; row < carsData.size(); row++) {
            if (id.equals(carsData.get(row)[0])) {
                addValue(searchedRow, carsData.get(row)[2]);

            }
        }
        addValue(searchedRow, "null");

    }

    public void sortByCoordinates(double centerX, double centerY, double range) throws IOException, CsvException {
        loadCsvData();
        rowsToRemove = new LinkedList<>();
        for (int row = 1; row < csvData.size(); row++) {

            double x = Double.parseDouble(csvData.get(row)[18]) * -1;
            double y = Double.parseDouble(csvData.get(row)[19]) * -1;

            if ((x > centerX + range || x < centerX - range) || (y < centerY - range || y > centerY + range)) {
                rowsToRemove.add(row);
            } else {

            }
        }
        for (int i = rowsToRemove.size() - 1; i > -1; i--) {
            csvData.remove((int) rowsToRemove.get(i));
        }
        writeData();
    }

    public boolean checkCoordinates(double x, double y, double newX, double newY, double range) {
        return !((newX > x + range || newX < x - range) || (newY < y - range || newY > y + range));
    }

    private void removeRows() {
        for (int i = rowsToRemove.size() - 1; i > -1; i--) {
            csvData.remove((int) rowsToRemove.get(i));
        }
    }

    private void removeColumns() {
        for (int i = 0; i < csvData.size(); i++) {
            String[] arrayRow = csvData.get(i);
            List<String> listRow = new LinkedList<>(Arrays.asList(arrayRow));

            for (int columnIndex : columnsToRemove) {
                listRow.remove(columnIndex);
            }
            csvData.set(i, listRow.stream().toArray(String[]::new));
        }
    }

    private String parseId(String id) {
        return id.substring(3, 4);
    }

   

    public boolean getByMaxTemp(double actualMax, double actualMin, double newMaxTemp, double newMinTemp) {

        if (!((actualMax > 0.4 && newMaxTemp <= 0.4) || (actualMin > 0.4 && newMinTemp <= 0.4)) && newMaxTemp >= actualMax - 5 && newMaxTemp <= actualMax && newMinTemp >= actualMin - 5 && newMinTemp <= actualMin) {
            return true;
        }

        return false;

    }

    public boolean getByPrec(double actualPrec, double newPrec) {
        if (actualPrec == 0 && newPrec == 0) {
            return true;
        } else if (actualPrec > 0 && actualPrec <= 0.3 && newPrec > 0 && newPrec <= 0.3) {
            return true;
        } else if (actualPrec > 0.3 && actualPrec <= 0.6 && newPrec > 0.3 && newPrec <= 0.6) {
            return true;
        } else if (actualPrec > 0.6 && actualPrec <= 0.9 && newPrec > 0.6 && newPrec <= 0.9) {

            return true;
        } else if (actualPrec > 0.9 && actualPrec <= 1.5 && newPrec > 0.9 && newPrec <= 1.5) {
            return true;
        } else if (actualPrec > 1.5 && actualPrec <= 2.9 && newPrec > 1.5 && newPrec <= 2.9) {
            return true;
        } else if (actualPrec > 2.9 && actualPrec <= 4.9 && newPrec > 2.9 && newPrec <= 4.9) {
            return true;
        } else if (actualPrec > 4.9 && newPrec > 4.9 && newPrec <= actualPrec + 5) {
            return true;
        }

        return false;
    }

    public List<String[]> getDat(List<String[]> data) {
        data = new LinkedList<String[]>();
        data.add(new String[]{"hodina", "umisteniObec", "druhNehody", "zavineniNehody", "alkohol", "pricinaNehody", "povrchVozovky", "stavVozovky", "povetrnostniPodminky", "viditelnost", "rozhled", "deleniKomunikace", "situovaniNehody", "specifickeMisto", "smerovyPomer", "druhKomunikace", "den", "souradniceX", "souradniceY", "maxPrumTeplota", "minPrumTeplota", "prumSrazky"});
        return data;
    }

    public void executePrec() throws IOException, CsvException {
        loadCsvData();

        while (csvData.size() != 1) {
            CsvManager srazkyc = new CsvManager(new File("data/predpovedNehody/srazky.csv"), ';');
            actualRow = csvData.get(1);
            rowsToRemove = new LinkedList<>();
            rowsToRemove.add(1);

            if (csvData.size() > 2) {

                srazkyc.loadCsvData();
                srazkyc.csvData = getDat(srazkyc.csvData);
                srazkyc.csvData.add(actualRow);

                for (int row = 2; row < csvData.size(); row++) {
                    String[] newRow = csvData.get(row);
                    if (getByMaxTemp(Double.parseDouble(actualRow[17]), Double.parseDouble(actualRow[18]), Double.parseDouble(newRow[17]), Double.parseDouble(newRow[18])) && getByPrec(Double.parseDouble(actualRow[19]), Double.parseDouble(newRow[19]))) {
                        rowsToRemove.add(row);
                        srazkyc.csvData.add(newRow);
                    }
                }
                srazkyc.writeData();
                removeRows();
            } else {

                srazkyc.loadCsvData();
                srazkyc.csvData = getDat(srazkyc.csvData);
                srazkyc.csvData.add(actualRow);
                srazkyc.writeData();
            }
        }
    }

  
   

    public boolean compareDate(String[] row1, String[] row2, int dateIndex) {
        return LocalDate.parse(row1[dateIndex], df).isEqual(LocalDate.parse(row2[dateIndex], df));
    }

  

   

   
   

    public String getVal(String value) {
        if (value == null) {
            return "null";
        }

        return value;
    }

    public String getVal(Long value) {
        if (value == null) {
            return "null";
        }

        return String.valueOf(value);
    }

    public String getVal(Double value) {
        if (value == null) {
            return "null";
        }

        return String.valueOf(value);
    }

    public String getVal(Integer value) {
        if (value == null) {
            return "null";
        }

        return String.valueOf(value);
    }

    /* public boolean checkDensity(Double d1,Double d2,double range)
    {
        
    }*/
    public void sortByDate(int index) throws IOException, CsvException {
        loadCsvData();

        csvData.sort(Comparator.comparing(a -> parseDate(a[index], true)));

        writeData();
    }

    public void sortByDay(int index) throws IOException, CsvException {
        loadCsvData();

        csvData.sort(Comparator.comparing(a -> a[5]));

        writeData();
    }

    public void removeDate(int index) throws IOException, CsvException {
        loadCsvData();

        columnsToRemove = new LinkedList<>();
        columnsToRemove.add(index);

        removeColumns();

        writeData();
    }

    public void removeMissingValues(int index) throws IOException, CsvException {
        loadCsvData();
        rowsToRemove = new LinkedList<>();
        for (int row = 1; row < csvData.size(); row++) {

            if (csvData.get(row).length < index + 1 || csvData.get(row)[index].length() == 0) {

                rowsToRemove.add(row);
            }

        }
        System.out.println(rowsToRemove.size());
        removeRows();
        writeData();
    }

   
public void addValues(String[]values)
{
   
    csvData.set(csvData.size()-1, getNewRow(csvData.getLast(), values));
}
public void addValue(String value)
{   
    csvData.set(csvData.size()-1, getNewRow(csvData.getLast(), value));
}
public void addNewRow()
{
   csvData.add(new String[0]); 
}
}
