package org.demo.production.planning.domain;

import lombok.Data;
import org.demo.production.planning.solver.AssignmentPinningFilter;
import org.demo.rostering.domain.AbstractPersistable;
import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.PlanningVariable;

/**
 * Created by IntelliJ IDEA.<br/>
 * User: Thilanka<br/>
 * Date: 5/7/2021<br/>
 * Time: 1:26 PM<br/>
 * To change this template use File | Settings | File Templates.
 */
@Data
@PlanningEntity(pinningFilter = AssignmentPinningFilter.class)
public class WorkOrderAssignment extends AbstractPersistable {
    private Production production;
    private String workOrderNumber;
    private int sequence;

    @PlanningVariable(valueRangeProviderRefs = {"resourceRange"})
    private Resource resource;

    private Room room;

    @PlanningVariable(valueRangeProviderRefs = {"periodsRange"})
    private Period period;

    /**
     * these variables are used to identify whether there are any changes to existing plan
     */
    private Resource originalResource;
    private Period originalPeriod;

    private Session session;
    private SkillType skillType;
    private int timeSlotDuration;

    public void setSkillType(SkillType skillType) {
        this.skillType = skillType;
        if (skillType.equals(SkillType.AUDIO_OPERATOR)) {
            timeSlotDuration = Period.TIME_SLOT_AUDIO_SLOT;
        } else {
            timeSlotDuration = Period.TIME_SLOT_FULL_DAY;
        }
    }

    public boolean isMoved() {
        return ((resource != null && originalResource != null && originalResource.getId() != resource.getId())
                || (period != null && originalPeriod != null && originalPeriod.getId() != period.getId()));
    }
}