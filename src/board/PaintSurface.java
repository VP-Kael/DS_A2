/*
    COMP90015 Distributed Systems
    2020 Semester 1
    Muyuan Zhu
    903767
 */

package board;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.GeneralPath;
import java.awt.geom.Ellipse2D.Double;
import java.awt.geom.Line2D.Float;
import java.rmi.RemoteException;
import java.util.ArrayList;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;

import remote.IRemoteClient;
import remote.IRemoteServer;
import remote.IRemoteShape;
import server.RemoteShape;

public class PaintSurface extends JComponent {
    private final IRemoteClient CLIENT;
    private final IRemoteServer SERVER;
    private final int FREE_POINT_ARRAY_SIZE = 10000;
    private final Color SURFACE_BACKGROUND = new Color(0, 0, 0, 65);
    private final String LINE = "Line";
    private final String RECTANGLE = "Rectangle";
    private final String CIRCLE = "Circle";
    private final String OVAL = "Oval";
    private final String TEXT = "Text";
    private final String ERASER = "Eraser";
    private final String FREE = "Free";
    private final int FILL_TYPE = 1;
    private final int HORIZONTAL_TEXT_DRAW_TYPE = 3;
    private final int ERASER_DRAW_TYPE = 4;
    private final int VERTICAL_TEXT_DRAW_TYPE = 5;
    private final int OPEN_FILE_TYPE = 6;
    private final Color DEFAULT_BACKGROUND = Color.LIGHT_GRAY;
    private final Color ERASER_COLOR = Color.BLACK;
    private final int COMPOSITE_RULE = 3;
    private final float COMPOSITE_ALPHA = 1.0F;

    ArrayList<IRemoteShape> shapes;
    Point startPoint;
    Point endPoint;
    int[] freeXCoordinate = new int[FREE_POINT_ARRAY_SIZE];
    int[] freeYCoordinate = new int[FREE_POINT_ARRAY_SIZE];
    static int pointCount = 0;
    static int freePointCount = 0;
    static Color color;
    static int stroke;
    static int eraserSize;
    static String shapeType;
    static int drawType;
    static String text;
    Graphics graphic;
    Shape eraserShape;

    static {
        color = Color.BLACK;
        stroke = 2;
        eraserSize = 10;
        shapeType = "";
        drawType = 0;
        text = "";
    }

    public PaintSurface(final IRemoteClient client, final IRemoteServer server)
            throws RemoteException {
        this.CLIENT = client;
        this.SERVER = server;
        this.shapes = new ArrayList(SERVER.GetShapeSet());
        this.setOpaque(false);
        this.setBackground(SURFACE_BACKGROUND);

        MouseListenerInitialize(this.CLIENT);
        MouseMotionListenerInitialize(this.CLIENT);
    }
    private void MouseListenerInitialize(IRemoteClient client){
        this.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                // click left
                if (SwingUtilities.isLeftMouseButton(e) &&
                        !SwingUtilities.isRightMouseButton(e)) {
                    PaintSurface.this.startPoint =
                            new Point(e.getX(), e.getY());
                    PaintSurface.this.endPoint = PaintSurface.this.startPoint;
                    PaintSurface.this.repaint();
                }

            }

            public void mouseReleased(MouseEvent e) {
                // release left
                if (SwingUtilities.isLeftMouseButton(e)
                        && !SwingUtilities.isRightMouseButton(e)) {
                    PaintSurface.freePointCount = 0;
                    Float shape;
                    Point startPoint = PaintSurface.this.startPoint;
                    int x1 = startPoint.x;
                    int y1 = startPoint.y;
                    int x2 = e.getX();
                    int y2 = e.getY();
                    Color color = PaintSurface.color;
                    int drawType = PaintSurface.drawType;
                    int stroke = PaintSurface.stroke;

                    try {
                        if (PaintSurface.shapeType.equals(LINE)) {
                            // draw line
                            shape = PaintSurface.this.LineBuilder(x1, y1, x2, y2);
                            PaintSurface.this.shapes.add(new RemoteShape(
                                    client, shape, color, drawType, stroke));
                            SERVER.NewShape(
                                    client, shape, color, drawType, stroke);
                            PaintSurface.pointCount = 0;
                        } else if (PaintSurface.shapeType.equals(RECTANGLE)) {
                            // draw rectangle
                            Shape rectangle = PaintSurface.this.RectangleBuilder(
                                    x1, y1, x2, y2);
                            PaintSurface.this.shapes.add(new RemoteShape(client,
                                    rectangle, color, drawType, stroke));
                            SERVER.NewShape(
                                    client, rectangle, color, drawType, stroke);
                            PaintSurface.pointCount = 0;
                        } else {
                            Double shapeDouble;
                            switch (PaintSurface.shapeType) {
                                case CIRCLE:
                                    // draw circle
                                    shapeDouble = PaintSurface.this.
                                            CircleBuilder(x1, y1, x2, y2);
                                    PaintSurface.this.shapes.add(new
                                            RemoteShape(client, shapeDouble,
                                            color, drawType, stroke));
                                    SERVER.NewShape(client, shapeDouble, color,
                                            drawType, stroke);
                                    PaintSurface.pointCount = 0;
                                    break;
                                case OVAL:
                                    // draw oval
                                    shapeDouble = PaintSurface.this.OvalBuilder(
                                            x1, y1, x2, y2);
                                    PaintSurface.this.shapes.add(new
                                            RemoteShape(client, shapeDouble,
                                            color, drawType, stroke));
                                    SERVER.NewShape(client, shapeDouble, color,
                                            drawType, stroke);
                                    PaintSurface.pointCount = 0;
                                    break;
                                case TEXT:
                                    // input text
                                    new TextInputWindow();
                                    RemoteShape myTextShape = new RemoteShape(
                                            client, null, color,
                                            drawType, stroke);
                                    myTextShape.SetPoint(startPoint);
                                    myTextShape.SetText(PaintSurface.text);
                                    PaintSurface.this.shapes.add(myTextShape);
                                    PaintSurface.pointCount = 0;
                                    SERVER.NewText(client, null, text, color,
                                            drawType, stroke, startPoint);
                                    break;
                            }
                        }

                        PaintSurface.this.startPoint = null;
                        PaintSurface.this.endPoint = null;
                        PaintSurface.this.repaint();
                    } catch (RemoteException re) {
                        re.printStackTrace();
                    }
                }
            }
        });
    }
    private void MouseMotionListenerInitialize(IRemoteClient client){
        this.addMouseMotionListener(new MouseMotionAdapter() {
            IRemoteShape shape;
            public void mouseDragged(MouseEvent e) {
                PaintSurface.this.endPoint = new Point(e.getX(), e.getY());
                int eraserSize = PaintSurface.eraserSize;
                int x = e.getX();
                int y = e.getY();

                try {
                    if (PaintSurface.shapeType.equals(ERASER)) {
                        // erase
                        PaintSurface.this.eraserShape = new
                                java.awt.geom.Rectangle2D.Float(
                                        (float)(x - eraserSize/2),
                                        (float)(y - eraserSize/2),
                                        eraserSize, eraserSize);
                        this.shape = new RemoteShape(
                                client, PaintSurface.this.eraserShape,
                                SURFACE_BACKGROUND, ERASER_DRAW_TYPE,
                                PaintSurface.eraserSize);
                        PaintSurface.this.shapes.add(this.shape);
                        SERVER.NewShape(client, PaintSurface.this.eraserShape,
                                Board.GetFrame().getBackground(),
                                ERASER_DRAW_TYPE, PaintSurface.eraserSize);
                    }else if (PaintSurface.shapeType.equals(FREE)) {
                        // free draw
                        PaintSurface.this.freeXCoordinate[
                                PaintSurface.freePointCount] = x;
                        PaintSurface.this.freeYCoordinate[
                                PaintSurface.freePointCount] = y;
                        PaintSurface.freePointCount++;
                        Shape freePath = PaintSurface.this.FreePathBuilder(
                                PaintSurface.this.freeXCoordinate,
                                PaintSurface.this.freeYCoordinate,
                                PaintSurface.freePointCount);
                        Color color = PaintSurface.color;
                        int drawType = PaintSurface.drawType;
                        int stroke = PaintSurface.stroke;
                        PaintSurface.this.InsertShape(new RemoteShape(client,
                                freePath, color, drawType, stroke));
                        SERVER.NewShape(client, freePath,
                                color, drawType, stroke);
                    }
                    PaintSurface.this.repaint();
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }

            public void mouseMoved(MouseEvent e) {
                if (PaintSurface.shapeType.equals(ERASER)) {
                    int x = e.getX();
                    int y = e.getY();
                    int eraserSize = PaintSurface.eraserSize;
                    PaintSurface.this.eraserShape = new
                            java.awt.geom.Rectangle2D.Float(
                                    (float)(x - eraserSize / 2),
                                    (float)(y - eraserSize / 2),
                                    eraserSize, eraserSize);
                    PaintSurface.this.repaint();
                }
            }
        });
    }

    public void InsertShape(IRemoteShape shape) {
        try {
            if (shape.GetShapeType() != HORIZONTAL_TEXT_DRAW_TYPE &&
                    shape.GetShapeType() != VERTICAL_TEXT_DRAW_TYPE) {
                if (shape.GetShapeType() == OPEN_FILE_TYPE) {
                    this.shapes.add(shape);
                } else {
                    this.shapes.add(new RemoteShape(this.CLIENT,
                            shape.GetShape(), shape.GetColor(),
                            shape.GetShapeType(), shape.GetStroke()));
                }
            }else {
                this.shapes.add(new RemoteShape(this.CLIENT,
                        shape.GetShape(), shape.GetText(),
                        shape.GetColor(), shape.GetShapeType(),
                        shape.GetStroke(), shape.GetPoint()));
            }
            this.repaint();
        } catch (RemoteException re) {
            re.printStackTrace();
        }
    }

    private void RendBackground(Graphics2D graphic) {
        graphic.setPaint(DEFAULT_BACKGROUND);
    }

    public void ErasePaint(Shape shape) {
        Graphics2D graphic = (Graphics2D) this.graphic;
        graphic.setColor(ERASER_COLOR);
        graphic.fill(shape);
    }

    public void paint(Graphics graphic) {
        this.graphic = graphic;
        Graphics2D graphics2D = (Graphics2D) graphic;
        graphics2D.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_OFF);
        this.RendBackground(graphics2D);
        graphics2D.setComposite(AlphaComposite.getInstance(
                COMPOSITE_RULE, COMPOSITE_ALPHA));

        IRemoteShape shape;
        try {
            int l = this.shapes.size();

            LOOP_LABEL:
            // add one while loop in case of component lost
            while(true) {
                for (int i=0; true; i++){
                    if (i == l){
                        break LOOP_LABEL;
                    }
                    shape = shapes.get(i);
                    Color color = shape.GetColor();
                    int stroke = shape.GetStroke();
                    int width = this.getWidth();
                    int height = this.getHeight();

                    graphics2D.setPaint(color);
                    if (shape.GetShapeType() == ERASER_DRAW_TYPE) {
                        graphics2D.setStroke(new
                                BasicStroke(shape.GetEraserSize()));
                    } else {
                        graphics2D.setStroke(new BasicStroke(stroke));
                    }

                    if (shape.GetShapeType() == HORIZONTAL_TEXT_DRAW_TYPE) {
                        int x = shape.GetPoint().x;
                        int y = shape.GetPoint().y;
                        graphics2D.drawString(shape.GetText(), x, y);
                    } else if (shape.GetShapeType() == VERTICAL_TEXT_DRAW_TYPE) {
                        int x = shape.GetPoint().x;
                        int y = shape.GetPoint().y;
                        graphics2D.translate(width/2, height/2);
                        graphics2D.rotate(Math.PI/2);
                        graphics2D.drawString(shape.GetText(),
                                (y - height/2), (width/2 - x));
                        graphics2D.rotate(Math.PI/2*3);
                        graphics2D.translate(-width/2, -height/2);
                    } else if (shape.GetShapeType() == OPEN_FILE_TYPE) {
                        Image image = shape.GetImage().getImage();
                        graphics2D.drawImage(image, 0, 0, this);
                    } else {
                        graphics2D.draw(shape.GetShape());
                        if (shape.GetShapeType() == FILL_TYPE ||
                                shape.GetShapeType() == ERASER_DRAW_TYPE) {
                            graphics2D.setPaint(color);
                            graphics2D.fill(shape.GetShape());
                        }
                    }
                }
            }
        } catch (RemoteException re) {
            re.printStackTrace();
        }

        if (this.startPoint != null && this.endPoint != null) {
            graphics2D.setPaint(DEFAULT_BACKGROUND);
            graphics2D.setStroke(new BasicStroke(stroke));
            int x1 = this.startPoint.x;
            int y1 = this.startPoint.y;
            int x2 = this.endPoint.x;
            int y2 = this.endPoint.y;
            switch (shapeType) {
                case LINE:
                    graphics2D.draw(this.LineBuilder(x1, y1, x2, y2));
                    break;
                case RECTANGLE:
                    graphics2D.draw(this.RectangleBuilder(x1, y1, x2, y2));
                    break;
                case CIRCLE:
                    graphics2D.draw(this.CircleBuilder(x1, y1, x2, y2));
                    break;
                case OVAL:
                    graphics2D.draw(this.OvalBuilder(x1, y1, x2, y2));
                    break;
            }
        }

        if (shapeType.equals(ERASER)) {
            this.ErasePaint(this.eraserShape);
        }
    }

    private Float LineBuilder(int x1, int y1, int x2, int y2) {
        return new Float(x1, y1, x2, y2);
    }
    private java.awt.geom.Rectangle2D.Float RectangleBuilder(
            int x1, int y1, int x2, int y2) {
        return new java.awt.geom.Rectangle2D.Float(
                Math.min(x1, x2), Math.min(y1, y2),
                Math.abs(x1 - x2), Math.abs(y1 - y2));
    }
    private Double CircleBuilder(double x1, double y1, double x2, double y2) {
        double diameter = GetDiameter(x1, y1, x2, y2);
        return new Double(Math.min(x1, x2), Math.min(y1, y2),
                diameter, diameter);
    }
    private double GetDiameter(double x1, double y1, double x2, double y2){
        return Math.pow(Math.pow(x1-x2, 2) + Math.pow(y1-y2, 2), 0.5);
    }
    private Double OvalBuilder(double x1, double y1, double x2, double y2) {
        return new Double(Math.min(x1, x2), Math.min(y1, y2),
                Math.abs(x1-x2), Math.abs(y1-y2));
    }
    private GeneralPath FreePathBuilder(int[] x, int[] y, int pointCount) {
        GeneralPath path = new GeneralPath();
        path.moveTo(x[0], y[0]);
        for(int i = 0; i < pointCount; i++) {
            path.lineTo(x[i], y[i]);
        }
        return path;
    }

    public void CleanShapesList() {
        this.shapes.clear();
    }
}
