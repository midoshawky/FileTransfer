import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import javax.swing.JFileChooser;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TCPFileTransfer {
    static final int SERVER_MODE = 0;
    static final int CLIENT_MODE = 1;
    private static final String BYE_MSG = "Okay bye ..";

    static class FileTransferManager implements Runnable{
        private int side;
        private int portNumber;
        private String host;
        FileTransferManager(int side,int portNumber) {
            this.side = side;
            this.portNumber = portNumber;
        }
        FileTransferManager(int side,int portNumber,String host) {
            this.side = side;
            this.portNumber = portNumber;
            this.host = host;
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
            Socket socket = new Socket(host,portNumber);
            PrintWriter printWriter = new PrintWriter(socket.getOutputStream());
            InputStreamReader in = new InputStreamReader(socket.getInputStream());
            BufferedReader bufferedReader = new BufferedReader(in);
            InputStream inputStream = socket.getInputStream();
            int count;
            byte[] fileSize  = new byte[8192];
            String choose = "";
            Scanner scanner = new Scanner(System.in);

            String msg = bufferedReader.readLine();
            System.out.println(msg);

            System.out.print("Choose : ");
            choose = scanner.next();
            printWriter.println(choose);
            printWriter.flush();

            String filePath  = fileSave("Text.txt");
            printWriter.println(filePath);
            printWriter.flush();
            inputStream = socket.getInputStream();
            String serverResponse = bufferedReader.readLine();

            System.out.println(serverResponse);

            if(serverResponse.equals(BYE_MSG))
            {
                socket.close();
            }else
            {
                FileOutputStream fileOutput = new FileOutputStream(filePath);
                while ((count = inputStream.read(fileSize)) > 0)
                {
                    fileOutput.write(fileSize,0,count);
                    if(count == (int)fileOutput.getChannel().size())
                    {
                        System.out.println("File Downloaded Successfully.");
                        break;
                    }
                }
                fileOutput.close();
            }

        }


        private  void initServerSide() throws Exception{

            ServerSocket serverSocket = new ServerSocket(portNumber);

            while (true){
                Socket socket = serverSocket.accept();
                PrintWriter printWriter = new PrintWriter(socket.getOutputStream());
                InputStreamReader in = new InputStreamReader(socket.getInputStream());
                File selectedFile = fileChoose();


                assert selectedFile != null;
                printWriter.println("You want to download this file : " + selectedFile.getName() + " -- " + selectedFile.length() + "KB  ? [Y/N]");
                printWriter.flush();

                BufferedReader bufferedReader = new BufferedReader(in);
                String msg = bufferedReader.readLine();
                System.out.println("Client :" + msg);
                if(msg.contains("Y") || msg.contains("y"))
                {
                    String locationResponse = bufferedReader.readLine();
                    System.out.println("File Path : " + locationResponse);
                    printWriter.println("File is sending .. ");
                    printWriter.flush();
                    sendFile(selectedFile.getPath(),socket);
                }else{
                    printWriter.println("Okay bye ..");
                    printWriter.flush();
                }
                //ClosingStreams
            }
        }

        private  void sendFile(String filePath,Socket socket) throws Exception{
            File f = new File(filePath);
            long fileSize = f.length();
            System.out.println(" File :" + f.getName() + " Sending .. ");
            byte[] fileBytes = new byte[(int)fileSize];
            FileInputStream fileInputStream = new FileInputStream(filePath);
            fileInputStream.read(fileBytes,0,fileBytes.length);
            OutputStream os = socket.getOutputStream();

            os.write(fileBytes,0,fileBytes.length);
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

        private String fileSave(String selectedFileName){
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
            File selectedFile = new File(selectedFileName);
            fileChooser.setSelectedFile(selectedFile);
            int returnVal = fileChooser.showDialog(null,"Save Here");
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File fileToSave = fileChooser.getCurrentDirectory();
                return fileToSave.getPath() +"/"+ selectedFileName;
            }
            return "";
        }

        private File fileChoose(){
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
            int returnVal = fileChooser.showDialog(null,"Select");
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                return fileChooser.getSelectedFile();
            }
            return null;
        }

    }

}
