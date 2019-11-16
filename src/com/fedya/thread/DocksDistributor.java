package com.fedya.thread;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import com.fedya.run.ModelSettings;
import com.fedya.stuff.Dock;
import java.util.concurrent.BlockingQueue;
import org.slf4j.LoggerFactory;

public class DocksDistributor extends Thread {

  private BlockingQueue<Ship> narrowChannel;
  private Dock breadDock;
  private Dock sausageDock;
  private Dock mayoDock;

  private Logger logger;

  public DocksDistributor(
    String name,
    BlockingQueue<Ship> narrowChannel,
    Dock breadDock, Dock sausageDock, Dock mayoDock
  ) {
    super(name + " thread");

    this.narrowChannel = narrowChannel;
    this.breadDock = breadDock;
    this.sausageDock = sausageDock;
    this.mayoDock = mayoDock;

    this.logger = (Logger) LoggerFactory.getLogger(name);
    logger.setLevel(Level.INFO);
  }

  @Override
  public void run() {
    while (true) {
      try {
        Ship ship = narrowChannel.take();
        logger.info("Pick a {}. Narrow channel: {} out of {}", ship,
          narrowChannel.size(), ModelSettings.NARROW_CHANNEL_CAPACITY.getValue() - 1);

        Dock targetDock = null;
        switch (ship.getFoodBlock().getType()) {
          case BREAD:
            targetDock = breadDock;
            break;
          case SAUSAGE:
            targetDock = sausageDock;
            break;
          case MAYO:
            targetDock = mayoDock;
            break;
        }

        // DocksDistributor and Dock inner unloading thread interact in the following way:
        // 1. DocksDistributor takes an element from narrowChannel
        // 2. Then it waits until the inner unloading tread unlocks the targetDock busyLock
        // (note that dock for new arrived ship product type may be free
        // and that is the whole point of running separate thread for unloading instead of
        // simply running unloading code in DocksDistributor thread)
        // 3. After inner thread unloaded a ship and unlocked the targetDock,
        // DocksDistributor locks the Dock and runs the unloading thread
        // (which waits until DockDistributor unlocks the Docks, but it happens in a moment)
        // A bit messy, but it works ... Please, suggest a fix.
        targetDock.getBusyLock().lock();
        logger.info("Push {} to {}", ship, targetDock.getName());
        targetDock.unloadShip(ship);
        targetDock.getBusyLock().unlock();
      } catch (InterruptedException ex) {
        logger.error(ex.getMessage(), ex);
      }
    }
  }
}
