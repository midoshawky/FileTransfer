import java.io.*;
import java.net.Socket;
import java.util.Scanner;

class Client{
    public static void main(String[]args) throws Exception{
        byte[] fileSize;
        Socket socket = new Socket("localhost",6777);
        InputStream inputStream = socket.getInputStream();
        FileOutputStream fileOutput;
        Scanner scanner = new Scanner(System.in);
        String fileNum = "";
        String[] serverFiles = new String[5];
        PrintWriter printWriter = new PrintWriter(socket.getOutputStream());
        InputStreamReader in = new InputStreamReader(socket.getInputStream());
        BufferedReader bufferedReader = new BufferedReader(in);

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
        fileOutput = new FileOutputStream("/Users/muhammedshawky/IdeaProjects/FileTransfer/src/ClientFolder/"+selectedFileName);
        fileSize = new byte[inputStream.readAllBytes().length];
        inputStream.read(fileSize,0,fileSize.length);

        fileOutput.write(fileSize,0,fileSize.length);

        socket.close();
        fileOutput.close();
    }

    static String getFileName(String[] arr,String num){
        for (var i : arr){
            if(i.startsWith(" "+num)){
                return i.split("-")[1].trim();
            }
        }
        return "";
    }
}