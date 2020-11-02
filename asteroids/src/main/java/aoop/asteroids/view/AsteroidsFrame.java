package aoop.asteroids.view;

import aoop.asteroids.controller.GameModes;
import aoop.asteroids.controller.ClientMoves;
import aoop.asteroids.controller.MenuBar;
import aoop.asteroids.controller.Player;
import aoop.asteroids.model.Game;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.SocketException;
import java.net.UnknownHostException;
import javax.swing.*;

/**
 * AsteroidsFrame is a class that extends JFrame and thus provides a game
 * window for the Asteroids game.
 * It has 4 Buttons, which start a game, host a multiplayer game, spectate a game and join a multiplayer game.
 * Moreover, it contains the controller GameModes and MenuBar classes. GameModes defines the behaviour of the Buttons, while MenuBar
 * defines the behaviour of the Menu items.
 */
public class AsteroidsFrame extends JFrame implements ActionListener {

    public static final long serialVersionUID = 1L;

    private JButton start;
    private JButton hostMP;
    private JButton spectate;
    private JButton joinMP;

    private GameModes gameModes;

    private Game game;

    private AsteroidsPanel ap;

    private MenuBar bar;

    /**
     * This constructor is used to create the frame for the Main Menu.
     */
    public AsteroidsFrame(Game game, Player controller) {
        setAsteroidFrameParameters(game, controller);
        this.gameModes = new GameModes();
        this.ap = new AsteroidsPanel(this.game);
        this.initButtons();
        this.addButtons();
        this.add(ap);
        this.setVisible(true);
    }

    /**
     * This sets the default values for the frame and adds the current player as a key listener.
     */

    private void setAsteroidFrameParameters(Game game, Player controller){
        this.game = game;
        this.setTitle("Asteroids");
        this.setSize(800, 800);
        this.addKeyListener(controller);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    /**
     * This constructor is used to initialize hosting a Server or a hosing a JoinerServer, or a single player game. The Boolean isSinglePlayer
     * checks if the current frame has a single player game, and if so, it will create a menu bar without the menu item "Disconnect".
     */

    public AsteroidsFrame(Game game, Player controller, Boolean isSinglePlayer) {
        setAsteroidFrameParameters(game, controller);
        MenuBar bar = new MenuBar( this, isSinglePlayer);
        this.bar = bar;
        this.setJMenuBar(bar);

        this.ap = new AsteroidsPanel(this.game);
        this.add(ap);
        this.setVisible(true);
    }

    /**
     * This constructor is used for joining a multiplayer game. It also take in ClientMoves, which means that we are able to send the
     * server updates about what keys are pressed on the client side.
     */

    public AsteroidsFrame(Game game, Player controller, ClientMoves clientMoves) {
        setAsteroidFrameParameters(game, controller);
        this.addKeyListener(clientMoves);
        MenuBar bar = new MenuBar( this, null);
        this.bar = bar;
        this.setJMenuBar(bar);
        this.ap = new AsteroidsPanel(this.game);
        this.add(ap);
        this.setVisible(true);
    }

    /**
     * Add the four buttons of the Main Menu.
     */

    private void addButtons() {
        start.addActionListener(this);
        hostMP.addActionListener(this);
        spectate.addActionListener(this);
        joinMP.addActionListener(this);

        ap.add(start);
        ap.add(hostMP);
        ap.add(spectate);
        ap.add(joinMP);
    }

    /**
     * Initializes the buttons.
     */

    private void initButtons() {
        start = new JButton("Start");
        hostMP = new JButton("Host Multi-player");
        spectate = new JButton("Spectate");
        joinMP = new JButton("Join Multi-player");
    }

    /**
     * Quits the old game and starts a new one.
     */
    public void newGame() {
        this.game.abort();
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            System.err.println("Could not sleep before initialing a new game.");
            e.printStackTrace();
        }
        this.game.initGameData();
    }

    /**
     * Calls the initDisconnectJoin method in Game which disconnects this client from the server.
     */

    public void disconnectJoin() {
        this.game.abort();
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            System.err.println("Could not sleep before initialing a new game.");
            e.printStackTrace();
        }
        this.game.initDisconnectJoin();
    }

    public void disconnect() {
        this.game.disconnect();
    }

    /**
     * This closes the current frame, starts a new frame on the Main Menu, and initializes a new Game.
     */

    public void newMenu() throws SocketException, UnknownHostException {
        this.setVisible(false);
        this.dispose();
        Player player = new Player();

        Game game = new Game();
        game.linkController(player);
        new AsteroidsFrame(game, player);
        Thread t = new Thread(game);
        t.start();
    }

    /**
     * Calls the initHostForSpectate method in Game which creates a Server Thread which accepts Spectators.
     */

    public void hostSpectator() {
        game.initHostForSpectate();
    }

    public MenuBar getBar() {
        return bar;
    }

    /**
     * Whenever a button is pressed, the corresponding function from GameModes is called, which then modifies the View and the Model.
     */

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            if (start == e.getSource()) {
                gameModes.startSinglePlayer();
                this.setVisible(false);
                this.dispose();
            } else if (hostMP == e.getSource()) {
                gameModes.hostMultiplayer();
                this.setVisible(false);
                this.dispose();
            } else if (spectate == e.getSource()) {
                gameModes.spectate();
                this.setVisible(false);
                this.dispose();
            } else if (joinMP == e.getSource()) {
                gameModes.joinMultiPlayer();
                this.setVisible(false);
                this.dispose();
            }
        } catch (UnknownHostException e1) {
            JOptionPane.showMessageDialog(null, "Wrong address!", "Error", JOptionPane.ERROR_MESSAGE);
            try {
                this.newMenu();
            } catch (SocketException | UnknownHostException e2) {
                e2.printStackTrace();
            }
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

}
