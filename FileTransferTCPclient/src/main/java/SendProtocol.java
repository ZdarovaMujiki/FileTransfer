import java.io.*;
import java.net.Socket;

public class SendProtocol {

    private final DataOutputStream dataSocketOutput;
    private final DataInputStream dataSocketInput;
    private final int bufferSize;
    private final Socket socket;
    private final File file;

    public SendProtocol(Socket socket, File file) throws IOException {
        dataSocketOutput = new DataOutputStream(socket.getOutputStream());
        dataSocketInput = new DataInputStream(socket.getInputStream());
        this.bufferSize = socket.getSendBufferSize();
        this.socket = socket;
        this.file = file;
    }

    public void sendFileName() throws IOException {
        dataSocketOutput.writeUTF(file.getName());
    }
    public void sendFileSize() throws IOException {
        dataSocketOutput.writeLong(file.length());
    }
    public void sendFile() throws IOException {
        byte[] buffer = new byte[bufferSize];
        int bytesAmount;
        try (BufferedInputStream bufferedFileInput = new BufferedInputStream(new FileInputStream(file))) {
            while ((bytesAmount = bufferedFileInput.read(buffer)) > 0) {
                dataSocketOutput.write(buffer, 0, bytesAmount);
            }
        }
        socket.shutdownOutput();
    }
    public String receiveTransferStatus() throws IOException {
        return dataSocketInput.readUTF();
    }
}
