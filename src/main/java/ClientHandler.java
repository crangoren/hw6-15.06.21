
import java.io.*;
import java.net.Socket;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.*;
import java.util.logging.Logger;

public class ClientHandler implements Runnable{



    private MyServer server;
    private Socket socket;
    private DataInputStream inputStream;
    private DataOutputStream outputStream;
    private LocalDateTime connectTime = LocalDateTime.now();
    private String name;
    private boolean isAuth = false;
    private static Connection connectionHistory;
    private static Statement stmt;
    private HistoryWriter historyDB = new HistoryWriter();

//    File history = new File("history.db");


    private static final Logger logger = Logger.getLogger(ClientHandler.class.getName());



    public String getName() {
        return name;
    }


    public ClientHandler(MyServer server, Socket socket) {

        try {
            this.server = server;
            this.socket = socket;
            this.inputStream = new DataInputStream(socket.getInputStream());
            this.outputStream = new DataOutputStream(socket.getOutputStream());
            this.name = "";
            new Thread(() -> {
                try {
                    authentification();
//                    readMessages();
                    new Client();
                } catch (IOException | SQLException e) {
                    e.printStackTrace();
                } finally {
                    closeConnection();
                }

            }).start();
        } catch (IOException ex) {
            System.out.println("Проблема при создании клиента");
        }
    }



    //    private synchronized void readMessages() throws IOException, SQLException, ClassNotFoundException {
    @Override
    public void run() {

        try {
            DataBaseApp.connect();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        while (true) {
            String messageFromClient = null;
            try {
                messageFromClient = inputStream.readUTF();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                historyDB.saveHistory(name, messageFromClient);
            } catch (IOException e) {
                e.printStackTrace();
            }
//            Client.writeHistory(messageFromClient); //вызов метода записи

            System.out.println("от " + name + ": " + messageFromClient);

            assert messageFromClient != null;
            if (messageFromClient.equals(ChatConstants.STOP_WORD)) {
                return;
            } else if (messageFromClient.startsWith(ChatConstants.SEND_TO_LIST)) {
                String[] splittedStr = messageFromClient.split("\\s+");
                List<String> nicknames = new ArrayList<>();
                for (int i = 1; i < splittedStr.length - 1; i++) {
                    nicknames.add(splittedStr[i]);
                }
            } else if (messageFromClient.startsWith(ChatConstants.CLIENTS_LIST)) {
                server.broadcastClients();
            } else if (messageFromClient.startsWith(ChatConstants.PRIVATE_MESSAGE)){
                String[] targetMessage = messageFromClient.split("\\s+");
                for (int i = 0; i < targetMessage.length; i++) {
                    targetMessage[i] = targetMessage[i].replaceAll("[^\\w]", "");
                }
                List<String> target = Collections.singletonList(targetMessage[1]);
                target.add(name);

                String[] messageArr = messageFromClient.split("\\s+");
                for (int i = 2; i < messageArr.length; i++) {
                    messageArr[i] = messageArr[i].replaceAll("[^\\w]", "");
                }
                String message = Arrays.toString(messageArr);

                server.privateMessage(message, target);

            } else {

                server.broadcastMessage("[" + name + "]: " + messageFromClient);
            }

        }
    }
//    }


    private void authentification() throws IOException, SQLException {


        while (true) {
            String message = inputStream.readUTF();
            if (message.startsWith(ChatConstants.AUTH_COMMAND)) {
                String[] parts = message.split("\\s+");

                Optional<String> nick = server.getAuthService().getNickByLoginAndPass(parts[1], parts[2]);
                if (nick.isPresent()) {

                    if (!server.isNickBusy(nick.get())) {

                        sendMsg(ChatConstants.AUTH_OK + " " + nick);
                        isAuth = true;
                        name = nick.get();
                        server.subscribe(this);
                        server.broadcastMessage(name + " вошел в чат");


//                        DataBaseApp.createHistoryDB();

                        return;
                    } else {
                        sendMsg("Ник уже используется");
                    }
                } else {
                    sendMsg("Неверные логин/пароль");
                }
            }
        }
    }
    boolean isActive() {
        return isAuth;
    }

    public void sendMsg(String message) {
        try {
            outputStream.writeUTF(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void closeConnection() {
        server.unsubscribe(this);
        server.broadcastMessage(name + " вышел из чата");
        try {
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    LocalDateTime getConnectTime() {
        return connectTime;
    }





}
