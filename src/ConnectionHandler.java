import java.awt.image.BufferedImage;
import java.net.Socket;
import javax.imageio.ImageIO;
import java.util.Date;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
/**
 * Handles client/server interactions.
 *
 */
public class ConnectionHandler extends Thread {
private Socket conn;       // socket representing TCP/IP connection to Client
private String directory;
private ConnectionCount conCounter;
private InputStream is; // get data from client on this input stream
private OutputStream os; // can send data back to the client on this output stream
private ByteArrayOutputStream baOutput;	// The data sent to the output stream is saved in the byte array buffer
private Date date = new Date();
private BufferedReader br;         // use buffered reader to read client data
private BufferedWriter bw;			// use buffered writer to write client data
/**
 * Initializes networking parameters. Start a new thread when new connection is established.
 * @param conn - socket representing TCP/IP connection to Client
 * @param directory - directory where web pages are located
 * @param conCounter - count the number of client connections
 */
public ConnectionHandler(Socket conn, String directory, ConnectionCount conCounter) {
this.conn = conn;
this.directory = directory;
this.conCounter = conCounter;
	try {
	is = conn.getInputStream();     // get data from client on this input stream
	os = conn.getOutputStream();  // to send data back to the client on this stream
	br = new BufferedReader(new InputStreamReader(is)); // use buffered reader to read client data
	bw = new BufferedWriter(new FileWriter("../log.txt", true)); // open log file in directory
	} catch (IOException ioe) {
	System.out.println("ConnectionHandler: " + ioe.getMessage());
	}
}
/**
 *  Start the main method when the new connection is correct.
 */
public void run() {
System.out.println("new ConnectionHandler thread started .... ");
	try {
	printClientData();
	} catch (Exception e) { // exit cleanly for any Exception (including IOException, ClientDisconnectedException)
	System.out.println("ConnectionHandler:run " + e.getMessage());
	cleanUp();     // cleanup and exit
	}
}
/**
 *  A method to deal with different functions includes HEAD, GET, Not Implemented.
 */
private void printClientData() throws DisconnectedException, IOException {
	while (true) {
	String line = br.readLine(); // get data from client over socket
	String filename = line.split(" ")[1];
	bw.append(date + " " + line); //log request
	bw.newLine();
		if (line.split(" ")[0].contains("HEAD")) {
		//function of HEAD
		byte[] getResponse = headerRecord(directory, filename);
		os.write(getResponse);
		} else if (line.split(" ")[0].contains("GET")) {
		//function of GET
		byte[] getResponse = getRecord(directory, filename);
		os.write(getResponse);
		} else {
		//function of Not Implement
		byte[] getResponse = nothingRecord();
		os.write(getResponse);
		}
	System.out.println("\nConnectionHandler: " + line); // assuming no exception, print out line received from client
	cleanUp();
	}
}
/**
 *  A method to deal with  function HEAD.
 * @param directory - directory where web pages are located
 * @param filename - the name of path file
 */
private byte[] headerRecord(String directory, String filename) throws IOException, UnsupportedEncodingException {
baOutput = null;
String path = directory + filename;
byte[] content = null;
String feedback = "";
try {
//test whether file can be find
	br = new  BufferedReader(new FileReader(path));
	br.close();
	content = contentRecord(path);
	feedback += "HTTP/1.1 200 OK\r\n";
	//check extension file
	if (filename.contains(".jpg")) {
		feedback += "Content-Type: image/jpeg\r\n";
	} else if (filename.contains(".gif")) {
		feedback += "Content-Type: image/gif\r\n";
	} else if (filename.contains(".png")) {
		feedback += "Content-Type: image/png\r\n";
	} else {
		feedback += "Content-Type: text/html\r\n";
	}
	feedback += "Content-Length: " + content.length + "\r\n";
	feedback += "\r\n";
	System.out.println(feedback);
	//log response
	bw.append(date + " " + feedback);
	bw.newLine();
	baOutput  = new ByteArrayOutputStream();
	baOutput.write(feedback.getBytes("UTF-8"));
	// baOutput.write(content);
} catch (IOException e) {
	//if file not find
	content = contentRecord(path);
	feedback += "HTTP/1.1 404 Not Found\r\n";
	feedback += "Content-Type: text/html\r\n";
	feedback += "Content-Length: " + content.length + "\r\n";
	System.out.println(feedback);
	//log response
	bw.append(date + " " + feedback);
	bw.newLine();
	baOutput  = new ByteArrayOutputStream();
	baOutput.write(feedback.getBytes("UTF-8"));
	baOutput.write(content);
}
return baOutput.toByteArray();
}
/**
 *  A method to deal with function GET.
 * @param directory - directory where web pages are located
 * @param filename - the name of path file
 */
private byte[] getRecord(String directory, String filename) throws IOException, UnsupportedEncodingException   {
baOutput = null;
String path = directory + filename;
byte[] content = null;
String feedback = "";
try {
	//test whether file can be find
	br = new  BufferedReader(new FileReader(path));
	br.close();
	content = contentRecord(path);
	feedback += "HTTP/1.1 200 OK\r\n";
	if (filename.contains(".jpg")) {
		feedback += "Content-Type: image/jpeg\r\n";
	} else if (filename.contains(".gif")) {
		feedback += "Content-Type: image/gif\r\n";
	} else if (filename.contains(".png")) {
		feedback += "Content-Type: image/png\r\n";
	} else {
		feedback += "Content-Type: text/html\r\n";
	}
	feedback += "Content-Length: " + content.length + "\r\n";
	feedback += "\r\n";
	System.out.println(feedback);
	//log response
	bw.append(date + " " + feedback);
	bw.newLine();
	baOutput  = new ByteArrayOutputStream();
	baOutput.write(feedback.getBytes("UTF-8"));
	baOutput.write(content);
} catch (Exception e) {
	//if file not find
	content = contentRecord(path);
	feedback += "HTTP/1.1 404 Not Found\r\n";
	feedback += "Content-Type: text/html\r\n";
	feedback += "Content-Length: " + content.length + "\r\n";
	System.out.println(feedback);
	//log response
	bw.append(date + " " + feedback);
	bw.newLine();
	baOutput  = new ByteArrayOutputStream();
	baOutput.write(feedback.getBytes("UTF-8"));
	baOutput.write(content);
}
return baOutput.toByteArray();
}
/**
 *  A content method to deal with functions HEAD, GET.
 * @param path - the path of directory with file name where web pages are located
 */
private byte[] contentRecord(String path) {
String content = "";
String string;
try {
	if (path.contains(".html")) {
		//get HTML file
		br = new BufferedReader(new FileReader(path));
		while ((string = br.readLine()) != null) {
			content += string + "\r\n"; //make the content show in next line, same as "Enter" function
		}
		br.close();
	} else if (path.contains(".jpg")) {
		//get the location of binary JPG image
		File file = new File(path);
		BufferedImage img = ImageIO.read(file);
			baOutput = new ByteArrayOutputStream();
		ImageIO.write(img, "jpeg", baOutput);
		byte[] jpg = baOutput.toByteArray();
		return jpg;
	} else if (path.contains(".gif")) {
		//get the location of binary GIF image
		File file = new File(path);
		BufferedImage img = ImageIO.read(file);
			baOutput = new ByteArrayOutputStream();
		ImageIO.write(img, "gif", baOutput);
		byte[] gif = baOutput.toByteArray();
		return gif;
	} else if (path.contains(".png")) {
		//get the location of binary PNG image
		File file = new File(path);
		BufferedImage img = ImageIO.read(file);
		ByteArrayOutputStream baOutput = new ByteArrayOutputStream();
		ImageIO.write(img, "png", baOutput);
		byte[] png = baOutput.toByteArray();
		return png;
	}
} catch (IOException e) {
	content += "404 Not Found";
}
return content.getBytes();
}
/**
 *  A method to deal with situation that is Not Implemented.
 */
private byte[] nothingRecord() throws IOException, UnsupportedEncodingException {
	baOutput = null;
	String content = null;
	String feedback = "";
	content += "501 Not Implemented";
	feedback += "HTTP/1.1 501 Not Implemented\r\n";
	feedback += "Content-Type: text/html\r\n";
	feedback += "Content-Length: " + content.length() + "\r\n";
	feedback += "\r\n";
	System.out.println(feedback);
	//log response
	bw.append(date + " " + feedback);
	bw.newLine();
	baOutput  = new ByteArrayOutputStream();
	baOutput.write(feedback.getBytes("UTF-8"));
	baOutput.write(content.getBytes());
	return baOutput.toByteArray();
}
/**
 *  A method to clean up all data when the system quit.
 */
private void cleanUp() {
	System.out.println("ConnectionHandler: ... cleaning up and exiting ... ");
	try {
		br.close();
		is.close();
		conn.close();
		conCounter.delete();
		baOutput.close();
	} catch (IOException ioe) {
		System.out.println("ConnectionHandler:cleanup " + ioe.getMessage());
	}
}
}
