import java.io.*;
import java.net.Socket;
import java.nio.file.Files;

public class ReceiveProtocol {
    private final String UPLOADS_FOLDER_NAME = "uploads\\";

    private final DataInputStream dataSocketInput;
    private final DataOutputStream dataSocketOutput;
    private final int bufferSize;
    private String fileName;
    private File file;
    private long fileSize;

    public ReceiveProtocol(Socket socket) throws IOException {
        dataSocketInput = new DataInputStream(socket.getInputStream());
        dataSocketOutput = new DataOutputStream(socket.getOutputStream());
        bufferSize = socket.getReceiveBufferSize();
    }

    public File receiveFileName() throws IOException {
        fileName = dataSocketInput.readUTF();
        file = createUniqueFile();
        return file;
    }
    public void receiveFileSize() throws IOException {
        fileSize = dataSocketInput.readLong();
    }
    public void receiveFile() throws IOException {
        byte[] bytes = new byte[bufferSize];
        int bytesAmount;
        try (BufferedOutputStream bufferedFileOutput = new BufferedOutputStream(new FileOutputStream(file.getPath()))) {
            while ((bytesAmount = dataSocketInput.read(bytes)) > 0) {
                bufferedFileOutput.write(bytes, 0, bytesAmount);
            }
        }
    }
    public void sendTransferStatus () throws IOException {
        String status = fileSize == file.length() ? "File transfer successful! File name: " + file.getName() : "File transfer failed";
        dataSocketOutput.writeUTF(status);
    }

    private File createUniqueFile() throws IOException {
        String filePath = UPLOADS_FOLDER_NAME + fileName;
        File file = new File(filePath);
        for (int i = 0; file.exists(); ++i) {
            file = new File(UPLOADS_FOLDER_NAME + "(" + i + ")" + fileName);
        }
        Files.createFile(file.toPath());
        return file;
    }
}
