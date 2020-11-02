package aoop.asteroids.controller;

import aoop.asteroids.model.Joiner;
import aoop.asteroids.model.Messages;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;

/**
 * This is a KeyListener which sends to the server string messages which represent the key pressed by a certain client.
 * If I am a client and I press up, this will be sent to the JoinerServer, which will update my ship to move up and return the
 * updated model.
 */

public class ClientMoves implements KeyListener {

    private Joiner joiner;

    public void addJoiner(Joiner joiner) {
        this.joiner = joiner;
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        try {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_UP:
                    this.joiner.sendControl(Messages.UP.toString());
                    break;
                case KeyEvent.VK_LEFT:
                    this.joiner.sendControl(Messages.LEFT.toString());
                    break;
                case KeyEvent.VK_RIGHT:
                    this.joiner.sendControl(Messages.RIGHT.toString());
                    break;
                case KeyEvent.VK_SPACE:
                    this.joiner.sendControl(Messages.SPACE.toString());
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        try {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_UP:
                    this.joiner.sendControl(Messages.STOP_UP.toString());
                    break;
                case KeyEvent.VK_LEFT:
                    this.joiner.sendControl(Messages.STOP_LEFT.toString());
                    break;
                case KeyEvent.VK_RIGHT:
                    this.joiner.sendControl(Messages.STOP_RIGHT.toString());
                    break;
                case KeyEvent.VK_SPACE:
                    this.joiner.sendControl(Messages.STOP_SPACE.toString());
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }
}
