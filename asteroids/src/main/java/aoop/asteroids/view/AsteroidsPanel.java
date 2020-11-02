package aoop.asteroids.view;

import aoop.asteroids.model.*;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.lang.Object;
import java.util.Collection;
import java.util.Observable;
import java.util.Observer;
import java.util.Random;
import javax.swing.JPanel;

/**
 * AsteroidsPanel extends JPanel and thus provides the actual graphical
 * representation of the game model.
 */
public class AsteroidsPanel extends JPanel {

    /**
     * serialVersionUID
     */
    public static final long serialVersionUID = 4L;

    /**
     * Game model.
     */
    private Game game;

    /**
     * Constructs a new game panel, based on the given model.
     *
     * @param game game model.
     */
    public AsteroidsPanel(Game game) {
        this.game = game;
        this.game.addObserver(new Observer() {
            @Override
            public void update(Observable o, Object arg) {
                AsteroidsPanel.this.repaint();
            }
        });
    }

    /**
     * Method for refreshing the GUI.
     *
     * @param g graphics instance to use.
     */
    @Override
    public synchronized void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        this.setBackground(Color.black);

        this.paintAsteroids(g2);
        Collection<Bullet> bullets = this.game.getBullets();
        this.paintBullets(g2, bullets);
        Spaceship s = this.game.getPlayer();
        this.paintClients(g2);
        this.paintSpaceship(g2,s);

        if (!game.getPlayer().getNickName().equals(" ")) {
            g2.setColor(Color.WHITE);
            g2.drawString("Score of " + game.getPlayer().getNickName() + " : " + String.valueOf(this.game.getPlayer().getScore()), 20, 20);
        }

        g2.drawString("IP address: " + game.getCurrentIP(0), this.getWidth()-150, 20);
        g2.drawString("Port: " + 8888, this.getWidth()-83, 37);

        paintTitle(g);

    }

    /**
     * Draws the Asteroids title on the main menu
     *
     * @param g graphics instance to use.
     * */
    private void paintTitle(Graphics g){
        if(this.game.getPlayer().getNickName().equals(" ")) {
            Font beforeFont = g.getFont();
            Color beforeColor = g.getColor();
            g.setFont(new Font("Courier New", Font.BOLD, 100));

            g.setColor(new Color(0.1f,0.1f,0.1f));
            g.drawString("ASTEROIDS", (this.getWidth()/2)-240, (this.getHeight()/2)+40);

            Random rng = new Random();
            float red = .4f+rng.nextFloat() * (1f-.4f);
            float green = .4f+rng.nextFloat()* (.5f-.4f);
            float blue = .4f+rng.nextFloat()* (.5f-.4f);
            red = Math.round(red*10)/10f;
            green = Math.round(green*10)/10f;
            blue = Math.round(blue*10)/10f;

            g.setColor(new Color(red, green, blue));
            g.drawString("ASTEROIDS", (this.getWidth()/2)-260, (this.getHeight()/2)+30);
            g.setFont(beforeFont);
            g.setColor(beforeColor);
        }
    }

    /**
     * Draws all bullets in the GUI as a yellow circle.
     *
     * @param g graphics instance to use.
     */
    private synchronized void paintBullets(Graphics2D g, Collection<Bullet> bullets) {
        g.setColor(Color.yellow);

        for (Bullet b : bullets)
            g.drawOval(b.getLocation().x - 2, b.getLocation().y - 2, 5, 5);
    }

    /**
     * Draws all asteroids in the GUI as a filled gray circle.
     *
     * @param g graphics instance to use.
     */
    private synchronized void paintAsteroids(Graphics2D g) {
        g.setColor(Color.GRAY);

        for (Asteroid a : this.game.getAsteroids()) {
            Ellipse2D.Double e = new Ellipse2D.Double();
            e.setFrame(a.getLocation().x - a.getRadius(), a.getLocation().y - a.getRadius(), 2 * a.getRadius(), 2 * a.getRadius());
            g.fill(e);
        }
    }

    /**
     * Draws the player in the GUI as a see-through white triangle. If the
     * player is accelerating a yellow triangle is drawn as a simple
     * representation of flames from the exhaust.
     *
     * @param g graphics instance to use.
     */
    private synchronized void paintSpaceship(Graphics2D g, Spaceship s) {

        if(!s.getNickName().equals(" ")) {
            // Draw body of the spaceship
            Polygon p = new Polygon();
            p.addPoint((int) (s.getLocation().x + Math.sin(s.getDirection()) * 20), (int) (s.getLocation().y - Math.cos(s.getDirection()) * 20));
            p.addPoint((int) (s.getLocation().x + Math.sin(s.getDirection() + 0.8 * Math.PI) * 20), (int) (s.getLocation().y - Math.cos(s.getDirection() + 0.8 * Math.PI) * 20));
            p.addPoint((int) (s.getLocation().x + Math.sin(s.getDirection() + 1.2 * Math.PI) * 20), (int) (s.getLocation().y - Math.cos(s.getDirection() + 1.2 * Math.PI) * 20));

            g.setColor(s.getColor());
            g.fill(p);
            g.setColor(Color.WHITE);
            g.draw(p);


            int midLength = s.getNickName().length() / 2;
            g.drawString(s.getNickName(), (int) s.getLocation().getX() - (7 * midLength), (int) s.getLocation().getY() - 40);

            if (this.game.gameOver()) {
                Font beforeFont = g.getFont();
                Color beforeColor = g.getColor();
                g.setFont(new Font("Courier New", Font.BOLD, 100));
                g.setColor(new Color(170, 0, 0));
                g.drawString("GAME OVER", (this.getWidth() / 2) - 260, (this.getHeight() / 2) + 30);
                g.setFont(beforeFont);
                g.setColor(beforeColor);
            }

            // Spaceship accelerating -> continue, otherwise abort.
            if (!s.isAccelerating()) return;

            // Draw flame at the exhaust
            p = new Polygon();
            p.addPoint((int) (s.getLocation().x - Math.sin(s.getDirection()) * 25), (int) (s.getLocation().y + Math.cos(s.getDirection()) * 25));
            p.addPoint((int) (s.getLocation().x + Math.sin(s.getDirection() + 0.9 * Math.PI) * 15), (int) (s.getLocation().y - Math.cos(s.getDirection() + 0.9 * Math.PI) * 15));
            p.addPoint((int) (s.getLocation().x + Math.sin(s.getDirection() + 1.1 * Math.PI) * 15), (int) (s.getLocation().y - Math.cos(s.getDirection() + 1.1 * Math.PI) * 15));
            g.setColor(Color.yellow);
            g.fill(p);
        }

    }

    /**
     * This paints the clients and the interactions between them and the model.
     */

    private synchronized void paintClients(Graphics2D g) {
        synchronized (this.game.getClients()) {
            for (int i = 0; i < this.game.getClients().size(); i++) {
                Spaceship s = this.game.getClients().get(i).getShip();
                paintSpaceship(g, s);
                Collection<Bullet> clientBullets = this.game.getClients().get(i).getBullets();
                paintBullets(g, clientBullets);
                g.setColor(Color.WHITE);
                g.drawString("Score of " + game.getClients().get(i).getShip().getNickName() + " : " + String.valueOf(this.game.getClients().get(i).getShip().getScore()), 20, 20+(i+1)*20);
            }
        }
    }
}
