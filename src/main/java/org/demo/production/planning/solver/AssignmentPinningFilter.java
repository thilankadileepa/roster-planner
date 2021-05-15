package org.demo.production.planning.solver;

import org.demo.production.planning.domain.WorkOrder;
import org.demo.production.planning.domain.WorkOrderAssignment;
import org.optaplanner.core.api.domain.entity.PinningFilter;

/**
 * Created by IntelliJ IDEA.<br/>
 * User: Thilanka<br/>
 * Date: 5/10/2021<br/>
 * Time: 1:36 PM<br/>
 * To change this template use File | Settings | File Templates.
 */
public class AssignmentPinningFilter implements PinningFilter<WorkOrder, WorkOrderAssignment> {
    @Override
    public boolean accept(WorkOrder workOrder, WorkOrderAssignment workOrderAssignment) {
        return !workOrder.getWorkOrderParametrization().isInPlanningWindow(workOrderAssignment.getPeriod());
    }
}