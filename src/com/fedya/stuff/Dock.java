package com.fedya.stuff;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import com.fedya.run.ModelSettings;
import com.fedya.thread.Ship;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.slf4j.LoggerFactory;

public class Dock {

  private String name;
  private FoodBlock storage;
  private Lock lock;

  private ThreadPoolExecutor unloadingThreadPool;
  private Logger logger;


  private class UnloadTask implements Runnable {
    private Ship ship;

    public UnloadTask(Ship ship) {
      this.ship = ship;
    }

    @Override
    public void run() {
      try {
        logger.info("Start unloading {}", ship);

        while (!ship.getFoodBlock().empty()) {
          ship.getFoodBlock().extractItems(ModelSettings.SHIP_UNLOAD_COUNT.getValue());
          logger.info("Unloaded {} {}s from {}", ModelSettings.SHIP_UNLOAD_COUNT.getValue(),
            ship.getFoodBlock().getType().toString(), ship.getShipName());
          Thread.sleep(ModelSettings.SHIP_UNLOAD_SPEED.getValue());
          lock.lock();
          storage.addItems(ModelSettings.SHIP_UNLOAD_COUNT.getValue());
          lock.unlock();
        }
        logger.info("SUCCESS: unloaded {}. {} goes back home!", ship, ship);
      } catch (InterruptedException ex) {
        logger.error(ex.getMessage(), ex);
      }
    }
  }

  public Dock(String name, FoodBlock initialStorage) {
    this.name = name;
    this.storage = initialStorage;
    this.lock = new ReentrantLock();

    this.unloadingThreadPool = (ThreadPoolExecutor) Executors.newFixedThreadPool(1);

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

  public Lock getLock() {
    return lock;
  }

  public void unloadShip(Ship ship) {
    // Dock itself is not a thread, but unloading runs separate thread
    unloadingThreadPool.execute(new UnloadTask(ship));
  }
}
