package aoop.asteroids.model;

import java.net.InetAddress;

/**
 * This is the Builder pattern for the Client class.
 */

public class ClientBuilder {
    private InetAddress address;
    private int port;

    public ClientBuilder setAddress(InetAddress address) {
        this.address = address;
        return this;
    }

    public ClientBuilder setPort(int port) {
        this.port = port;
        return this;
    }

    public Client createClient() {
        return new Client(address, port);
    }
}