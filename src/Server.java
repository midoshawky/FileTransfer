import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    public static void main(String[] args) {
        TCPFileTransfer.FileTransferManager tcpFileTransferManger = new TCPFileTransfer.FileTransferManager(TCPFileTransfer.SERVER_MODE,6677);
        ExecutorService executorService =  Executors.newFixedThreadPool(1);
        executorService.submit(tcpFileTransferManger);
    }
}

