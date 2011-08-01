package com.couchbase.demo.gamesim;

import java.util.UUID;

import java.util.ArrayList;

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
    private ArrayList<String> playerItems;

    public Player(String playerName) {
	name = playerName;
	uuid = UUID.randomUUID();
	hitpoints = GameSimDriver.getRandom().random(70, 150);
	level = GameSimDriver.getRandom().random(1, 5);
	experience = GameSimDriver.getRandom().random((100*(2^level)), (100*(2^(level+1))-1));
    }

    protected Player() {
	// for GSON
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }

    public Integer getHitpoints() {
	if (hitpoints == null) {
	    hitpoints = 100;
	}
	return hitpoints;
    }

    public Integer getLevel() {
        return level;
    }

    public void wound(int hps) {
	setHitpoints(getHitpoints() - hps);
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

    UUID getUuid() {
	return uuid;
    }

    void gainExperience(int experienceGained) {
	experience = experience + experienceGained;
	if (experience > (100*(2^level))) {
	    level++;
	}
    }

}
