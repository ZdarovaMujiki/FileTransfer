import java.io.*;
import java.net.Socket;

public class SendProtocol {

    private final DataInputStream primitiveDataTypesSocketInput;
    private final DataOutputStream primitiveDataTypesSocketOutput;
    private final int bufferSize;
    private final Socket socket;
    private final File file;

    public SendProtocol(Socket socket, File file) throws IOException {
        primitiveDataTypesSocketOutput = new DataOutputStream(socket.getOutputStream());
        primitiveDataTypesSocketInput = new DataInputStream(socket.getInputStream());
        this.bufferSize = socket.getSendBufferSize();
        this.socket = socket;
        this.file = file;
    }

    public void sendFileName() throws IOException {
        primitiveDataTypesSocketOutput.writeUTF(file.getName());
    }
    public void sendFileSize() throws IOException {
        primitiveDataTypesSocketOutput.writeLong(file.length());
    }
    public void sendFile() throws IOException {
        byte[] buffer = new byte[bufferSize];
        int bytesAmount;
        try (BufferedInputStream bufferedFileInput = new BufferedInputStream(new FileInputStream(file))) {
            while ((bytesAmount = bufferedFileInput.read(buffer)) > 0) {
                primitiveDataTypesSocketOutput.write(buffer, 0, bytesAmount);
            }
        }
        socket.shutdownOutput();
    }
    public String receiveTransferStatus() throws IOException {
        return primitiveDataTypesSocketInput.readUTF();
    }
}
