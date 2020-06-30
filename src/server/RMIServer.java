/*
    COMP90015 Distributed Systems
    2020 Semester 1
    Muyuan Zhu
    903767
 */

package server;

import java.rmi.ConnectException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import javax.swing.JOptionPane;

public class RMIServer {
    public final static String APPLICATION_REGISTRY_NAME = "RoomManage";
    public final static String CONNECTION_FAIL_MESSAGE = "Connection Failed";
    public final static String ERROR_WINDOW_TITLE = "Error";
    public final static String SUCCESS_WINDOW_TITLE = "Succeed";

    public static void main(String[] args) {
        String serverName = args[0];
        String port = args[1];
        String ip = args[2];

        try {
            RemoteServant remoteServant =
                    new RemoteServant(serverName, port, ip);
            try {
                Registry registry = LocateRegistry.getRegistry(ip);
                registry.bind(APPLICATION_REGISTRY_NAME, remoteServant);
            } catch (ConnectException ce) {
                JOptionPane.showMessageDialog(
                        null, CONNECTION_FAIL_MESSAGE,
                        ERROR_WINDOW_TITLE, JOptionPane.ERROR_MESSAGE);
                ce.printStackTrace();
                System.exit(0);
            }

            System.out.println(String.format("%s server is ready", serverName));
            JOptionPane.showMessageDialog(null, serverName + "ready!",
                    SUCCESS_WINDOW_TITLE, JOptionPane.INFORMATION_MESSAGE);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}
