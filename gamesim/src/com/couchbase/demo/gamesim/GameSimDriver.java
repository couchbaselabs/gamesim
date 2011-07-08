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
import net.spy.memcached.MemcachedClient;

@BenchmarkDefinition(name = "Game Simulator",
version = "0.1",
configPrecedence = true)
@BenchmarkDriver(name = "GameSimDriver",
threadPerScale = (float) 1)
@MatrixMix (
    operations = {"Login", "Logout", "Eat", "AttackRandom"},
    mix = { @Row({0, 0, 66, 34 }),
            @Row({100, 0, 0, 0 }),
            @Row({0, 10, 56, 34 }),
	    @Row({0, 10, 56, 34})
          }
    )
@NegativeExponential(cycleType = CycleType.CYCLETIME,
cycleMean = 300,
cycleDeviation = 2)
public class GameSimDriver {

    /** The driver context for this instance. */
    private DriverContext ctx;
    Logger logger;
    Random random;
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
    byte[] buffer = new byte[8192];
    private static MemcachedClient playersStore;
    private static MemcachedClient currentStatsStore;

    private static final String[] players = {"Matt Ingenthron", "Steve Yen", "Dustin Sallings",
					     "James Phillips", "Trond Norbye", "Melinda Wilken",
					     "Bob Weiderhold", "Perry Krug", "Steven Mih",
					     "Leila Iravini", "Tony Nguyen", "Damien Katz", "Jan Lehnardt", "JChris Anderson",
					     "Volker Mische", "Dale Harvey", "Aaron Miller", "Aliaksey Kandratsenka"
                                            };


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
	fileCount = Integer.parseInt(ctx.getXPathValue(
		"/gamesimBenchmark/serverConfig/fileCount").trim());
	user = ctx.getProperty("user");
	password = ctx.getProperty("password");
	URI server;
	try {
	    // Create a basic client
	    server = new URI("http://localhost:8091/pools");
	} catch (URISyntaxException ex) {
	    Logger.getLogger(GameSimDriver.class.getName()).log(Level.SEVERE, null, ex);
	    throw new IOException("Could not deal with URI.");
	}
	ArrayList<URI> servers = new ArrayList<URI>();
	servers.add(server);
	try {
	    // @todo fix up this singletonness of the clients
	    if (playersStore == null) {
		playersStore = new MemcachedClient(servers, "gamesim-players", "gamesim-players", "letmein");
	    }
	    if (currentStatsStore == null) {
		currentStatsStore = new MemcachedClient(servers, "gamesim-stats", "gamesim-stats", "letmein");
	    }
	} catch (javax.naming.ConfigurationException ex) {
	    Logger.getLogger(GameSimDriver.class.getName()).log(Level.SEVERE, null, ex);
	    throw new IOException("Could not configure the memcached client.");
	}
    }

    /**
     * Operation to simulate a login.
     * @throws IOException Error connecting to server
     */
    @BenchmarkOperation(name = "Login",
    max90th = 2,
    timing = Timing.MANUAL)
    public void doLogin() throws IOException {

	if (player != null) {
	    return;
	}

	ctx.recordTime();
	playerName = getRandomPlayer();
	String playerJsonRepresentation = (String) playersStore.get(stripBlanks(playerName));
	logger.log(Level.FINE, "Player JSON:\n {0}", playerJsonRepresentation);
	if (playerJsonRepresentation == null) {
	    logger.log(Level.FINE, "Player JSON:\n {0}", playerJsonRepresentation);
	    player = new Player(playerName);
	} else {
	    player = gson.fromJson(playerJsonRepresentation, Player.class);
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
	Future<Boolean> setRes = playersStore.set(stripBlanks(player.getName()), 0, gson.toJson(player));
	setRes.get();
	player = null;
	ctx.recordTime();
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
	ctx.recordTime();
	player.wound(1);
	String defender = getRandomPlayer();
	String winner;
	if (random.random(0, 1) == 1) {
	    winner = defender;
	} else {
	    winner = player.getName();
	}

	Fight newFight = new Fight(player.getName(), defender, winner, random.random(50, 1000));
	Future<Boolean> setRes = currentStatsStore.set(stripBlanks(newFight.getVersus()), port, gson.toJson(newFight));
	setRes.get();
	ctx.recordTime();
    }

    /**
     * Operation to simulate eating.
     * @throws IOException Error connecting to server
     */
    @BenchmarkOperation(name = "Eat",
    max90th = 2,
    timing = Timing.MANUAL)
    public void doEat() throws IOException {
	doLogin();
	ctx.recordTime();
	player.feed(100);
	ctx.recordTime();
    }

    @EndRun
    public void cleanup() {
	playersStore.flush();
	currentStatsStore.flush();
	playersStore.shutdown();
	currentStatsStore.shutdown();
    }

    public static String stripBlanks(String s) {
	return s.replaceAll("\\s", "");
    }

    public String getRandomPlayer() {
	int i = random.random(0, 17);
	return players[i];
    }

}
