import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Random;

public class Cat implements Runnable {

    /*lives denotes how many lives a cat has.
    * boxPort denotes which box the cat connects to.
    * view is used to display the updates and messages exchanged by cat.
    * */

    private int lives;
    private int boxPort;
    private View view;

    public Cat(int lives, int boxPort, View view) {
        this.lives = lives;
        this.boxPort = boxPort;
        this.view = view;
    }

    /*teleport is a function that is used to teleport cats to different boxes. Since only the manager
    * has access to all the boxes, the cat has to establish a special connection with the manager to
    * receive a box port for teleportation. The manager port for teleportation is 8889. If the cat is not
    * broken, it will teleport to different boxes at random times. The thread for teleport is slept for
    * random periods of time to randomize the teleportation.
    * */
    public synchronized void teleport(Socket catToBoxSocket) throws IOException {

        Socket catToManagerSocket = new Socket();
        int managerPortForTeleport = 8889;
        catToManagerSocket.connect(new InetSocketAddress(managerPortForTeleport));
        PrintWriter writeToManager = new PrintWriter(catToManagerSocket.getOutputStream(), true);
        BufferedReader brToManager = new BufferedReader(new InputStreamReader(catToManagerSocket.getInputStream()));

        Runnable connectionThread = () -> {
            try {
                Random r = new Random();
                while (true) {
                    int i = r.nextInt(5);
                    Thread.sleep(15000 + i * 1000);

                    writeToManager.println("Cat: Requesting a random box to connect to.");
                    view.addText("Cat: Requesting a random box to connect to.");
                    if (brToManager.ready()) {
                        String randomBoxPort = brToManager.readLine();
                        catToBoxSocket.close();

                        if (lives > 0) {
                            catToBoxSocket.connect(new InetSocketAddress("localhost", Integer.parseInt(randomBoxPort)), 1000);
                            boxPort = Integer.parseInt(randomBoxPort);
                            view.addText("Cat: connected to random box:" + randomBoxPort);
                        }

                        this.lives--;
                        view.addText("Cat: I have this many lives: " + lives);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
        new Thread(connectionThread).start();
    }

    /*cloneCat() contains a thread that is slept for random periods of time and
    * when it is awake it creates a new cat and starts the cat thread. The box checks
    * for its capacity when connecting the cloned cat.
    * */
    public synchronized void cloneCat() {
        Runnable connectionThread = () -> {
            try {
                Random r = new Random();
                int i = r.nextInt(3);
                Thread.sleep(1000 + i * 1000);
                Cat clonedCat = new Cat(9, this.boxPort, this.view);
                Thread catCloneThread = new Thread(clonedCat);
                catCloneThread.start();
                view.addText("Cat: I cloned myself.");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };
        new Thread(connectionThread).start();
    }

    public Boolean isBroken() {
        if (lives > 0) {
            return false;
        }
        return true;
    }

    /*cat is simply slept for 1 second. purr() is called at random times in run().*/
    public void purr() throws InterruptedException {
        Thread.sleep(1000);
        view.addText("Cat: Purr I am sleeping.");
    }

    /*In run() cat connects to its delegated box and makes a reader and writer for
    * that connection. And then the thread for teleportation is started. Cats sleep
    * and teleport at random times.
    *
    * If no messages arrive from the corresponding box the cat thread sleeps for 0.5 seconds.
    * The incoming messages from the box are checked with a switch statement and the corresponding
    * action is performed.
    * */
    @Override
    public synchronized void run() {

        try {
            Socket catToBoxSocket = new Socket();

            catToBoxSocket.connect(new InetSocketAddress("localhost", boxPort), 1000);
            PrintWriter writeToBox = new PrintWriter(catToBoxSocket.getOutputStream(), true);
            BufferedReader brToBox = new BufferedReader(new InputStreamReader(catToBoxSocket.getInputStream()));
            writeToBox.println("Cat: Meow I'm a cat connected to box port " + boxPort);
            view.addText("Cat: Meow I'm a cat connected to box port " + boxPort);

            teleport(catToBoxSocket);

            Random r = new Random();

            while (true) {

                int randNum = r.nextInt(10);
                if (randNum > 6) {                   // cats cloned at random times
                    cloneCat();
                }
                if (randNum == 3) {                  // cats sleep at random times
                    purr();
                }

                while (!brToBox.ready()) {
                    Thread.sleep(500);
                }
                String incomingMsg = brToBox.readLine();

                switch (incomingMsg) {
                    case "Box: Manager recharging cats.":
                        if (!isBroken()) {
                            lives = 9;
                            view.addText("Cat: fully recharged");
                        }
                        break;
                    case "Box: Starting killing mechanism.":
                        this.lives = 0;
                        view.addText("Cat: I have died.");
                        catToBoxSocket.close();
                        break;
                    case "Box: Manager cleaning box.":
                        if (this.isBroken()) {
                            view.addText("Cat: is removed.");
                            catToBoxSocket.close();
                        }
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
