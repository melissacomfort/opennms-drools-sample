package com.example.sample.drools;

import java.text.DateFormat;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.Temporal;
import java.util.*;

public class Main {
    public static void main(String[] args) throws java.text.ParseException{
        //Convert local to UTC
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date gmt = new Date(sdf.format(date));
        System.out.println("GMT: " + gmt);

        //Convert UTC to Local
        Date inputDate = new Date();
        Format formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        ((SimpleDateFormat) formatter).setTimeZone(TimeZone.getTimeZone("UTC"));
        String dateStr = formatter.format(new Date());
        try {
            inputDate = ((SimpleDateFormat) formatter).parse(dateStr);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(inputDate);

        LocalTime start =  LocalTime.of(21,50,00,00);
        System.out.println(start);
        LocalTime end =  LocalTime.of(22,10,00,00);
        System.out.println(end);

        OffsetTime startTime = OffsetTime.of(start,  ZoneOffset.UTC);
        OffsetTime endTime = OffsetTime.of(end,  ZoneOffset.UTC);

        String datestring = formatter.format(new Date());
        SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");//take a look at MM
        Date date1 = dt.parse(datestring);

        Instant instant = Instant.ofEpochMilli(date1.getTime());
        LocalTime res = LocalDateTime.ofInstant(instant, ZoneId.systemDefault()).toLocalTime();

        OffsetTime t = OffsetTime.of(LocalTime.now(), ZoneOffset.UTC);
        System.out.println(t);
        OffsetTime current = t.plusHours(getCurrentTimezoneOffset());
        System.out.println(current);

        if(current.isAfter(startTime) && current.isBefore(endTime)){
            System.out.println("Time is in suppression period");
        }else{
            System.out.println("Time is not in suppression period");
        }


    }
    static int getCurrentTimezoneOffset() {

        TimeZone tz = TimeZone.getDefault();
        Calendar cal = GregorianCalendar.getInstance(tz);
        int offsetInMillis = tz.getOffset(cal.getTimeInMillis());

        String offset = String.format("%d", Math.abs(offsetInMillis / 3600000), Math.abs((offsetInMillis / 60000) % 60));
        offset = (offsetInMillis >= 0 ? "+" : "-") + offset;
        System.out.println("Offset Time: " + offset);
        return Integer.parseInt(offset);
    }
    static String getCurrentTime() {
        Date date = new Date();
        String strDateFormat = "kk:mm";
        DateFormat dateFormat = new SimpleDateFormat(strDateFormat);
        String formattedDate = dateFormat.format(date) + "+00:00";
        System.out.println("Current time of the day using Date - 24 hour format: " + formattedDate);
        return formattedDate;
    }
}
