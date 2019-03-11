import java.net.*;
import java.io.*;
import tcpclient.TCPClient;

/**
 * The runnable part of the server that handles a client. {@link ConcHTTPAsk}
 * uses this class to be able to handle multiple clients in parallel.
 */
public class MyRunnable implements Runnable{
    private final static String END_OF_LINE = "\r\n"; //carriage return and line feed
    Socket connSocket; //The connection socket for this client

    /**
     * Creates an instance of this class with the given connectin socket for
     * a client.
     *
     * @param connSocket The client socket
     */
    public MyRunnable(Socket connSocket){
        this.connSocket = connSocket;
    }

    /**
     * This method is called right after an instance of this class is created.
     * This will handle the writing and reading for the client.
     */
    public void run(){
        try{
            String msgFromClient;
            connSocket.setSoTimeout(3000);
            BufferedReader fromClient = new BufferedReader(new InputStreamReader(connSocket.getInputStream()));
            DataOutputStream toClient = new DataOutputStream(connSocket.getOutputStream());

            String clientRequest = null;
            try{
                clientRequest = fromClient.readLine();
                System.out.println(clientRequest);
            } catch(SocketTimeoutException exc){
                System.out.println("408 Request Timeout");
                toClient.writeBytes("HTTP/1.1 408 Request Timeout" + END_OF_LINE);
                connSocket.close();
                System.out.println("Closed connection");
                return;
            }
            if(clientRequest == null){
                System.out.println("400 Bad Request");
                toClient.writeBytes("HTTP/1.1 400 Bad Request" + END_OF_LINE);
                connSocket.close();
                System.out.println("Closed connection");
                return;
            }

            String[] splittedRequest = clientRequest.split(" ");
            if(!splittedRequest[0].equals("GET")){
                System.out.println("400 Bad Request");
                toClient.writeBytes("HTTP/1.1 400 Bad Request" + END_OF_LINE);
                connSocket.close();
                System.out.println("Closed connection");
                return;
            }

            String fakeUrl = "http://localhost" + splittedRequest[1]; //get the "/ask" part and create fake url
            try{
                URL url = new URL(fakeUrl); //to simplify some URL parsing
                if(!url.getPath().equals("/ask")) //only accept paths starting with "/ask"
                    throw new MalformedURLException("Path does not start with '/ask'");
                String hostname = "";
                int port = 80; //default
                String string = null;
                String[] params = url.getQuery().split("[=&?]");
                for(int i = 0; i < params.length; i = i + 2){
                    switch (params[i]) {
                        case "hostname":
                            hostname = params[i+1];
                            break;
                        case "port":
                            port = Integer.parseInt(params[i+1]);
                            break;
                        case "string":
                            string = params[i+1];
                            break;
                        default:
                            System.out.println("Not supported parameter!");
                    }
                }
                try{
                    String respondFromServer = TCPClient.askServer(hostname, port, string);
                    System.out.println(respondFromServer);
                    toClient.writeBytes("HTTP/1.1 200 OK" + END_OF_LINE + END_OF_LINE + respondFromServer);

                } catch(Exception exc){
                    System.out.println("404 Not found");
                    toClient.writeBytes("HTTP/1.1 404 Not Found" + END_OF_LINE);
                }

            } catch(MalformedURLException exc){
                System.out.println("400 Bad request");
                toClient.writeBytes("HTTP/1.1 400 Bad Request" + END_OF_LINE);
            }

            connSocket.close();
            System.out.println("Closed one connection"); //to see when a connection is closed
        } catch(Exception exc){
            System.err.println("THREAD ERROR!");
        }
    }

}
