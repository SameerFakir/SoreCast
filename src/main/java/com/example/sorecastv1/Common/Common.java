package com.example.sorecastv1.Common;

import android.location.Location;

import java.text.SimpleDateFormat;
import java.util.Date;


public class Common {
    public static final String API_ID = "27635186d03ce338464d91449ed0b080";
    public static Location current_location=null; //STOPPED HERE VID @26:02 https://youtu.be/awYSrhUZQL0?t=1559

    public static String convertUnixToDate(long dt) {
        Date date = new Date(dt*1000L);

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm dd EEE MM yyyy");
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
