package com.couchbase.demo.gamesim;

import java.util.UUID;

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
    private int loggedIn;

    public Player(final String playerName) {
	this.name = playerName;
	uuid = UUID.randomUUID();
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

    public void feed(int level) {
	setHitpoints(getHitpoints() + level);
    }

    String getName() {
	return this.name;
    }

    private void setHitpoints(int i) {
	hitpoints = i;
    }


}
