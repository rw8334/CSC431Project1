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
final class HttpRequest implements Runnable{
	final static String CRLF = "\r\n";
	Socket socket;
	
	/**
	 * Constructor for an http request, binding a socket.
	 * @param socket: the socket to be binded for this request
	 */
	public HttpRequest(Socket socket) {
		this.socket = socket;
	}

	@Override
	public void run() {
		try{
			processRequest();
		}catch(Exception e){
			System.out.println(e);
		}
	}
	
	private void processRequest() throws Exception{
		InputStream is = socket.getInputStream();
		DataOutputStream os = new DataOutputStream(socket.getOutputStream());
		
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		
		String requestLine = br.readLine();
		System.out.println();
		System.out.println(requestLine);
		
		StringTokenizer tokens = new StringTokenizer(requestLine);
		tokens.nextToken();
		
		String fileName = tokens.nextToken();
		fileName = "." + fileName;
		
		String headerLine = null;
		while((headerLine = br.readLine()).length() != 0 ){
			System.out.println(headerLine);
		}
		
		os.close();
		br.close();
		socket.close();
	}
	
}