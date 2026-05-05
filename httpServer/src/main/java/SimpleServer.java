import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(8080);
        System.out.println("Server listening on port 8080...");


        Socket client = serverSocket.accept();
        System.out.println("Connection from: " + client.getInetAddress());

        BufferedReader bf = new BufferedReader(new InputStreamReader(client.getInputStream()));


        HttpRequest request = parseRequest(bf);
        System.out.println(request);


        client.close();
        serverSocket.close();
    }
}
