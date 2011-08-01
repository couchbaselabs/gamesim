package com.couchbase.demo.gamesim;

import java.util.UUID;

/**
 *
 * @author ingenthr
 */
public class Item {

    private static String[] weapons = {"Axe", "Buckeyballs", "Demolisher", "Forsaken_Catapult",
	"Goblinshredder", "Meatwagon", "Bladethrower", "Plague_spreader", "Trebuchet", "Glaive_thrower",
	"Infinityblade", "Mood_of_Shadowsong", "Corrupted_Ash_bringer", "Bristleblitzstriker", "Bloodyorchid",
	"Broadsword", "Dagger", "Mace", "Katana"};

    private String jsonType = "item";
    private String name;
    private UUID uuid;
    private String ownerId;

    public Item(String itemOwner) {
	ownerId = itemOwner;
	uuid = UUID.randomUUID();
	name = weapons[GameSimDriver.getRandom().random(0, weapons.length-1)] + "_" + uuid;
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
