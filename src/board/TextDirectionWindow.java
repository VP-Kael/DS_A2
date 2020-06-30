/*
    COMP90015 Distributed Systems
    2020 Semester 1
    Muyuan Zhu
    903767
 */

package board;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JButton;
import javax.swing.JDialog;

public class TextDirectionWindow {
    private final int WINDOW_X_BOUND = 300;
    private final int WINDOW_Y_BOUND = 300;
    private final int WINDOW_WIDTH = 200;
    private final int WINDOW_HEIGHT = 80;
    private final String WINDOW_TITLE = "Text Direction";
    private final int SELECTION_ROW = 1;
    private final int SELECTION_COLUMN = 3;
    private final Color BUTTON_BACKGROUND = Color.white;
    private final String HORIZONTAL_BUTTON_TEXT = "Horizontal";
    private final int HORIZONTAL_DRAW_TYPE = 3;
    private final String VERTICAL_BUTTON_TEXT = "Vertical";
    private final int VERTICAL_DRAW_TYPE = 5;

    public JDialog directionWindow;

    public TextDirectionWindow() {
        this.Initialize();

        this.directionWindow.setModal(true);
        this.directionWindow.setVisible(true);
    }

    private void Initialize() {
        WindowInitialize();

        OptionButtonInitialize(HORIZONTAL_BUTTON_TEXT, HORIZONTAL_DRAW_TYPE);
        OptionButtonInitialize(VERTICAL_BUTTON_TEXT, VERTICAL_DRAW_TYPE);
    }
    private void WindowInitialize(){
        this.directionWindow = new JDialog();
        this.directionWindow.setBounds(WINDOW_X_BOUND, WINDOW_Y_BOUND,
                WINDOW_WIDTH, WINDOW_HEIGHT);
        this.directionWindow.setTitle(WINDOW_TITLE);
        this.directionWindow.getContentPane().setLayout(
                new GridLayout(SELECTION_ROW, SELECTION_COLUMN));
    }
    private void OptionButtonInitialize(String buttonText, int type){
        JButton button = new JButton(buttonText);
        button.setBackground(BUTTON_BACKGROUND);
        this.directionWindow.getContentPane().add(button);
        button.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                PaintSurface.drawType = type;
                TextDirectionWindow.this.directionWindow.dispose();
            }
        });
    }
}
