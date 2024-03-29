package com.example.kanjiapplication;

import com.example.kanjiapplication.threads.MyThreads;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.CountDownLatch;

public class SendToServer extends Thread {

    private static final String SERVER_IP = "10.0.2.2";
    private static final int SERVER_PORT = 3030;
    private CountDownLatch countDownLatch;
    private String image;
    private String listKanji;
    private byte[] bytes;

    public SendToServer(CountDownLatch countDownLatch, String image) {
        this.countDownLatch = countDownLatch;
        this.image = image;
        listKanji = "";
        bytes = new byte[30]; //Max length of kanji list is 10, size one kanji is 3
        this.start();
    }

    public String getKanjiList() { return listKanji; }

    @Override
    public void run() {
        super.run();
        try {
            InetSocketAddress serverAddress = new InetSocketAddress(SERVER_IP, SERVER_PORT);
            Socket socket = new Socket();
            socket.connect(serverAddress, 10000);
            PrintStream outStream = new PrintStream(socket.getOutputStream(), true);
            DataInputStream inStream = new DataInputStream(socket.getInputStream());
            outStream.println(image + 'w');
            inStream.read(bytes);
            outStream.println('0'); //Call to close the socket to the server
            listKanji = new String(bytes, "UTF-8");
            socket.close();
            deleteNull();
            countDownLatch.countDown();
        } catch (UnknownHostException e) {
            countDownLatch.countDown();
            e.printStackTrace();
        } catch (IOException e) {
            countDownLatch.countDown();
            e.printStackTrace();
        }
    }

    private void deleteNull() {
        char[] chars = listKanji.toCharArray();
        listKanji = "";
        for (int i = 0; i < chars.length; i++)
            if (chars[i] != 0)
                listKanji += chars[i];
            else
                break;
    }
}
