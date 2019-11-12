package com.fedya.stuff;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import com.fedya.thread.Ship;
import java.util.concurrent.locks.ReentrantLock;
import org.slf4j.LoggerFactory;

public class Dock {

  private FoodBlock storage;
  private ReentrantLock lock;

  private Logger logger;

  public Dock(String name, FoodBlock initialStorage) {
    this.storage = initialStorage;
    this.lock = new ReentrantLock();

    this.logger = (Logger) LoggerFactory.getLogger(name);
    logger.setLevel(Level.INFO);
  }

  public Dock(String name, FoodBlock.Type foodType) {
    this(name, new FoodBlock(foodType, 0));
  }

  public FoodBlock getStorage() {
    return storage;
  }

  public ReentrantLock getLock() {
    return lock;
  }

  public void unloadShip(Ship ship) {
    try {
      logger.info("Start unloading {}", ship.getName());
      while (!ship.getFoodBlock().empty()) {
        ship.getFoodBlock().extractItem();
        Thread.sleep(5_000);
        logger.info("Unloaded a {}", ship.getFoodBlock().getType().toString());
      }
      logger.info("Unloaded {} successfully", ship.getName());
    } catch (InterruptedException ex) {
      logger.error(ex.getMessage(), ex);
    }
  }
}
