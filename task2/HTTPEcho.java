import java.net.*;
import java.io.*;

/**
 * This class represents an HTTP Echo Server. It creates a socket with a given
 * port number and accepts TCP connections (one client at a time) and returns
 * the same data that was sent by the client back to the client as an HTTP response.
 */
public class HTTPEcho {
    private final static int TIMEOUT = 6000; //ms
    private final static String END_OF_LINE = "\r\n"; //carriage return and line feed

    /**
     * Creates a {@link ServerSocket} with the given port number and its task is
     * to accept incoming TCP connections (one at a time), read the data from
     * it and respond to the client with an HTTP response with the same data in it.
     *
     * @param args takes only one argument, the port number.
     *
     * @throws IOException if something went wrong when opening the socket.
     */
    public static void main( String[] args) throws IOException{
        ServerSocket serverSocket = new ServerSocket(Integer.parseInt(args[0]));

        while(true){
            try{
                StringBuilder msgOut = new StringBuilder();
                Socket connSocket = serverSocket.accept();
                System.out.println("Created one connection"); //fun to see when a client connects
                connSocket.setSoTimeout(TIMEOUT);
                BufferedReader fromClient = new BufferedReader(new InputStreamReader(connSocket.getInputStream()));
                DataOutputStream toClient = new DataOutputStream(connSocket.getOutputStream());

                msgOut.append("HTTP/1.1 200 OK" + END_OF_LINE +
                                "Content-type: text/plain" + END_OF_LINE + END_OF_LINE); //or text/html
                String lineFromClient;
                while((lineFromClient = fromClient.readLine()) != null && lineFromClient.length() != 0){
                    msgOut.append(lineFromClient + END_OF_LINE);
                }

                toClient.writeBytes(msgOut.toString());
                connSocket.close();
                System.out.println("Closed one connection"); //to see when a connection is closed

            } catch(SocketTimeoutException exc){
                System.out.println("Time out");
            } catch(IOException exc){
                System.out.println("IOException");
            } catch(Exception exc){
                System.out.println("Exception");
            }

        }

    }
}
