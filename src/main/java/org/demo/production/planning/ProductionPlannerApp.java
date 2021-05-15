package org.demo.production.planning;

import org.demo.production.planning.domain.Period;
import org.demo.production.planning.domain.Room;
import org.demo.production.planning.domain.WorkOrder;
import org.demo.production.planning.domain.WorkOrderAssignment;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.util.List;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

/**
 * Created by IntelliJ IDEA.<br/>
 * User: Thilanka<br/>
 * Date: 5/11/2021<br/>
 * Time: 2:59 PM<br/>
 * To change this template use File | Settings | File Templates.
 */
public class ProductionPlannerApp extends JFrame {
    private static final Insets insets = new Insets(0, 0, 0, 0);

    public ProductionPlannerApp(String title) throws HeadlessException {
        super(title);
    }

    private static JPanel buttonPanel = new JPanel();
    private static JPanel datePanel = new JPanel();
    private static JPanel roomsPanel = new JPanel();
    private static JPanel schedulePanel = new JPanel();

    private static JProgressBar progressBar;

    private static WorkOrder problem = null;
    private WorkOrder solution;

    private static List<LocalDate> dateList = null;
    private static List<Room> rooms = null;


    private void createAndShowGUI() {
        ProductionPlannerApp frame = new ProductionPlannerApp("Production Planning");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        buttonPanel.setLayout(new FlowLayout());
        buttonPanel.setBorder(new EtchedBorder());

        //Set up the content pane.
        frame.setLayout(new GridBagLayout());
        JButton btnSolve = new JButton("Solve");
        btnSolve.setPreferredSize(new Dimension(200, 50));
        btnSolve.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setSolvingState(true);
                new SolveWorker(problem).execute();
                System.out.println("####################### Problem Solving Completed #####################");
            }
        });
        JButton btnReset = new JButton("Reset");
        btnReset.setPreferredSize(new Dimension(200, 50));
        btnReset.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                populateSchedule(problem);
            }
        });

        progressBar = new JProgressBar(0, 100);
        progressBar.setPreferredSize(new Dimension(400, 40));

        buttonPanel.add(btnReset);
        buttonPanel.add(btnSolve);
        buttonPanel.add(progressBar, RIGHT_ALIGNMENT);


        datePanel.setLayout(new GridLayout(1, 0));
        roomsPanel.setLayout(new GridLayout(0, 1));

        JPanel middlePanel = new JPanel();
        middlePanel.setLayout(new GridBagLayout());
        addComponent(middlePanel, datePanel, 0, 0, 1, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH);
        addComponent(middlePanel, schedulePanel, 0, 1, 1, 1, 1, 5, GridBagConstraints.CENTER, GridBagConstraints.BOTH);
        JScrollPane scrollPane = new JScrollPane(middlePanel);

        addComponent(frame, new JPanel(), 0, 0, 1, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH);
        addComponent(frame, roomsPanel, 0, 1, 1, 1, 1, 5, GridBagConstraints.CENTER, GridBagConstraints.BOTH);
        addComponent(frame, scrollPane, 1, 0, 1, 2, 10, 12, GridBagConstraints.CENTER, GridBagConstraints.BOTH);
        addComponent(frame, buttonPanel, 0, 2, 2, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH);

        loadData();

        frame.setVisible(true);
        frame.setExtendedState(frame.getExtendedState() | JFrame.MAXIMIZED_BOTH);
    }

    protected class SolveWorker extends SwingWorker<WorkOrder, WorkOrder> {

        protected final WorkOrder problem;

        public SolveWorker(WorkOrder problem) {
            this.problem = problem;
        }

        @Override
        protected WorkOrder doInBackground() throws Exception {
            ProductionPlanner productionPlanner = new ProductionPlanner();
            return productionPlanner.solve(ProductionPlannerApp.problem);
        }

        @Override
        protected void done() {
            try {
                solution = get();
                populateSchedule(solution);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new IllegalStateException("Solving was interrupted.", e);
            } catch (ExecutionException e) {
                throw new IllegalStateException("Solving failed.", e.getCause());
            } finally {
                setSolvingState(false);
            }
        }
    }

    private static void setSolvingState(boolean solving) {
        progressBar.setIndeterminate(solving);
        progressBar.setStringPainted(solving);
        progressBar.setString(solving ? "Solving..." : null);
    }

    private static void populateSchedule(WorkOrder workOrder) {
        schedulePanel.removeAll();

        List<WorkOrderAssignment> workOrderAssignments = workOrder.getAssignmentList();
        java.util.Map<String, Map<String, JLabel>> labelMap = new HashMap();
        schedulePanel.setLayout(new GridLayout(rooms.size(), dateList.size()));
        for (int i = 0; i < rooms.size(); i++) {
            Map<String, JLabel> perRoomMap = new HashMap<>();
            for (int j = 0; j < dateList.size(); j++) {
                JLabel label = new JLabel();

                label.setBorder(BorderFactory.createLineBorder(Color.black));
                label.setPreferredSize(new Dimension(400, 40));
                schedulePanel.add(label);

                perRoomMap.put(dateList.get(j).toString(), label);
            }

            labelMap.put(rooms.get(i).getRoomType().name(), perRoomMap);
        }

        Map<String, WorkOrderAssignment> workOrderMap = new HashMap<>();
        Map<String, List<String>> employeeMap = new HashMap<>();
        for (WorkOrderAssignment workOrderAssignment : workOrderAssignments) {
            if (workOrderAssignment.getResource() != null) {
                if (!employeeMap.containsKey(workOrderAssignment.getWorkOrderNumber())) {
                    List<String> employeeList = new ArrayList<>();
                    employeeList.add(workOrderAssignment.getResource().getNameWithSkill());
                    employeeMap.put(workOrderAssignment.getWorkOrderNumber(), employeeList);
                } else {
                    employeeMap.get(workOrderAssignment.getWorkOrderNumber()).add(workOrderAssignment.getResource().getNameWithSkill());
                }
            }

            if (!workOrderMap.containsKey(workOrderAssignment.getWorkOrderNumber())) {
                workOrderMap.put(workOrderAssignment.getWorkOrderNumber(), workOrderAssignment);
            }
        }

        Map<String, List<String>> dayWorkOrderMap = new HashMap<>();
        for (String workOrderNumber : workOrderMap.keySet()) {
            WorkOrderAssignment workOrderAssignment = workOrderMap.get(workOrderNumber);
            if (!dayWorkOrderMap.containsKey(workOrderAssignment.getPeriod().getDate().toString())) {
                List<String> dayWorkOrders = new ArrayList<>();
                dayWorkOrders.add(workOrderNumber);
                dayWorkOrderMap.put(workOrderAssignment.getPeriod().getDate().toString(), dayWorkOrders);
            } else {
                dayWorkOrderMap.get(workOrderAssignment.getPeriod().getDate().toString()).add(workOrderNumber);
            }
        }

        for (String room : labelMap.keySet()) {
            Map<String, JLabel> map = labelMap.get(room);
            for (String date : map.keySet()) {
                // get the label related to room and date
                List<String> workOrders = dayWorkOrderMap.get(date);
                if (workOrders != null) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("<html><div>");
                    for (String order : workOrders) {
                        WorkOrderAssignment workOrderAssignment = workOrderMap.get(order);
                        if (workOrderAssignment.getRoom().getRoomType().name().equals(room)) {
                            sb.append("<p>");
                            sb.append(order).append(workOrderAssignment.getPeriod()._getPeriod());
                            List<String> resources = employeeMap.get(order);
                            if (resources != null) {
                                sb.append("<br>");
                                sb.append(resources.stream().map(resource -> String.valueOf(resource)).collect(Collectors.joining(",")));
                            }
                            sb.append("</p>");
                        }
                    }
                    sb.append("</div></html>");

                    JLabel label = map.get(date);
                    label.setText(sb.toString());
                }

            }
        }
    }

    private static void populateRooms(WorkOrder workOrder) {
        rooms = workOrder.getRoomList();
        for (Room room : rooms) {
            JLabel label = new JLabel();
            label.setText(room.getRoomType().name());
            label.setBorder(BorderFactory.createEtchedBorder());
            roomsPanel.add(label);
        }

    }

    private static void populateDates(WorkOrder workOrder) {
        Set<LocalDate> dates = new HashSet<>();
        for (Period period : workOrder.getPeriodList()) {
            dates.add(period.getDate());
        }

        dateList = new ArrayList<>(dates).stream().sorted().collect(Collectors.toList());
        for (LocalDate date : dateList) {
            JLabel label = new JLabel();

            label.setText(date.toString());
            label.setHorizontalAlignment(SwingConstants.CENTER);
            label.setPreferredSize(new Dimension(400, 40));
            label.setMaximumSize(new Dimension(400, 40));
            label.setMinimumSize(new Dimension(400, 40));

            label.setBorder(BorderFactory.createEtchedBorder());
            datePanel.add(label);
        }
    }

    private static void addComponent(Container container, Component component, int gridx, int gridy,
                                     int gridwidth, int gridheight, int weightx, int weighty, int anchor, int fill) {
        GridBagConstraints gbc = new GridBagConstraints(gridx, gridy, gridwidth, gridheight, weightx, weighty,
                anchor, fill, insets, 0, 0);
        container.add(component, gbc);
    }

    private static void loadData() {
        problem = ProblemJsonGenerator.readProblem();

        populateDates(problem);
        populateRooms(problem);
        populateSchedule(problem);
    }


    public static void main(String[] args) {
        /* Use an appropriate Look and Feel */
        try {
            //UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
            UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
        } catch (UnsupportedLookAndFeelException ex) {
            ex.printStackTrace();
        } catch (IllegalAccessException ex) {
            ex.printStackTrace();
        } catch (InstantiationException ex) {
            ex.printStackTrace();
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                ProductionPlannerApp app = new ProductionPlannerApp("Production Planner");
                app.createAndShowGUI();
            }
        });
    }
}