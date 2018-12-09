/**
 * Create server so that it cna listen for client connection requests.
 *
 */
public class WebServerMain {
/**
 * Start a main function.
 * @param args - user input
 */
public static void main(String[] args) {
try {
int port = Integer.parseInt(args[1]);
new ServerTest(args[0], port);
} catch (Exception e) {
System.out.print("Usage: java WebServerMain <document_root> <port>");
System.exit(0);
}
}
}
