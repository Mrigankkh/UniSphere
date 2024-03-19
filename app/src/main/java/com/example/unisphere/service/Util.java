package com.example.unisphere.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Util {

    public static SimpleDateFormat TIMESTAMP_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
    public static SimpleDateFormat USER_TIME_FORMAT = new SimpleDateFormat("MMM dd, yyyy hh:mm a");

    public static String convertDateTime(String inputDateTime) {
        try {
            Date date = TIMESTAMP_FORMAT.parse(inputDateTime);
            return USER_TIME_FORMAT.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Date parseDateString(String dateString) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        try {
            return dateFormat.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
}
