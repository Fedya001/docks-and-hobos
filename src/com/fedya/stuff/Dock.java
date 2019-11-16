package com.fedya.stuff;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import com.fedya.run.ModelSettings;
import com.fedya.thread.Ship;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
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

        boolean unloaded;
        while (!ship.getFoodBlock().empty()) {
          unloaded = false;

          // Wait a bit to get a lock
          if (lock.tryLock(50, TimeUnit.MILLISECONDS)) {
            logger.debug("Get a lock of {}", name);

            ship.getFoodBlock().extractItems(ModelSettings.SHIP_UNLOAD_COUNT.getValue());
            storage.addItems(ModelSettings.SHIP_UNLOAD_COUNT.getValue());
            unloaded = true;

            logger.info("Unloading {} {}s from {}... DONE. "
                + "{Dock: {} left. Ship: {} left}", ModelSettings.SHIP_UNLOAD_COUNT.getValue(),
              ship.getFoodBlock().getType().toString(), ship.getShipName(),
              storage.getCount(), ship.getFoodBlock().getCount());

            lock.unlock();
            logger.debug("Release a lock of {}", name);
          } else {
            logger.debug("Didn't get a lock of {}", name);
          }

          if (unloaded) {
            // Unloading thread sleeps outside of the lock
            // (otherwise hobos wouldn't be able to steal anything)
            Thread.sleep(ModelSettings.SHIP_UNLOAD_SPEED.getValue());
          }
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
