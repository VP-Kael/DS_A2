/*
    COMP90015 Distributed Systems
    2020 Semester 1
    Muyuan Zhu
    903767
 */

package server;

import java.awt.*;
import javax.swing.*;

public class ServerStart {
    private final int WINDOW_WIDTH = 380;
    private final int WINDOW_HEIGHT = 170;
    private final String WINDOW_TITLE = "Server Creating";
    private final int X_GAP = 18;
    private final int Y_GAP = 7;
    private final String DEFAULT_FONT = "Times New Roman";
    private final int DEFAULT_FONT_SIZE = 14;
    private final int TEXT_FIELD_COLUMNS = 12;
    private final String NAME_LABEL_TEXT = "Please input server name: ";
    private final String PORT_LABEL_TEXT = "Please input port: ";
    private final String IP_LABEL_TEXT = "Please input ip: ";
    private final String CONFIRM_BUTTON_TEXT = "Confirm";
    private final String IP_REGULAR_EXPRESSION =
            "^(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[1-9])\\." +
                    "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\." +
                    "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\." +
                    "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)$";
    private final int PORT_LOWER_BOUND = 1024;
    private final int PORT_UPPER_BOUND = 65535;
    private final String NAME_EMAPTY_MESSAGE = "Please input room name";
    private final String ERROR_WINDOW_TITLE = "Invalid Input";
    private final String PORT_ERROR_MESSAGE =
            "Port should be integer in [1024, 65535]";
    private final String IP_ERROR_MESSAGE = "Please input valid ip";
    private final String IP_EMPTY_MESSAGE = "Please inout ip";

    JLabel nameLabel;
    JTextField nameInput;
    JLabel portLabel;
    JTextField portInput;
    JLabel ipLabel;
    JTextField ipInput;
    JButton confirmButton;
    JFrame frame;

    /*
    private static String serverName = null;
    private static int port = 1099;
    private static String ip = "localhost";
    */
    private static String serverName;
    private static int port;
    private static String ip;

    public ServerStart() {
        this.Initialize();
    }

    private void Initialize() {
        WindowInitialize();

        InfoSectionInitialize();

        ConfirmSectionInitialize();
    }

    private void WindowInitialize(){
        this.frame = new JFrame();

        this.frame.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        this.frame.setTitle(WINDOW_TITLE);
        this.frame.setLocationRelativeTo(null);
        this.frame.setVisible(true);
        this.frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }
    private void InfoSectionInitialize(){
        this.frame.setLayout(new FlowLayout(FlowLayout.CENTER, X_GAP, Y_GAP));
        this.nameLabel = new JLabel(NAME_LABEL_TEXT);
        this.nameLabel.setFont(new Font(
                DEFAULT_FONT, Font.PLAIN, DEFAULT_FONT_SIZE));
        this.frame.add(this.nameLabel);
        this.nameInput = new JTextField(TEXT_FIELD_COLUMNS);
        this.frame.add(this.nameInput);

        this.portLabel = new JLabel(PORT_LABEL_TEXT);
        this.portLabel.setFont(new Font(
                DEFAULT_FONT, Font.PLAIN, DEFAULT_FONT_SIZE));
        this.frame.add(this.portLabel);
        this.portInput = new JTextField(TEXT_FIELD_COLUMNS);
        this.frame.add(this.portInput);

        this.ipLabel = new JLabel(IP_LABEL_TEXT);
        this.ipLabel.setFont(new Font(
                DEFAULT_FONT, Font.PLAIN, DEFAULT_FONT_SIZE));
        this.frame.add(this.ipLabel);
        this.ipInput = new JTextField(TEXT_FIELD_COLUMNS);
        this.frame.add(this.ipInput);
    }
    private void ConfirmSectionInitialize(){
        this.confirmButton = new JButton();
        this.confirmButton.setText(CONFIRM_BUTTON_TEXT);
        this.confirmButton.setFont(new Font(
                DEFAULT_FONT, Font.PLAIN, DEFAULT_FONT_SIZE));
        this.frame.add(this.confirmButton, "South");

        this.confirmButton.addActionListener(e -> {
            NameChecker();

            PortChecker();

            IPChecker();

            RMIServer.main(new String[]{ServerStart.serverName,
                    "" + port, ServerStart.ip});
            ServerStart.this.frame.dispose();
        });
    }
    private void NameChecker(){
        ServerStart.serverName = ServerStart.this.nameInput.getText();
        if (ServerStart.serverName.length() == 0) {
            JOptionPane.showMessageDialog(null, NAME_EMAPTY_MESSAGE,
                    ERROR_WINDOW_TITLE, JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }
    }
    private void PortChecker(){
        try{
            ServerStart.port =
                    Integer.parseInt(ServerStart.this.portInput.getText());
            if (ServerStart.port<PORT_LOWER_BOUND ||
                    ServerStart.port>PORT_UPPER_BOUND) {
                JOptionPane.showMessageDialog(null, PORT_ERROR_MESSAGE,
                        ERROR_WINDOW_TITLE, JOptionPane.ERROR_MESSAGE);
                System.exit(0);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private void IPChecker(){
        ServerStart.ip = ServerStart.this.ipInput.getText();
        if (ServerStart.ip.length() != 0) {
            if (!ServerStart.ip.matches(IP_REGULAR_EXPRESSION)) {
                JOptionPane.showMessageDialog(null, IP_ERROR_MESSAGE,
                        ERROR_WINDOW_TITLE, JOptionPane.ERROR_MESSAGE);
                System.exit(0);
            }
        } else {
            JOptionPane.showMessageDialog(null, IP_EMPTY_MESSAGE,
                    ERROR_WINDOW_TITLE, JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                ServerStart window = new ServerStart();
                window.frame.setVisible(true);
            }catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
