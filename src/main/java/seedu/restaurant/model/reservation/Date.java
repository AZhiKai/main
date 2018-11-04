package seedu.restaurant.model.reservation;

import static java.util.Objects.requireNonNull;
import static seedu.restaurant.commons.util.AppUtil.checkArgument;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;

//@@author m4dkip
/**
 * Represents a date in the calendar in DD-MM-YYYY format Guarantees: immutable; is valid as declared in {@link
 * #isValidDate(String)}
 */
public class Date {

    public static final String MESSAGE_DATE_CONSTRAINTS =
            "Dates should only contain numbers, and it should be in the format DD-MM-YYYY.\nThe date must exist in "
                    + "the calendar";
    public static final String DATE_FORMAT_PATTERN = "dd-MM-uuuu";

    private final LocalDate date;

    /**
     * Constructs a {@code date}.
     *
     * @param date A valid date.
     */
    public Date(String date) {
        requireNonNull(date);
        checkArgument(isValidDate(date), MESSAGE_DATE_CONSTRAINTS);
        DateTimeFormatter validFormat =
                DateTimeFormatter.ofPattern(DATE_FORMAT_PATTERN).withResolverStyle(ResolverStyle.STRICT);
        this.date = LocalDate.parse(date, validFormat);
    }

    /**
     * Returns true if a given date string is a valid date.
     */
    public static boolean isValidDate(String test) {
        DateTimeFormatter validFormat =
                DateTimeFormatter.ofPattern(DATE_FORMAT_PATTERN).withResolverStyle(ResolverStyle.STRICT);
        try {
            LocalDate.parse(test, validFormat);
        } catch (DateTimeParseException e) {
            return false;
        }
        return true;
    }


    public LocalDate getValue() {
        return date;
    }

    @Override
    public String toString() {
        return date.format(DateTimeFormatter.ofPattern(DATE_FORMAT_PATTERN).withResolverStyle(ResolverStyle.STRICT));
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof Date // instanceof handles nulls
                    && date.equals(((Date) other).date)); // state check
    }

    @Override
    public int hashCode() {
        return date.hashCode();
    }
}
