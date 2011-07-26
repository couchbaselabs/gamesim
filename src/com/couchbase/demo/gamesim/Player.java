package com.couchbase.demo.gamesim;

import java.util.UUID;

import com.sun.faban.driver.util.Random;

/**
 * This class represents a player in the game simulator.
 * @author ingenthr
 */
public class Player {
    private String jsonType = "player";
    private UUID uuid;
    private String name;
    private Integer hitpoints;
    private int experience;
    private int level;
    private boolean loggedIn;
    private final Random random = new Random();

    public Player(String playerName) {
	name = playerName;
	uuid = UUID.randomUUID();
	hitpoints = random.random(70, 150);
	level = random.random(1, 5);
    }

    public Player() {
	
    }

    public Integer getHitpoints() {
	if (hitpoints == null) {
	    hitpoints = 100;
	}
	return hitpoints;
    }

    public void wound(int level) {
	setHitpoints(getHitpoints() - level);
    }

    public void feed(int foodEnergy) {
	setHitpoints(getHitpoints() + foodEnergy);
    }

    String getName() {
	return this.name;
    }

    private void setHitpoints(int i) {
	hitpoints = i;
    }

    public void logIn() {
	loggedIn = true;
    }

    public void logOut() {
	loggedIn = false;
    }

    void wound() {
	hitpoints = 10;
    }

}
