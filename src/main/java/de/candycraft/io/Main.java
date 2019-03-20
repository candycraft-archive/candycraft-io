package de.candycraft.io;

import ch.qos.logback.classic.Level;
import de.progme.iris.Iris;
import de.progme.iris.IrisConfig;
import de.progme.iris.config.Header;
import de.progme.iris.config.Key;
import de.progme.iris.config.Value;
import de.progme.iris.exception.IrisException;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 * Created by Marvin Erkes on 2019-03-20.
 */
public class Main {

    private static final String IO_PACKAGE_NAME = "de.candycraft.io";

    @Getter
    private static ch.qos.logback.classic.Logger rootLogger = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(IO_PACKAGE_NAME);

    private static Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] arguments) {

        // get current time millis to calculate the whole time of the start of this plugin.
        double currentTimeMillis = System.currentTimeMillis();

        // set the level of the root logger to info
        rootLogger.setLevel(Level.INFO);

        logger.info("Input Output(alias IO) v" + Main.class.getPackage().getImplementationVersion());

        // copy the config to the plugin folder path
        File config = new File("config.iris");

        if (!config.exists())

            try {
                Files.copy(Main.class.getClassLoader().getResourceAsStream("config.iris"), config.toPath());
            } catch (IOException exception) {

                logger.error("Unable to copy default config! No write permissions?", exception);

                System.exit(0);

                return;
            }

        // get the config as cope config
        try {

            IrisConfig irisConfig = Iris.from(config)
                    .def(new Header("general"), new Key("host"), new Value("0.0.0.0"), new Value("8080"))
                    .def(new Header("general"), new Key("logger"), new Value("INFO"))
                    .def(new Header("mysql"), new Key("host"), new Value("0.0.0.0"), new Value("3306"))
                    .def(new Header("mysql"), new Key("authentication"), new Value("user"), new Value("password"))
                    .def(new Header("mysql"), new Key("database"), new Value("yourdatabase"))
                    .def(new Header("mysql"), new Key("poolsize"), new Value("10"))
                    .def(new Header("thor"), new Key("host"), new Value("0.0.0.0"), new Value("1337"))
                    .build();

            logger.info("Config loaded");

            // create a ts3bot server instance
            IO io = new IO(irisConfig);

            // start the ts3bot server instance
            io.start(currentTimeMillis);
            io.console();
        } catch (IrisException exception) {

            logger.error("Unable to load config", exception);

            System.exit(0);
        }
    }
}
