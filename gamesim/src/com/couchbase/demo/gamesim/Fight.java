/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.couchbase.demo.gamesim;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author ingenthr
 */
public class Fight {

    private String attackingPlayer;
    private String defendingPlayer;
    private String winner;
    private String when;
    private Integer damage;

    public Fight() {

    }

    public Fight(final String attacker, final String defender, final String winner, int i) {
	this.attackingPlayer = attacker;
	this.defendingPlayer = defender;
	this.winner = winner;
	this.damage = i;
	Date now = new Date();
	SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	when = df.format(now);	//yyyy-mm-dd hh:mm:ss
    }

    String getVersus() {
	return attackingPlayer + " : vs : " + defendingPlayer;
    }


}
