package aoop.asteroids.model;

import java.io.IOException;
import java.net.*;

public class Joiner implements Runnable {
    private DatagramSocket socket;
    private byte[] buf = new byte[6550];
    private Game game;
    private boolean reqDisconnect;
    private DatagramPacket packet;

    /**
     * This implements the Joiner thread. This represents the client that connects to the JoinerServer in order to play multiplayer.
     * The client sends the key press commands such as up, left or space to the server. The server then updates the model and sends
     * it back to all the clients.
     * @param game
     * @throws UnknownHostException
     * @throws SocketException
     */

    public Joiner(Game game) throws UnknownHostException, SocketException {
        socket = new DatagramSocket();
        this.game = game;
        this.packet = new DatagramPacket(buf, buf.length, InetAddress.getByName(this.getCurrentIP(8888)), 8888);
        this.reqDisconnect = false;
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

    public void setPacketAddress(String ipAddress) throws UnknownHostException {
        this.packet.setAddress(InetAddress.getByName(ipAddress));
    }

    /**
     * This sends the key command of this client to the server, such as up or shoot.
     * @param str The current move of the ship in String form.
     * @throws IOException
     */

    public void sendControl(String str) throws IOException {
        buf = new byte[6550];
        buf = str.getBytes();
        this.packet.setData(buf, 0, buf.length);
        socket.send(packet);
    }

    /**
     * This connects the client to the server. It sends the message connect and the name of the ship of this client.
     * @throws IOException
     */

    private void sendConnect() throws IOException {
        String str = Messages.CONNECT + "@" + game.getPlayer().getNickName();
        buf = new byte[6550];
        buf = str.getBytes();
        this.packet.setData(buf, 0, buf.length);
        socket.send(packet);
    }

    /**
     * Sends an empty message to force the server to constantly send the model to this client.
     * @throws IOException
     */

    private void sendEmpty() throws IOException {
        String str = "";
        buf = new byte[6550];
        buf = str.getBytes();
        this.packet.setData(buf, 0, buf.length);
        socket.send(packet);
    }

    /**
     * This sends the request for a disconnect from the JoinerServer.
     * @throws IOException
     */

    private void sendDisconnect() throws IOException {
        String str = Messages.DISCONNECT.toString();
        buf = new byte[6550];
        buf = str.getBytes();
        this.packet.setData(buf, 0, buf.length);
        socket.send(packet);
    }

    /**
     * This receives the updated model or Game from the JoinerServer.
     * @return the Game received from the server.
     * @throws IOException
     */

    private Game receiveServerGame() throws IOException {
        socket.setSoTimeout(5000);
        buf = new byte[6550];
        DatagramPacket packet = new DatagramPacket(buf, buf.length);
        socket.receive(packet);
        Game game = new Game();
        game = game.fromByteArray(packet.getData());
        return game;
    }

    /**
     * This is the behaviour of the client. If the flag is true, we send a connect request and set the flag to false.
     * Then, we check if reqDisconnect is true, and if it's not, we send the empty message. We send this message in every iteration
     * of the while loop to force the Server to constantly send back the updated model. We then add the received server spaceship
     * as a client to our clients list and update our model.
     * The port 8888 is the port for connecting to a JoinerServer.
     */

    @Override
    public void run() {
        boolean flag = true;
        try {
            while (true) {

                if(flag) {
                    sendConnect();
                    flag = false;
                }

                if (reqDisconnect) {
                    sendDisconnect();
                    this.socket.close();
                    this.socket.setReuseAddress(true);
                    break;
                }
                sendEmpty();

                Game receiveServerGame = receiveServerGame();
                this.game.setAsteroids(receiveServerGame.getAsteroids());

                int indexOfThisClient = receiveServerGame.getClientIndex();
                this.game.setPlayer(receiveServerGame.getClients().get(indexOfThisClient).getShip());
                this.game.setBullets(receiveServerGame.getClients().get(indexOfThisClient).getBullets());
                receiveServerGame.getClients().remove(indexOfThisClient);

                Client hostSpaceShip = new ClientBuilder().setAddress(InetAddress.getByName("localhost")).setPort(8888).createClient();
                hostSpaceShip.setShip(receiveServerGame.getPlayer());
                hostSpaceShip.setBullets(receiveServerGame.getBullets());

                receiveServerGame.getClients().add(hostSpaceShip);
                this.game.setClients(receiveServerGame.getClients());
            }
        } catch (IndexOutOfBoundsException | IOException e) {
            System.out.println("Connection with host timeout. Server is not found or has disconnected.");
            System.exit(0);

        }
    }

    /**
     * This ends the thread by closing and clearing the socket.
     */

    public void end() {
        reqDisconnect = true;
    }
}
