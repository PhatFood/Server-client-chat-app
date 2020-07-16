import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerHandle extends Thread {

    private Socket clientSocket;

    public ServerHandle(Socket clientSocket)
    {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        try {
            handleClient();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void handleClient() throws IOException, InterruptedException {
        OutputStream outputStream = clientSocket.getOutputStream();
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(outputStream));
        InputStream inputStream = clientSocket.getInputStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));

        String receivedMessage;

        do {
            receivedMessage=br.readLine();
            System.out.println("Received : " + receivedMessage);
            if(receivedMessage.equalsIgnoreCase("quit"))
            {
                System.out.println("Client has left");
                break;
            }
            /*else
            {
                DataInputStream din = new DataInputStream(System.in);
                String k = din.readLine();
                bw.write(k);
                bw.newLine();
                bw.flush();
            }*/
        } while(true);
        bw.close();
        br.close();
    }
}
