package org.example;

import java.io.File;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {

    public static void main(String[] args) {
        try {
          
            Weather w=new Weather();
            w.getWeather(LocalDate.of(2019,12,1),LocalDate.of(2019,12,2), Region.JIHOCESKY);

            //w.readHTML("hustota.csv","dojoxGridHeader");//dojoxGridRow
            //w.executeDensityTable(60,2000);
            //w.executeDensityTable();
    
        }
        catch(Exception e)
        {
          e.printStackTrace();
        }

    }


}