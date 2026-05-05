import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.*;
import java.util.HashMap;
import java.util.Map;

public class SimpleServer {

    static class HttpRequest{
        String method;
        String path;
        String version;
        Map<String, String> headers;
        String body;

        HttpRequest(String method, String path, String version, Map<String, String> headers, String body){
            this.method = method;
            this.path = path;
            this.version = version;
            this.headers = headers;
            this.body = body;
        }

        public String toString(){
            return "Method: "+method + "\n" + "Path: "+path + "\n" + "Version: "+version + "\n" + "Headers: " +headers + "\n" + "Body: "+body;
        }
    }


    static HttpRequest parseRequest(BufferedReader in) throws IOException {
        //parse request line
        String requestLine = in.readLine();
        String[] parts = requestLine.split(" ");
        String method = parts[0];
        String path = parts[1];
        String version = parts[2];

        //parse headers
        Map<String, String> headers = new HashMap<>();
        String line;
        while((line = in.readLine()) !=null && !line.isEmpty()){
            int colonIndex = line.indexOf(":");
            String key = line.substring(0,colonIndex).trim().toLowerCase();
            String val = line.substring(colonIndex+1).trim();
            headers.put(key,val);
        }

        String body = " ";
        if(headers.containsKey("content-length")){
            int contentLength = Integer.parseInt(headers.get("content-length"));
            char[] bodyChars = new char[contentLength];
            in.read(bodyChars, 0, contentLength);
            body = new String(bodyChars);
        }

        return new HttpRequest(method,path,version,headers,body);
    }

    static void sendResponse(OutputStream out, int statusCode, String statusMessage, String body) throws IOException {
        byte[] bodyBytes = body.getBytes("UTF-8");
        String headers = "HTTP/1.1 " + statusCode + " " + statusMessage + "\r\n" +
                "Content-Type: text/html\r\n" +
                "Content-Length: " + bodyBytes.length+"\r\n" +
                "Connection: close\r\n" +
                "\r\n" +
                body;

        out.write(headers.getBytes("UTF-8"));
        out.write(bodyBytes);
        out.flush();
    }

    static void handleRequest(HttpRequest request, OutputStream out) throws IOException {
        if(request.method.equals("GET")){
            switch(request.path){
                case "/":
                    sendResponse(out, 200,"OK", "<html><body><h1>Welcome</h1><p>Home Page</p></body></html>");
                    break;

                case "/hello":
                    sendResponse(out, 200,"OK", "<html><body><h1>Hello</h1><p>You hit the hello route</p></body></html>");
                    break;

                case "/about":
                    sendResponse(out, 200,"OK", "<html><body><h1>About</h1><p>This is a raw Java HTTP server</p></body></html>");
                    break;

                default:
                    sendResponse(out, 404,"Not Found", "<html><body><h1>404 - Not Found</h1><p>No route for "+ request.path + "</p></body></html>");
                    break;
            }
        }
        else if(request.method.equals("POST")){
            switch (request.path){
                case "/echo":
                    sendResponse(out, 200, "OK", "<html><body><h1>ECHO</h1><p>You sent: </p>" + request.body + "</body></html>");
                break;

                default:
                    sendResponse(out, 404, "Not Found", "<html><body><h1>404 -Not Found</h1></body></html>");
                    break;
            }
        }
        else{
            sendResponse(out, 405, "Method Not Allowed","<html><body><h1>405 - Method Not Allowed</h1></body></html>");

        }


    }

    static void handleClient(Socket client){
        try{
            BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            OutputStream out = client.getOutputStream();

            HttpRequest request = parseRequest(in);
            System.out.println(request);

            handleRequest(request,out);
            System.out.println("Response sent to "+client.getInetAddress());
        }
        catch (IOException e){
            System.out.println("Error handling client "+e.getMessage());
        }
        finally {
            try{
                client.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }


    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(8080);
        System.out.println("Server listening on port 8080...");


        while (true){
            Socket client  = serverSocket.accept();
            Thread thread = new Thread(() -> handleClient(client));
            thread.start();
        }
    }
}
