package org.demo.production.planning.domain;

import lombok.Data;
import org.demo.rostering.domain.AbstractPersistable;
import org.optaplanner.core.api.domain.solution.*;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;

import java.util.List;

/**
 * Created by IntelliJ IDEA.<br/>
 * User: Thilanka<br/>
 * Date: 5/7/2021<br/>
 * Time: 12:57 PM<br/>
 * To change this template use File | Settings | File Templates.
 */
@Data
@PlanningSolution()
public class WorkOrder extends AbstractPersistable {
    @ProblemFactProperty
    private WorkOrderParametrization workOrderParametrization;

    @ProblemFactCollectionProperty
    private List<Room> roomList;
    @ProblemFactCollectionProperty
    private List<SkillType> skillTypeList;

    @ValueRangeProvider(id = "resourceRange")
    @ProblemFactCollectionProperty
    private List<Resource> resourceList;

    @ValueRangeProvider(id = "periodsRange")
    @ProblemFactCollectionProperty
    private List<Period> periodList;

    @PlanningEntityCollectionProperty
    private List<WorkOrderAssignment> assignmentList;

    @PlanningScore
    private HardSoftScore score;
}