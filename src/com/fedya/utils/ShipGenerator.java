package com.fedya.utils;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import com.fedya.run.ModelSettings;
import com.fedya.stuff.Dock;
import com.fedya.stuff.FoodBlock;
import com.fedya.thread.Ship;
import com.fedya.thread.Ship.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import org.slf4j.LoggerFactory;

public class ShipGenerator extends Thread {

  private BlockingQueue<Ship> narrowChannel;
  private List<Dock> docks;

  private Logger logger;

  private Random generator;
  private final List<String> SHIP_NAMES_POOL = new ArrayList<String>() {
    {
      add("Titanic%d ship");
      add("Aurora%d ship");
      add("Dolphin%d ship");
      add("Pioneer%d ship");
      add("Mary%d ship");
      add("Secret%d ship");
      add("Emma%d ship");
      add("Thunder%d ship");
      add("Jellyfish%d ship");
      add("Marine%d band ship");
      add("Jane%d ship");
    }
  };

  public ShipGenerator(
    String name,
    BlockingQueue<Ship> narrowChannel,
    Dock breadDock, Dock sausageDock, Dock mayoDock
  ) {
    super(name + " thread");
    // Generator with infinite loop is daemon
    setDaemon(true);

    this.narrowChannel = narrowChannel;
    this.docks = new ArrayList<Dock>() {{
      add(breadDock);
      add(sausageDock);
      add(mayoDock);
    }};

    this.logger = (Logger) LoggerFactory.getLogger(name);
    logger.setLevel(Level.INFO);
    this.generator = new Random();
  }

  @Override
  public void run() {
    while (true) {
      int foodTypeIndex = Math.abs(generator.nextInt()) % FoodBlock.Type.values().length;
      FoodBlock.Type foodType = FoodBlock.Type.values()[foodTypeIndex];

      Ship ship = new Ship(
        String.format(SHIP_NAMES_POOL.get(Math.abs(generator.nextInt()) % SHIP_NAMES_POOL.size()),
          Math.abs(new Random().nextInt()) % 100),
        Ship.Type.values()[Math.abs(generator.nextInt()) % Ship.Type.values().length],
        foodType,
        docks.get(foodTypeIndex),
        narrowChannel
      );

      ship.start();

      logger.info("Created a {}", ship);
      try {
        Thread.sleep(ModelSettings.GENERATOR_FREQUENCY.getValue());
      } catch (InterruptedException ex) {
        logger.error(ex.getMessage(), ex);
      }
    }
  }
}
