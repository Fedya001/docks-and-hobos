package com.fedya.thread;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import com.fedya.observer.EventObserver;
import com.fedya.run.ModelSettings;
import com.fedya.stuff.BurningBarrel;
import com.fedya.stuff.CookeryPlace;
import com.fedya.stuff.Dock;
import com.fedya.stuff.FoodBlock;
import com.fedya.stuff.FoodBlock.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Phaser;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.slf4j.LoggerFactory;

public class Hobo extends Thread implements EventObserver {

  private final List<String> HOBO_PHRASE_POOL = new ArrayList<String>() {
    {
      add("Ye, that was nice, 10q guys!");
      add("Mmm... such a yummy sandwich :)");
      add("Yo, brothers! I am full!");
      add("Oh, god. I have never eaten anything like that before!");
      add("Thanks everybody. It's time to work!");
      add("My stomach now fells better :)");
    }
  };

  private String name;
  private Dock breadDock;
  private Dock sausageDock;
  private Dock mayoDock;

  // This eatSandwichLock unlocks when sandwich is ready.
  private Lock eatSandwichLock;

  // All hobos are synchronized after each sandwich
  private Phaser phaser;
  private BurningBarrel burningBarrel;

  // After each synchronized iteration all hobos strive to get a Fireplace
  private List<CookeryPlace> cookeryPlaces;

  private Random random;
  private Logger logger;

  public Hobo(String name, Phaser phaser, BurningBarrel burningBarrel,
    List<CookeryPlace> cookeryPlaces,
    Dock breadDock, Dock sausageDock, Dock mayoDock) {

    super(name + " thread");
    // Hobos are also daemons
    setDaemon(true);

    this.breadDock = breadDock;
    this.sausageDock = sausageDock;
    this.mayoDock = mayoDock;
    this.eatSandwichLock = new ReentrantLock();
    eatSandwichLock.lock();

    this.phaser = phaser;
    this.phaser.register();
    this.burningBarrel = burningBarrel;

    this.cookeryPlaces = cookeryPlaces;

    this.random = new Random();
    this.logger = (Logger) LoggerFactory.getLogger(name);
    logger.setLevel(Level.INFO);
  }

  @Override
  public void run() {
    logger.info("I am born!");
    final double TALKATIVENESS_FACTOR = 0.25;

    // Hobo behaviour pattern cycle
    while (true) {
      // Step 1. Try to get a cookeryPlace
      boolean grabbed = false;
      for (CookeryPlace cookeryPlace : cookeryPlaces) {
        if (cookeryPlace.grab(phaser, logger)) {
          // Hobo succeed to get a cookeryPlace
          // All cookery hobos are synced at this point
          grabbed = true;
          break;
        }
      }

      if (!grabbed) {
        boolean stole;
        // Hobo didn't get a cookeryPlace and have to steal and carry food
        while (!eatSandwichLock.tryLock()) {
          stole = false;

          int stealIndex = Math.abs(random.nextInt()) % Type.values().length;
          Dock dock = getDockByType(FoodBlock.Type.values()[stealIndex]);

          // TODO: now if sandwich was prepared, when hobo started to steal food,
          //  then all hobos will wait that hobo, until he comes
          try {
            // Wait a bit to get a lock
            if (dock.getLock().tryLock(50, TimeUnit.MILLISECONDS)) {
              logger.debug("Get a lock of {} dock", FoodBlock.Type.values()[stealIndex]);
              if (!dock.getStorage().empty()) {
                dock.getStorage().extractItems(1);
                stole = true;

                logger.info("Steal a {} from {} dock. Dock: {} left",
                  FoodBlock.Type.values()[stealIndex],
                  FoodBlock.Type.values()[stealIndex],
                  dock.getStorage().getCount());
                burningBarrel.getLatchByType(FoodBlock.Type.values()[stealIndex]).countDown();
              }
              dock.getLock().unlock();
              logger.debug("Release {} dock lock", FoodBlock.Type.values()[stealIndex]);
            } else {
              logger.debug("Didn't get a lock of {} dock", FoodBlock.Type.values()[stealIndex]);
            }

            if (stole) {
              // Hobo sleeps outside of the lock
              // (otherwise unloading thread wouldn't be able to unload anything)
              Thread.sleep(ModelSettings.STEAL_DURATION.getValue());
            }
          } catch (InterruptedException ex) {
            logger.error(ex.getMessage(), ex);
          }
        }
        // All working hobos are synced at this point
        phaser.arriveAndAwaitAdvance();

        if (random.nextDouble() < TALKATIVENESS_FACTOR) {
          logger.info(HOBO_PHRASE_POOL.get(Math.abs(random.nextInt()) % HOBO_PHRASE_POOL.size()));
        }
      }
    }
  }

  @Override
  public void updateState() {
    // ObservableEvent notifies subscribers, i.e. unlocks the lock
    eatSandwichLock.unlock();
  }

  private Dock getDockByType(FoodBlock.Type type) {
    switch (type) {
      case BREAD:
        return breadDock;
      case SAUSAGE:
        return sausageDock;
      default:
        return mayoDock;
    }
  }
}
