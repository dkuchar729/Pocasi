package org.example;

import java.time.LocalDate;
import java.time.Month;

public class StatniSvatky {

    private final LocalDate[]pevneStatniSvatky;

    public StatniSvatky()
    {
        pevneStatniSvatky=new LocalDate[]{LocalDate.of(0, Month.MAY, 8)};
    }
}
