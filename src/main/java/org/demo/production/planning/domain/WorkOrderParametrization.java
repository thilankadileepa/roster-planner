package org.demo.production.planning.domain;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * Created by IntelliJ IDEA.<br/>
 * User: Thilanka<br/>
 * Date: 5/10/2021<br/>
 * Time: 11:15 AM<br/>
 * To change this template use File | Settings | File Templates.
 */
@Data
public class WorkOrderParametrization {
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;

    public boolean isInPlanningWindow(Period period) {
        boolean isInWindow = false;
        // check whether its greater than startTime
        if (period.getDate().isAfter(startDateTime.toLocalDate()) || (period.getDate().isEqual(startDateTime.toLocalDate()) && !period.getStartTime().isBefore(startDateTime.toLocalTime()))) {
            isInWindow = true;
        }

        // check whether its less than end time
        if (isInWindow && period.getDate().isBefore(endDateTime.toLocalDate()) || (period.getDate().isEqual(endDateTime.toLocalDate()) && !period.getEndTime().isAfter(endDateTime.toLocalTime()))) {
            return true;
        }

        return false;
    }
}