package tcpclient;

import java.net.*;
import java.io.*;

/**
 * This is a TCP Client class that has the method to set up a connection
 * ({@link Socket}) to a server with the given host name and port number and ask
 * the server something and return the respond from the server as a <code>String</code>.
 */
public class TCPClient {
    private final static char NEWLINE = '\n';
    private final static int MAX_LINES = 1024;

    /**
     * Creates a socket to a given host and to the given port number. Sends the
     * given message to the server (optional) and returns the respond as a
     * <code>String</code>.
     *
     * @param hostname
     *            the host name to connect to.
     * @param port
     *            the port number.
     * @param ToServer
     *            the message to the server.
     * @return the respond from the server as a <code>String</code>.
     * @throws IOException
     */
    public static String askServer(String hostname, int port, String ToServer) throws IOException {
        if (ToServer == null)
            return askServer(hostname, port);

        StringBuilder respondFromServer = new StringBuilder();
        Socket clientSocket = new Socket(hostname, port);
        try {
            clientSocket.setSoTimeout(3000);
            BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
            outToServer.writeBytes(ToServer + NEWLINE);

            String lineFromServer;
            int counter = 0; //limit stream coming in
            while (((lineFromServer = inFromServer.readLine()) != null) && (lineFromServer != "\n") && counter < MAX_LINES)  {
                respondFromServer.append(lineFromServer + NEWLINE);
                counter++;
            }

        } catch (SocketTimeoutException timeOutExc) {
            clientSocket.close();
            return respondFromServer.toString();

        } catch (IOException exc) {
            if (clientSocket != null)
                clientSocket.close();
            //exc.printStackTrace();
            return respondFromServer.toString();
        }

        clientSocket.close();
        return respondFromServer.toString();
    }

    /**
     * Creates a socket to the given host and the given port number. Does not
     * send anything to the server, instead it receives a limited respond.
     *
     * @param hostname
     *            the host name to connect to.
     * @param port
     *            the port number.
     * @return the respond from the server as a <code>String</code>
     * @throws IOException
     */
    public static String askServer(String hostname, int port) throws IOException {
        StringBuilder respondFromServer = new StringBuilder();
        Socket clientSocket = new Socket(hostname, port);
        try {
            clientSocket.setSoTimeout(3000);
            BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            String lineFromServer;
            int counter = 0; //limit the stream coming in
            while (((lineFromServer = inFromServer.readLine()) != null) && (lineFromServer != "\n") && counter < MAX_LINES) {
                respondFromServer.append(lineFromServer + NEWLINE);
                counter++;
            }

        } catch (SocketTimeoutException timeOutExc) {
            clientSocket.close();
            return respondFromServer.toString();

        } catch (IOException exc) {
            if (clientSocket != null)
                clientSocket.close();
            //exc.printStackTrace();
            return respondFromServer.toString();
        }

        clientSocket.close();
        return respondFromServer.toString();
    }
}
