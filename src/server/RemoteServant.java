/*
    COMP90015 Distributed Systems
    2020 Semester 1
    Muyuan Zhu
    903767
 */

package server;

import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.swing.JOptionPane;
import remote.IRemoteClient;
import remote.IRemoteServer;
import remote.IRemoteServant;
import server.RemoteClient.ClientStatus;

public class RemoteServant extends UnicastRemoteObject implements IRemoteServant {
    private final Map<String, IRemoteServer> ROOM_MAP;
    private final String CONNECTION_REFUSE_MESSAGE = "Connection Collapse";
    private final String ERROR_WINDOW_TITLE  = "Error";
    private final String ROOM_EXISTS_MESSAGE = "Room exists";
    private final String ROOM_CREATED_MESSAGE = "Room Created";
    private final String ENTER_DENY_MESSAGE = "You are not a Manager";
    private final String NO_ALLOWED_MESSAGE = "Your are not the Manager";
    private final String ROOM_REMOVED_MESSAGE = "Room is removed";
    private final String ROOM_REMOVED_NOTIFIER = "Room is removed by Manager";

    /*
    public static String SERVER_NAME = null;
    public static int SERVER_PORT = 1099;
    public static String IP_ADDRESS = "localhost";
    */
    public static String SERVER_NAME;
    public static int SERVER_PORT;
    public static String IP_ADDRESS;
    private Registry registry;

    protected RemoteServant(String serverName, String serverPort,
                            String serverIP) throws RemoteException {
        SERVER_NAME = serverName;
        try{
            SERVER_PORT = Integer.parseInt(serverPort);
        }catch (Exception e){
            e.printStackTrace();
        }
        IP_ADDRESS = serverIP;
        this.ROOM_MAP = new ConcurrentHashMap();
        try {
            this.registry = LocateRegistry.getRegistry(IP_ADDRESS);
        } catch (ConnectException ce) {
            JOptionPane.showMessageDialog(
                    null, CONNECTION_REFUSE_MESSAGE,
                    ERROR_WINDOW_TITLE, JOptionPane.ERROR_MESSAGE);
            ce.printStackTrace();
            System.exit(0);
        }
    }

    public IRemoteServer NewRoom(IRemoteClient remoteClient, String roomName)
            throws RemoteException, AlreadyBoundException {
        if (this.ManagerChecker(remoteClient)) {
            if (this.ROOM_MAP.containsKey(roomName)) {
                remoteClient.Notify(ROOM_EXISTS_MESSAGE);
                return null;
            }else {
                IRemoteServer remoteServer = new RemoteServer(roomName);
                this.registry.bind(roomName, remoteServer);
                this.ROOM_MAP.put(roomName, remoteServer);
                remoteClient.Notify(ROOM_CREATED_MESSAGE);
                return remoteServer;
            }
        }else {
            remoteClient.Notify(ENTER_DENY_MESSAGE);
            return null;
        }
    }

    public IRemoteServer GetRoom(IRemoteClient client, String roomName) throws RemoteException {
        if (this.ROOM_MAP.containsKey(roomName)) {
            client.Notify(String.format("Entered room %s", roomName));
            return this.ROOM_MAP.get(roomName);
        }else {
            client.Notify(String.format("Room %s not exists", roomName));
            return null;
        }
    }

    public void RemoveRoom(IRemoteClient manager, String roomName)
            throws RemoteException, NotBoundException {
        if (!this.ManagerChecker(manager)) {
            manager.Notify(NO_ALLOWED_MESSAGE);
        }else if (!this.ROOM_MAP.containsKey(roomName)) {
            manager.Notify(String.format("Room %s not exists", roomName));
        }else {
            IRemoteServer remoteServer = this.ROOM_MAP.get(roomName);
            if (manager.GetName().equalsIgnoreCase(
                    remoteServer.GetManager().GetName())) {
                manager.Notify(ROOM_REMOVED_MESSAGE);
                remoteServer.MessageNotifier(ROOM_REMOVED_NOTIFIER);
                remoteServer.CleanRoom(remoteServer.GetManager());
                this.ROOM_MAP.remove(roomName);
                this.registry.unbind(roomName);
                UnicastRemoteObject.unexportObject(remoteServer, true);
            }else {
                manager.Notify(NO_ALLOWED_MESSAGE);
            }
        }
    }

    public boolean ManagerChecker(IRemoteClient client) throws RemoteException {
        return client.GetClientStatus() == ClientStatus.MANAGER;
    }
}
