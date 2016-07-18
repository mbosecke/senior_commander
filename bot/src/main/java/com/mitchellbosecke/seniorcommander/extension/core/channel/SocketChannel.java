package com.mitchellbosecke.seniorcommander.extension.core.channel;

import com.mitchellbosecke.seniorcommander.channel.Channel;
import com.mitchellbosecke.seniorcommander.message.Message;
import com.mitchellbosecke.seniorcommander.message.MessageQueue;
import com.mitchellbosecke.seniorcommander.message.MessageUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

/**
 * Created by mitch_000 on 2016-07-08.
 */
public class SocketChannel implements Channel {

    Logger logger = LoggerFactory.getLogger(getClass());

    private ServerSocket serverSocket;

    private PrintWriter output;

    /**
     * Ensure that either startup or shutdown are performed exclusively.
     */
    private Object startupLock = new Object();

    private volatile boolean running = true;

    private final long id;

    private final Integer port;

    /**
     * How long it blocks while reading from the socket before
     * it temporarily stops to perform maintenance (ex. handle shutdown request).
     */
    private static final int READ_TIMEOUT = 1000;

    public SocketChannel(long id, Integer port) {
        this.id = id;
        this.port = port;
    }

    @Override
    public void listen(MessageQueue messageQueue) throws IOException {
        BufferedReader input = null;

        synchronized (startupLock) {
            if (running) {
                serverSocket = new ServerSocket(port);

                // block until a client connects
                Socket clientSocket = serverSocket.accept();
                clientSocket.setSoTimeout(READ_TIMEOUT);

                output = new PrintWriter(clientSocket.getOutputStream(), true);
                input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                logger.debug("Socket channel started");
            }
        }

        if (input != null) {

            while (true) {
                String inputLine = null;

                try {
                    inputLine = input.readLine();
                } catch (SocketTimeoutException ex) {
                    // do nothing
                }

                if (inputLine != null) {
                    String[] split = MessageUtils.splitRecipient(inputLine);
                    String recipient = split[0];
                    String message = split[1];
                    messageQueue.add(Message.userInput(this, "user", recipient, message, false));
                }
                if (!running) {
                    break;
                }
            }
        }

    }

    @Override
    public void sendMessage(String content) {
        if (running && output != null) {
            output.println(content);
        }
    }

    @Override
    public void sendMessage(String recipient, String content) {
        if (running && output != null) {
            content = String.format("@%s, %s", recipient, content);
            output.println(content);
        }
    }

    @Override
    public void sendWhisper(String recipient, String content) {
        if (running && output != null) {
            content = String.format("/w @%s, %s", recipient, content);
            output.println(content);
        }
    }

    @Override
    public void timeout(String user, long duration) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void shutdown() {
        synchronized (startupLock) {
            running = false;
            if (serverSocket != null) {
                logger.debug("Shutting down socket channel.");
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public long getId() {
        return id;
    }
}

