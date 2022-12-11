package com.example.graph_editor.client;

import com.example.graph_editor.model.extensions.ExtensionsClient;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Client implements ExtensionsClient {
    private final String ip;
    private Socket socket;
    private DataInputStream socketInput;
    private DataOutputStream socketOutput;
    private static final int bufsize = 4 * 1024;

    public Client(String ip) {
        this.ip = ip;
    }
    public void connect() throws Exception {
        socket = new Socket(ip, 5000);
        socketInput = new DataInputStream(socket.getInputStream());
        socketOutput = new DataOutputStream(socket.getOutputStream());
    }

    // TO DO exception should actually be handled
    public void downloadExtension(File root, String name) throws IOException {
        System.out.println("downloading...");
        socketOutput.writeUTF("get");
        socketOutput.writeUTF(name);
        
        // what to do if the directory already exists?
        File pluginDirectory = new File(root, name);
        pluginDirectory.mkdirs();
        int fileCount = socketInput.readInt();
        for(int i = 0; i < fileCount; i++) {
            String filename = socketInput.readUTF();
            File downloaded = new File(pluginDirectory, filename);
            downloaded.createNewFile();
            FileOutputStream stream = new FileOutputStream(downloaded);
            long size = socketInput.readLong(); // get the size of the file
            byte[] buffer = new byte[Client.bufsize];
            int bytes = 0;
            while(size > 0 && (bytes = socketInput.read(buffer, 0 , (int)Math.min(buffer.length, size))) != -1) {
                stream.write(buffer, 0, bytes);
                size -= bytes;
            }
            stream.close();
        }
    }

    public List<String> getExtensionsList() throws Exception {
        socketOutput.writeUTF("list");
        int numOfPlugins = socketInput.readInt();
        List<String> result = new ArrayList<>(numOfPlugins);
        for(int i = 0; i < numOfPlugins; i++) {
            result.add(socketInput.readUTF());
        }
        System.out.println(result);
        return result;
    }

    public void disconnect() throws IOException {
        // this might not be the best place to do that
        socketOutput.writeUTF("bye");
        socket.close();
        socketInput.close();
        socketOutput.close();
    }

//    public static void main(String[] argv) {
//        try {
//            Client client = new Client("20.0.120.235");
//            client.getList();
//            client.getPlugin("Dummy");
//            client.disconnect();
//        }
//        catch(IOException e) {
//            e.printStackTrace();
//        }
//    }
}
