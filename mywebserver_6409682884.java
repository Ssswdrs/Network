import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;

public class socket {
    public static void main(String[] args) {
        int port = 5884; //6409682884
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server is listening on port " + port);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                handleClientRequest(clientSocket);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void handleClientRequest(Socket clientSocket) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream())) {

            String request = in.readLine();
            if (request != null) {
                System.out.println("Received request: " + request);

                String[] requestParts = request.split(" ");
                if (requestParts.length < 2) {
                    return;
                }

                String method = requestParts[0];
                String path = requestParts[1];

                if (method.equals("GET")) {
                    // Remove the leading '/' from the path
                    path = path.substring(1);

	                 File file = new File(path);
	                 try {
	                    if (file.exists() && file.isFile()) {
	                        FileInputStream fis = new FileInputStream(file);
	                        byte[] data = new byte[(int) file.length()];
	                        fis.read(data);
	
	                        // Send HTTP response
	                        out.writeBytes("HTTP/1.0 200 OK\r\n");
	                        out.writeBytes("Date: " + new Date() + "\r\n");
	                        out.writeBytes("Server: SimpleWebServer\r\n");
	                        out.writeBytes("Content-Length: " + data.length + "\r\n");
	                        out.writeBytes("\r\n");
	                        out.write(data, 0, data.length);
	                        fis.close();
	                    } else {
	                        // File not found, send a 404 response
	                        out.writeBytes("HTTP/1.0 404 Not Found\r\n");
	                        out.writeBytes("Date: " + new Date() + "\r\n");
	                        out.writeBytes("Server: SimpleWebServer\r\n");
	                        out.writeBytes("\r\n");
	                    }
	                  } catch (FileNotFoundException e) {
	                        e.printStackTrace();
	                  }
                } else {
                    // Only support GET requests
                    out.writeBytes("HTTP/1.0 501 Not Implemented\r\n");
                    out.writeBytes("Date: " + new Date() + "\r\n");
                    out.writeBytes("Server: SimpleWebServer\r\n");
                    out.writeBytes("\r\n");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
