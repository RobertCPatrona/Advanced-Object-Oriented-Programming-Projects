import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;

public class BoxManager implements Runnable {

    /*The managerServerSocket takes in the sockets from incoming boxes and puts them in the ArrayList incoming sockets.
    * The array lists readers and writersToBox contain the readers and the writers to all the boxes connected to the Manager.
    * Whenever a box connects, its socket is added to incomingSockets, and the reader and writer corresponding to the box in their
    * respective array lists.
    * TeleportingCatSocket contains the socket of the cat that is trying to connect to the Manager, in order to receive a random
    * box to teleport to. catReader and catWriter correspond to the teleporting cat and facilitate the transmission of messages
    * between the cat and the Manager.*/

    private ServerSocket managerServerSocket;
    private ArrayList<Socket> incomingSockets;
    private ArrayList<BufferedReader> readers;
    private ArrayList<PrintWriter> writersToBox;

    private ServerSocket teleportingCatSocket;
    private BufferedReader catReader;
    private PrintWriter catWriter;
    private View view;

    public BoxManager(int port, View view) throws IOException {
        this.managerServerSocket = new ServerSocket(port);
        this.incomingSockets = new ArrayList<>();
        this.readers = new ArrayList<>();
        this.writersToBox = new ArrayList<>();
        this.teleportingCatSocket = new ServerSocket(8889);
        this.view = view;
    }

    /*This function is a thread that accepts incoming connections from boxes and creates readers and writers which are used
    * to receive and send messages to boxes.*/

    public synchronized void establishConnectionsWithBoxes() {
        Runnable connectionThread = () -> {
            try {
                while (true) {
                    Socket incomingSocket = managerServerSocket.accept();
                    view.addText("Manager: Accepted connection with box.");

                    incomingSockets.add(incomingSocket);
                    BufferedReader br = new BufferedReader(new InputStreamReader(incomingSocket.getInputStream()));
                    synchronized (readers) {
                        readers.add(br);
                    }
                    PrintWriter write = new PrintWriter(incomingSocket.getOutputStream(), true);
                    writersToBox.add(write);

                    Thread.sleep(500); //The thread sleeps for a bit between accepting connections.
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        };
        new Thread(connectionThread).start();
    }

    /*This is another thread that establishes connections with cats. When a cat want to teleport to a random box,
    * it connects to the Manager and asks for the port of a random box. The Manager then responds to the cat using
    * the giveRandomBoxPort function, which returns the index of a random box port. Then, the cat connects to the
    * random box port corresponding to the index. */

    public synchronized void establishConnectionsWithCats() {
        Runnable connectionThread = () -> {
            try {
                while (true) {
                    Socket catSocket = teleportingCatSocket.accept();
                    view.addText("Manager: Accepted connection with teleporting cat.");

                    catReader = new BufferedReader(new InputStreamReader(catSocket.getInputStream()));

                    catWriter = new PrintWriter(catSocket.getOutputStream(), true);
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }

        };
        new Thread(connectionThread).start();
    }

    /*This function chooses a random box, on which the manager can recharge the cats, or remove the dead ones.*/

    private int randomlyChooseBox() throws IOException {
        Random r2 = new Random();
        int size = incomingSockets.size();
        if (size < 1) {
            size = 1;
        }
        int index = r2.nextInt(size);
        return index;
    }

    /*This gives a random box port to a teleporting cat. The ports of the boxes range from 4444 to 4448.*/

    private synchronized int giveRandomBoxPort() throws IOException {
        Random r2 = new Random();
        int nr = r2.nextInt(5);
        return (4444 + nr);
    }

    /*This thread chooses a random box to recharge every 3 seconds.*/

    public synchronized void rechargeBox() {
        Runnable connectionThread = () -> {
            try {
                while (true) {
                    int index = randomlyChooseBox();

                    Thread.sleep(3000);
                    PrintWriter writer = writersToBox.get(index);
                    writer.println("Manager: Recharging a box.");
                    view.addText("Manager: Recharging a box.");
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }

        };
        new Thread(connectionThread).start();
    }

    /*This thread chooses a random box to clean up, every 4 seconds.*/

    public synchronized void cleanupBox() {
        Runnable connectionThread = () -> {
            try {
                while (true) {
                    int index = randomlyChooseBox();

                    Thread.sleep(4000);
                    writersToBox.get(index).println("Manager: Removing dead cats from a box.");
                    view.addText("Manager: Removing dead cats from a box.");
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        };
        new Thread(connectionThread).start();
    }

    /*This function spawns a cat by the Manager, and starts a new cat thread.*/

    public synchronized int spawnCat() throws IOException {
        int port = giveRandomBoxPort();
        Cat spawnCat = new Cat(9, port, view);
        Thread spawnCatThread = new Thread(spawnCat);
        spawnCatThread.start();
        return port;
    }

    /*In the run function, we start the four threads discussed above, and at a random time, spawns a cat in a random box.
    * If we have a message from a cat in the catReader, we know that the cat is trying to connect to a random box,
    * and so we send a message back a random box port which the cat can teleport to.*/

    @Override
    public synchronized void run() {
        Random r = new Random();
        try {
            establishConnectionsWithBoxes();
            rechargeBox();
            cleanupBox();
            establishConnectionsWithCats();

            while (true) {      //Loop for randomly timed actions
                Thread.sleep(5000);

                int randomInt = r.nextInt(3);
                if (randomInt == 1) {
                    view.addText("Manager: Spawned a cat at box port: " + spawnCat());
                }

                if (catReader != null && catReader.ready()) {
                    String incomingMsg = catReader.readLine();
                    switch (incomingMsg) {
                        case "Cat: Requesting a random box to connect to.":
                            catWriter.println(giveRandomBoxPort());
                            break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
