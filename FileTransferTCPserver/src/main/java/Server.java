import lombok.extern.java.Log;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

@Log
public class Server {
    private static final int ONE_THREAD = 1;
    private static final int SPEED_CHECK_DELAY = 3;

    private static final ExecutorService threadPool = Executors.newCachedThreadPool();

    private static void receiveFile(Socket socket) {
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(ONE_THREAD);
        try (InputStream socketInput = socket.getInputStream();
             OutputStream socketOutput = socket.getOutputStream()) {
            ReceiveProtocol receiveProtocol = new ReceiveProtocol(socket);

            File file = receiveProtocol.receiveFileName();
            SpeedCounter speedCounter = new SpeedCounter(file);
            String fileName = file.getName();
            receiveProtocol.receiveFileSize();

            log.info(fileName + " downloading started");
            scheduledExecutorService.scheduleAtFixedRate(speedCounter, SPEED_CHECK_DELAY, SPEED_CHECK_DELAY, TimeUnit.SECONDS);
            receiveProtocol.receiveFile();
            receiveProtocol.sendTransferStatus();
            scheduledExecutorService.awaitTermination(SPEED_CHECK_DELAY, TimeUnit.SECONDS);
            log.info(fileName + " downloading finished");
        }
        catch (IOException | InterruptedException e) {
            e.printStackTrace();
            log.log(Level.WARNING, "client disconnected due to error");
        }
        finally {
            scheduledExecutorService.shutdown();
        }
    }

    public static void main(String[] args) {
        if (args.length < 1) {
            log.log(Level.WARNING, "Server port must be specified");
            return;
        }
        try {
            int port = Integer.parseInt(args[0]);
            try (ServerSocket serverSocket = new ServerSocket(port)) {
                while (!serverSocket.isClosed()) {
                    Socket clientSocket = serverSocket.accept();
                    threadPool.execute(() -> receiveFile(clientSocket));
                }
            }
        }
        catch (IOException | IllegalArgumentException e) {
            log.log(Level.WARNING, "bad port");
        }
        finally {
            threadPool.shutdown();
        }
    }
}
