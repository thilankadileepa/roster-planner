/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.demo.rostering.domain.pattern;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.demo.rostering.domain.ShiftType;

import java.time.DayOfWeek;

@XStreamAlias("WorkBeforeFreeSequencePattern")
public class WorkBeforeFreeSequencePattern extends Pattern {

    private DayOfWeek workDayOfWeek; // null means any
    private ShiftType workShiftType; // null means any

    private int freeDayLength;

    public DayOfWeek getWorkDayOfWeek() {
        return workDayOfWeek;
    }

    public void setWorkDayOfWeek(DayOfWeek workDayOfWeek) {
        this.workDayOfWeek = workDayOfWeek;
    }

    public ShiftType getWorkShiftType() {
        return workShiftType;
    }

    public void setWorkShiftType(ShiftType workShiftType) {
        this.workShiftType = workShiftType;
    }

    public int getFreeDayLength() {
        return freeDayLength;
    }

    public void setFreeDayLength(int freeDayLength) {
        this.freeDayLength = freeDayLength;
    }

    @Override
    public String toString() {
        return "Work " + workShiftType + " on " + workDayOfWeek + " followed by " + freeDayLength + " free days";
    }

}
