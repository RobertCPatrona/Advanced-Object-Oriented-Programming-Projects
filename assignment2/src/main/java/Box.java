import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Random;

public class Box implements Runnable {

    /*catSocket contains all the sockets that box uses to connect to the cats inside it.
    * Corresponding to the array list of cat sockets, the box contains readers and writers
    * for receiving and sending messages to its cats. Box also has capacity to which the
    * number of cats inside a box are limited to. If the capacity is zero, cats who try to
    * connect to the box stay in a limbo because they already closed their connection with
    * their previous box. The box connects to its cats through its boxServerSocket and
    * connects to its manager through its managerSocket. Box also contains the View class
    * object to display its status and the messages it exchanges with cats and the box
    * manager.
    * */

    private ArrayList<Socket> catSockets;
    private ArrayList<BufferedReader> readers;
    private ArrayList<PrintWriter> writers;
    private int capacity;
    private ServerSocket boxServerSocket;
    private Socket managerSocket;
    private View view;

    public Box(int capacity, int port, View view) throws IOException {
        this.boxServerSocket = new ServerSocket(port);
        this.managerSocket = new Socket();
        this.catSockets = new ArrayList<>();
        this.readers = new ArrayList<>();
        this.writers = new ArrayList<>();
        this.capacity = capacity;
        this.view = view;
    }

    /* establishConnections() receives incoming connections from cats. It adds
    * the socket connected to cat into the catSockets arrayList and adds the
    * corresponding readers and writers into an arrayList as well.
    * establishConnections only takes a connection if the capacity of box is not
    * zero. establishConnections runs a separate thread that waits for connections
    * with cats.
    * */
    private synchronized void establishConnections() {
        Runnable connectionThread = () -> {
            try {
                while (capacity > 0) {
                    Socket incomingSockets = boxServerSocket.accept();
                    view.addText("BOX: Accepted connection with cat.");

                    catSockets.add(incomingSockets);
                    BufferedReader br = new BufferedReader(new InputStreamReader(incomingSockets.getInputStream()));
                    synchronized (readers) {
                        readers.add(br);
                    }
                    PrintWriter write = new PrintWriter(incomingSockets.getOutputStream(), true);
                    writers.add(write);
                    this.capacity--;
                    view.addText("Box " + boxServerSocket.getLocalPort() + " now has " + this.capacity + " capacity");
                    Thread.sleep(50);
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        };
        new Thread(connectionThread).start();
    }

    /*rechargeCats sends message to all the cats that they are
    * being recharged. This is called when the manager decides
    * to recharge cats.
    * */
    public synchronized void rechargeCats() {
        synchronized (writers) {
            for (PrintWriter write : writers) {
                write.println("Box: Manager recharging cats.");
            }
            view.addText("Box: Manager recharging cats.");
        }
    }

    /*cleanupCats sends message to all the cats that they are
    * being removed if they are broken. This is called when the manager
    * decides to clean up cats.
    * */
    public synchronized void cleanupCats() {

        for (PrintWriter write : writers) {
            write.println("Box: Manager cleaning box.");
        }
        view.addText("Box: Manager cleaning box.");
    }

    /*All the cats inside the box are sent the message that they are being
    * killed and the cats are killed accordingly. The mechanism to kill
    * cats is implemented as a thread that sleeps for a random period of
    * time.
    * */
    public synchronized void killCats() {
        Runnable connectionThread = () -> {
            Random r = new Random();
            try {
                int nr = r.nextInt(5);
                Thread.sleep(10000 + 1000 * nr);
                for (PrintWriter write : writers) {
                    write.println("Box: Starting killing mechanism.");
                }
                view.addText("Box: Starting killing mechanism.");
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        };
        new Thread(connectionThread).start();
    }

    /*When the box thread is started, the box connects to the predefined port of the boxManager (8888)
    * and creates a reader and writer for the connection with the manager. The establishConnections
    * and killCats thread is also started. Box loops through its readers to check for available messages
    * from cats and the manager to perform the required actions corresponding to the given message. The
    * thread for checking message is put to sleep for a small period of time because messages do not
    * arrive very often and rapidly.
    * */

    @Override
    public synchronized void run() {
        Boolean running = true;
        try {

            int managerPort = 8888;
            managerSocket.connect(new InetSocketAddress("localhost", managerPort), 1000); //Change this
            PrintWriter write = new PrintWriter(managerSocket.getOutputStream(), true);
            write.println("Box: Connected to manager.");
            view.addText("Box: Connected to manager.");

            BufferedReader managerBr = new BufferedReader(new InputStreamReader(managerSocket.getInputStream()));
            synchronized (readers) {
                readers.add(managerBr);
            }

            establishConnections();
            killCats();

            while (running) {
                synchronized (readers) {
                    for (BufferedReader br : readers) {
                        if (br.ready()) {
                            String incomingMsg = br.readLine();

                            switch (incomingMsg) {
                                case "Manager: Recharging a box.":
                                    rechargeCats();
                                    break;

                                case "Manager: Removing dead cats from a box.":
                                    cleanupCats();
                                    break;
                            }
                        }
                    }
                    Thread.sleep(50);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
