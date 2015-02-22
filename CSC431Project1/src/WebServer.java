/*
 * Project 1: Web Server
 * Programmer: Ryan Weaver and Ethan Womer
 * Course: CSC 431
 * Section: 1 (12-1:50pm)
 * Instructor: S. Lee 
 */

import java.io.*;
import java.net.*;
import java.util.*;

/**
 * 
 * @author Ethan and Ryan
 *	The class that sets the port and socket for our server, as well as
 *	sets threads to listen for HTTP requests.
 */
public final class WebServer {

	@SuppressWarnings("resource")
	public static void main(String[] args) throws Exception{
		//Set the port number.
		int port = 6789;
		
		ServerSocket serverSocket = null;
		
		//Establish the listen socket.
		try{
			serverSocket = new ServerSocket(port);
		}catch(IOException e){
			e.printStackTrace();
		}
		
		//Process HTTP service requests in an infinite loop.
		while(true){
			Socket socket = serverSocket.accept();
			HttpRequest request = new HttpRequest(socket);
			
			Thread thread = new Thread(request);
			thread.start();
		}
	}

}

/**
 * 
 * @author Ethan and Ryan
 *	The class that process HTTP requests, and delivers an 
 *	appropriate response according to the details of the request.
 */
final class HttpRequest implements Runnable{
	final static String CRLF = "\r\n"; //Character return
	Socket socket;
	
	/**
	 * Constructor for an http request, binding a socket.
	 * @param socket: the socket to be binded for this request
	 */
	public HttpRequest(Socket socket) {
		this.socket = socket;
	}

	/*
	 * @see java.lang.Runnable#run()
	 * Runs our thread.
	 */
	@Override
	public void run() {
		try{
			processRequest();
		}catch(Exception e){
			System.out.println(e);
		}
	}
	
	/*
	 * The method that actually processes an HTTP request.
	 * It parses the request, figures out what it's asking for
	 * and returns the necessary information or file to the browser.
	 */
	private void processRequest() throws Exception{
		InputStream is = socket.getInputStream();
		DataOutputStream os = new DataOutputStream(socket.getOutputStream());
		PrintWriter pw = new PrintWriter(socket.getOutputStream());
		
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		
		String requestLine = br.readLine();
		System.out.println();
		System.out.println(requestLine);
		
		StringTokenizer tokens = new StringTokenizer(requestLine);
		tokens.nextToken();
		
		String fileName = tokens.nextToken();
		fileName = "." + fileName;
		
		FileInputStream fis = null;
		boolean fileExists = true;
		
		try{
			fis = new FileInputStream(fileName);
		} catch (FileNotFoundException e){
			fileExists = false;
		}
		
		String statusLine = null;
		String contentTypeLine = null;
		String entityBody = null;
		
		if(fileExists){
			statusLine = "200 OK";
			contentTypeLine = "Content-type: " + contentType(fileName) + CRLF;
						
			pw.write(statusLine);
			pw.write(CRLF);
			pw.write(contentTypeLine);
			pw.write(CRLF);
		} else{
			statusLine = "404 Not Found";
			contentTypeLine = "Content-type: " + contentType(fileName) + CRLF;
			entityBody = "<html>" + "<head><title>Not Found</title></head>"+
						"<body>Not Found</body></html>";
			
			os.writeBytes(statusLine);
			os.writeBytes(CRLF);
			os.writeBytes(contentTypeLine);
			os.writeBytes(CRLF);
		}
		
		if(fileExists){
			sendBytes(fis, os);
			fis.close();
		} else{
			os.writeBytes(entityBody);
		}
		
		String headerLine = null;
		while((headerLine = br.readLine()).length() != 0 ){
			System.out.println(headerLine);
		}

		os.close();
		br.close();
		socket.close();
	}
	
	/*
	 * Method that specifies how the server will send information
	 * back to the browser that sent the request.
	 */
	private static void sendBytes(FileInputStream fis, OutputStream os) throws Exception{
		byte[] buffer = new byte[1024];
		int bytes = 0;
		
		while((bytes = fis.read(buffer)) != -1){
			os.write(buffer, 0, bytes);
		}
	}
	
	/*
	 * Takes the file extension of our file and returns
	 * it with the appropriate HTTP content type.
	 */
	private static String contentType(String fileName){
		if(fileName.endsWith(".htm") || fileName.endsWith(".html")){
			return "text/html";
		}
		if(fileName.endsWith(".gif")){
			return "image/gif";
		}
		if(fileName.endsWith(".jpeg") || fileName.endsWith(".jpg")){
			return "image/jpeg";
		}
		return "application/octet-stream";
	}
	
}