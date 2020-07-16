import javax.xml.crypto.Data;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    public static void main(String [] args)
    {
        int port = 8888;
        ServerManager serverManager = new ServerManager(port);
        serverManager.start();
    }
}
