/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.demo.rostering.domain;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.demo.rostering.domain.contract.Contract;
import org.demo.rostering.domain.solver.EmployeeStrengthComparator;
import org.demo.rostering.domain.solver.ShiftAssignmentDifficultyComparator;
import org.demo.rostering.domain.solver.ShiftAssignmentPinningFilter;
import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.PlanningVariable;

import java.time.DayOfWeek;
import java.util.Comparator;

@PlanningEntity(pinningFilter = ShiftAssignmentPinningFilter.class,
        difficultyComparatorClass = ShiftAssignmentDifficultyComparator.class)
@XStreamAlias("ShiftAssignment")
public class ShiftAssignment extends AbstractPersistable implements Comparable<ShiftAssignment> {

    private static final Comparator<Shift> COMPARATOR = Comparator.comparing(Shift::getShiftDate)
            .thenComparing(a -> a.getShiftType().getType());

    private Shift shift;
    private int indexInShift;

    // Planning variables: changes during planning, between score calculations.
    @PlanningVariable(valueRangeProviderRefs = {"employeeRange"}, strengthComparatorClass = EmployeeStrengthComparator.class)
    private Employee employee;

    public Shift getShift() {
        return shift;
    }

    public void setShift(Shift shift) {
        this.shift = shift;
    }

    public int getIndexInShift() {
        return indexInShift;
    }

    public void setIndexInShift(int indexInShift) {
        this.indexInShift = indexInShift;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    // ************************************************************************
    // Complex methods
    // ************************************************************************

    public ShiftDate getShiftDate() {
        return shift.getShiftDate();
    }

    public ShiftType getShiftType() {
        return shift.getShiftType();
    }

    public Type getType() {
        return getShiftType().getType();
    }

    public int getShiftDateDayIndex() {
        return shift.getShiftDate().getDayIndex();
    }

    public DayOfWeek getShiftDateDayOfWeek() {
        return shift.getShiftDate().getDayOfWeek();
    }

    public Contract getContract() {
        if (employee == null) {
            return null;
        }
        return employee.getContract();
    }

    public boolean isSunday() {
        return getShiftDate().isSunday();
    }

    public boolean isWeekend() {
        if (employee == null) {
            return false;
        }
        WeekendDefinition weekendDefinition = employee.getContract().getWeekendDefinition();
        DayOfWeek dayOfWeek = shift.getShiftDate().getDayOfWeek();
        return weekendDefinition.isWeekend(dayOfWeek);
    }

    public int getWeekendSundayIndex() {
        return shift.getShiftDate().getWeekendSundayIndex();
    }

    @Override
    public String toString() {
        return shift.toString() + (employee != null ? ("/" + employee.getName()) : "");
    }

    @Override
    public int compareTo(ShiftAssignment o) {
        try {
            return COMPARATOR.compare(shift, o.shift);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }
}
