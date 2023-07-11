package holidays;

import java.time.LocalDate;

public abstract class Holiday {
    abstract String getName();
    abstract DateRange getObservedDates(int year);

    int getDayCount(LocalDate start, LocalDate end) {
        if (!start.isBefore(end) && !start.equals(end)) {
            throw new IllegalArgumentException("Start date must be on or before end date");
        }

        // Start in the year of the start date
        DateRange observed = getObservedDates(start.getYear());
        int count = 0;
        LocalDate calcStart;
        LocalDate calcEnd;

        // For all relevant years, add however many observed dates are within the given range
        while (observed.getStart().isBefore(end) || observed.getStart().equals(end)) {
            // Calculation is performed using
            // - holiday start, or start of period, whichever is later
            // - holiday end, or end of period, whichever is earlier
            // So that a holiday occurring before the period will end up having a negative day count, with the same
            // being true for a holiday occurring after the period. Negative values are, obviously, ignored.
            calcStart = observed.getStart().isBefore(start) ? start : observed.getStart();
            calcEnd = observed.getEnd().isAfter(end) ? end : observed.getEnd();

            count += Math.max(0, calcStart.until(calcEnd.plusDays(1), java.time.temporal.ChronoUnit.DAYS));

            // Go to the next year
            observed = getObservedDates(observed.getStart().getYear() + 1);
        }

        return count;
    };


    protected class DateRange {
        private final LocalDate _start;
        private final LocalDate _end;
        public DateRange(LocalDate start, LocalDate end) {
            this._start = start;
            this._end = end;
        }
        public LocalDate getStart() {
            return _start;
        }
        public LocalDate getEnd() {
            return _end;
        }
    }
}
