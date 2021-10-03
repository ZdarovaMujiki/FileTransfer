import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

public class ReceiveProtocol {
    private final String UPLOADS_FOLDER_NAME = "FileTransferTCPserver\\uploads\\";

    private final DataInputStream primitiveDataTypesSocketInput;
    private final DataOutputStream primitiveDataTypesSocketOutput;
    private final int bufferSize;
    private String fileName;
    private File file;
    private long fileSize;

    public ReceiveProtocol(Socket socket) throws IOException {
        primitiveDataTypesSocketInput = new DataInputStream(socket.getInputStream());
        primitiveDataTypesSocketOutput = new DataOutputStream(socket.getOutputStream());
        bufferSize = socket.getReceiveBufferSize();
    }

    public File receiveFileName() throws IOException {
        fileName = primitiveDataTypesSocketInput.readUTF();
        file = createUniqueFile();
        return file;
    }
    public void receiveFileSize() throws IOException {
        fileSize = primitiveDataTypesSocketInput.readLong();
    }
    public void receiveFile() throws IOException {
        byte[] bytes = new byte[bufferSize];
        int bytesAmount;
        try (BufferedOutputStream bufferedFileOutput = new BufferedOutputStream(new FileOutputStream(file.getPath()))) {
            while ((bytesAmount = primitiveDataTypesSocketInput.read(bytes)) > 0) {
                bufferedFileOutput.write(bytes, 0, bytesAmount);
            }
        }
    }
    public void sendTransferStatus () throws IOException {
        String status = fileSize == file.length() ? "File transfer successful! File name: " + file.getName() : "File transfer failed";
        primitiveDataTypesSocketOutput.writeUTF(status);
    }

    private File createUniqueFile() throws IOException {
        String filePath = UPLOADS_FOLDER_NAME + UUID.randomUUID() + fileName;
        File file = new File(filePath);
        Files.createFile(file.toPath());
        return file;
    }
}
