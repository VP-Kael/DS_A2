/*
    COMP90015 Distributed Systems
    2020 Semester 1
    Muyuan Zhu
    903767
 */

package remote;

import java.rmi.*;

public interface IRemoteServant extends Remote {
    IRemoteServer NewRoom(IRemoteClient manager, String roomName)
            throws RemoteException, AlreadyBoundException;

    IRemoteServer GetRoom(IRemoteClient client, String roomName)
            throws RemoteException;

    void RemoveRoom(IRemoteClient manager, String roomName)
            throws RemoteException, NotBoundException;

    boolean ManagerChecker(IRemoteClient client)
            throws RemoteException;
}
