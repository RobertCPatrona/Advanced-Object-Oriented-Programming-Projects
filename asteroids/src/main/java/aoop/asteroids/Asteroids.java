package aoop.asteroids;

import aoop.asteroids.view.AsteroidsFrame;
import aoop.asteroids.controller.Player;
import aoop.asteroids.model.Game;

import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * Main class of the Asteroids program.
 * <p>
 * Asteroids is simple game, in which the player is represented by a small
 * spaceship. The goal is to destroy as many asteroids as possible and thus
 * survive for as long as possible.
 *
 * @author Yannick Stoffers
 */
public class Asteroids {

	/**
	 * Constructs a new instance of the program.
	 */
	public Asteroids() throws SocketException, UnknownHostException {
		Player player = new Player();
		Game game = new Game();
		game.linkController(player);
		new AsteroidsFrame(game, player);
		Thread t = new Thread(game);
		t.start();
	}

	/**
	 * Main function.
	 *
	 * @param args input arguments.
	 */
	public static void main(String[] args) throws SocketException, UnknownHostException {
		new Asteroids();
	}

}
