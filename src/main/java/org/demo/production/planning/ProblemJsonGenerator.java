package org.demo.production.planning;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.demo.production.planning.domain.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by IntelliJ IDEA.<br/>
 * User: Thilanka<br/>
 * Date: 5/7/2021<br/>
 * Time: 10:12 PM<br/>
 * To change this template use File | Settings | File Templates.
 */
public class ProblemJsonGenerator {

    private static final String PROBLEM_LOCATION = "data/production.json";

    public static WorkOrder readProblem() {
        try {
            List<String> lines = Files.readAllLines(Paths.get(PROBLEM_LOCATION));
            String content = lines.stream().collect(Collectors.joining(System.lineSeparator()));


            ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule())
                    .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
            WorkOrder workOrder = mapper.readValue(content, WorkOrder.class);
            updateProblem(workOrder);

            return workOrder;

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private static void updateProblem(WorkOrder workOrder) {
        WorkOrderParametrization workOrderParametrization = new WorkOrderParametrization();
        workOrderParametrization.setStartDateTime(LocalDateTime.of(2021, 2, 1, 8, 0));
        workOrderParametrization.setEndDateTime(LocalDateTime.of(2021, 2, 12, 14, 0));
        workOrder.setWorkOrderParametrization(workOrderParametrization);

        java.util.Map<String, Period> periodMap = new HashMap();
        workOrder.setPeriodList(new ArrayList<>());
        LocalDate startDate = workOrderParametrization.getStartDateTime().toLocalDate();
        LocalDate endDate = workOrderParametrization.getEndDateTime().toLocalDate();
        LocalDate audioEditingDate = LocalDate.of(2021, 2, 12);
        long sequence = 0;
        for (int i = 0; (i <= java.time.Period.between(startDate, endDate).getDays()); i++) {
            Period period = new Period();
            period.setId(sequence++);
            period.setDate(startDate.plusDays(i));
            period.setStartTime(LocalTime.of(8, 0));
            period.setDuration(Period.TIME_SLOT_FULL_DAY);

            workOrder.getPeriodList().add(period);
            periodMap.put(period.getPeriodKey(), period);

            // add audio room time slots
            if (period.getDate().equals(audioEditingDate)) {
                for (int j = 0; j < 5; j++) {
                    Period audioSlot = new Period();
                    audioSlot.setId(sequence++);
                    audioSlot.setDate(period.getDate());
                    audioSlot.setStartTime(LocalTime.of((8 + j), 0));
                    audioSlot.setDuration(Period.TIME_SLOT_AUDIO_SLOT);

                    workOrder.getPeriodList().add(audioSlot);
                    periodMap.put(audioSlot.getPeriodKey(), audioSlot);
                }
            }
        }

        java.util.Map<String, Room> roomMap = new HashMap();
        for (Room room : workOrder.getRoomList()) {
            roomMap.put(room.getRoomType().toString(), room);
        }

        sequence = 0;
        long i = 0;
        List<WorkOrderAssignment> duplicateAssignments = new ArrayList<>();
        for (WorkOrderAssignment workOrderAssignment : workOrder.getAssignmentList()) {
            Room roomWithId = roomMap.get(workOrderAssignment.getRoom().getRoomType().toString());
            workOrderAssignment.setRoom(roomWithId);

            Session session = workOrderAssignment.getSession();
            session.setId(i);

            Period period = workOrderAssignment.getPeriod();
            Period selectedPeriod = periodMap.get(period.getPeriodKey());
            if (selectedPeriod != null) {
                period.setId(selectedPeriod.getId());
            }

            workOrderAssignment.setId(sequence++);
            workOrderAssignment.setOriginalPeriod(period);
            duplicateAssignments.add(workOrderAssignment);
            int count = 0;
            for (int j = 0; j < session.getSkillList().size(); j++) {
                for (int k = 0; k < session.getSkillList().get(j).getRequiredCount(); k++) {
                    if (count > 0) {
                        WorkOrderAssignment duplicate = new WorkOrderAssignment();
                        duplicate.setWorkOrderNumber(workOrderAssignment.getWorkOrderNumber());
                        duplicate.setSession(session);
                        duplicate.setPeriod(period);
                        duplicate.setOriginalPeriod(period);
                        duplicate.setRoom(roomWithId);
                        duplicate.setProduction(workOrderAssignment.getProduction());

                        duplicate.setId(sequence++);
                        duplicateAssignments.add(duplicate);
                        duplicate.setSkillType(session.getSkillList().get(j).getSkillType());
                    } else {
                        workOrderAssignment.setSkillType(session.getSkillList().get(j).getSkillType());
                    }

                    count++;
                }
            }

            i++;
        }

        workOrder.setAssignmentList(duplicateAssignments);

    }

    public static void main(String[] args) {
        WorkOrder workOrder = readProblem();
        System.out.println(workOrder);
    }

    public static void mainW(String[] args) {
        WorkOrder workOrder = new WorkOrder();

        Production voice = new Production("The Voice");
        WorkOrderAssignment wo1 = new WorkOrderAssignment();
        wo1.setProduction(voice);
        Session s1 = new Session();
        Period p1 = new Period(LocalDate.of(2021, 2, 1), ProductionPlanner.fullDay);
        wo1.setPeriod(p1);

        List<Skill> skills = new ArrayList<>();
        Skill skill = new Skill();
        skill.setSkillType(SkillType.CAMERAMEN);
        skill.setRequiredCount(2);
        skills.add(skill);

        s1.setSkillList(skills);
        wo1.setSession(s1);

        Room room = new Room(RoomType.STUDIO);
        wo1.setRoom(room);

        workOrder.setAssignmentList(new ArrayList<>());
        workOrder.setRoomList(new ArrayList<>());
        workOrder.setSkillTypeList(new ArrayList<>());
        workOrder.setResourceList(new ArrayList<>());
        workOrder.setPeriodList(new ArrayList<>());

        workOrder.getAssignmentList().add(wo1);
        workOrder.getRoomList().add(room);
        workOrder.getSkillTypeList().add(skill.getSkillType());
        workOrder.getPeriodList().add(p1);

        ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule())
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

        try {
            String json = mapper.writeValueAsString(workOrder);
            System.out.println(json);

            WorkOrder backToObj = mapper.readValue(json, WorkOrder.class);
            System.out.println(backToObj);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

    }
}