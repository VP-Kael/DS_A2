/*
    COMP90015 Distributed Systems
    2020 Semester 1
    Muyuan Zhu
    903767
 */

package remote;

import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Set;
import javax.swing.ImageIcon;

import server.RemoteClient.ClientStatus;

public interface IRemoteClient extends Remote {
    // basic information set and get
    void SetID(int id) throws RemoteException;
    int GetID() throws RemoteException;

    String GetName() throws RemoteException;
    void SetName(String name) throws RemoteException;

    void SetClientStatus(ClientStatus status) throws RemoteException;
    ClientStatus GetClientStatus() throws RemoteException;

    // notify method
    void Notify(String message) throws RemoteException;
    void NotifyClientList(Set<String> clientList) throws RemoteException;

    // shape operation method
    void ObtainShape(IRemoteShape shape) throws RemoteException;

    // advanced operation method
    void ObtainImage(ImageIcon image) throws RemoteException;
    void Message(String message) throws RemoteException;
    boolean Permit(String userName) throws IOException;
    void ClearCanvas() throws RemoteException;
    void RemoveMessage() throws RemoteException;
}
