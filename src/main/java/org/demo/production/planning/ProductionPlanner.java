package org.demo.production.planning;

import org.demo.production.planning.domain.*;
import org.optaplanner.core.api.solver.SolverFactory;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.<br/>
 * User: Thilanka<br/>
 * Date: 5/6/2021<br/>
 * Time: 8:13 PM<br/>
 * To change this template use File | Settings | File Templates.
 */
public class ProductionPlanner {

    public static DaySession fullDay = new DaySession("FULL_DAY", LocalTime.of(8, 0), 8);

    public static void main(String[] args) {
        System.out.println("########################## Application Started #######################");
        WorkOrder workOrder = ProblemJsonGenerator.readProblem();

        ProductionPlanner productionPlanner = new ProductionPlanner();
        workOrder = productionPlanner.solve(workOrder);
        System.out.println(workOrder);
    }

    public WorkOrder solve(WorkOrder workOrder) {
        SolverFactory<WorkOrder> solverFactory = SolverFactory.createFromXmlResource(
                "production/solver/productionSolverConfig.xml");

        workOrder = solverFactory.buildSolver().solve(workOrder);
        return workOrder;
    }

    public static void populateProblem(WorkOrder workOrder) {
        workOrder.setResourceList(populateResources());

        Production voice = new Production("The Voice");
        WorkOrderAssignment wo1 = new WorkOrderAssignment();
        wo1.setProduction(voice);
        Session s1 = new Session();
        Period p1 = new Period(LocalDate.of(1, 2, 2021), fullDay);
        wo1.setPeriod(p1);

        List<Skill> skills = new ArrayList<>();
        Skill skill = new Skill();
        skill.setSkillType(SkillType.CAMERAMEN);
        skill.setRequiredCount(2);
        skills.add(skill);

        s1.setSkillList(skills);
        wo1.setSession(s1);

    }


    public static List<Resource> populateResources() {
        List<Resource> resources = new ArrayList<>();

        // 8 cameramen
        resources.add(new Resource("Tom Smith", SkillType.CAMERAMEN));
        resources.add(new Resource("June Smith", SkillType.CAMERAMEN));
        resources.add(new Resource("John Smith", SkillType.CAMERAMEN));
        resources.add(new Resource("Jane Smith", SkillType.CAMERAMEN));
        resources.add(new Resource("Bibi Smith", SkillType.CAMERAMEN));
        resources.add(new Resource("Mike Smith", SkillType.CAMERAMEN));
        resources.add(new Resource("Dawn Smith", SkillType.CAMERAMEN));
        resources.add(new Resource("Don Smith", SkillType.CAMERAMEN));

        resources.add(new Resource("Bob Jones", SkillType.PRODUCER));
        resources.add(new Resource("Alan Barnes", SkillType.EDITOR));
        resources.add(new Resource("Steve James", SkillType.AUDIO_OPERATOR));

        return resources;
    }
}