//Autor Kamil Pajączkowski

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class Server {
    private ServerSocket serverSocket;
    private List<ClientHandler> clients = new ArrayList<>();

    public Server(int port) {
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Serwer uruchomiony na porcie: " + port);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                ClientHandler clientHandler = new ClientHandler(clientSocket);
                clients.add(clientHandler);
                new Thread(clientHandler).start();
            }
        } catch (IOException e) {
            System.out.println("Błąd serwera: " + e.getMessage());
        } finally {
            try {
                if (serverSocket != null && !serverSocket.isClosed()) {
                    serverSocket.close(); // Zamknięcie serverSocket po zakończeniu
                }
            } catch (IOException e) {
                System.out.println("Błąd przy zamykaniu serwera: " + e.getMessage());
            }
        }
    }

    public void broadcastMessage(String message, ClientHandler excludeClient) {
        for (ClientHandler client : clients) {
            if (client != excludeClient) {
                client.sendMessage(message);
            }
        }
    }

    private class ClientHandler implements Runnable {
        private Socket clientSocket;
        private PrintWriter out;
        private BufferedReader in;

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        @Override
        public void run() {
            try {
                out = new PrintWriter(clientSocket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                String message;
                while ((message = in.readLine()) != null) {
                    System.out.println("Odebrano: " + message);
                    broadcastMessage(message, this);
                }
            } catch (IOException e) {
                System.out.println("Błąd klienta: " + e.getMessage());
            } finally {
                try {
                    if (clientSocket != null && !clientSocket.isClosed()) {
                        clientSocket.close(); // Zamknięcie clientSocket po zakończeniu
                    }
                } catch (IOException e) {
                    System.out.println("Błąd przy zamykaniu połączenia klienta: " + e.getMessage());
                }
            }
        }

        public void sendMessage(String message) {
            out.println(message);
        }
    }

    public static void main(String[] args) {
        new Server(12345); // Port serwera
    }
}
