package org.demo.production.planning.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.demo.rostering.domain.AbstractPersistable;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;

/**
 * Created by IntelliJ IDEA.<br/>
 * User: Thilanka<br/>
 * Date: 5/7/2021<br/>
 * Time: 1:17 PM<br/>
 * To change this template use File | Settings | File Templates.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Period extends AbstractPersistable {

    public static final int TIME_SLOT_FULL_DAY = 8;
    public static final int TIME_SLOT_AUDIO_SLOT = 2;

    private static LocalDate AUDIO_EDITING_DATE = LocalDate.of(2021, 2, 12);

    public static final String PERIOD_SEPERATOR = "_";

    public Period(LocalDate date, DaySession daySession) {
        this.date = date;
        this.startTime = daySession.getStartTime();
        this.duration = daySession.getDuration();
    }

    private LocalDate date;
    private LocalTime startTime;
    /**
     * duration in hours
     */
    private int duration;

    public LocalTime getEndTime() {
        return startTime.plusHours(duration);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Period)) return false;
        Period period = (Period) o;
        return getDuration() == period.getDuration() &&
                getDate().equals(period.getDate()) &&
                getStartTime().equals(period.getStartTime());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getDate(), getStartTime(), getDuration());
    }

    public String getPeriodKey() {
        return date.getYear() + PERIOD_SEPERATOR + date.getMonthValue() + PERIOD_SEPERATOR + date.getDayOfMonth() +
                PERIOD_SEPERATOR + startTime.getHour() + PERIOD_SEPERATOR + startTime.getMinute() +
                PERIOD_SEPERATOR + duration;
    }

    public boolean isWeekEnd() {
        return (date.getDayOfWeek().equals(DayOfWeek.SUNDAY) || date.getDayOfWeek().equals(DayOfWeek.SATURDAY));
    }

    public String _getPeriod() {
        return ("[" + getStartTime() + "-" + getEndTime() + "]");
    }

    public boolean isAudioEditingDay() {
        return date.equals(AUDIO_EDITING_DATE);
    }

    public boolean isOverlapping(Period period) {
        if (period.getDate() != null && getDate() != null && period.getDate().equals(getDate())) {
            if (period.getStartTime().isBefore(getStartTime())) {
                return period.getEndTime().isAfter(getStartTime());
            } else {
                return getEndTime().isAfter(period.getStartTime());
            }
        }
        return false;
    }
}