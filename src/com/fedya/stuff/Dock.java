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

  // This lock is intended for Hobo and Dock relations
  // It's discrete, i.e. thread doesn't sleep when locking it
  private Lock discreteLock;

  // This lock is intended for DocksDistributor and Dock relations
  // It is used to lock the Dock when it is busy with some ship and
  // not allow DockDistributor to give it a new task
  private Lock busyLock;

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
        busyLock.lock();
        logger.info("Start unloading {}", ship);

        boolean unloaded;
        while (!ship.getFoodBlock().empty()) {
          unloaded = false;

          // Wait a bit to get a lock
          if (discreteLock.tryLock(50, TimeUnit.MILLISECONDS)) {
            logger.debug("Get a lock of {}", name);

            ship.getFoodBlock().extractItems(ModelSettings.SHIP_UNLOAD_COUNT.getValue());
            storage.addItems(ModelSettings.SHIP_UNLOAD_COUNT.getValue());
            unloaded = true;

            logger.info("Unloading {} {}s from {}... DONE. "
                + "{Dock: {} left. Ship: {} left}", ModelSettings.SHIP_UNLOAD_COUNT.getValue(),
              ship.getFoodBlock().getType().toString(), ship.getShipName(),
              storage.getCount(), ship.getFoodBlock().getCount());

            discreteLock.unlock();
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
      } finally {
        busyLock.unlock();
      }
    }
  }

  public Dock(String name, FoodBlock initialStorage) {
    this.name = name;
    this.storage = initialStorage;

    this.discreteLock = new ReentrantLock();
    this.busyLock = new ReentrantLock();

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

  public Lock getDiscreteLock() {
    return discreteLock;
  }

  public Lock getBusyLock() {
    return busyLock;
  }

  public void unloadShip(Ship ship) {
    // Dock itself is not a thread, but unloading runs separate thread
    unloadingThreadPool.execute(new UnloadTask(ship));
  }
}
