package com.example.sorecastv1.Common;

import android.location.Location;

import java.text.SimpleDateFormat;
import java.util.Date;


public class Common {
    public static final String API_ID = "27635186d03ce338464d91449ed0b080";
    // old api key: 27635186d03ce338464d91449ed0b080
    //new api key: 0d07107c154301fd0cb02e3b0c41f490
    public static Location current_location=null;

    public static String convertUnixToDate(long dt) {
        Date date = new Date(dt*1000L);

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm EEE dd/MM/yyyy");
        String formatted = sdf.format(date);

        return formatted;

    }

    public static String convertUnixToHour(long dt) {
        Date date = new Date(dt*1000L);

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        String formatted = sdf.format(date);

        return formatted;

    }
}
