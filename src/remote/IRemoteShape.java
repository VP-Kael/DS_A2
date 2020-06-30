/*
    COMP90015 Distributed Systems
    2020 Semester 1
    Muyuan Zhu
    903767
 */

package remote;

import java.awt.Color;
import java.awt.Point;
import java.awt.Shape;
import java.rmi.Remote;
import java.rmi.RemoteException;
import javax.swing.ImageIcon;

public interface IRemoteShape extends Remote {
    // creator
    IRemoteClient GetCreator() throws RemoteException;

    // component
    Shape GetShape() throws RemoteException;

    // attribute
    int GetShapeType() throws RemoteException;
    Color GetColor() throws RemoteException;
    int GetStroke() throws RemoteException;
    Point GetPoint() throws RemoteException;

    // special attribute
    int GetEraserSize() throws RemoteException;

    String GetText() throws RemoteException;

    ImageIcon GetImage() throws RemoteException;
}
