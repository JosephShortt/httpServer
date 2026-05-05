import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;

public class SimpleServer {

    static class HttpRequest{
        String method;
        String path;
        String version;

        HttpRequest(String method, String path, String version){
            this.method = method;
            this.path = path;
            this.version = version;
        }

        public String toString(){
            return "Method: "+method + " | Path: "+path + " | Version: "+version;
        }
    }

    static HttpRequest parseRequestLine(String requestLine){
        String[] parts = requestLine.split(" ");
        return new HttpRequest(parts[0], parts[1], parts[2]);
    }

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(8080);
        System.out.println("Server listening on port 8080...");


        Socket client = serverSocket.accept();
        System.out.println("Connection from: " + client.getInetAddress());

        BufferedReader bf = new BufferedReader(new InputStreamReader(client.getInputStream()));

        String requestLine = bf.readLine();
        HttpRequest httpRequest = parseRequestLine(requestLine);
        System.out.println(httpRequest);


        String line;
        while((line = bf.readLine()) != null && !line.isEmpty()){
            System.out.println(line);
        }

        client.close();
        serverSocket.close();
    }
}
