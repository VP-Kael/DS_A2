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
import java.net.URL;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;

public class PaintSizeWindow {
    private final int WINDOW_X_BOUND = 100;
    private final int WINDOW_Y_BOUND = 100;
    private final int WINDOW_WIDTH = 200;
    private final int WINDOW_HEIGHT = 80;
    private final String WINDOW_TITLE = "Select Size";
    private final int SELECTION_ROW = 1;
    private final int SELECTION_COLUMN = 3;
    private final String SMALL_STROKE_ICON_PATH = "/images/Small_Stroke.png";
    private final String MEDIUM_STROKE_ICON_PATH = "/images/Medium_Stroke.png";
    private final String LARGE_STROKE_ICON_PATH = "/images/Large_Stroke.png";
    private final Color SIZE_ICON_BACKGROUND = Color.white;
    private final int SMALL_STROKE = 2;
    private final int MEDIUM_STROKE = 6;
    private final int LARGE_STROKE = 10;

    public JDialog sizeWindow;

    public PaintSizeWindow() {
        this.Initialize();

        this.sizeWindow.setModal(true);
        this.sizeWindow.setVisible(true);
    }

    private void Initialize() {
        WindowInitialize();

        OptionButtonInitialize(SMALL_STROKE_ICON_PATH, SMALL_STROKE);
        OptionButtonInitialize(MEDIUM_STROKE_ICON_PATH, MEDIUM_STROKE);
        OptionButtonInitialize(LARGE_STROKE_ICON_PATH, LARGE_STROKE);
    }
    private void WindowInitialize(){
        this.sizeWindow = new JDialog();
        this.sizeWindow.setBounds(WINDOW_X_BOUND, WINDOW_Y_BOUND,
                WINDOW_WIDTH, WINDOW_HEIGHT);
        this.sizeWindow.setTitle(WINDOW_TITLE);
        this.sizeWindow.getContentPane().setLayout(
                new GridLayout(SELECTION_ROW, SELECTION_COLUMN));
    }
    private void OptionButtonInitialize(String iconPath, int stroke) {
        JButton button = new JButton();
        URL url = this.getClass().getResource(iconPath);
        Icon icon = new ImageIcon(url);
        button.setIcon(icon);
        button.setBackground(SIZE_ICON_BACKGROUND);
        this.sizeWindow.getContentPane().add(button);
        button.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                PaintSurface.stroke = stroke;
                PaintSizeWindow.this.sizeWindow.dispose();
            }
        });
    }
}
