package com.couchbase.demo.gamesim;

import java.util.UUID;

/**
 *
 * @author ingenthr
 */
public class Item {

    private static String[] weapons = {"axe", "buckeyballs", "demolisher", "forsakencatapult",
	"goblinshredder", "meatwagon", "bladethrower", "plaguespreader", "trebuchet", "glaivethrower",
	"infinityblade", "moodofshadowsong", "corruptedashbringer", "bristleblitzstriker", "bloodyorchid",
	"broadsword", "dagger", "mace", "katana"};

    private String jsonType = "item";
    private String name;
    private UUID uuid;
    private UUID ownerUuid;

    public Item(UUID itemOwner) {
	ownerUuid = itemOwner;
	uuid = UUID.randomUUID();
	name = weapons[GameSimDriver.getRandom().random(0, weapons.length-1)] + itemOwner;
    }

    protected Item() {
	// for GSON
    }

    /**
     * @return the uuid
     */
    public UUID getUuid() {
	return uuid;
    }

    public String getItemName() {
	return name;
    }
}
