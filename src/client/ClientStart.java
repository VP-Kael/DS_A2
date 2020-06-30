/*
    COMP90015 Distributed Systems
    2020 Semester 1
    Muyuan Zhu
    903767
 */

package client;

import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Font;
import javax.swing.*;

public class ClientStart {
    public final static String LOOK_AND_FEEL =
            "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel";
    public final static String APPLICATION_REGISTRY_NAME = "RoomManage";
    private final int WINDOW_WIDTH = 400;
    private final int WINDOW_HEIGHT = 300;
    private final String WINDOW_TITLE = "Client Info";
    private final int X_GAP = 18;
    private final int Y_GAP = 7;
    private final String ID_LABEL_TEXT = "please input your id: ";
    private final String DEFAULT_FONT = "Times New Roman";
    private final int DEFAULT_FONT_SIZE = 14;
    private final int TEXT_FIELD_COLUMNS = 12;
    private final String CLIENT_NAME_LABEL_TEXT = "please input your name: ";
    private final String ROOM_NAME_LABEL_TEXT = "please input your room name: ";
    private final String PORT_LABEL_TEXT = "please input port number: ";
    private final String IP_LABEL_TEXT = "please input ip address: ";
    private final String STATUS_LABEL_TEXT = "please select your status: ";
    private final String MANAGER_TEXT = "Manager";
    private final String CLIENT_TEXT = "Client";
    private final String CONFIRM_BUTTON_TEXT = "Confirm";
    private final String ERROR_TITLE = "Invalid Input";
    private final String ID_ERROR_MESSAGE = "Invalid ID, Number Only";
    private final String ID_EMPTY_MESSAGE = "Please input your ID";
    private final String CLIENT_NAME_EMPTY_MESSAGE = "Please input your name";
    private final String ROOM_NAME_EMPTY_MESSAGE = "Please input room name";
    private final int PORT_LOWER_BOUND = 1024;
    private final int PORT_UPPER_BOUND = 65535;
    private final String PORT_ERROR_MESSAGE =
            "Port should be integer in [1024, 65535]";
    private final String IP_REGULAR_EXPRESSION =
            "^(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[1-9])\\." +
                    "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\." +
                    "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\." +
                    "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)$";
    private final String IP_ERROR_MESSAGE = "Please input valid ip address";
    private final String IP_EMPTY_MESSAGE = "Please input ip address";

    private final JFrame PERSONAL_FRAME = new JFrame();
    JPanel infoPanel = new JPanel();
    JLabel clientIDLabel;
    JTextField clientIDInput;
    JLabel clientNameLabel;
    JTextField clientNameInput;
    JLabel roomNameLabel;
    JTextField roomNameInput;
    JLabel portLabel;
    JTextField portInput;
    JLabel IPLabel;
    JTextField IPInput;
    JLabel statusLabel;
    JComboBox statusBox;
    JPanel buttonPanel;
    JButton confirmButton;

    public ClientStart() {
        WindowInitialize();

        InfoSectionInitialize();

        ConfirmSectionInitialize();
    }
    private void WindowInitialize(){
        this.PERSONAL_FRAME.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        this.PERSONAL_FRAME.setTitle(WINDOW_TITLE);
        this.PERSONAL_FRAME.setLocationRelativeTo(null);
    }
    private void InfoSectionInitialize(){
        this.infoPanel.setLayout(new FlowLayout(FlowLayout.LEFT, X_GAP, Y_GAP));
        this.clientIDLabel = new JLabel(ID_LABEL_TEXT);
        this.clientIDLabel.setFont(new Font(
                DEFAULT_FONT, Font.PLAIN, DEFAULT_FONT_SIZE));
        this.infoPanel.add(this.clientIDLabel);
        this.clientIDInput = new JTextField(TEXT_FIELD_COLUMNS);
        this.infoPanel.add(this.clientIDInput);

        this.clientNameLabel = new JLabel(CLIENT_NAME_LABEL_TEXT);
        this.clientNameLabel.setFont(new Font(
                DEFAULT_FONT, Font.PLAIN, DEFAULT_FONT_SIZE));
        this.infoPanel.add(this.clientNameLabel);
        this.clientNameInput = new JTextField(TEXT_FIELD_COLUMNS);
        this.infoPanel.add(this.clientNameInput);

        this.roomNameLabel = new JLabel(ROOM_NAME_LABEL_TEXT);
        this.roomNameLabel.setFont(new Font(
                DEFAULT_FONT, Font.PLAIN, DEFAULT_FONT_SIZE));
        this.infoPanel.add(this.roomNameLabel);
        this.roomNameInput = new JTextField(TEXT_FIELD_COLUMNS);
        this.infoPanel.add(this.roomNameInput);

        this.portLabel = new JLabel(PORT_LABEL_TEXT);
        this.portLabel.setFont(new Font(
                DEFAULT_FONT, Font.PLAIN, DEFAULT_FONT_SIZE));
        this.infoPanel.add(this.portLabel);
        this.portInput = new JTextField(TEXT_FIELD_COLUMNS);
        this.infoPanel.add(this.portInput);

        this.IPLabel = new JLabel(IP_LABEL_TEXT);
        this.IPLabel.setFont(new Font(
                DEFAULT_FONT, Font.PLAIN, DEFAULT_FONT_SIZE));
        this.infoPanel.add(this.IPLabel);
        this.IPInput = new JTextField(TEXT_FIELD_COLUMNS);
        this.infoPanel.add(this.IPInput);

        this.statusLabel = new JLabel(STATUS_LABEL_TEXT);
        this.statusLabel.setFont(new Font(
                DEFAULT_FONT, Font.PLAIN, DEFAULT_FONT_SIZE));
        this.infoPanel.add(this.statusLabel);
        this.statusBox = new JComboBox(
                new String[]{MANAGER_TEXT, CLIENT_TEXT});
        this.infoPanel.add(this.statusBox);

        this.PERSONAL_FRAME.add(this.infoPanel);
    }
    private void ConfirmSectionInitialize(){
        this.buttonPanel = new JPanel();
        this.confirmButton = new JButton();


        this.confirmButton.setText(CONFIRM_BUTTON_TEXT);
        this.confirmButton.setFont(new Font(
                DEFAULT_FONT, Font.PLAIN, DEFAULT_FONT_SIZE));
        this.buttonPanel.add(this.confirmButton);
        this.PERSONAL_FRAME.add(this.buttonPanel, "South");

        this.confirmButton.addActionListener(e -> {
            String id = ClientStart.this.clientIDInput.getText();
            String clientName = ClientStart.this.clientNameInput.getText();
            String roomName = ClientStart.this.roomNameInput.getText();
            String port = ClientStart.this.portInput.getText();
            String ip = ClientStart.this.IPInput.getText();

            IDChecker(id);
            ClientNameChecker(clientName);
            RoomNameChecker(roomName);
            PortChecker(port);
            IPChecker(ip);

            String[] args;
            if (ClientStart.this.statusBox.getSelectedIndex() == 0) {
                // manager
                args = new String[]{id, clientName, roomName, port, ip};
                ManagerClient.main(args);
            } else if (ClientStart.this.statusBox.getSelectedIndex() == 1) {
                // client
                args = new String[]{clientName, roomName, port, ip};
                GuestClient.main(args);
            }

            ClientStart.this.PERSONAL_FRAME.dispose();
        });
    }
    private void IDChecker(String id){
        if (id.length()==0 && ClientStart.this.statusBox.getSelectedIndex()==0) {
            JOptionPane.showMessageDialog(null, ID_EMPTY_MESSAGE,
                    ERROR_TITLE, JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }
        for(int i = 0; i < id.length(); i++) {
            if (id.charAt(i) < '0' || id.charAt(i) > '9') {
                JOptionPane.showMessageDialog(null, ID_ERROR_MESSAGE,
                        ERROR_TITLE, JOptionPane.ERROR_MESSAGE);
                System.exit(0);
            }
        }
    }
    private void ClientNameChecker(String clientName){
        if (clientName.length() == 0) {
            JOptionPane.showMessageDialog(null, CLIENT_NAME_EMPTY_MESSAGE,
                    ERROR_TITLE, JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }
    }
    private void RoomNameChecker(String roomName){
        if (roomName.length() == 0) {
            JOptionPane.showMessageDialog(null, ROOM_NAME_EMPTY_MESSAGE,
                    ERROR_TITLE, JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }
    }
    private void PortChecker(String port){
        int portInt;
        try{
            portInt = Integer.parseInt(port);
            if (portInt < PORT_LOWER_BOUND || portInt > PORT_UPPER_BOUND) {
                JOptionPane.showMessageDialog(null, PORT_ERROR_MESSAGE,
                        ERROR_TITLE, JOptionPane.ERROR_MESSAGE);
                System.exit(0);
            }
        }catch (Exception e){
            JOptionPane.showMessageDialog(null, PORT_ERROR_MESSAGE,
                    ERROR_TITLE, JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }
    }
    private void IPChecker(String ip){
        if (ip.length() != 0) {
            if (!ip.matches(IP_REGULAR_EXPRESSION)) {
                JOptionPane.showMessageDialog(null, IP_ERROR_MESSAGE,
                        ERROR_TITLE, JOptionPane.ERROR_MESSAGE);
                System.exit(0);
            }
        }else {
            JOptionPane.showMessageDialog(null, IP_EMPTY_MESSAGE,
                    ERROR_TITLE, JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                ClientStart window = new ClientStart();
                window.PERSONAL_FRAME.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
