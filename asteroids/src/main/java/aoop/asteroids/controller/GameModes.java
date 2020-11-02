package aoop.asteroids.controller;

import aoop.asteroids.model.Game;
import aoop.asteroids.view.AsteroidsFrame;

import javax.swing.*;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * This is a controller class which define how the buttons behave.
 */

public class GameModes {

    private AsteroidsFrame frame;
    private Game newGame;

    public void initializeGame (Boolean isSinglePlayer) throws SocketException, UnknownHostException {
        Player player = new Player();
        String shipName = JOptionPane.showInputDialog("Enter name");
        while(shipName==null || shipName.equals("") || shipName.equals(" ")) {
            shipName = JOptionPane.showInputDialog("Enter name");
        }
        this.newGame = new Game(shipName);
        newGame.linkController(player);
        this.frame = new AsteroidsFrame(newGame, player, isSinglePlayer);
        Thread t = new Thread(newGame);
        t.start();
    }

    /**
     * Upon pressing the Start button, we will ask for an input for the name of the ship and check for the validity of the input.
     * Then, we create a new Game, a new single player Frame and start the Game thread. We disable the Disconnect buttons since you
     * cannot disconnect from a single player game.
     */

    public void startSinglePlayer() throws SocketException, UnknownHostException {
        initializeGame(true);

        frame.getBar().getDisconnectSpectateAction().setEnabled(false);
        frame.getBar().getDisconnectJoinAction().setEnabled(false);
    }

    /**
     * Upon pressing the Host Multi-player button, we create a new Game and ask for the name of the player ship. Then we make a new
     * Multi player frame, then start the JoinerServer and Game threads. Finally, we set the Host Spectating action to false since we
     * are hosting a JoinerServer.
     */

    public void hostMultiplayer() throws SocketException, UnknownHostException {
        initializeGame(false);

        newGame.initJoinServer();
        frame.getBar().getDisconnectJoinAction().setEnabled(false);
        frame.getBar().getDisconnectSpectateAction().setEnabled(false);
    }

    /**
     * When pressing the Spectate button, we make a new Game, ask for the player name, start a Spectator thread, and
     * set the New Game and Host Spectate actions to false.
     */

    public void spectate() throws SocketException, UnknownHostException {
        Game newGame = new Game();
        AsteroidsFrame frame = new AsteroidsFrame(newGame, null, false);
        Thread t = new Thread(newGame);
        t.start();
        String ipAddress = JOptionPane.showInputDialog("Host IP address");
        while(ipAddress==null || ipAddress.equals("")) {
            ipAddress = JOptionPane.showInputDialog("Host IP address");
        }

        newGame.initSpectate(ipAddress);
        frame.getBar().getNewGameAction().setEnabled(false);
        frame.getBar().getHostSpectateAction().setEnabled(false);
    }

    /**
     * When pressing the Join Multi-player button, we create a ClientMoves class to send the key commands to the server, we
     * ask for the client ship name, make a new frame for joining, ask for the ip address of the server, then start the JoinerServer thread.
     * Finally, the New Game and Host Spectate actions are disabled since we are joining a game.
     */

    public void joinMultiPlayer() throws SocketException, UnknownHostException {
        Player player = new Player();
        ClientMoves clientMoves = new ClientMoves();
        String shipName = JOptionPane.showInputDialog("Enter name");
        while(shipName==null || shipName.equals("") || shipName.equals(" ")) {
            shipName = JOptionPane.showInputDialog("Enter name");
        }
        Game newGame = new Game(shipName);
        newGame.linkController(player);
        newGame.linkClientMoves(clientMoves);
        AsteroidsFrame frame = new AsteroidsFrame(newGame, player, clientMoves);
        Thread t = new Thread(newGame);
        t.start();
        String ipAddress = JOptionPane.showInputDialog("Host IP address");
        while(ipAddress==null || ipAddress.equals("")) {
            ipAddress = JOptionPane.showInputDialog("Host IP address");
        }
        newGame.initJoin(ipAddress);
        frame.getBar().getNewGameAction().setEnabled(false);
    }
}
