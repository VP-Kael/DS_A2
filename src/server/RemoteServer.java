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
import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import javax.swing.ImageIcon;
import remote.IRemoteClient;
import remote.IRemoteServer;
import remote.IRemoteShape;
import server.RemoteClient.ClientStatus;

public class RemoteServer extends UnicastRemoteObject implements IRemoteServer {
    String NAME_EXIST_MESSAGE = "This name is taken";
    String NOT_MANAGER_MESSAGE = "You are not a manager";
    String DENIED_MESSAGE = "You are not allowed";

    private final Map<String, IRemoteClient> clients;
    private final Map<String, IRemoteClient> appliers;
    private Set<IRemoteShape> shapeSet;
    private ImageIcon image;
    private static int clientCount;
    private static IRemoteClient manager;
    private String roomName;
    private Set<Entry<String, IRemoteClient>> clientSet;

    protected RemoteServer(String roomName) throws RemoteException {
        // the client count only increases, which is an invisible id
        clientCount = 0;
        this.clients = new ConcurrentHashMap();
        this.shapeSet = Collections.newSetFromMap(
                Collections.synchronizedMap(new LinkedHashMap()));
        this.appliers = new ConcurrentHashMap();
        this.roomName = roomName;
        clientSet = this.clients.entrySet();
    }

    public String GetRoomName(){
        return this.roomName;
    }

    public void NewClient(IRemoteClient client) throws RemoteException {
        int id = clientCount;
        String clientName = client.GetName();
        client.SetID(id);
        if (!this.clients.containsKey(clientName)) {
            ++clientCount;
            this.clients.put(clientName, client);
            String message = String.format(
                    "%s joins room %s", clientName, roomName);
            System.out.println(message);
            this.MessageNotifier(message);
            this.NewJoinerUpdate();
        }else {
            client.Notify(NAME_EXIST_MESSAGE);
        }
    }

    public IRemoteClient GetClient(String name) {
        return this.clients.get(name);
    }
    public Set<String> GetClientList() {
        return this.clients.keySet();
    }

    public void SetManager(IRemoteClient manager) throws RemoteException {
        if (manager.GetClientStatus() == ClientStatus.MANAGER) {
            RemoteServer.manager = manager;
        }
    }
    public IRemoteClient GetManager() {
        return RemoteServer.manager;
    }

    public void Deny(IRemoteClient manager, String clientName)
            throws RemoteException {
        if (this.CheckStatus(manager)) {
            if (this.appliers.containsKey(clientName)) {
                IRemoteClient client = this.appliers.get(clientName);
                client.Notify(DENIED_MESSAGE);
                this.appliers.remove(clientName);
            }
        }else {
            manager.Notify(NOT_MANAGER_MESSAGE);
        }
    }

    public void KickClient(IRemoteClient client) throws RemoteException {
        String clientName = client.GetName();
        if (this.clients.containsKey(clientName)){
            client.RemoveMessage();
            KickClient(clientName);
        }
    }
    public void KickClient(String clientName) throws RemoteException {
        if (this.clients.containsKey(clientName)) {
            this.clients.remove(clientName);
            this.MessageNotifier(String.format("%s is kicked", clientName));
            this.NewJoinerUpdate();
        }else {
            System.out.print(String.format("No client names %s", clientName));
        }
    }
    public void CleanRoom(IRemoteClient client) throws RemoteException {
        for (Entry<String, IRemoteClient> entry : clientSet){
            IRemoteClient remoteClient = entry.getValue();
            if (!remoteClient.GetName().equalsIgnoreCase(client.GetName())) {
                remoteClient.RemoveMessage();
            }
        }
    }

    public void Messaging(String message) throws RemoteException {
        for (Entry<String, IRemoteClient> entry : clientSet){
            entry.getValue().Message(message);
        }
    }
    public void MessageNotifier(String message) throws RemoteException {
        for (Entry<String, IRemoteClient> entry : clientSet){
            entry.getValue().Notify(message);
        }
    }
    public void NewJoinerUpdate() throws RemoteException {
        for (Entry<String, IRemoteClient> entry : clientSet){
            entry.getValue().NotifyClientList(this.GetClientList());
        }
    }

    public void NewShape(IRemoteClient client, Shape shape, Color color,
                         int type, int stroke) throws RemoteException {
        IRemoteShape remoteShape = new RemoteShape(
                client, shape, color, type, stroke);
        this.shapeSet.add(remoteShape);
        for (Entry<String, IRemoteClient> entry : clientSet){
            IRemoteClient temp = entry.getValue();
            temp.ObtainShape(remoteShape);
            temp.Notify(String.format("%s adds new shape", client.GetName()));
        }
    }
    public void NewText(IRemoteClient client, Shape shape, String text,
                        Color color, int type, int stroke, Point point)
            throws RemoteException {
        IRemoteShape remoteShape = new RemoteShape(
                client, shape, text, color, type, stroke, point);
        this.shapeSet.add(remoteShape);
        for (Entry<String, IRemoteClient> entry : clientSet){
            IRemoteClient temp = entry.getValue();
            temp.ObtainShape(remoteShape);
            temp.Notify(String.format("%s adds new text", client.GetName()));
        }
    }
    public void NewImage(IRemoteClient client, ImageIcon image, int type)
            throws RemoteException {
        IRemoteShape remoteShape = new RemoteShape(client, image, type);
        this.shapeSet.add(remoteShape);
        for (Entry<String, IRemoteClient> entry : clientSet){
            IRemoteClient temp = entry.getValue();
            temp.ObtainShape(remoteShape);
            temp.Notify(String.format("%s adds new image", client.GetName()));
        }
    }
    public void NewImage(IRemoteClient client, ImageIcon image)
            throws RemoteException {
        this.image = image;
        for (Entry<String, IRemoteClient> entry : clientSet){
            entry.getValue().ObtainImage(image);
        }
    }

    public Set<IRemoteShape> GetShapeSet() {
        return this.shapeSet;
    }
    public ImageIcon GetImage() {
        return this.image;
    }

    public void ClearShapes() throws RemoteException {
        for (Entry<String, IRemoteClient> entry : clientSet){
            IRemoteClient temp = entry.getValue();
            temp.ClearCanvas();
        }
        this.shapeSet.clear();
        this.MessageNotifier("Canvas Cleaned");
    }

    public boolean Permit(String name) throws IOException {
        for (Entry<String, IRemoteClient> entry : clientSet){
            return entry.getValue().Permit(name);
        }
        return false;
    }

    private boolean CheckStatus(IRemoteClient manager) throws RemoteException {
        return this.GetManager().GetName().equalsIgnoreCase(manager.GetName());
    }
}