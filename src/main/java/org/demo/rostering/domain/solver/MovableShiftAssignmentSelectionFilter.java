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

package org.demo.rostering.domain.solver;

import org.demo.rostering.domain.EmployeeRoster;
import org.demo.rostering.domain.ShiftAssignment;
import org.optaplanner.core.api.domain.entity.PinningFilter;
import org.optaplanner.core.api.score.director.ScoreDirector;
import org.optaplanner.core.impl.heuristic.selector.common.decorator.SelectionFilter;

public class MovableShiftAssignmentSelectionFilter implements SelectionFilter<EmployeeRoster, ShiftAssignment> {

    private final PinningFilter<EmployeeRoster, ShiftAssignment> pinningFilter =
            new ShiftAssignmentPinningFilter();

    @Override
    public boolean accept(ScoreDirector<EmployeeRoster> scoreDirector, ShiftAssignment selection) {
        return !pinningFilter.accept(scoreDirector.getWorkingSolution(), selection);
    }

}
