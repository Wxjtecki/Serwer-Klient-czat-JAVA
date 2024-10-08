//Autor Kamil Pajączkowski
import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    public Client(String address, int port) {
        try {
            socket = new Socket(address, port);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            new Thread(new ReceiveMessage()).start(); // Uruchomienie wątku odbierającego wiadomości

            Scanner scanner = new Scanner(System.in);
            try {
                while (true) {
                    System.out.print("Ty: ");
                    String message = scanner.nextLine();
                    sendMessage(message);
                }
            } finally {
                scanner.close(); // Zamknięcie skanera po zakończeniu jego użytkowania
            }
        } catch (IOException e) {
            System.out.println("Błąd połączenia: " + e.getMessage());
        }
    }

    public void sendMessage(String message) {
        out.println(message);
    }

    public class ReceiveMessage implements Runnable {
        @Override
        public void run() {
            try {
                String message;
                while ((message = in.readLine()) != null) {
                    System.out.println("Wiadomość: " + message);
                }
            } catch (IOException e) {
                System.out.println("Błąd podczas odbierania wiadomości: " + e.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        new Client("localhost", 12345); // Adres IP i port serwera
    }
}
