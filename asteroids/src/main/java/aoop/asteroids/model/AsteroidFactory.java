package aoop.asteroids.model;

import java.util.ArrayList;
import java.util.Collection;

/**
 * This is the Factory Pattern for Asteroids. If we pass this a Large Asteroid, we will create 2 new Medium Asteroids and add them to the
 * list of asteroids and return the list. Similarly, if we pass this a Medium Asteroid, we will create 2 new Small Asteroids and add them to
 * the asteroids list and return the list.
 */

public class AsteroidFactory {

    public Collection<Asteroid> getAsteroids(Asteroid asteroid) {
        if(LargeAsteroid.class.isInstance(asteroid)){
            ArrayList<Asteroid> list = new ArrayList<>();
            list.add(new MediumAsteroid(asteroid.getLocation(), asteroid.getVelocityX() * Math.cos(Math.PI / 2) * 1.5 - asteroid.getVelocityY() * Math.sin(Math.PI / 2) * 1.5,
                    asteroid.getVelocityX() * Math.sin(Math.PI / 2) * 1.5 + asteroid.getVelocityY() * Math.cos(Math.PI / 2) * 1.5));
            list.add(new MediumAsteroid(asteroid.getLocation(), asteroid.getVelocityX() * Math.cos(-Math.PI / 2) * 1.5 - asteroid.getVelocityY() * Math.sin(-Math.PI / 2) * 1.5,
                    asteroid.getVelocityX() * Math.sin(-Math.PI / 2) * 1.5 + asteroid.getVelocityY() * Math.cos(-Math.PI / 2) * 1.5));
            return list;
        } else if (MediumAsteroid.class.isInstance(asteroid)) {
            ArrayList<Asteroid> list = new ArrayList<>();
            list.add(new SmallAsteroid(asteroid.getLocation(), asteroid.getVelocityX() * Math.cos(Math.PI / 2) * 1.5 - asteroid.getVelocityY() * Math.sin(Math.PI / 2) * 1.5,
                    asteroid.getVelocityX() * Math.sin(Math.PI / 2) * 1.5 + asteroid.getVelocityY() * Math.cos(Math.PI / 2) * 1.5));
            list.add(new SmallAsteroid(asteroid.getLocation(), asteroid.getVelocityX() * Math.cos(-Math.PI / 2) * 1.5 - asteroid.getVelocityY() * Math.sin(-Math.PI / 2) * 1.5,
                    asteroid.getVelocityX() * Math.sin(-Math.PI / 2) * 1.5 + asteroid.getVelocityY() * Math.cos(-Math.PI / 2) * 1.5));
            return list;
        }
        return null;
    }
}