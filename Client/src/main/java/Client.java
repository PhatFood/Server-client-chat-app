import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.InputMismatchException;

public class Client {
    public static void main(String [] args)
    {
        try
        {
            Socket socket = new Socket("localhost",8888);
            System.out.println(socket.getPort());

            InputStream is = socket.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));

            OutputStream os=socket.getOutputStream();
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os));

            String sentMessage="";
            String receivedMessage;

            System.out.println("Talking to server");

            do {
                DataInputStream din = new DataInputStream(System.in);
                sentMessage = din.readLine();
                bw.write(sentMessage);
                bw.newLine();
                bw.flush();

                if(sentMessage.equalsIgnoreCase("quit"))
                    break;
                /*else
                {
                    receivedMessage = br.readLine();
                    System.out.println("Received : " + receivedMessage);
                }*/
            } while(true);

            bw.close();
            br.close();

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
