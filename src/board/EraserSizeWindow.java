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

public class EraserSizeWindow {
    private final int WINDOW_X_BOUND = 100;
    private final int WINDOW_Y_BOUND = 400;
    private final int WINDOW_WIDTH = 200;
    private final int WINDOW_HEIGHT = 80;
    private final String WINDOW_TITLE = "Select Size";
    private final int SELECTION_ROW = 1;
    private final int SELECTION_COLUMN = 3;
    private final String SMALL_SIZE_ICON_PATH =
            "/images/Small_Eraser_Icon.png";
    private final String MEDIUM_SIZE_ICON_PATH =
            "/images/Medium_Eraser_Icon.png";
    private final String LARGE_SIZE_ICON_PATH =
            "/images/Large_Eraser_Icon.png";
    private final Color SIZE_ICON_BACKGROUND = Color.white;
    private final int SMALL_SIZE = 20;
    private final int MEDIUM_SIZE = 30;
    private final int LARGE_SIZE = 40;

    public JDialog sizeWindow;

    public EraserSizeWindow() {
        this.Initialize();

        this.sizeWindow.setModal(true);
        this.sizeWindow.setVisible(true);
    }

    private void Initialize() {
        WindowInitialize();

        OptionButtonInitialize(SMALL_SIZE_ICON_PATH, SMALL_SIZE);
        OptionButtonInitialize(MEDIUM_SIZE_ICON_PATH, MEDIUM_SIZE);
        OptionButtonInitialize(LARGE_SIZE_ICON_PATH, LARGE_SIZE);
    }
    private void WindowInitialize(){
        this.sizeWindow = new JDialog();
        this.sizeWindow.setBounds(WINDOW_X_BOUND, WINDOW_Y_BOUND,
                WINDOW_WIDTH, WINDOW_HEIGHT);
        this.sizeWindow.setTitle(WINDOW_TITLE);
        this.sizeWindow.getContentPane().setLayout(
                new GridLayout(SELECTION_ROW, SELECTION_COLUMN));
    }
    private void OptionButtonInitialize(String iconPath, int iconSize) {
        JButton button = new JButton();
        URL url = this.getClass().getResource(iconPath);
        Icon icon = new ImageIcon(url);
        button.setIcon(icon);
        button.setBackground(SIZE_ICON_BACKGROUND);
        this.sizeWindow.getContentPane().add(button);
        button.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                PaintSurface.eraserSize = iconSize;
                EraserSizeWindow.this.sizeWindow.dispose();
            }
        });
    }
}