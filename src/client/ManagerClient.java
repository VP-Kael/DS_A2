/*
    COMP90015 Distributed Systems
    2020 Semester 1
    Muyuan Zhu
    903767
 */

package client;

import java.awt.EventQueue;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Set;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import board.Board;
import remote.IRemoteClient;
import remote.IRemoteServer;
import remote.IRemoteServant;
import server.RemoteClient;
import server.RemoteClient.ClientStatus;

public class ManagerClient {
    private final static String CONNECTING_MESSAGE = "Connecting now~~~\n";
    private final static  String ROOM_EXISTS_MESSAGE = "Room Exists";
    private final static String INFO_WINDOW_TITLE = "Info";
    private final static String ERROR_WINDOW_TITLE = "Error";
    private final static String KICK_ASKING = "Sure Kick?";
    private final static String CONFIRM_WINDOW_TITLE = "Confirm";
    private final static String CLOSE_MESSAGE = "Close your room now?";
    private final static String INVALID_MESSAGE = "Invalid Connection";

    public static void main(final String[] args) {
        try{
            UIManager.setLookAndFeel(ClientStart.LOOK_AND_FEEL);
        }catch (Exception e) {
            e.printStackTrace();
        }

        String id = args[0];
        String clientName = args[1];
        String roomName = args[2];
        String port = args[3];
        String ip = args[4];
        
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    JOptionPane.showMessageDialog(null, CONNECTING_MESSAGE,
                            INFO_WINDOW_TITLE, JOptionPane.INFORMATION_MESSAGE);
                    Registry registry = LocateRegistry.getRegistry(ip);
                    final IRemoteServant REMOTE_SERVANT = 
                            (IRemoteServant) registry.lookup(
                                    ClientStart.APPLICATION_REGISTRY_NAME);
                    final IRemoteClient MANAGER = new RemoteClient(
                            Integer.parseInt(id), clientName);
                    MANAGER.SetClientStatus(ClientStatus.MANAGER);
                    final IRemoteServer REMOTE_SERVER = 
                            REMOTE_SERVANT.NewRoom(MANAGER, roomName);
                    if (REMOTE_SERVER != null) {
                        REMOTE_SERVER.SetManager(MANAGER);
                    }else {
                        JOptionPane.showMessageDialog(
                                null, ROOM_EXISTS_MESSAGE, 
                                ERROR_WINDOW_TITLE, JOptionPane.ERROR_MESSAGE);
                        System.exit(0);
                    }

                    REMOTE_SERVER.NewClient(MANAGER);
                    Board appWindow = new Board(MANAGER, REMOTE_SERVER);
                    Board.frame.setVisible(true);
                    Board.frame.setDefaultCloseOperation(
                            WindowConstants.DO_NOTHING_ON_CLOSE);
                    Set<String> clientList = REMOTE_SERVER.GetClientList();
                    for (String clientName : clientList){
                        Board.GetListModel().addElement(clientName);
                        Board.GetClientList().setModel(Board.GetListModel());
                    }

                    ((RemoteClient) MANAGER).SetBoard(appWindow);
                    Board.GetClientList().addListSelectionListener(
                            new ListSelectionListener() {
                        boolean kicking = false;
                        public void valueChanged(ListSelectionEvent e) {
                            String name = (String)
                                    Board.GetClientList().getSelectedValue();
                            if (name != null) {
                                int kick;
                                if (!name.equals(clientName)) {
                                    // client but not manager
                                    if (!e.getValueIsAdjusting() &&
                                            !this.kicking) {
                                        this.kicking = true;
                                        kick = JOptionPane.showConfirmDialog(
                                                null, KICK_ASKING,
                                                CONFIRM_WINDOW_TITLE,
                                                JOptionPane.YES_NO_OPTION);
                                        if (kick == 0) {
                                            Board.GetListModel().
                                                    removeElement(name);
                                            try {
                                                REMOTE_SERVER.KickClient(REMOTE_SERVER.GetClient(name));
                                                REMOTE_SERVER.Deny(MANAGER, name);
                                                MANAGER.NotifyClientList(REMOTE_SERVER.GetClientList());
                                                REMOTE_SERVER.NewJoinerUpdate();
                                            }catch (RemoteException re) {
                                                re.printStackTrace();
                                            }
                                        }
                                        this.kicking = false;
                                    }
                                }else {
                                    // kicking manager
                                    kick = JOptionPane.showConfirmDialog(
                                            null, CLOSE_MESSAGE,
                                            CONFIRM_WINDOW_TITLE,
                                            JOptionPane.YES_NO_OPTION);
                                    if (kick == 0) {
                                        try {
                                            REMOTE_SERVANT.RemoveRoom(
                                                    MANAGER, roomName);
                                        }catch (NotBoundException | RemoteException f) {
                                            f.printStackTrace();
                                        }
                                        Board.GetFrame().dispose();
                                        System.exit(0);
                                    }
                                }
                            }
                        }
                    });
                    Board.GetFrame().addWindowListener(new WindowAdapter() {
                        public void windowClosing(WindowEvent f) {
                            int close = JOptionPane.showConfirmDialog(
                                    null, CLOSE_MESSAGE,
                                    CONFIRM_WINDOW_TITLE,
                                    JOptionPane.YES_NO_OPTION);
                            if (close == 0) {
                                try {
                                    REMOTE_SERVANT.RemoveRoom(MANAGER, roomName);
                                }catch (NotBoundException | RemoteException e) {
                                    e.printStackTrace();
                                }
                                Board.GetFrame().dispose();
                                System.exit(0);
                            }
                        }
                    });
                }catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(null, INVALID_MESSAGE,
                            ERROR_WINDOW_TITLE, JOptionPane.ERROR_MESSAGE);
                    System.exit(0);
                }
            }
        });
    }
}