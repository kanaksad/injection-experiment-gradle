/**
 *
 */
package net.sf.colossus.webcommon;


import java.text.SimpleDateFormat;
import java.util.Date;
import edu.ucr.cs.riple.taint.ucrtainting.qual.RUntainted;


public class FormatWhen
{
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    public static final String TIME_FORMAT = "HH:mm:ss";

    private final @RUntainted SimpleDateFormat dateFormatter;
    private final @RUntainted SimpleDateFormat timeFormatter;

    private String datePrev;
    private @RUntainted String changedDateString = null;

    public FormatWhen()
    {
        datePrev = "";
        dateFormatter = new SimpleDateFormat(DATE_FORMAT);
        timeFormatter = new SimpleDateFormat(TIME_FORMAT);

    }

    /* call this *after* timeAsString() call
     * It will return the new date, if changed, null otherwise */

    public @RUntainted String hasDateChanged()
    {
        return changedDateString;
    }

    public @RUntainted String timeAsString(@RUntainted long when)
    {
        Date whenDate = new Date(when);
        String timeNow = timeFormatter.format(whenDate);
        String dateNow = dateFormatter.format(whenDate);

        if (!dateNow.equals(datePrev))
        {
            changedDateString = dateNow;
        }
        else
        {
            changedDateString = null;
        }
        datePrev = dateNow;

        return timeNow;
    }

    public @RUntainted String timeAndDateAsString(@RUntainted long when)
    {
        Date whenDate = new Date(when);
        String timeNow = timeFormatter.format(whenDate);
        String dateNow = dateFormatter.format(whenDate);

        return dateNow + " " + timeNow;
    }
}
