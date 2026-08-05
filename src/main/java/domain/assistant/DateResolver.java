package domain.assistant;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.temporal.TemporalAdjusters;

public class DateResolver {

    private final Clock clock;

    public DateResolver(Clock clock) {
        this.clock = clock;
    }

    public record Range(LocalDateTime from, LocalDateTime to) {}

    public Range today() {
        LocalDate today = LocalDate.now(clock);
        return dayRange(today);
    }

    public Range tomorrow() {
        LocalDate tomorrow = LocalDate.now(clock).plusDays(1);
        return dayRange(tomorrow);
    }

    public Range thisWeek() {
        LocalDate today = LocalDate.now(clock);
        LocalDate start = today.with(TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY));
        LocalDate end = today.with(TemporalAdjusters.nextOrSame(java.time.DayOfWeek.SUNDAY));
        return new Range(start.atStartOfDay(), end.plusDays(1).atStartOfDay());
    }

    public Range nextWeek() {
        Range thisWeek = thisWeek();
        return new Range(thisWeek.from().plusWeeks(1), thisWeek.to().plusWeeks(1));
    }

    public Range thisMonth() {
        YearMonth month = YearMonth.now(clock);
        return monthRange(month);
    }

    public Range nextMonth() {
        YearMonth month = YearMonth.now(clock).plusMonths(1);
        return monthRange(month);
    }

    public Range thisYear() {
        LocalDate today = LocalDate.now(clock);
        LocalDate start = today.withDayOfYear(1);
        LocalDate end = start.plusYears(1);
        return new Range(start.atStartOfDay(), end.atStartOfDay());
    }

    private Range dayRange(LocalDate day) {
        return new Range(day.atStartOfDay(), day.plusDays(1).atStartOfDay());
    }

    private Range monthRange(YearMonth month) {
        LocalDate start = month.atDay(1);
        LocalDate end = month.plusMonths(1).atDay(1);
        return new Range(start.atStartOfDay(), end.atStartOfDay());
    }
}