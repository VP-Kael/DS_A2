/*
    COMP90015 Distributed Systems
    2020 Semester 1
    Muyuan Zhu
    903767
 */

package client;

import java.awt.EventQueue;
import java.awt.HeadlessException;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.rmi.ConnectException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import javax.swing.*;

import board.Board;
import remote.IRemoteClient;
import remote.IRemoteServer;
import remote.IRemoteServant;
import server.RemoteClient;
import server.RemoteClient.ClientStatus;

public class GuestClient {
    private final static  String ERROR_TITLE = "Service Collapse";
    private final static  String CONNECTION_ERROR_MESSAGE =
            "Connection Failed\n";
    private final static  String REGISTRY_LOOKUP_ERROR_MESSAGE =
            "Lookup Registry Failed";
    private final static  String NO_ROOM_ERROR_MESSAGE = "No Room Exists";
    private final static  String DUPLICATE_NAME_ERROR_MESSAGE =
            "Your name is taken";
    private final static  String NOT_PERMIT_ERROR_MESSAGE =
            "You are not allowed enter";
    private final static  String LEAVE_MESSAGE = "Exit Now?";
    private final static String KICKED_MESSAGE = "Your are kicked, sorry";
    private final static int TIME_LIMIT = 10;

    static IRemoteClient client = null;
    static IRemoteServer server = null;
    static Board appWindow = null;

    public static void main(final String[] args) {
        try{
            UIManager.setLookAndFeel(ClientStart.LOOK_AND_FEEL);
        }catch (Exception e) {
            e.printStackTrace();
        }

        String clientName = args[0];
        String roomName = args[1];
        String port = args[2];
        String ip = args[3];

        EventQueue.invokeLater(() -> {
            try {
                GuestClient.client = new RemoteClient();
                GuestClient.client.SetName(clientName);
                GuestClient.client.SetClientStatus(ClientStatus.USER);
                Registry registry = null;

                try {
                    registry = LocateRegistry.getRegistry(ip);
                } catch (ConnectException ce) {
                    JOptionPane.showMessageDialog(
                            null, CONNECTION_ERROR_MESSAGE,
                            ERROR_TITLE, JOptionPane.ERROR_MESSAGE);
                    System.exit(0);
                }

                IRemoteServant remoteServant = null;
                try {
                    remoteServant = (IRemoteServant)registry.lookup(
                            ClientStart.APPLICATION_REGISTRY_NAME);
                } catch (ConnectException ce) {
                    JOptionPane.showMessageDialog(
                            null, REGISTRY_LOOKUP_ERROR_MESSAGE,
                            ERROR_TITLE, JOptionPane.ERROR_MESSAGE);
                    System.exit(0);
                }

                GuestClient.server = remoteServant.GetRoom(
                        GuestClient.client, roomName);
                if (GuestClient.server == null) {
                    JOptionPane.showMessageDialog(
                            null, NO_ROOM_ERROR_MESSAGE,
                            ERROR_TITLE, JOptionPane.ERROR_MESSAGE);
                    System.exit(0);
                }

                System.out.print(String.format(
                        "Enter %s's room: %s\nHave a good Play\n",
                        GuestClient.server.GetManager().GetName(), roomName));

                GuestClient.appWindow = new Board(
                        GuestClient.client, GuestClient.server);
                Board.frame.setVisible(false);
                if (GuestClient.server.GetClientList().contains(clientName)) {
                    JOptionPane.showMessageDialog(
                            null, DUPLICATE_NAME_ERROR_MESSAGE,
                            ERROR_TITLE, JOptionPane.ERROR_MESSAGE);
                    System.exit(0);
                }

                if (!GuestClient.server.Permit(GuestClient.client.GetName())) {
                    JOptionPane.showMessageDialog(
                            null, NOT_PERMIT_ERROR_MESSAGE,
                            ERROR_TITLE, JOptionPane.ERROR_MESSAGE);
                    System.exit(0);
                }else {
                    Board.frame.setVisible(true);
                    Board.frame.getJMenuBar().setVisible(false);
                    GuestClient.server.NewClient(GuestClient.client);
                    System.out.print(String.format(
                            "No.%s Client %s enters this room, Welcome\n",
                            GuestClient.client.GetID(),
                            GuestClient.client.GetName()
                    ));
                    GuestClient.client.ObtainImage(
                            GuestClient.server.GetImage());
                    Set<String> clientList = GuestClient.server.GetClientList();
                    for (String name : clientList){
                        Board.GetListModel().addElement(name);
                        Board.GetClientList().setModel(Board.GetListModel());
                    }

                    ((RemoteClient)GuestClient.client).SetBoard(
                            GuestClient.appWindow);
                }

                Board.GetFrame().addWindowListener(new WindowAdapter() {
                    public void windowClosing(WindowEvent e) {
                        int i = JOptionPane.showConfirmDialog(
                                null, LEAVE_MESSAGE,
                                ERROR_TITLE, JOptionPane.YES_NO_OPTION);
                        if (i == 0) {
                            try {
                                GuestClient.server.KickClient(
                                        GuestClient.client.GetName());
                            } catch (RemoteException re) {
                                re.printStackTrace();
                            }
                            Board.GetFrame().dispose();
                            System.exit(0);
                        }
                    }
                });
            }catch (Exception e) {
                e.printStackTrace();
            }
        });

        while(server != null) {
            try {
                Set<String> clientList = server.GetClientList();
                client.NotifyClientList(clientList);
                if (!server.GetClientList().contains(clientName)) {
                    JOptionPane.showMessageDialog(null, KICKED_MESSAGE,
                            ERROR_TITLE, JOptionPane.ERROR_MESSAGE);
                    Board.GetFrame().setDefaultCloseOperation(
                            WindowConstants.EXIT_ON_CLOSE);
                    System.exit(0);
                }
            }catch (HeadlessException | RemoteException e) {
                e.printStackTrace();
            }

            try {
                TimeUnit.SECONDS.sleep(TIME_LIMIT);
            }catch (InterruptedException ie) {
                ie.printStackTrace();
            }
        }
    }
}