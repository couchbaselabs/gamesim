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
public class Item {

    private String jsonType = "item";
    private String name;
    private UUID uuid;
    private UUID ownerUuid;

    public Item() {
	uuid = UUID.randomUUID();
    }
}
