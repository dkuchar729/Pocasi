package org.example;

import java.io.File;
import java.time.LocalDate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {

    public static void main(String[] args) {
        try {
          
            Weather w=new Weather();
            w.getWeather(LocalDate.of(2020,1,2),LocalDate.of(2020,1,3), Region.JIHOCESKY);
    
        }
        catch(Exception e)
        {
          e.printStackTrace();
        }

    }

}