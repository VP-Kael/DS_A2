/*
    COMP90015 Distributed Systems
    2020 Semester 1
    Muyuan Zhu
    903767
 */

package board;

import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;

public class TextInputWindow {
    private final int INPUT_X_BOUND = 300;
    private final int INPUT_Y_BOUND = 300;
    private final int INPUT_WIDTH = 260;
    private final int INPUT_HEIGHT = 100;
    private final String INPUT_TITLE = "Input text";
    private final int INPUT_ROW = 1;
    private final int INPUT_COLUMN = 1;
    private final int INPUT_FIELD_COLUMN = 80;
    private final String CONFIRM_BUTTON_TEXT = "OK";

    public JDialog inputWindow;
    public JTextField textField;
    public JButton confirmButton;

    public TextInputWindow() {
        this.Initialize();

        this.inputWindow.setModal(true);
        this.inputWindow.setVisible(true);
    }

    public void Initialize() {
        this.inputWindow = new JDialog();
        this.inputWindow.setBounds(INPUT_X_BOUND, INPUT_Y_BOUND,
                INPUT_WIDTH, INPUT_HEIGHT);
        this.inputWindow.setTitle(INPUT_TITLE);
        this.inputWindow.getContentPane().setLayout(
                new GridLayout(INPUT_ROW, INPUT_COLUMN));
        textField = new JTextField(INPUT_FIELD_COLUMN);
        this.inputWindow.add(textField);

        /*
        JTextArea area = new JTextArea();
        area.setBounds(0, 0, 130, 100);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        JScrollPane insertScrollPane = new JScrollPane();
        insertScrollPane.setBounds(
                0, 0, 130, 100);
        insertScrollPane.setViewportView(area);
        this.inputWindow.add(insertScrollPane);
        */

        confirmButton = new JButton(CONFIRM_BUTTON_TEXT);
        this.inputWindow.add(confirmButton);
        confirmButton.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                PaintSurface.text = textField.getText();
                TextInputWindow.this.inputWindow.dispose();
            }
        });
    }
}
