package bytes.com.firstresponderapp.util;

import android.text.format.DateFormat;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {

    public static String getTimeAgo(long time) {
        Calendar calendarTime = Calendar.getInstance();
        calendarTime.setTimeInMillis(time);

        Calendar now = Calendar.getInstance();

        final String timeFormatString = "h:mm aa";
        final String dateTimeFormatString = "EEEE, MMMM d, h:mm aa";
        if (now.get(Calendar.DATE) == calendarTime.get(Calendar.DATE)) {
            return "Today " + DateFormat.format(timeFormatString, calendarTime);
        } else if (now.get(Calendar.DATE) - calendarTime.get(Calendar.DATE) == 1) {
            return "Yesterday " + DateFormat.format(timeFormatString, calendarTime);
        } else if (now.get(Calendar.YEAR) == calendarTime.get(Calendar.YEAR)) {
            return DateFormat.format(dateTimeFormatString, calendarTime).toString();
        } else {
            return DateFormat.format("MMMM dd yyyy, h:mm aa", calendarTime).toString();
        }
    }

}
