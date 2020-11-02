package aoop.asteroids.model;

import java.io.IOException;
import java.net.*;

/**
 * This implements the server which hosts other spectators. It constantly sends the model to the Spectators.
 */

public class Server implements Runnable {

    private DatagramSocket socket;
    private boolean running;
    private byte[] buf = new byte[6550];
    private Game game;

    public Server(Game game) throws SocketException, UnknownHostException {
        this.game = game;
        socket = new DatagramSocket(8889, InetAddress.getByName(this.getCurrentIP(8889)));
    }

    /**
     * In the run method, we constantly respond to the incoming messages by the clients by sending back the model. So,
     * the clients constantly request the model and we send it to them.
     */

    @Override
    public void run() {
        try {
            running = true;
            while (running) {

                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);
                InetAddress address = packet.getAddress();
                int port = packet.getPort();

                buf = new byte[6550];
                buf = game.getByteArray();
                DatagramPacket packet1 = new DatagramPacket(buf, buf.length, address, port);

                socket.send(packet1);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getCurrentIP(int portNumber) {
        String IP = null;

        try (final DatagramSocket socket = new DatagramSocket()) {
            socket.connect(InetAddress.getByName("8.8.8.8"), portNumber);
            IP = socket.getLocalAddress().getHostAddress();
        } catch (SocketException | UnknownHostException e) {
            e.printStackTrace();
        }

        return IP;
    }
}
