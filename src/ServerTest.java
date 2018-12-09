import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Create server so that it cna listen for client connection requests.
 *
 */
public class ServerTest {
private ServerSocket ss;
private ConnectionCount conCounter;
//limit the number of concurrent client connections
private final int maxConnections = 30;
/**
 * Start a server that listen for client connection requests.
 * @param directory - located web pages directory
 * @param port - port number
 */
public ServerTest(String directory, int port) {
try {
ss = new ServerSocket(port);
conCounter = new ConnectionCount();
System.out.println("Server started ... listening on port " + port + " ...");
//get number of connections
int counter = conCounter.getCounter();
while (counter < maxConnections) {
// will wait until client requests a connection, then returns connection (socket)
Socket conn = ss.accept();
conCounter.add();
System.out.println("Server got new connection request from " + conn.getInetAddress());
System.out.println("Number of connection " + conCounter);
// create new handler for this connection
ConnectionHandler ch = new ConnectionHandler(conn, directory, conCounter);
// start handler thread
ch.start();
}
} catch (IOException ioe) {
System.out.print("Server Connection error : " + ioe.getMessage());
}
}
}


