package aoop.asteroids.model;

import java.io.IOException;
import java.net.*;

/**
 * The spectator connects to a Server thread and spectates the model of the server.
 */

public class Spectator implements Runnable {

    private DatagramSocket socket;
    private InetAddress address;
    private boolean reqDisconnect;
    private byte[] buf;
    private Game game;

    public Spectator(Game game, String ipAddress) throws SocketException, UnknownHostException {
        socket = new DatagramSocket();
        address = InetAddress.getByName(ipAddress);
        this.game = game;
        reqDisconnect = false;
    }

    /**
     * This initializes the connection to the server. The port 8889 is the port to connect to a spectator.
     * @throws IOException
     */

    private void connectionRequest() throws IOException {
        String msg = "MSG";
        buf = msg.getBytes();
        DatagramPacket packet = new DatagramPacket(buf, buf.length, address, 8889);
        socket.send(packet);
    }

    /**
     * This receives the model from the server. This times out if nothing is received in 4 sec.
     * @return game from model
     * @throws IOException
     */

    private Game receiveGameModel() throws IOException {
        socket.setSoTimeout(4000);
        buf = new byte[6550];
        DatagramPacket packet = new DatagramPacket(buf, buf.length);
        socket.receive(packet);
        Game game = new Game();
        game = game.fromByteArray(packet.getData());
        return game;
    }

    /**
     * This constantly sends a connection request to the server and then receives the Game from the server and updates its own Game.
     */

    @Override
    public void run() {
        try {
            while (true) {
                if(reqDisconnect) {
                    this.socket.close();
                    this.socket.setReuseAddress(true);
                    break;
                }
                connectionRequest();
                Game game = receiveGameModel();

                this.game.setPlayer(game.getPlayer());
                this.game.setBullets(game.getBullets());
                this.game.setAsteroids(game.getAsteroids());
                this.game.setClients(game.getClients());
            }
        } catch (IOException e) {
            System.out.println("Server is not found or has stopped hosting. Connection timed out.");
        }
    }

    /**
     * Ends the thread and closes the socket.
     */

    public void end() {
        reqDisconnect = true;
    }
}
