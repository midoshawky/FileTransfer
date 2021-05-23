import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import javax.swing.JFileChooser;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TCPFileTransfer {
    private static final int SERVER_MODE = 0;
    private static final int CLIENT_MODE = 1;
    private static final String[] files = {
            "/Users/muhammedshawky/IdeaProjects/FileTransfer/src/ServerFolder/file2.txt",
            "/Users/muhammedshawky/IdeaProjects/FileTransfer/src/ServerFolder/file3.txt",
            "/Users/muhammedshawky/IdeaProjects/FileTransfer/src/ServerFolder/hello.txt",
    };

    public static void main(String[] args) throws Exception{

        ExecutorService executorService = Executors.newFixedThreadPool(2);

        FileTransferManager fileTransferManagerServer = new FileTransferManager(SERVER_MODE);
        FileTransferManager fileTransferManagerClient = new FileTransferManager(CLIENT_MODE);

        executorService.submit(fileTransferManagerServer);
        executorService.submit(fileTransferManagerClient);
    }


    static class FileTransferManager implements Runnable{
        int side;
        FileTransferManager(int side) {
            this.side = side;
        }

        @Override
        public void run() {
            switch (side)
            {
                case SERVER_MODE : {
                    try {
                        initServerSide();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                }
                case CLIENT_MODE : {
                    try {
                        initClientSide();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

        }

        private  void initClientSide() throws Exception{
            Socket socket = new Socket("localhost",6777);
            PrintWriter printWriter = new PrintWriter(socket.getOutputStream());
            InputStreamReader in = new InputStreamReader(socket.getInputStream());
            BufferedReader bufferedReader = new BufferedReader(in);
            InputStream inputStream = socket.getInputStream();
            String[] serverFiles = new String[5];
            byte[] fileSize;
            String fileNum = "";
            Scanner scanner = new Scanner(System.in);

            for(int i =  0 ; i < 5 ; i++){
                String msg = bufferedReader.readLine();
                System.out.println(msg);
                serverFiles[i] = msg;
            }


            System.out.print("Enter file number : ");
            fileNum = scanner.next();
            printWriter.println(fileNum);
            printWriter.flush();

            String selectedFileName = getFileName(serverFiles,fileNum);
            String selectedFilePath = fileChoose(selectedFileName);
            FileOutputStream fileOutput = new FileOutputStream(selectedFilePath);
            fileSize = new byte[inputStream.readAllBytes().length];
            inputStream.read(fileSize,0,fileSize.length);

            fileOutput.write(fileSize,0,fileSize.length);

            socket.close();
            fileOutput.close();
        }


        private  void initServerSide() throws Exception{

            ServerSocket serverSocket = new ServerSocket(6777);

            while (true){
                Socket socket = serverSocket.accept();
                PrintWriter printWriter = new PrintWriter(socket.getOutputStream());

                int fileIndex = 0;
                printWriter.println(" Hello File Transfer Server is here ..  ");
                printWriter.println(" Choose which file you need to download ");

                for(var i : files){
                    File f = new File(i);
                    printWriter.println(" " + fileIndex + "- " + f.getName() + " -- " + f.length() + " kb ");
                    fileIndex++;
                    printWriter.flush();
                }

                InputStreamReader in = new InputStreamReader(socket.getInputStream());
                BufferedReader bufferedReader = new BufferedReader(in);
                String msg = bufferedReader.readLine();
                int fileNum = Integer.parseInt(msg);
                System.out.println("Selected File  :" + getFileName(files[fileNum]));
                //Thread.sleep(2000);
                sendFile(files[fileNum],printWriter,socket);

                //ClosingStreams
            }
        }

        private  void sendFile(String filePath,PrintWriter printWriter,Socket socket) throws Exception{
            File f = new File(filePath);
            long fileSize = f.length();
            printWriter.println(" File :" + f.getName() + " Downloading .. ");
            byte[] fileBytes = new byte[(int)fileSize];
            FileInputStream fileInputStream = new FileInputStream(filePath);
            fileInputStream.read(fileBytes,0,fileBytes.length);
            OutputStream os = socket.getOutputStream();
            os.write(fileBytes,0,fileBytes.length);

            //serverSocket.close();
            fileInputStream.close();
        }


        private  String getFileName(String[] serverFiles, String fileNum) {
            for (var i : serverFiles){
                if(i.startsWith(" "+fileNum)){
                    return i.split("-")[1].trim();
                }
            }
            return "";
        }

        private  String getFileName(String  filePath) {
            File file = new File(filePath);
            return file.getName();
        }

        private  Long getFileSize(String  filePath) {
            File file = new File(filePath);
            return file.length();
        }

        private String fileChoose(String selectedFileName){
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
            File selectedFile = new File(selectedFileName);
            fileChooser.setSelectedFile(selectedFile);
            int returnVal = fileChooser.showSaveDialog(null);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File fileToSave = fileChooser.getSelectedFile();
                return fileToSave.getPath();
            }
            return "";
        }

    }

}
