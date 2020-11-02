package aoop.asteroids.model;

import java.io.IOException;
import java.net.*;

public class JoinerServer implements Runnable {

    private DatagramSocket socket;
    private byte[] buf = new byte[6550];
    private Game game;
    private InetAddress address;
    private int port;

    /**
     * This computes the JoinerServer which takes in joiners for the multiplayer.
     * @param game
     * @throws SocketException
     * @throws UnknownHostException
     */

    public JoinerServer(Game game) throws SocketException, UnknownHostException {
        this.game = game;
        socket = new DatagramSocket(8888, InetAddress.getByName(this.getCurrentIP(8888)));
    }

    /**
     * In the run method, we first receive the key command from a client such as left or shoot, and update the model. We add the client to the
     * array list if it is a new client. We remove the client if we receive the disconnect message. We then send back the updated model to all
     * the clients.
     */

    @Override
    public void run() {
        while (true) {
            buf = new byte[6550];
            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            try {
                socket.receive(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }

            address = packet.getAddress();  // Create and add a new client.
            port = packet.getPort();

            String str = new String(buf, 0, packet.getLength());
            String[] strings = str.split("@");

            str = strings[0];

            if (str.equals("DISCONNECT")) {
                game = this. removedClient(address);
                continue;
            }

            if (str.equals("CONNECT")) {
                Client newClient = new ClientBuilder().setAddress(address).setPort(port).createClient();
                newClient.getShip().setNickName(strings[1]);
                if (!this.containsClient(newClient)) {
                    game.getClients().add(newClient);
                }
            }

            Client currentClient = findClient(address);
            try {
                if (currentClient != null) {
                    switch (Messages.valueOf(str)) {
                        case UP:
                            currentClient.getShip().setUp(true);
                            break;
                        case LEFT:
                            currentClient.getShip().setLeft(true);
                            break;
                        case RIGHT:
                            currentClient.getShip().setRight(true);
                            break;
                        case SPACE:
                            currentClient.getShip().setIsFiring(true);
                            break;
                        case STOP_UP:
                            currentClient.getShip().setUp(false);
                            break;
                        case STOP_LEFT:
                            currentClient.getShip().setLeft(false);
                            break;
                        case STOP_RIGHT:
                            currentClient.getShip().setRight(false);
                            break;
                        case STOP_SPACE:
                            currentClient.getShip().setIsFiring(false);
                            break;
                    }
                }
            } catch (IllegalArgumentException e) {
                /* Messages.valueOf(str) can throw an exception if message is not an enumeration.
                * catching this exception is part of the program.
                * */
            }

            game.setClientIndex(findClientLocation(currentClient));
            buf = new byte[6550];
            buf = game.getByteArray();
            DatagramPacket packet1 = new DatagramPacket(buf, buf.length, address, port);
            try {
                socket.send(packet1);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Remove a client form the clients list.
     * @param address of the client to be removed
     * @return game with clients list with removed client
     */

    private Game removedClient(InetAddress address) {
        Game returnGame = new Game(game);
        for (Client c : returnGame.getClients()) {
            if (c.getAddress().equals(address)) {
                returnGame.getClients().remove(c);
                return returnGame;
            }
        }
        return returnGame;
    }

    private boolean containsClient(Client client) {
        for (Client c : this.game.getClients()) {
            if (c.getAddress().equals(client.getAddress())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Finds client location in the clients list.
     * @param client
     * @return
     */

    private int findClientLocation(Client client) {
        for (Client c : this.game.getClients()) {
            if (c.getAddress().equals(client.getAddress())) {
                return this.game.getClients().indexOf(c);
            }
        }
        return -1;
    }

    private Client findClient(InetAddress address) {
        for (Client c : this.game.getClients()) {
            if (c.getAddress().equals(address)) {
                return c;
            }
        }
        return null;
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
