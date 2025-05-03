package util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateHelper {
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    public static String formatDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        return sdf.format(date);
    }

    public static Date getCurrentDate() {
        return new Date();
    }


}