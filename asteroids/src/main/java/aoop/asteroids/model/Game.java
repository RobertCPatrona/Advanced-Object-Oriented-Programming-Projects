package aoop.asteroids.model;

import aoop.asteroids.controller.ClientMoves;
import aoop.asteroids.controller.Player;

import java.awt.*;
import java.io.*;
import java.lang.Runnable;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Observable;
import java.util.Random;

/**
 * The game class is the backbone of all simulations of the asteroid game. It
 * contains all game object and keeps track of some other required variables
 * in order to specify game rules.
 * <p>
 * The game rules are as follows:
 * <ul>
 * <li> All game objects are updated according to their own rules every
 * game tick. </li>
 * <li> Every 200th game tick a new asteroid is spawn. An asteroid cannot
 * spawn within a 50 pixel radius of the player. </li>
 * <li> There is a maximum amount of asteroids that are allowed to be
 * active simultaneously. Asteroids that spawn from destroying a
 * larger asteroid do count towards this maximum, but are allowed to
 * spawn if maximum is exceeded. </li>
 * <li> Destroying an asteroid spawns two smaller asteroids. I.e. large
 * asteroids spawn two medium asteroids and medium asteroids spawn two
 * small asteroids upon destruction. </li>
 * <li> The player dies upon colliding with either a buller or an
 * asteroid. </li>
 * <li> Destroying every 5th asteroid increases the asteroid limit by 1,
 * increasing the difficulty. </li>
 * </ul>
 * <p>
 * This class implements Runnable, so all simulations will be run in its own
 * thread. This class extends Observable in order to notify the view element
 * of the program, without keeping a reference to those objects.
 * <p>
 * The main components of Game are : the spaceship of the player, its bullets, the asteroids and a list of all the other clients.
 *
 * @author Yannick Stoffers
 */
public class Game extends Observable implements Runnable, Serializable {

    /**
     * The spaceship of the player.
     */
    private Spaceship ship;

    /**
     * List of bullets.
     */
    private Collection<Bullet> bullets;

    /**
     * List of asteroids.
     */
    private Collection<Asteroid> asteroids;

    /**
     * Random number generator.
     */
    private static Random rng;

    /**
     * Game tick counter for spawning random asteroids.
     */
    private int cycleCounter;

    /**
     * Asteroid limit.
     */
    private int asteroidsLimit;

    /**
     * Indicates whether the a new game is about to be started.
     *
     * @see #run()
     */
    private boolean aborted;

    /**
     * The spectator thread which spectates a Server.
     */

    private transient Spectator spectator;

    /**
     * The joiner thread which joins to a JoinerServer.
     */

    private transient Joiner joiner;

    /**
     * JoinerServer which accepts clients and facilitates the multiplayer.
     */

    private transient JoinerServer joinerServer;

    /**
     * Array List of clients.
     */

    private ArrayList<Client> clients;

    /**
     * Index in the array list of a client.
     */

    private int clientIndex;

    public int getClientIndex() {
        return clientIndex;
    }

    public void setClientIndex(int clientIndex) {
        this.clientIndex = clientIndex;
    }

    /**
     * Initializes a new game from scratch.
     */
    public Game() throws SocketException, UnknownHostException {
        initializeGame();
        this.initGameData();
    }

    /**
     * Creates a new game with a given ship name.
     *
     * @param shipName is the name of the ship of the game
     * @throws SocketException
     * @throws UnknownHostException
     */

    public Game(String shipName) throws SocketException, UnknownHostException {
        initializeGame();
        this.ship.setNickName(shipName);
        this.initGameData();
    }

    /**
     * Copy and create a new Game from the given Game.
     *
     * @param dummyGame A game that is copied.
     */

    public Game(Game dummyGame) {
        this.ship = dummyGame.ship.clone();
        this.bullets = dummyGame.cloneBullets();
        this.asteroids = dummyGame.cloneAsteroids();
        this.clients = dummyGame.cloneClients();
    }

    /**
     * Initializes the rng, ship and joiner thread of this game.
     *
     * @throws UnknownHostException
     * @throws SocketException
     */

    private void initializeGame() throws UnknownHostException, SocketException {
        Game.rng = new Random();
        this.ship = new Spaceship();
        this.joiner = new Joiner(this);
    }

    /**
     * Sets all game data to hold the values of a new game.
     */
    public void initGameData() {
        this.aborted = false;
        this.cycleCounter = 0;
        this.asteroidsLimit = 7;
        this.bullets = new ArrayList<>();
        this.asteroids = new ArrayList<>();
        this.ship.reinit();
        this.clients = new ArrayList<>();
    }

    /**
     * Links the given controller to the spaceship.
     *
     * @param p the controller that is supposed to control the spaceship.
     */
    public void linkController(Player p) {
        p.addShip(this.ship);
    }

    public void linkClientMoves(ClientMoves clientMoves) {
        clientMoves.addJoiner(this.joiner);
    }

    /**
     * Returns a clone of the spaceship, preserving encapsulation.
     *
     * @return a clone the spaceship.
     */
    public Spaceship getPlayer() {
        return this.ship.clone();
    }

    /**
     * Returns a clone of the asteroid set, preserving encapsulation.
     *
     * @return a clone of the asteroid set.
     */
    public Collection<Asteroid> getAsteroids() {
        Collection<Asteroid> c = new ArrayList<>();
        for (Asteroid a : this.asteroids) c.add(a.clone());
        return c;
    }

    /**
     * Used to compute the copied Game from a dummy Game.
     *
     * @return a copied list of asteroids
     */

    public Collection<Asteroid> cloneAsteroids() {
        Collection<Asteroid> newList = new ArrayList<>();
        for (Asteroid asteroid : this.asteroids) {
            Asteroid newAsteroid = asteroid.clone();
            newList.add(newAsteroid);
        }
        return newList;
    }

    /**
     * Returns a clone of the bullet set, preserving encapsulation.
     *
     * @return a clone of the bullet set.
     */
    public Collection<Bullet> getBullets() {
        Collection<Bullet> c = new ArrayList<>();
        for (Bullet b : this.bullets) c.add(b.clone());
        return c;
    }

    /**
     * Used to compute the copied Game from a dummy Game.
     *
     * @return a copied list of bullets
     */

    public Collection<Bullet> cloneBullets() {
        Collection<Bullet> newList = new ArrayList<>();
        for (Bullet bullet : this.bullets) {
            Bullet newBullet = bullet.clone();
            newList.add(newBullet);
        }
        return newList;
    }

    /**
     * This is used to compute the copied Game from a dummy Game.
     *
     * @return a copied list of clients
     */

    public ArrayList<Client> cloneClients() {
        ArrayList<Client> newList = new ArrayList<>();
        for (Client client : this.clients) {
            Client newClient = client.clone();
            newList.add(newClient);
        }
        return newList;
    }

    /**
     * Method invoked at every game tick. It updates all game objects first.
     * Then it adds a bullet if the player is firing. Afterwards it checks all
     * objects for collisions and removes the destroyed objects. Finally the
     * game tick counter is updated and a new asteroid is spawn upon every
     * 200th game tick.
     */
    private void update() {
        for (Asteroid a : this.asteroids) a.nextStep();
        for (Bullet b : this.bullets) b.nextStep();
        if (!this.getPlayer().getNickName().equals(" ")) {
            this.ship.nextStep();
            if (this.ship.isFiring()) {
                double direction = this.ship.getDirection();
                this.bullets.add(new Bullet(this.ship.getLocation(), this.ship.getVelocityX() + Math.sin(direction) * 15, this.ship.getVelocityY() - Math.cos(direction) * 15));
                this.ship.setFired();
            }
        } else {
            if(asteroids.size()<this.asteroidsLimit) {
                for (int i = 0; i < this.asteroidsLimit; i++) {
                    this.addRandomAsteroid();
                }
            }
        }

        this.checkCollisions();
        this.removeDestroyedObjects();

        if (this.cycleCounter == 0 && this.asteroids.size() < this.asteroidsLimit) this.addRandomAsteroid();
        this.cycleCounter++;
        this.cycleCounter %= 200;

        this.setChanged();
        this.notifyObservers();

    }

    /**
     * Does the same as the previous function, but executes the same code for each client in the clients list of the model.
     * The checkCollisions and removeDestroyedObjects functions have been updated to facilitate the interaction between the clients and
     * the asteroids of the model.
     */

    public void updateClients() {
        for (Client client : this.clients) {
            for (Bullet b : client.getBullets()) b.nextStep();
            client.getShip().nextStep();

            if (client.getShip().isFiring()) {
                double direction = client.getShip().getDirection();
                client.getBullets().add(new Bullet(client.getShip().getLocation(), client.getShip().getVelocityX() + Math.sin(direction) * 15, client.getShip().getVelocityY() - Math.cos(direction) * 15));
                client.getShip().setFired();
            }

            this.checkCollisionsClient(client);
            this.removeDestroyedObjectsClient(client);

            this.setChanged();
            this.notifyObservers();
        }
    }

    /**
     * Adds a randomly sized asteroid at least 50 pixels removed from the
     * player.
     */
    private void addRandomAsteroid() {
        int prob = Game.rng.nextInt(3000);
        Point loc, shipLoc = this.ship.getLocation();
        int x, y;
        do {
            loc = new Point(Game.rng.nextInt(800), Game.rng.nextInt(800));
            x = loc.x - shipLoc.x;
            y = loc.y - shipLoc.y;
        } while (Math.sqrt(x * x + y * y) < 50);

        if (prob < 1000)
            this.asteroids.add(new LargeAsteroid(loc, Game.rng.nextDouble() * 6 - 3, Game.rng.nextDouble() * 6 - 3));
        else if (prob < 2000)
            this.asteroids.add(new MediumAsteroid(loc, Game.rng.nextDouble() * 6 - 3, Game.rng.nextDouble() * 6 - 3));
        else this.asteroids.add(new SmallAsteroid(loc, Game.rng.nextDouble() * 6 - 3, Game.rng.nextDouble() * 6 - 3));
    }

    /**
     * Checks all objects for collisions and marks them as destroyed upon
     * collision. All objects can collide with objects of a different type,
     * but not with objects of the same type. I.e. bullets cannot collide with
     * bullets etc.
     */
    private void checkCollisions() { // Destroy all objects that collide.
        for (Bullet b : this.bullets) { // For all bullets.
            for (Asteroid a : this.asteroids) { // Check all bullet/asteroid combinations.
                if (a.collides(b)) { // Collision -> destroy both objects.
                    b.destroy();
                    a.destroy();
                }
            }

            for (Client c : this.clients) {
                if (c.getShip().collides(b)) {
                    b.destroy();
                    c.getShip().destroy();
                }
            }

            if (b.collides(this.ship)) { // Collision with playerß -> destroy both objects
                b.destroy();
                this.ship.destroy();
            }
        }

        for (Asteroid a : this.asteroids) { // For all asteroids, no cross check with bullets required.
            if (a.collides(this.ship)) { // Collision with player -> destroy both objects.
                a.destroy();
                this.ship.destroy();
            }
        }
    }

    /**
     * Takes in a client and computes its interactions with the current model just like the function above.
     *
     * @param client input client
     */

    private void checkCollisionsClient(Client client) { // Destroy all objects that collide.
        for (Bullet b : client.getBullets()) { // For all bullets.
            for (Asteroid a : this.asteroids) { // Check all bullet/asteroid combinations.
                if (a.collides(b)) { // Collision -> destroy both objects.
                    b.destroy();
                    a.destroy();
                }
            }

            for (Client other : this.clients) {
                if (!other.equals(client)) {
                    if (other.getShip().collides(b)) {
                        b.destroy();
                        other.getShip().destroy();
                    }
                }
            }

            if (b.collides(this.ship)) { // Collision -> destroy both objects.
                b.destroy();
                this.ship.destroy();
            }

            if (b.collides(client.getShip())) { // Collision with playerß -> destroy both objects
                b.destroy();
                client.getShip().destroy();
            }
        }

        for (Asteroid a : this.asteroids) { // For all asteroids, no cross check with bullets required.
            if (a.collides(client.getShip())) { // Collision with player -> destroy both objects.
                a.destroy();
                client.getShip().destroy();
            }
        }
    }

    /**
     * Increases the score of the player by one and updates asteroid limit
     * when required.
     */
    private void increaseScore() {
        this.ship.increaseScore();
        if (this.ship.getScore() % 5 == 0) this.asteroidsLimit++;
    }

    /**
     * Removes all destroyed objects. Destroyed asteroids increase the score
     * and spawn two smaller asteroids if it wasn't a small asteroid. New
     * asteroids are faster than their predecessor and travel in opposite
     * direction.
     */
    private void removeDestroyedObjects() {
        Collection<Asteroid> newAsts = new ArrayList<>();
        for (Asteroid a : this.asteroids) {
            if (a.isDestroyed()) {
                this.increaseScore();
                Collection<Asteroid> successors = a.getSuccessors();
                newAsts.addAll(successors);
            } else newAsts.add(a);
        }
        this.asteroids = newAsts;

        Collection<Bullet> newBuls = new ArrayList<>();
        for (Bullet b : this.bullets) if (!b.isDestroyed()) newBuls.add(b);
        this.bullets = newBuls;
    }

    /**
     * Similar to the previous function, it removes objects destroyed by clients and increases the score of the clients.
     *
     * @param client input client
     */

    private void removeDestroyedObjectsClient(Client client) {
        Collection<Asteroid> newAsts = new ArrayList<>();
        for (Asteroid a : this.asteroids) {
            if (a.isDestroyed()) {
                client.getShip().increaseScore();
                Collection<Asteroid> successors = a.getSuccessors();
                newAsts.addAll(successors);
            } else newAsts.add(a);
        }
        this.asteroids = newAsts;

        Collection<Bullet> newBuls = new ArrayList<>();
        for (Bullet b : client.getBullets()) if (!b.isDestroyed()) newBuls.add(b);
        client.setBullets(newBuls);
    }

    private boolean clientShipsAreDestroyed() {
        for (Client c : this.getClients()) {
            if (!c.getShip().isDestroyed()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns whether the game is over. The game is over when the spaceship
     * is destroyed.
     *
     * @return true if game is over, false otherwise.
     */
    public boolean gameOver() {
        return this.ship.isDestroyed() && clientShipsAreDestroyed();
    }

    /**
     * Aborts the game.
     *
     * @see #run()
     */
    public void abort() {
        this.aborted = true;
    }

    /**
     * This method allows this object to run in its own thread, making sure
     * that the same thread will not perform non essential computations for
     * the game. The thread will not stop running until the program is quit.
     * If the game is aborted or the player died, it will wait 100
     * milliseconds before reevaluating and continuing the simulation.
     * <p>
     * While the game is not aborted and the player is still alive, it will
     * measure the time it takes the program to perform a game tick and wait
     * 40 minus execution time milliseconds to do it all over again. This
     * allows the game to update every 40th millisecond, thus keeping a steady
     * 25 frames per second.
     * This updates both the current player model and the interactions with the clients.
     */

    public void run() { // Update -> sleep -> update -> sleep -> etc...
        long executionTime, sleepTime;
        while (true) {
            if (!this.gameOver() && !this.aborted) {
                executionTime = System.currentTimeMillis();
                this.updateClients();
                this.update();
                executionTime -= System.currentTimeMillis();
                sleepTime = Math.max(0, 40 + executionTime);
            } else sleepTime = 100;

            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                System.err.println("Could not perform action: Thread.sleep(...)");
                System.err.println("The thread that needed to sleep is the game thread, responsible for the game loop (update -> wait -> update -> etc).");
                e.printStackTrace();
            }
        }
    }

    public void setPlayer(Spaceship receivedShip) {
        this.ship = receivedShip;
    }

    public void setBullets(Collection<Bullet> bullets) {
        this.bullets = bullets;
    }

    public void setAsteroids(Collection<Asteroid> asteroids) {
        this.asteroids = asteroids;
    }

    public ArrayList<Client> getClients() {
        return this.clients;
    }

    public void setClients(ArrayList<Client> clients) {
        this.clients = clients;
    }

    /**
     * This creates a new Server which other clients connect to in order to spectate. This server sends the server model to all the clients.
     */

    public void initHostForSpectate() {
        try {
            Server server = new Server(this);
            Thread serverThread = new Thread(server);
            serverThread.start();
        } catch (SocketException | UnknownHostException e) {
            e.printStackTrace();
        }
    }

    /**
     * This creates a Spectator thread which connects to a Server in order to receive its model and spectate the server ship.
     *
     * @param ipAddress ip address of the server you need to connect to.
     * @throws SocketException
     * @throws UnknownHostException
     */

    public void initSpectate(String ipAddress) throws SocketException, UnknownHostException {
        initGameData();
        this.spectator = new Spectator(this, ipAddress);
        Thread spectatorThread = new Thread(spectator);
        spectatorThread.start();
    }

    /**
     * This ends the Joiner thread and then creates a new game.
     */

    public void initDisconnectJoin() {
        this.joiner.end();
        int i = 0;
        while (i < 100) {
            i++;
        }
        this.aborted = true;
        initGameData();
    }

    /**
     * This function disconnects this game from any connection, either a spectate or a join connection.
     */

    public void disconnect() {
        if (this.spectator != null) {
            this.spectator.end();
        }
        if (this.joiner != null) {
            this.joiner.end();
        }
    }

    /**
     * This creates a Joiner thread which connects to a JoinServer in order to receive its model and play multiplayer.
     *
     * @param ipAddress ip address of the server you need to connect to.
     * @throws UnknownHostException
     */

    public void initJoin(String ipAddress) throws UnknownHostException {
        initGameData();
        this.joiner.setPacketAddress(ipAddress);
        Thread joinerThread = new Thread(joiner);
        joinerThread.start();
    }

    /**
     * This method creates a JoinerServer thread which receives messages of key inputs from the clients, updates the model,
     * then sends the model to all the clients.
     */

    public void initJoinServer() {
        try {
            this.joinerServer = new JoinerServer(this);
        } catch (SocketException | UnknownHostException e) {
            e.printStackTrace();
        }
        Thread joinerServerThread = new Thread(joinerServer);
        joinerServerThread.start();
    }

    /**
     * Converts this game to a byte array in order to be sent via UDP.
     *
     * @return the resulting byte array.
     */

    public synchronized byte[] getByteArray() {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutput out;
        byte[] returnBytes = new byte[6550];

        try {
            out = new ObjectOutputStream(bos);
            synchronized (out) {
                out.writeObject(this);
                out.flush();
            }
            returnBytes = bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                bos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return returnBytes;
    }

    /**
     * This converts a received byte array into a Game.
     *
     * @param bytes take in a byte array.
     * @return the game extracted from the byte array.
     * @throws SocketException
     * @throws UnknownHostException
     */

    public Game fromByteArray(byte[] bytes) throws SocketException, UnknownHostException {
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        ObjectInput in = null;
        Game game = new Game();
        try {
            in = new ObjectInputStream(bis);
            game = (Game) in.readObject();

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                System.err.println(ex.getMessage());
            }
        }
        return game;
    }

    public String getCurrentIP(int portNumber) {
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
