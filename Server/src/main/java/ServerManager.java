import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class ServerManager extends Thread {
    private final int port;
    private ArrayList<ServerHandle> serverHandles = new ArrayList<>();

    public ServerManager(int port) {
        this.port = port;
    }

    @Override
    public void run() {
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            while(true) {
                System.out.println("Waiting for conection...");
                Socket clientSocket = serverSocket.accept();
                System.out.println("Accepted one connection from " + clientSocket);
                ServerHandle serverHandle = new ServerHandle(clientSocket);
                serverHandle.start();
                serverHandles.add(serverHandle);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
