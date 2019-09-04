package com.example.sample.drools;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.time.OffsetTime;

public class MainTest {
    public static void main(String[] args) {
        OffsetTime realTime;
        OffsetTime fromTime =OffsetTime.parse("21:50+00:00");
        OffsetTime endTime =OffsetTime.parse("22:10+00:00");
        System.out.println("Hello World!");
        String offset = getCurrentTimezoneOffset();
        int offSetTime = Integer.parseInt(offset);
        System.out.println(offset);
        String eventTime = getCurrentTime();
        System.out.println(eventTime);
        OffsetTime time = OffsetTime.parse(eventTime);
        System.out.println(time);
        if(offSetTime < 0) {
            realTime = time.minusHours(Math.abs(offSetTime));
        }else{
            realTime = time.plusHours(Math.abs(offSetTime));
        }
        if (realTime.isBefore(fromTime) || realTime.isAfter(endTime)){
            System.out.println("Time is not in  suppress period");
        }else{
            System.out.println("Time is in suppress period");
        }
        long ns = Duration.between(fromTime, endTime).toMinutes();
        System.out.println("Time in supression Period " + ns);
    }



    static String getCurrentTime() {
        Date date = new Date();
        String strDateFormat = "kk:mm";
        DateFormat dateFormat = new SimpleDateFormat(strDateFormat);
        String formattedDate = dateFormat.format(date) + "+00:00";
        System.out.println("Current time of the day using Date - 24 hour format: " + formattedDate);
        return formattedDate;
    }

    static String getCurrentTimezoneOffset() {

        TimeZone tz = TimeZone.getDefault();
        Calendar cal = GregorianCalendar.getInstance(tz);
        int offsetInMillis = tz.getOffset(cal.getTimeInMillis());

        String offset = String.format("%d", Math.abs(offsetInMillis / 3600000), Math.abs((offsetInMillis / 60000) % 60));
        offset = (offsetInMillis >= 0 ? "+" : "-") + offset;
        System.out.println("Offset Time: " + offset);
        return offset;
    }
}
