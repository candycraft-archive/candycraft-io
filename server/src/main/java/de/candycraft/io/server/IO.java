package de.candycraft.io.server;

import ch.qos.logback.classic.Level;
import com.zaxxer.hikari.pool.HikariPool;
import de.candycraft.io.server.command.Command;
import de.candycraft.io.server.command.CommandManager;
import de.candycraft.io.server.command.impl.DebugCommand;
import de.candycraft.io.server.command.impl.EndCommand;
import de.candycraft.io.server.command.impl.HelpCommand;
import de.candycraft.io.server.manager.player.PlayerManager;
import de.candycraft.io.server.rest.filters.AuthenticationFilter;
import de.candycraft.io.server.rest.resources.PlayerResource;
import de.progme.athena.Athena;
import de.progme.athena.db.settings.AthenaSettings;
import de.progme.hermes.server.HermesServer;
import de.progme.hermes.server.HermesServerFactory;
import de.progme.hermes.server.impl.HermesConfig;
import de.progme.iris.IrisConfig;
import de.progme.iris.config.Header;
import de.progme.iris.config.Key;
import de.progme.thor.client.cache.PubSubCache;
import de.progme.thor.client.cache.PubSubCacheFactory;
import de.progme.thor.client.pub.Publisher;
import de.progme.thor.client.pub.PublisherFactory;
import de.progme.thor.client.sub.Subscriber;
import de.progme.thor.client.sub.SubscriberFactory;
import de.progme.thor.shared.net.ConnectException;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * Created by Marvin Erkes on 2019-03-20.
 */
public class IO {

    private static final Pattern ARGS_PATTERN = Pattern.compile(" ");

    @Getter
    private static IO instance;

    private static Logger logger = LoggerFactory.getLogger(IO.class);

    @Getter
    private IrisConfig irisConfig;

    @Getter
    private CommandManager commandManager;

    @Getter
    private Athena athena;

    @Getter
    private Publisher publisher;
    @Getter
    private Subscriber subscriber;
    @Getter
    private PubSubCache pubSubCache;

    @Getter
    private PlayerManager playerManager;

    @Getter
    private HermesServer restServer;

    private Scanner scanner;

    /**
     * IO contructor
     *
     * @param irisConfig the iris config of the config.iris
     */
    public IO(IrisConfig irisConfig) {

        // initialize instance
        instance = this;

        // initialize the config
        this.irisConfig = irisConfig;

        changeDebug(Level.toLevel(irisConfig.getHeader("general").getKey("logger").getValue(0).asString()));

        // initialize command manager
        this.commandManager = new CommandManager();

        // initialize rest api
        restServer = HermesServerFactory.create(new Config());
    }

    private static class Config extends HermesConfig {

        public Config() {

            Header generalHeader = IO.getInstance().getIrisConfig().getHeader("general");

            Key hostKey = generalHeader.getKey("host");

            host(hostKey.getValue(0).asString());
            port(hostKey.getValue(1).asInt());
            backLog(25);

            corePoolSize(8);
            maxPoolSize(16);

            filter(AuthenticationFilter.class);
            register(PlayerResource.class);
        }
    }

    /**
     * Start the IO
     * @param startTimeMillis The time when IO got started
     */
    public void start(double startTimeMillis) {

        commandManager.addCommand(new HelpCommand("help", "List of available commands", "h"));
        commandManager.addCommand(new EndCommand("end", "Stops IO", "stop", "exit"));
        commandManager.addCommand(new DebugCommand("debug", "Turns the debug mode on/off", "d"));

        // intialize athena
        Header mysqlHeader = irisConfig.getHeader("mysql");
        Key mysqlHostKey = mysqlHeader.getKey("host");
        Key mysqlAuthenticationKey = mysqlHeader.getKey("authentication");
        Key mysqlDatabaseKey = mysqlHeader.getKey("database");
        Key mysqlPoolsizeKey = mysqlHeader.getKey("poolsize");

        athena = new Athena(new AthenaSettings.Builder()
                .host(mysqlHostKey.getValue(0).asString())
                .port(mysqlHostKey.getValue(1).asInt())
                .user(mysqlAuthenticationKey.getValue(0).asString())
                .password(mysqlAuthenticationKey.getValue(1).asString())
                .database(mysqlDatabaseKey.getValue(0).asString())
                .poolSize(mysqlPoolsizeKey.getValue(0).asInt())
                .poolName("IO")
                .build());

        // connect to Athena(MySQL)
        connectAthena();

        // connect to Thor
        connectThor();

        // initialize managers
        playerManager = new PlayerManager(athena, pubSubCache, irisConfig.getHeader("thor").getKey("expire").getValue(0).asInt());
        playerManager.createTables();

        // start rest api
        restServer.start();

        logger.debug("Hermes(rest server) started");

        logger.info("IO started - took {}s to start", (System.currentTimeMillis() - startTimeMillis) / 1000);
    }

    /**
     * Initialize console
     */
    public void console() {

        System.out.print(" > ");

        scanner = new Scanner(System.in);

        try {
            String line;
            while ((line = scanner.nextLine()) != null) {
                if (!line.isEmpty()) {
                    String[] split = ARGS_PATTERN.split(line);

                    if (split.length == 0) {
                        continue;
                    }

                    // Get the command name
                    String commandName = split[0].toLowerCase();

                    // Try to get the command with the name
                    Command command = commandManager.findCommand(commandName);

                    if (command != null) {
                        logger.info("Executing command: {}", line);

                        String[] cmdArgs = Arrays.copyOfRange(split, 1, split.length);
                        command.execute(cmdArgs);
                    } else {
                        logger.info("Command not found!");
                    }
                }
                System.out.print(" > ");
            }
        } catch (IllegalStateException ignore) {}
    }


    /**
     * Stop the IO
     */
    public void stop() {

        logger.info("IO is going to be stopped");

        // Close the scanner
        scanner.close();

        try {
            restServer.stop();
            logger.debug("RESTful API server stopped");
        } catch (Exception e) {
            logger.warn("RESTful API server already stopped!");
        }

        athena.close();
        logger.debug("Athena(MySQL) connection closed");

        publisher.disconnect(true);
        subscriber.disconnect(true);
        pubSubCache.disconnect(true);
        logger.debug("Thor connection closed");

        logger.info("IO has been stopped");

        // Explicitly exit
        System.exit(0);
    }

    private void connectThor() {
        try {

            // initialize thor
            Header thorHeader = irisConfig.getHeader("thor");
            Key thorHostKey = thorHeader.getKey("host");
            publisher = PublisherFactory.create(thorHostKey.getValue(0).asString(), thorHostKey.getValue(1).asInt());
            subscriber = SubscriberFactory.create(thorHostKey.getValue(0).asString(), thorHostKey.getValue(1).asInt(), "de/candycraft/io/server");
            pubSubCache = PubSubCacheFactory.create(thorHostKey.getValue(0).asString(), thorHostKey.getValue(1).asInt());
            logger.debug("Connected to Thor(pubsubcache)");
        } catch(ConnectException ex) {

            logger.error("Error while connecting to Thor - trying again in 5s..");
            try {
                Thread.sleep(5000);
            } catch(InterruptedException ignore) {}
            connectThor();
        }
    }

    private void connectAthena() {
        try {
            // let athena connect to the mysql
            athena.connect();
        } catch(HikariPool.PoolInitializationException ex) {

            logger.error("Error while connecting Athena(MySQL) - trying again in 5s..");
            try {
                Thread.sleep(5000);
            } catch(InterruptedException ignore) {}
            connectAthena();
        }
    }

    /**
     * Changes the debug level of the root logger
     * @param level the level what should be logged
     */
    public void changeDebug(Level level) {

        // Set the log level to debug or info based on the config value
        Main.getRootLogger().setLevel(level);

        logger.info("Logger level is now {}", Main.getRootLogger().getLevel());
    }

    /**
     * Toggle the debug level or the root logger
     */
    public void changeDebug() {

        // Change the log level based on the current level
        changeDebug((Main.getRootLogger().getLevel() == Level.INFO) ? Level.DEBUG : Level.INFO);
    }
}
