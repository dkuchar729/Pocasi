package org.example;

import java.time.LocalDate;

public class Main {

    public static void main(String[] args) {
        try {
          
            Weather w=new Weather();
            //w.getWeather(LocalDate.of(2019,3,15),LocalDate.of(2019,3,25), Region.JIHOCESKY);
        }
        catch(Exception e)
        {
          e.printStackTrace();
        }

    }

}