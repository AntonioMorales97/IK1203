import java.net.*;
import java.io.*;
import tcpclient.TCPClient;

/**
 * This class represents a web server with a given port number that reads
 * the URL (including: host name, port number, string) and using a {@link TCPClient} returns
 * the respond from the {@link TCPClient}'s connection to the given host name and
 * port number as an HTTP response to the client.
 */
public class HTTPAsk {
    private final static String END_OF_LINE = "\r\n"; //carriage return and line feed

    /**
     * Sets up a {@link ServerSocket} with the given port number and uses a {@link TCPClient}
     * to connect to the given host name and port number (in the URL) and returns it as an
     * HTTP response.
     *
     * @param args takes only one argument, the port number.
     *
     * @throws IOException if something went wrong when opening the server socket.
     */
    public static void main(String[] args) throws IOException{
        ServerSocket serverSocket = new ServerSocket(Integer.parseInt(args[0]));

        while(true){
            String msgFromClient;
            Socket connSocket = serverSocket.accept();
            connSocket.setSoTimeout(3000);
            System.out.println("Created one connection"); //fun to see when a client connects
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
                continue;
            }

            if(clientRequest == null){
                System.out.println("400 Bad Request");
                toClient.writeBytes("HTTP/1.1 400 Bad Request" + END_OF_LINE);
                connSocket.close();
                System.out.println("Closed connection");
                continue;
            }

            String[] splittedRequest = clientRequest.split(" ");
            if(!splittedRequest[0].equals("GET")){
                System.out.println("400 Bad Request");
                toClient.writeBytes("HTTP/1.1 400 Bad Request" + END_OF_LINE);
                connSocket.close();
                System.out.println("Closed connection");
                continue;
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
        }

    }
}
