import java.io.*;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class Client{
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int choose;
        int portNumber;
        String hostName;

        System.out.println("Hello To File Transfer \n" +
                "Choose what you need to do :\n" +
                "1- Start File Transfer \n" +
                "2- Exit ..");
        System.out.print("-> ");
        choose = scanner.nextInt();
        switch (choose)
        {
            case 1 : {
                System.out.print("Enter server port number , (hint : 6677) : ");
                portNumber = scanner.nextInt();
                System.out.print("Enter HostName , (hint : localhost) : ");
                hostName = scanner.next();
                TCPFileTransfer.FileTransferManager tcpFileTransferManger = new TCPFileTransfer.FileTransferManager(TCPFileTransfer.CLIENT_MODE,portNumber,hostName);
                ExecutorService executorService =  Executors.newFixedThreadPool(1);
                executorService.submit(tcpFileTransferManger);
                break;
            }
            case 2 : {
                System.exit(0);
            }
        }

    }
}