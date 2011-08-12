/*
 * This benchmark has been based on the FTP101 driver
 */
package com.couchbase.demo.gamesim;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.faban.driver.*;
import com.sun.faban.driver.util.Random;
import com.sun.faban.harness.EndRun;
import java.net.URISyntaxException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.logging.Level;
import sun.net.ftp.FtpClient;

import javax.xml.xpath.XPathExpressionException;
import java.io.*;
import java.net.URI;
import java.util.ArrayList;
import java.util.logging.Logger;
import net.spy.memcached.CouchbaseClient;

@BenchmarkDefinition(name = "Game Simulator",
version = "0.4",
configPrecedence = true)
@BenchmarkDriver(name = "GameSimDriver",
threadPerScale = (float) 1)
@MatrixMix(operations = {"Login", "Logout", "Eat", "AttackRandom"},
mix = {
    @Row({0, 0, 66, 34}),
    @Row({30, 70, 0, 0}),
    @Row({0, 20, 34, 56}),
    @Row({0, 20, 56, 34})
})
@NegativeExponential(cycleType = CycleType.CYCLETIME,
cycleMean = 200,
cycleDeviation = 5)
public class GameSimDriver {

    /** The driver context for this instance. */
    private DriverContext ctx;
    Logger logger;
    private static Random random;
    FtpClient ftpClient;
    int fileCount;
    String host;
    int port = -1;
    int threadId;
    int putSequence = 1;
    String localFileName;
    String uploadPrefix;
    String user;
    Gson gson;
    GsonBuilder gsonBuilder;
    String playerName;
    String password;
    Player player;
    private final String bucketname;
    private String bucketpass;
    private static CouchbaseClient gamesimStore;
    private final int ACTORMULT = 300000 / players.length;
    private static final String[] players = {"Matt", "Steve", "Dustin",
	"James", "Trond", "Melinda",
	"Bob", "Perry", "Sharon",
	"Leila", "Tony", "Damien", "Jan", "JChris",
	"Volker", "Dale", "Aaron", "Aliaksey", "Frank",
	"Mike", "Claire", "Benjamin", "Tony", "Keith",
	"Bin", "Chiyoung", "Jens", "Srini"
    };
    // See http://en.wikipedia.org/wiki/Category:Celtic_legendary_creatures
    private static final String[] monsters = {"Bauchan", "Fachen", "Fuath", "Joint-eater", "Kelpie",
	"Knocker", "Merrow", "Morgen", "Pictish-beast", "Wild-man"};

    /**
     * Constructs an instance of a user session on the game simulator.
     * @throws XPathExpressionException An XPath error occurred
     * @throws IOException I/O error creating the driver instance
     */
    public GameSimDriver() throws XPathExpressionException, IOException {
	ctx = DriverContext.getContext();

	// set up gson for serialization
	gsonBuilder = new GsonBuilder();
	gson = gsonBuilder.create();

	logger = ctx.getLogger();

	random = ctx.getRandom();
	threadId = ctx.getThreadId();
	uploadPrefix = "up" + threadId + '_';
	localFileName = "/tmp/ftp" + threadId;
	host =
		ctx.getXPathValue("/gamesimBenchmark/serverConfig/host").trim();
	String portNum =
		ctx.getXPathValue("/gamesimBenchmark/serverConfig/port").trim();
	user = ctx.getProperty("user");
	password = ctx.getProperty("password");
	URI server;
	try {
	    // Create a basic client
	    server = new URI(ctx.getXPathValue("/gamesimBenchmark/serverConfig/host").trim());
	} catch (URISyntaxException ex) {
	    Logger.getLogger(GameSimDriver.class.getName()).log(Level.SEVERE, null, ex);
	    throw new IOException("Could not deal with URI.");
	}
	bucketname = ctx.getXPathValue("/gamesimBenchmark/serverConfig/bucket").trim();
	bucketpass = ctx.getXPathValue("/gamesimBenchmark/serverConfig/bucketpw").trim();
	if (bucketpass == null) {
	    bucketpass = "";
	}
	ArrayList<URI> servers = new ArrayList<URI>();
	servers.add(server);
	// @todo fix up this singletonness of the clients
	if (gamesimStore == null) {
	    // can't do this, bug SPY-2
	    //ConnectionFactoryBuilder cfb = new ConnectionFactoryBuilder();
	    //cfb.setOpTimeout(20000).setProtocol(ConnectionFactoryBuilder.Protocol.BINARY);
	    gamesimStore = new CouchbaseClient(servers, bucketname, bucketpass);
	}
    }

    @OnceBefore
    public void setup() throws InterruptedException {
	logger.info("The creator is starting the spike of population.");
	populatePlayers(ACTORMULT);
	populateMonsters(ACTORMULT);
	logger.info("The creator will now rest; the world has been populated.");


    }

    private void populatePlayers(int number) {
	logger.log(Level.INFO, "Creating {0} players to rid the world of monsters.", number);
	for (int i = 0; i < number; i++) {
	    for (String aplayer : players) {
		Player newPlayer = new Player(aplayer + i);
		gamesimStore.add(newPlayer.getName(), 0, gson.toJson(newPlayer));
	    }
	}
    }

    private void populateMonsters(int number) {
	logger.log(Level.INFO, "Creating {0} monsters to vanquish players.", number);
	for (int i = 0; i < number; i++) {
	    for (String amonster : monsters) {
		Monster newMonster = new Monster(amonster + i);
		gamesimStore.add(newMonster.getName(), 0, gson.toJson(newMonster));
	    }
	}
    }

    /**
     * Operation to simulate a login.
     * @throws IOException Error connecting to server
     */
    @BenchmarkOperation(name = "Login",
    max90th = 2,
    timing = Timing.MANUAL)
    public void doLogin() throws IOException, InterruptedException, ExecutionException {

	if (player != null) {
	    return;
	}
	ctx.recordTime();
	playerName = getRandomPlayer();
	String playerJsonRepresentation = (String) gamesimStore.get(stripBlanks(playerName));
	logger.log(Level.FINE, "Player JSON:\n {0}", playerJsonRepresentation);
	if (playerJsonRepresentation == null) {
	    logger.log(Level.FINE, "Player JSON:\n {0}", playerJsonRepresentation);
	    player = new Player(playerName);
	} else {
	    player = gson.fromJson(playerJsonRepresentation, Player.class);
	}
        //Only write to the store if the status changed
        if (!player.isLoggedIn()){
            player.logIn();
            storePlayer();
        }
	ctx.recordTime();
    }

    /**
     * Operation to simulate a login.
     * @throws IOException Error connecting to server
     */
    @BenchmarkOperation(name = "Logout",
    max90th = 2,
    timing = Timing.MANUAL)
    public void doLogout() throws IOException, InterruptedException, ExecutionException {
	if (player == null) {
	    return; // can't log out when logged out
	}
	ctx.recordTime();
        //Only write to the store if the status changed
        if (player.isLoggedIn()){
            player.logOut();
            storePlayer();
        }

        // Add some read work
        for (int i=0; i<400; i++) {
            playerName = getRandomPlayer();
            gamesimStore.asyncGet(stripBlanks(playerName));
        }
	player = null;
	ctx.recordTime();
    }

    private void storePlayer() throws InterruptedException, ExecutionException {
	Future<Boolean> setRes = gamesimStore.set(stripBlanks(player.getName()), 0, gson.toJson(player));
	setRes.get();
    }

    /**
     * Operation to simulate attacking another player.
     * @throws IOException Error connecting to server
     */
    @BenchmarkOperation(name = "AttackRandom",
    max90th = 2,
    timing = Timing.MANUAL)
    public void doAttackRandom() throws IOException, InterruptedException, ExecutionException {
	doLogin();
	String attackerName = getRandomMonster();
	ctx.recordTime();
	Monster attacker = gson.fromJson((String) gamesimStore.get(attackerName), Monster.class);
	assert attacker != null : "There is no monster " + attackerName;


	Double ahpd = null;
	Double phpd = null;
	try {
	    phpd = new Double(player.getHitpoints())* new Double(2^player.getLevel());
	    ahpd = new Double(attacker.getHitpoints());
	} catch (NullPointerException e) {
	}

	assert (ahpd != null);
	assert (phpd != null);

	Double playerWinProbable = phpd / (phpd + ahpd);
	if (playerWinProbable > 0.5d) {
	    Double itemProb = random.drandom(0.0d, 1.0d);
	    if (itemProb <= attacker.getItemProbability()) {
		Item bounty = new Item(player.getName());
		gamesimStore.set(bounty.getItemName(), 0, gson.toJson(bounty)).get();
		logger.log(Level.FINER, "Player {0} won a {1}", new Object[]{player.getName(), bounty.getItemName()});
	    }
	    // 100*2^level
	    player.gainExperience(attacker.getExperienceWhenKilled());
	} else {
	    player.wound();
	}

	storePlayer();
	ctx.recordTime();
    }

    /**
     * Operation to simulate eating.
     * @throws IOException Error connecting to server
     */
    @BenchmarkOperation(name = "Eat",
    max90th = 2,
    timing = Timing.MANUAL)
    public void doEat() throws IOException, InterruptedException, ExecutionException {
	doLogin();
	int foodEnergy = random.random(10, 100);
	ctx.recordTime();
	player.feed(foodEnergy);
	ctx.recordTime();
    }

    @EndRun
    public void cleanup() {
	//gamesimStore.flush();
	gamesimStore.shutdown();
    }

    public static String stripBlanks(String s) {
	return s.replaceAll("\\s", "");
    }

    private String getRandomPlayerName() {
	int i = random.random(0, players.length - 1);
	return players[i];
    }

    private String getRandomMonsterName() {
	int i = random.random(0, monsters.length - 1);
	return monsters[i];
    }

    private String getRandomMonster() {
	return getRandomMonsterName() + random.random(0, ACTORMULT - 1);
    }

    private String getRandomPlayer() {
	return getRandomPlayerName() + random.random(0, ACTORMULT - 1);
    }

    /**
     * @return the random
     */
    public static Random getRandom() {
	return random;
    }
}
