package com.lapurema.manhhoan.weatherappcode.Common;

import android.location.Location;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Common {
    public static final String APP_ID = "b21701043d971b381a83e7e2324bc952";
    public static Location current_location = null;

    public static String convertUnixToDate(long dt) {
        Date date = new Date(dt * 1000L);
        SimpleDateFormat format = new SimpleDateFormat("HH:mm dd EEE MM yyyy");
        String formatted = format.format(date);
        return formatted;
    }

    public static String convertUnixToHour(long dt) {
        Date date = new Date(dt * 1000L);
        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        String formatted = format.format(date);
        return formatted;
    }
}
