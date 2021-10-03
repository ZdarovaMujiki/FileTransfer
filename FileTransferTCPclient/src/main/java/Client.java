import lombok.extern.java.Log;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Level;

@Log
public class Client {
    private static void sendFile(Socket clientSocket, File file) {
        try (InputStream socketInput = clientSocket.getInputStream();
             OutputStream socketOutput = clientSocket.getOutputStream()) {
            SendProtocol sendProtocol = new SendProtocol(clientSocket, file);

            sendProtocol.sendFileName();
            sendProtocol.sendFileSize();
            sendProtocol.sendFile();
            String status = sendProtocol.receiveTransferStatus();

            log.info(status);
        } catch (IOException e) {
            log.log(Level.WARNING, "disconnected from server due to error");
        }
    }

    public static void main(String[] args) {
        if (args.length < 3) {
            log.log(Level.WARNING, "File name, server address and server port must be specified");
            return;
        }
        String filePath = args[0];
        File file = new File(filePath);
        if (!file.isFile()) {
            log.log(Level.WARNING, "No file " + filePath);
            return;
        }

        try {
            InetAddress serverAddress = InetAddress.getByName(args[1]);
            int serverPort = Integer.parseInt(args[2]);
            try (Socket socket = new Socket(serverAddress, serverPort)) {
                sendFile(socket, file);
            }
            catch (IOException e) {
                log.log(Level.WARNING, "no server");
            }
        } catch (UnknownHostException e) {
            log.log(Level.WARNING, "bad server address");
        } catch (IllegalArgumentException e) {
            log.log(Level.WARNING, "bad port");
        }
    }
}
