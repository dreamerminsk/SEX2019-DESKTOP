package ch.caro62.utils;

import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

public class DateTimeUtils {

    public static PeriodFormatter daysHoursMinutes = new PeriodFormatterBuilder()
            .appendDays()
            .appendSuffix(" d", " d")
            .appendSeparator(" ")
            .appendHours()
            .minimumPrintedDigits(2)
            .printZeroAlways()
            .appendSeparator(":")
            .appendMinutes()
            .minimumPrintedDigits(2)
            .printZeroAlways()
            .appendSeparator(":")
            .appendSeconds()
            .minimumPrintedDigits(2)
            .printZeroAlways()
            .toFormatter();

    public static PeriodFormatter getDaysHoursMinutes() {
        return daysHoursMinutes;
    }

}
