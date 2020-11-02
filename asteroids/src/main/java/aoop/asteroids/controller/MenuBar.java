package aoop.asteroids.controller;
import aoop.asteroids.view.AsteroidsFrame;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * This class defines the behaviour of the JMenu, or what happens when we click a certain Menu item.
 */

public class MenuBar extends JMenuBar {

    private JMenu menu;

    private AbstractAction quitAction;
    private AbstractAction newGameAction;
    private AbstractAction hostSpectateAction;
    private AbstractAction disconnectSpectateAction;
    private AbstractAction disconnectJoinAction;
    private AbstractAction backToMenu;

    private AsteroidsFrame frame;

    /**
     * This constructor creates the Menu, and adds and initializes the actions.
     * @param frame this is the AsteroidsFrame on which we will set this Menu.
     * @param isSinglePlayer tells whether we are playing a single player game or not. If the boolean is true, we
     */

    public MenuBar(AsteroidsFrame frame, Boolean isSinglePlayer) {
        this.frame = frame;
        this.menu = new JMenu("Game");
        this.add(menu);
        initActions();
        addActions(isSinglePlayer);
    }

    /**
     * This adds the actions to the Menu for hosting or single player.
     * @param isSinglePlayer boolean which tells whether we are are playing a single player game. If it is null,
     * we will add the Disconnect action for Multiplayer games, and if it false we will add the Disconnect action
     * for the Spectate games, else we do not add a Disconnect action since you cannot disconnect from a single player game.
     */

    private void addActions(Boolean isSinglePlayer){
        menu.add(this.quitAction);
        menu.add(this.newGameAction);
        menu.add(this.hostSpectateAction);
        menu.add(this.backToMenu);
        if(isSinglePlayer==null) {
            menu.add(this.disconnectJoinAction);
        } else if(!isSinglePlayer) {
            menu.add(this.disconnectSpectateAction);
        }
    }

    /**
     * This initializes the actions. Quit ends the process, New Game creates a new Game, Host Spectate starts a Server thread in the model,
     * Disconnect breaks the connection to server from client for either Spectate or Join, and Main Menu takes you back to the main menu.
     */

    private void initActions() {
        this.quitAction = new AbstractAction("Quit") {
            public static final long serialVersionUID = 2L;

            @Override
            public void actionPerformed(ActionEvent arg0) {
                System.exit(0);
            }
        };

        this.newGameAction = new AbstractAction("New Game") {
            public static final long serialVersionUID = 3L;

            @Override
            public void actionPerformed(ActionEvent arg0) {
                frame.newGame();
            }
        };

        this.hostSpectateAction = new AbstractAction("Host Spectate") {
            public static final long serialVersionUID = 4L;

            @Override
            public void actionPerformed(ActionEvent arg0) {
                frame.hostSpectator();
                hostSpectateAction.setEnabled(false);
            }
        };

        this.disconnectSpectateAction = new AbstractAction("Disconnect") {
            public static final long serialVersionUID = 5L;

            @Override
            public void actionPerformed(ActionEvent arg0) {
                GameModes gameMode = new GameModes();
                try {
                    gameMode.startSinglePlayer();
                    setVisible(false);
                    frame.dispose();
                } catch (SocketException | UnknownHostException e) {
                    e.printStackTrace();
                }
            }
        };

        this.disconnectJoinAction = new AbstractAction("Disconnect") {
            public static final long serialVersionUID = 6L;

            @Override
            public void actionPerformed(ActionEvent arg0) {
                frame.disconnectJoin();
            }
        };

        this.backToMenu = new AbstractAction("Main Menu") {
            public static final long serialVersionUID = 7L;

            @Override
            public void actionPerformed(ActionEvent arg0) {
                try {
                    frame.disconnect();
                    frame.newMenu();
                } catch (SocketException | UnknownHostException e) {
                    e.printStackTrace();
                }
            }
        };
    }

    /**
     * Getters for the actions.
     */

    public AbstractAction getNewGameAction() {
        return newGameAction;
    }

    public AbstractAction getHostSpectateAction() {
        return hostSpectateAction;
    }

    public AbstractAction getDisconnectSpectateAction() {
        return disconnectSpectateAction;
    }

    public AbstractAction getDisconnectJoinAction() {
        return disconnectJoinAction;
    }
}
