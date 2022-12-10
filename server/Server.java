import java.net.*;
import java.io.*;

public class Server {
    private ServerSocket serverSocket;
    public static final String pluginsDirectory = "../Plugins";
    public static final int bufsize = 4 * 1024;

    public Server(int port) throws IOException {
        serverSocket = new ServerSocket(port); 
    }

    public void start() throws IOException {
        while(true) {
            // System.out.println("Waiting...");
            new ClientHandler(serverSocket.accept()).start();
        }
    }

    private static class ClientHandler extends Thread {
        private Socket clientSocket;
        private DataInputStream socketInput;
        private DataOutputStream socketOutput;

        public ClientHandler(Socket socket) throws IOException {
            clientSocket = socket;
            socketInput = new DataInputStream(socket.getInputStream());
            socketOutput = new DataOutputStream(socket.getOutputStream());
        }

        private void clear() {
            try {
                clientSocket.close();
                socketInput.close();
                socketOutput.close();
            }
            catch(IOException e) {
                e.printStackTrace();
            }
        }

        private void sendFile(String name) throws IOException {
            File file = new File(name);
            // send the length of file
            socketOutput.writeLong(file.length());
            FileInputStream stream = new FileInputStream(file);

            byte[] buf = new byte[Server.bufsize];
            int bytes = 0;
            while((bytes = stream.read(buf)) != -1) {
                socketOutput.write(buf, 0, bytes);
                socketOutput.flush();
            }
            stream.close();
        }

        private void sendPlugin(String name) throws IOException {
            String fullName = Server.pluginsDirectory + '/' + name;
            File pluginDir = new File(fullName);
            String[] files = pluginDir.list();
            // send the number of files in the plugin directory
            socketOutput.writeInt(files.length);
            for(String filename : files) {
                // send the name of the file
                socketOutput.writeUTF(filename);
                // send the file itself
                sendFile(fullName + '/' + filename);
            }
        }

        private void sendList() throws IOException {
            File dir = new File(Server.pluginsDirectory);
            String[] names = dir.list();
            socketOutput.writeInt(names.length);
            for(String name : names)
                socketOutput.writeUTF(name);
        }

        @Override
        public void run() {
            try {
                while(true) {
                    String query = socketInput.readUTF();
                    if(query.equals("get")) {
                        String pluginName = socketInput.readUTF();
                        sendPlugin(pluginName);
                    }
                    else if(query.equals("list")) {
                        sendList();
                    }
                    else if(query.equals("bye")) {
                        break;
                    } 
                    else {
                        break; // sth went wrong
                    }
                }
                clear();
            }
            catch(IOException e) {
                e.printStackTrace();
            }
            finally {
                clear();
            }
        }
    }

    public static void main(String[] argv) {
        try {
            Server server = new Server(5000);
            server.start();
        }
        catch(IOException e) {
            e.printStackTrace();
        }
    }
}
