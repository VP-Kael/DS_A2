/*
    COMP90015 Distributed Systems
    2020 Semester 1
    Muyuan Zhu
    903767
 */

package server;

import java.awt.Color;
import java.awt.Point;
import java.awt.Shape;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import javax.swing.ImageIcon;
import remote.IRemoteClient;
import remote.IRemoteShape;

public class RemoteShape extends UnicastRemoteObject implements IRemoteShape {
    private final int DEFAULT_ERASER_SIZE = 20;

    private Shape shape;
    private final IRemoteClient remoteClient;
    private Color color;
    private String text;
    private ImageIcon image;
    private Point point;
    private final int drawType;
    private int stroke;
    private int eraserSize;

    public RemoteShape(IRemoteClient remoteClient, ImageIcon image,
                       int drawType) throws RemoteException {
        this.remoteClient = remoteClient;
        this.image = image;
        this.drawType = drawType;
        this.eraserSize = DEFAULT_ERASER_SIZE;
    }
    public RemoteShape(IRemoteClient remoteClient, Shape shape, Color color,
                       int drawType, int stroke) throws RemoteException {
        this.remoteClient = remoteClient;
        this.shape = shape;
        this.drawType = drawType;
        this.color = color;
        this.text = "";
        this.stroke = stroke;
        this.eraserSize =DEFAULT_ERASER_SIZE;
    }
    public RemoteShape(IRemoteClient remoteClient, Shape shape, String text,
                       Color color, int drawType, int stroke, Point point)
            throws RemoteException {
        this.remoteClient = remoteClient;
        this.shape = shape;
        this.drawType = drawType;
        this.color = color;
        this.text = text;
        this.stroke = stroke;
        this.point = point;
        this.eraserSize = DEFAULT_ERASER_SIZE;
    }

    public IRemoteClient GetCreator(){
        return this.remoteClient;
    }

    public Shape GetShape(){
        return this.shape;
    }
    public int GetShapeType(){
        return this.drawType;
    }
    public Color GetColor(){
        return this.color;
    }
    public int GetStroke(){
        return this.stroke;
    }

    public void SetPoint(Point point) {
        this.point = point;
    }
    public Point GetPoint(){
        return this.point;
    }

    public int GetEraserSize(){
        return this.eraserSize;
    }

    public void SetText(String text) {
        this.text = text;
    }
    public String GetText(){
        return this.text;
    }

    public ImageIcon GetImage(){
        return this.image;
    }
}