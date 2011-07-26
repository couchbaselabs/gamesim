/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.couchbase.demo.gamesim;

import java.util.UUID;

import com.sun.faban.driver.util.Random;

/**
 *
 * @author ingenthr
 */
public class Monster {

    private Random random = new Random();
    private String jsonType = "monster";
    private final String name;
    private UUID uuid;
    private int hitpoints;
    private int experienceWhenKilled;
    private double itemProbability;

    public Monster(String monstername) {
	name = monstername;
	uuid = UUID.randomUUID();
	experienceWhenKilled = random.random(10, 100);
	hitpoints = random.random(10, 100);
	itemProbability = random.drandom(0.1d, 0.6d);
    }

    String getName() {
	return name;
    }

    /**
     * @return the hitpoints
     */
    public int getHitpoints() {
	return hitpoints;
    }

    /**
     * @return the experienceWhenKilled
     */
    public int getExperienceWhenKilled() {
	return experienceWhenKilled;
    }

    /**
     * @return the itemProbability
     */
    public double getItemProbability() {
	return itemProbability;
    }

}
