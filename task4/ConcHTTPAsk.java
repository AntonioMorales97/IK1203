import java.net.*;

/**
 * This class represents a web server that can handle multiple clients in parallel.
 * It will create a {@link ServerSocket} to a given port number and then read the
 * URL to that port number and perform TCP connections with the given information.
 * See TCPClient.java under tcpclient and also MyRunnable.java (@link MyRunnable).
 */
public class ConcHTTPAsk {

    /**
     * This will create a server socket with the given port number and be able
     * to handle multiple clients in parallel by creating {@link Tread}s for each
     * client.
     *
     * @param args Takes one argument, the port number.
     */
    public static void main(String[] args){
        try{
            ServerSocket serverSocket = new ServerSocket(Integer.parseInt(args[0]));
            while(true){
                Socket connSocket = serverSocket.accept();
                System.out.println("Client connected");
                new Thread(new MyRunnable(connSocket)).start();
            }
        } catch(Exception exc){
            System.err.println("SERVER ERROR!");
        }
    }
}
