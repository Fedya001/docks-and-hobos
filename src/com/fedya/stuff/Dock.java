package com.fedya.stuff;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import com.fedya.run.ModelSettings;
import com.fedya.thread.Ship;
import java.util.concurrent.locks.ReentrantLock;
import org.slf4j.LoggerFactory;

public class Dock {

  String name;
  private FoodBlock storage;
  private ReentrantLock lock;

  private Logger logger;

  public Dock(String name, FoodBlock initialStorage) {
    this.name = name;
    this.storage = initialStorage;
    this.lock = new ReentrantLock();

    this.logger = (Logger) LoggerFactory.getLogger(name);
    logger.setLevel(Level.INFO);
  }

  public Dock(String name, FoodBlock.Type foodType) {
    this(name, new FoodBlock(foodType, 0));
  }

  public String getName() {
    return name;
  }

  public FoodBlock getStorage() {
    return storage;
  }

  public ReentrantLock getLock() {
    return lock;
  }

  public void unloadShip(Ship ship) {
    // Dock itself is not a thread, but unloading runs separate thread
    new Thread(logger.getName() + " unloading thread") {
      @Override
      public void run() {
        try {
          lock.lock();
          logger.info("Start unloading {}", ship);
          while (!ship.getFoodBlock().empty()) {
            ship.getFoodBlock().extractItems(ModelSettings.SHIP_UNLOAD_COUNT.getValue());
            logger.info("Unloaded {} {}s from {}", ModelSettings.SHIP_UNLOAD_COUNT.getValue(),
              ship.getFoodBlock().getType().toString(), ship.getShipName());
            Thread.sleep(ModelSettings.SHIP_UNLOAD_SPEED.getValue());
          }
          logger.info("SUCCESS: unloaded {}. {} goes back home!", ship, ship);
        } catch (InterruptedException ex) {
          logger.error(ex.getMessage(), ex);
        } finally {
          lock.unlock();
        }

        // Resume ship thread
        synchronized (ship) {
          ship.notify();
        }
      }
    }.start();
  }
}
