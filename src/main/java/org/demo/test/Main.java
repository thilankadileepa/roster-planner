package org.demo.test;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by IntelliJ IDEA.<br/>
 * User: Thilanka<br/>
 * Date: 5/12/2021<br/>
 * Time: 1:13 PM<br/>
 * To change this template use File | Settings | File Templates.
 */
public class Main extends JFrame {

    static Main f1 = null;


    public static void main(String[] args) {
        JWindow window = new JWindow();
        window.getContentPane().add(new JLabel("Loading", SwingConstants.CENTER));
        window.setBounds(500, 150, 300, 200);

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
        }
        window.setVisible(false);
        JFrame frame = new JFrame();
        frame.add(new JLabel("Welcome Swing application..."));
        frame.setVisible(true);
        frame.setSize(300, 200);
        window.dispose();
    }
}