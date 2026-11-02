package hu.nye.carrental.ui;

import javax.swing.JSpinner;
import javax.swing.SpinnerDateModel;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;

/**
 * Swing has no date picker, so we use a spinner that only accepts dates (yyyy-MM-dd).
 * These helpers convert between the spinner's old java.util.Date and LocalDate.
 */
public final class DateSpinners {

    private DateSpinners() {
    }

    public static JSpinner create(LocalDate initial) {
        Date date = Date.from(initial.atStartOfDay(ZoneId.systemDefault()).toInstant());
        JSpinner spinner = new JSpinner(new SpinnerDateModel(date, null, null, Calendar.DAY_OF_MONTH));
        spinner.setEditor(new JSpinner.DateEditor(spinner, "yyyy-MM-dd"));
        return spinner;
    }

    /** Returns the date in the spinner, or null if the typed text is not a valid date. */
    public static LocalDate getDate(JSpinner spinner) {
        try {
            spinner.commitEdit();
        } catch (ParseException e) {
            return null;
        }
        Date date = (Date) spinner.getValue();
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }
}
