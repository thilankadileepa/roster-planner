package org.demo;

import org.demo.rostering.domain.*;
import org.demo.rostering.domain.contract.Contract;
import org.demo.rostering.domain.request.DayOffRequest;
import org.demo.rostering.export.ExcelExporter;
import org.optaplanner.core.api.solver.SolverFactory;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by IntelliJ IDEA.<br/>
 * User: Thilanka<br/>
 * Date: 4/22/2021<br/>
 * Time: 4:21 PM<br/>
 * To change this template use File | Settings | File Templates.
 */
public class Planner {

    public static final String EMPLOYEE_PREFIX = "EMP_";
    public static final String GENERAL_EMPLOYEE = "GEN";
    public static final String JOHN_DOE = "John Doe";

    private static String[] employees = {"John Doe", "Jane Doe", "Jim Doe", "Tom Doe", "Bob Doe"};

    public static void main(String[] args) {
        System.out.println("########################## Application Started #######################");
        EmployeeRoster employeeRoster = new EmployeeRoster();
        SolverFactory<EmployeeRoster> solverFactory = SolverFactory.createFromXmlResource(
                "solver/employeeRosteringSolverConfig.xml");
        populateProblem(employeeRoster);
        employeeRoster = solverFactory.buildSolver().solve(employeeRoster);

        System.out.println("Plan Created, Starting Exporting to excel");
        ExcelExporter excelExporter = new ExcelExporter();
        excelExporter.export(employeeRoster);
        System.out.println("Process Completed, Please find the generated file (roster.xsls) in the same location");
        System.out.println("########################## Application Completed #######################");

    }

    private static List<Employee> populateEmployees() {
        List<Employee> employeeList = new ArrayList<>();

        int i = 0;
        for (String empName : employees) {
            Employee employee = new Employee();

            employee.setId((long) i);
            employee.setCode(EMPLOYEE_PREFIX + i);
            employee.setName(empName);

            Contract contract = new Contract();
            contract.setCode(GENERAL_EMPLOYEE);
            contract.setWeekendDefinition(WeekendDefinition.SATURDAY_SUNDAY);
            employee.setContract(contract);

            employeeList.add(employee);
            i++;
        }

        return employeeList;
    }

    private static Shift getShift(Type type, ShiftDate shiftDate) {
        Shift shift = new Shift();

        ShiftType shiftType = getShiftType(type);

        shift.setShiftType(shiftType);
        shift.setShiftDate(shiftDate);

        return shift;
    }

    private static ShiftType getShiftType(Type type) {
        ShiftType shiftType = new ShiftType();
        shiftType.setCode(type.toString());
        shiftType.setType(type);

        return shiftType;
    }

    private static void populateProblem(EmployeeRoster employeeRoster) {

        employeeRoster.setEmployeeList(populateEmployees());


        java.util.Map<LocalDate, ShiftDate> shiftDateMap = new HashMap<>();
        List<ShiftDate> shiftDateList = new ArrayList<>();

        List<ShiftType> shiftTypeList = new ArrayList<>();
        List<Shift> shiftList = new ArrayList<>();

        employeeRoster.setShiftAssignmentList(new ArrayList<>());

        int assignmentNo = 0;
        for (int i = 0; i < 365; i++) {
            ShiftDate shiftDate = new ShiftDate();
            shiftDate.setDayIndex(i);
            shiftDate.setDate((LocalDate.now().plusDays(i)));

            // morning
            ShiftAssignment morningShiftAssignment = new ShiftAssignment();
            morningShiftAssignment.setId((long) assignmentNo++);

            Shift morningShift = getShift(Type.M, shiftDate);
            shiftList.add(morningShift);
            morningShiftAssignment.setShift(morningShift);
            if(i==0) {
                morningShiftAssignment.setEmployee(employeeRoster.getEmployeeList().get(4));
            }
            employeeRoster.getShiftAssignmentList().add(morningShiftAssignment);

            // afternoon
            if (!shiftDate.isWeekEnd()) {
                // no afternoon roster for weekends
                ShiftAssignment afternoonShiftAssignment = new ShiftAssignment();
                afternoonShiftAssignment.setId((long) assignmentNo++);

                Shift afternoonShift = getShift(Type.A, shiftDate);
                shiftList.add(afternoonShift);
                afternoonShiftAssignment.setShift(afternoonShift);
                employeeRoster.getShiftAssignmentList().add(afternoonShiftAssignment);
            }

            // evening
            ShiftAssignment eveningShiftAssignment = new ShiftAssignment();
            eveningShiftAssignment.setId((long) assignmentNo++);

            Shift eveningShift = getShift(Type.E, shiftDate);
            shiftList.add(eveningShift);
            eveningShiftAssignment.setShift(eveningShift);
            employeeRoster.getShiftAssignmentList().add(eveningShiftAssignment);

            shiftDateList.add(shiftDate);
            shiftDateMap.put(shiftDate.getDate(), shiftDate);
        }

        employeeRoster.setCode("R");
        employeeRoster.setContractLineList(new ArrayList<>());
        employeeRoster.setContractList(new ArrayList<>());
        employeeRoster.setDayOffRequestList(new ArrayList<>());
        employeeRoster.setDayOnRequestList(new ArrayList<>());
        employeeRoster.setPatternContractLineList(new ArrayList<>());
        employeeRoster.setPatternList(new ArrayList<>());
        employeeRoster.setShiftOffRequestList(new ArrayList<>());
        employeeRoster.setShiftOnRequestList(new ArrayList<>());
        employeeRoster.setSkillList(new ArrayList<>());
        employeeRoster.setSkillProficiencyList(new ArrayList<>());
        employeeRoster.setShiftTypeSkillRequirementList(new ArrayList<>());

        // general shift types
        shiftTypeList.add(getShiftType(Type.M));
        shiftTypeList.add(getShiftType(Type.A));
        shiftTypeList.add(getShiftType(Type.E));

        employeeRoster.setShiftTypeList(shiftTypeList);
        employeeRoster.setShiftList(shiftList);
        employeeRoster.setShiftDateList(shiftDateList);

        NurseRosterParametrization nurseRosterParametrization = new NurseRosterParametrization();
        nurseRosterParametrization.setFirstShiftDate(shiftDateList.get(0));
        nurseRosterParametrization.setPlanningWindowStart(shiftDateList.get(0));
        nurseRosterParametrization.setLastShiftDate(shiftDateList.get(shiftDateList.size() - 1));
        employeeRoster.setNurseRosterParametrization(nurseRosterParametrization);

        // day off requests
        List<DayOffRequest> dayOffRequestList = new ArrayList<>();
        LocalDate start = LocalDate.of(2021, 5, 1);
        LocalDate end = LocalDate.of(2021, 6, 30);
        long id = 0L;
        Employee employee = employeeRoster.getEmployeeList().get(0);
        employee.setDayOffRequestMap(new HashMap<>());
        for (LocalDate date = start; date.isBefore(end); date = date.plusDays(1)) {
            DayOffRequest dayOffRequest = new DayOffRequest();
            dayOffRequest.setId(id);

            dayOffRequest.setEmployee(employee);

            ShiftDate shiftDate = shiftDateMap.get(date);
            dayOffRequest.setShiftDate(shiftDate);
            dayOffRequest.setWeight(1);
            dayOffRequestList.add(dayOffRequest);
            employee.getDayOffRequestMap().put(shiftDate, dayOffRequest);

            id++;
        }
        employeeRoster.setDayOffRequestList(dayOffRequestList);
    }
}