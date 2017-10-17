package com.nate.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class Server {
	
	static int port = 8099;

    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/", new RootHandler());
        server.createContext("/post", new PostHandler());
        server.start();
        System.out.println("Server started");
    }

    static class RootHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
        	String path = t.getRequestURI().toString();
        	File file;
        	if(path.equals("/"))
        		path = "/index.html";
        	file = new File("resources" + path);
            Headers h = t.getResponseHeaders();
            h.set("Content-Type", "text/html");
            
            t.sendResponseHeaders(200, 0);
            OutputStream os = t.getResponseBody();
            FileInputStream fs = new FileInputStream(file);
            final byte[] buffer = new byte[0x10000];
            int count = 0;
            while ((count = fs.read(buffer)) >= 0) {
              os.write(buffer,0,count);
            }
            fs.close();
            os.close();
        }
    }
    
    static class GetHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
        	
        }
    }

    static class PostHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
        	//Reading data
        	int auto = -1;
        	BufferedReader in = new BufferedReader(new InputStreamReader(t.getRequestBody()));
        	String line = "";
        	while((line = in.readLine()) != null){
        		if(line.contains("auto")){
        			auto = Integer.parseInt(line.substring(line.indexOf("=") + 1));
        		}
        	}
        	in.close();
        	
        	//Response
            String response;
            switch(auto){
            case 1:
            	response = "Auto changed to Center Gear.";
            	break;
            case 2:
            	response = "Auto changed to Left Gear.";
            	break;
            case 3:
            	response = "Auto changed to Right Gear.";
            	break;
            default:
            	response = "Error!";
            }
            System.out.println(response);
            t.sendResponseHeaders(200, response.length());
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }
}
