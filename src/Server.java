import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private static FileInputStream fileInputStream;
    private static ServerSocket serverSocket;
    private static Socket socket;
    private static  PrintWriter printWriter;

    private static final String[] files = {
            "/Users/muhammedshawky/IdeaProjects/FileTransfer/src/ServerFolder/file2.txt",
            "/Users/muhammedshawky/IdeaProjects/FileTransfer/src/ServerFolder/file3.txt",
            "/Users/muhammedshawky/IdeaProjects/FileTransfer/src/ServerFolder/hello.txt",
    };

    public static void main(String[] args) throws Exception{
        //FilesListS



        serverSocket= new ServerSocket(6777);

        while (true){
            socket = serverSocket.accept();
            printWriter = new PrintWriter(socket.getOutputStream());

            int fileIndex = 1;
            printWriter.println(" Hello File Transfer Server is here ..  ");
            printWriter.println(" Choose which file you need to download ");

            for(var i : files){
                File f = new File(i);
                printWriter.println(" " + fileIndex + "- " + f.getName() + " -- " + f.length() + " kb ");
                fileIndex++;
                printWriter.flush();
            }

            System.out.println("Waiting for client response ..");

            InputStreamReader in = new InputStreamReader(socket.getInputStream());
            BufferedReader bufferedReader = new BufferedReader(in);
            String msg = bufferedReader.readLine();
            int fileNum = Integer.parseInt(msg);
            System.out.println("Selected File  :" + getFileName(files[fileNum]));
            //Thread.sleep(2000);
            sendFile(files[fileNum]);

            //ClosingStreams
        }

    }

    private static void sendFile(String filePath) throws Exception{
        File f = new File(filePath);
        long fileSize = f.length();
        printWriter.println(" File :" + f.getName() + " Downloading .. ");
        byte[] fileBytes = new byte[(int)fileSize];
        fileInputStream = new FileInputStream(filePath);
        fileInputStream.read(fileBytes,0,fileBytes.length);
        OutputStream os = socket.getOutputStream();
        os.write(fileBytes,0,fileBytes.length);

        //serverSocket.close();
        //fileInputStream.close();
    }


    private static String getFileName(String  filePath) {
        File file = new File(filePath);
        return file.getName();
    }

    private static Long getFileSize(String  filePath) {
        File file = new File(filePath);
        return file.length();
    }
}
