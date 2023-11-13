package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.Socket;

public class Client {

    private Socket socket;
    private BufferedReader bufferedReader;

    public Client(Socket socket){
        this.socket = socket;
    }
    public void listeningToServer(){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                String msg = "";
                while (socket.isConnected()) {
                    try {
                        msg = bufferedReader.readLine();
                        System.out.printf("New server notification: %s%n", msg);
                    } catch (IOException e) {
                        try {
                            socket.close();
                            bufferedReader.close();
                        } catch (IOException ex) {
                            throw new RuntimeException(ex);
                        }
                    }
                }
            }
        });
    }
}
