package org.example;

import java.io.File;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.print.Doc;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Weather {

    private Pattern tempPattern;
    private Matcher matcher;
    DateTimeFormatter datePattern = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private CsvManager cm;
    private double maxTemp = 0;
    private double minTemp = 0;
    private double precipitation = 0;

    Map<Integer, Integer> regionNumber;
    private int[] missingValueIndexes;
    private List<Double> precList = new LinkedList<>();
    private int rowNumber = 0;

    public Weather() {
        try {
            cm = new CsvManager(new File("pocasii.csv"), ';');
            //doc = Jsoup.connect("https://www.in-pocasi.cz/archiv/archiv.php?historie=2024-01-26&region=1").get();
            //tbodies = doc.getElementsByTag("tbody");
            missingValueIndexes = new int[3];
            //regionNumber=new HashMap<>();

            //regionNumber.put(1,2);
        } catch (Exception e) {
            System.out.println(e);
        }
        tempPattern = Pattern.compile("-?\\d{1,2}(\\.\\d{0,2})?");

    }

    public void getWeather(LocalDate startDate, LocalDate endDate, Region region) throws Exception {
        cm.loadCsvData();
        for (LocalDate start = startDate; start.isBefore(endDate); start = start.plusDays(1)) {
            Thread.sleep(2000);
            System.out.println("dalsi");
            String link = prepareLink(start, region.getWeatherRegionNumber());

           // Document doc = Jsoup.connect(link).get();
            //Document doc = Jsoup.connect("http://C:/Users/dorgu/Documents/Test").get();
            Document doc=Jsoup.parse(new File("C:/Users/dorgu/Documents/Test.html"), null);
            //System.out.println(doc.getElementsByTag("tbody"));
            int a=0;
            for (Element tBody : doc.getElementsByTag("tbody")) {
                try{
                    loadData(tBody.children(), start, region);
                    a++;
                    System.out.println(tBody.child(0).child(0).child(0).child(0));
                    System.out.println("index: "+a);
                    System.out.println("pocet: " +tBody.child(0).child(0).child(0).childNodeSize()+"\n");

                } catch (IndexOutOfBoundsException e) {
                   System.out.println(e);
                }

            }
            
           /* double maxTempMean = round(mean(maxTemp, rowNumber - missingValueIndexes[0]), 1);
            double minTempMean = round(mean(minTemp, rowNumber - missingValueIndexes[1]), 1);
            double precMean = round(mean(precipitation, rowNumber - missingValueIndexes[2]), 1);

            double[] dprec = getTwoPrec();

            writeToFile(start, region.getOutputRegionNumber(), maxTempMean, minTempMean, precMean, dprec[0], dprec[1]);
            maxTemp = 0;
            minTemp = 0;
            precipitation = 0;
            precList = new LinkedList<>();
            missingValueIndexes = new int[3];
            rowNumber = 0;
*/
        }
       // cm.writeData();

    }

    private double getPrecMean(List<Double> tempList) {
        double sum = 0;
        for (double number : tempList) {
            sum += number;

        }

        return round(sum / tempList.size(), 1);
    }

    private void loadData(Elements rows, LocalDate date, Region region) throws Exception {
        //System.out.println(rows.get(0).child(0));
        rowNumber += rows.size();

        for (Element row : rows) {
            cm.addNewRow();
            cm.addValues(new String[]{row.child(0).text(), date.toString()});
            addWeatherInfoToFile(row.child(1).text(), row.child(2).text(), row.child(4).text());
            if (!Arrays.stream(region.getStations()).anyMatch(x -> x.equals(row.child(0).text()))) {
                getMaxTemp(row.child(1));
                getMinTemp(row.child(2));
                getPrec(row.child(4));
            } else {
                missingValueIndexes[0]++;
                missingValueIndexes[1]++;
                missingValueIndexes[2]++;
            }
        }
    }

    private void addWeatherInfoToFile(String maxTemp, String minTemp, String prec) {
        matcher = tempPattern.matcher(maxTemp);
        if ("-".equals(maxTemp)) {
            cm.addValue("");
        } else if(matcher.find()) {
            cm.addValue(matcher.group(0));
        }
        else{
        cm.addValue("wrong value - "+maxTemp);
        }
        
        matcher = tempPattern.matcher(minTemp);
        if ("-".equals(minTemp)) {
            cm.addValue("");
        } else if(matcher.find()) {
            cm.addValue(matcher.group(0));
        }
        else{
        cm.addValue("wrong value - "+minTemp);
        }

        matcher = tempPattern.matcher(prec);
        if ("-".equals(prec)) {
            cm.addValue("");
         } else if(matcher.find()) {
            cm.addValue(matcher.group(0));
        }
        else{
        cm.addValue("wrong value - "+prec);
        }

    }

    private void getMaxTemp(Element tempElement) throws Exception {
        matcher = tempPattern.matcher(tempElement.text());
        if ("-".equals(tempElement.text())) {
            missingValueIndexes[0]++;
        } else if (matcher.find()) {
            maxTemp += Double.parseDouble(matcher.group(0));
        } else {
            System.out.println(tempElement.text().length());
            throw new Exception("Wrong Input");
        }

    }

    private void getMinTemp(Element tempElement) throws Exception {
        matcher = tempPattern.matcher(tempElement.text());

        if ("-".equals(tempElement.text())) {
            missingValueIndexes[1]++;
        } else if (matcher.find()) {
            minTemp += Double.parseDouble(matcher.group(0));
        } else {
            throw new Exception("Wrong Input");
        }

    }

    private double mean(double number, int count) {
        return number / count;
    }

    private void getPrec(Element tempElement) throws Exception {
        matcher = tempPattern.matcher(tempElement.text());

        if (tempElement.text().equals("-")) {
            missingValueIndexes[2]++;

        } else if (matcher.find()) {
            double num = Double.parseDouble(matcher.group(0));
            precList.add(num);
            precipitation += num;

        } else {
            throw new Exception("Wrong Input");
        }
    }

    private double[] getTwoPrec() {
        Collections.sort(precList);
        int mid = precList.size() / 2;

        double prec1 = 0;
        double prec2 = 0;

        if (precList.size() % 2 == 1) {
            double middle = precList.get(mid);

            double previous = precList.get(mid - 1);

            if (middle > previous && middle != previous) {
                prec1 = getPrecMean(precList.subList(0, mid));
                prec2 = getPrecMean(precList.subList(mid, precList.size()));

            } else {

                prec1 = getPrecMean(precList.subList(0, mid + 1));
                prec2 = getPrecMean(precList.subList(mid + 1, precList.size()));
            }

        } else {

            prec1 = getPrecMean(precList.subList(0, mid));
            prec2 = getPrecMean(precList.subList(mid, precList.size()));
        }

        return new double[]{round(prec1, 1), round(prec2, 1)};
    }

    private String prepareLink(LocalDate date, int regionNumber) {
        String baseLink = "https://www.in-pocasi.cz/archiv/archiv.php?historie=";
        return baseLink + date + "&region=" + regionNumber;
    }

    public double round(double value, int places) {
        if (places < 0) {
            throw new IllegalArgumentException();
        }

        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    private void writeToFile(LocalDate date, int regionNumber, double maxTemp, double minTemp, double prec, double minPrec, double maxPrec) throws IOException {
        FileWriter myWriter = new FileWriter("data.txt", true);

        StringBuilder sb = new StringBuilder();
        sb.append(date.format(datePattern) + ",");
        sb.append(regionNumber + ",");
        sb.append(maxTemp + ",");
        sb.append(minTemp + ",");
        sb.append(prec + ",");
        sb.append(minPrec + ",");
        sb.append(maxPrec + "\n");
        myWriter.write(sb.toString());

        myWriter.close();
    }

}
