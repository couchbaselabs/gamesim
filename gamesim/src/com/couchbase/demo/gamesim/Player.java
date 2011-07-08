package com.couchbase.demo.gamesim;

/**
 * This class represents a player in the game simulator.
 * @author ingenthr
 */
public class Player {
    private String playerName;
    private Integer hitpoints;

    public Player(final String playerName) {
	this.playerName = playerName;
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
	return this.playerName;
    }

    private void setHitpoints(int i) {
	hitpoints = i;
    }


}
