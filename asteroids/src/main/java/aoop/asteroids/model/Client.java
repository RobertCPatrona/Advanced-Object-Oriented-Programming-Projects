package aoop.asteroids.model;

import java.io.Serializable;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collection;

/**
 * The Client represents one player. It contain a spaceship, its collection of bullets, and the address and port of the player corresponding to that ship.
 */

public class Client implements Serializable {

    private Spaceship ship;
    private Collection<Bullet> bullets;
    private InetAddress address;
    private int port;

    public Client(InetAddress address, int port) {
        this.ship = new Spaceship();
        this.bullets = new ArrayList<>();
        this.address = address;
        this.port = port;
    }

    public Spaceship getShip() {
        return ship;
    }

    public Collection<Bullet> getBullets() {
        return bullets;
    }

    public InetAddress getAddress() {
        return address;
    }

    public int getPort() {
        return port;
    }

    public void setShip(Spaceship ship) {
        this.ship = ship;
    }

    public void setBullets(Collection<Bullet> bullets) {
        this.bullets = bullets;
    }
    /**
     * Creates an exact clone of this spaceship.
     */
    public Client clone() {
        Client returnClone = new ClientBuilder().setAddress(this.getAddress()).setPort(this.getPort()).createClient();
        returnClone.setShip(this.ship);
        returnClone.setBullets(this.bullets);
        return returnClone;
    }
}
