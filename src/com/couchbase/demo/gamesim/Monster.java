/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.couchbase.demo.gamesim;

import java.util.UUID;

/**
 *
 * @author ingenthr
 */
public class Monster {
    
    private String jsonType = "monster";
    private String name;
    private UUID uuid;
    private int hitpoints;
    private int experienceWhenKilled;
    private int itemProbability;

    public Monster() {
	uuid = UUID.randomUUID();
    }

}
