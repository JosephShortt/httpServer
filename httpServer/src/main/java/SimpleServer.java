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

        HttpRequest(String method, String path, String version, Map<String, String> headers){
            this.method = method;
            this.path = path;
            this.version = version;
            this.headers = headers;
        }

        public String toString(){
            return "Method: "+method + "\n" + "Path: "+path + "\n" + "Version: "+version + "\n" + "Headers: " +headers;
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

        return new HttpRequest(method,path,version,headers);
    }

    static void sendResponse(OutputStream out, int statusCode, String statusMessage, String body) throws IOException {
        String reponse = "HTTP/1.1" + statusCode + " " + statusMessage + "\r\n" +
                "Content-Type: text/html\r\n" +
                "Content-Length: " + body.length()+"\r\n" +
                "\r\n" +
                body;

        out.write(reponse.getBytes());
        out.flush();
    }

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(8080);
        System.out.println("Server listening on port 8080...");


        Socket client = serverSocket.accept();
        System.out.println("Connection from: " + client.getInetAddress());

        BufferedReader bf = new BufferedReader(new InputStreamReader(client.getInputStream()));
        OutputStream out = client.getOutputStream();

        HttpRequest request = parseRequest(bf);
        System.out.println(request);

        String body = "<html><body><h1>Hello from my java http server!</h1><p>Path: " +request.path + "</p></body></html>";
        sendResponse(out, 200, "OK", body);

        client.close();
        serverSocket.close();
    }
}
