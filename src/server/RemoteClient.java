/*
    COMP90015 Distributed Systems
    2020 Semester 1
    Muyuan Zhu
    903767
 */

package server;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Set;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import board.PaintSurface;
import board.Board;
import remote.IRemoteClient;
import remote.IRemoteShape;

public class RemoteClient extends UnicastRemoteObject implements IRemoteClient {
    private final String JOINER_WINDOW_TITLE = "New Joiner";
    private final String KICKED_MESSAGE =
            "Your are kicked\nOr the room is closed";
    private final String OUT_TITLE = "Out";
    private int id;
    private String name;
    private ClientStatus status;
    private PaintSurface surface;
    private Board board;

    public RemoteClient() throws RemoteException {
    }
    public RemoteClient(int id, String name) throws RemoteException {
        this.id = id;
        this.SetName(name);
        this.status = ClientStatus.USER;
    }

    public void SetID(int id){
        this.id = id;
    }
    public int GetID(){
        return this.id;
    }

    public String GetName() {
        return this.name;
    }
    public void SetName(String clientName) {
        this.name = clientName;
    }

    public void SetClientStatus(ClientStatus status) {
        this.status = status;
    }
    public ClientStatus GetClientStatus() {
        return this.status;
    }

    public void Notify(String message) {
        if (this.board != null) {
            System.out.println(String.format("Notify Message: %s!!!", message));
            this.board.DisplayActions(message);
        }
    }
    public void NotifyClientList(Set<String> clientList) {
        if (this.board != null) {
            Board.GetListModel().removeAllElements();
            for (String clientName : clientList){
                Board.GetListModel().addElement(clientName);
            }
            Board.GetClientList().setModel(Board.GetListModel());
        }
    }

    public void ObtainShape(IRemoteShape shape) throws RemoteException {
        System.out.print(String.format(
                "New shape by: %s\n",shape.GetCreator().GetName()));
        this.surface.InsertShape(shape);
    }

    public void ObtainImage(ImageIcon image) {
        if (image != null) {
            System.out.println("Obtain image");
            JLabel imageLabel = new JLabel();
            imageLabel.setIcon(image);
            Board.GetFrame().getContentPane().add(imageLabel);
        }

    }
    public void Message(String message) {
        if (this.board != null) {
            Board.DisplayChat().append(message);
        }
    }
    public boolean Permit(String name) {
        int allowed = JOptionPane.showConfirmDialog(
                null, String.format("%s wants to enter room", name),
                JOINER_WINDOW_TITLE, JOptionPane.YES_NO_OPTION);
        if (allowed == 0) {
            System.out.println(String.format("User %s join", name));
            return true;
        } else {
            return false;
        }
    }
    public void ClearCanvas() {
        this.surface.CleanShapesList();
        this.surface.repaint();
    }
    public void RemoveMessage() {
        Thread thread = new Thread(() -> {
            JOptionPane.showMessageDialog(null, KICKED_MESSAGE,
                    OUT_TITLE, JOptionPane.ERROR_MESSAGE);
            Board.GetFrame().dispose();
            System.exit(0);
        });
        thread.start();
    }

    public void SetSurface(PaintSurface surface) {
        this.surface = surface;
    }
    public void SetBoard(Board board) {
        this.board = board;
    }

    public enum ClientStatus {
        MANAGER(0),
        USER(1);

        private int status;

        ClientStatus(int status) {
            this.status = status;
        }
    }
}