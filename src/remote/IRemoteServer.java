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
import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Set;
import javax.swing.ImageIcon;

public interface IRemoteServer extends Remote {
    String GetRoomName() throws RemoteException;

    // client
    void NewClient(IRemoteClient client) throws RemoteException;

    IRemoteClient GetClient(String name) throws RemoteException;
    Set<String> GetClientList() throws RemoteException;

    // manager
    void SetManager(IRemoteClient manager) throws RemoteException;
    IRemoteClient GetManager() throws RemoteException;

    void Deny(IRemoteClient manager, String clientName) throws RemoteException;

    void KickClient(IRemoteClient client) throws RemoteException;
    void KickClient(String clientName) throws RemoteException;
    void CleanRoom(IRemoteClient client) throws RemoteException;

    // messaging
    void Messaging(String message) throws RemoteException;
    void MessageNotifier(String message) throws RemoteException;
    void NewJoinerUpdate() throws RemoteException;

    // draw
    void NewShape(IRemoteClient client, Shape shape, Color color, int type,
                  int stroke) throws RemoteException;
    void NewText(IRemoteClient client, Shape shape, String text, Color color,
                 int type, int stroke, Point point) throws RemoteException;
    void NewImage(IRemoteClient client, ImageIcon image,
                  int type) throws RemoteException;
    void NewImage(IRemoteClient client,
                  ImageIcon image) throws RemoteException;

    Set<IRemoteShape> GetShapeSet() throws RemoteException;
    ImageIcon GetImage() throws RemoteException;

    void ClearShapes() throws RemoteException;

    boolean Permit(String name) throws IOException;
}
