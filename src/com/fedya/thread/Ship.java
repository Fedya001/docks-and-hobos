package com.fedya.thread;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.pattern.ClassNameOnlyAbbreviator;
import com.fedya.exception.DockTypeMismatch;
import com.fedya.run.ModelSettings;
import com.fedya.stuff.Dock;
import com.fedya.stuff.FoodBlock;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import org.slf4j.LoggerFactory;

public class Ship extends Thread {

  public enum Type {
    SMALL(10),
    MEDIUM(20),
    LARGE(30);

    private int value;

    Type(int value) {
      this.value = value;
    }

    public int getValue() {
      return value;
    }
  }

  private String shipName;
  private Type type;
  private FoodBlock foodBlock;

  private Dock acceptingDock;
  private BlockingQueue<Ship> narrowChannel;

  private Logger logger;

  public Ship(
    String shipName, Type type, FoodBlock.Type foodType,
    Dock acceptingDock, BlockingQueue<Ship> narrowChannel
  ) {
    super(shipName + " thread");

    this.shipName = shipName;
    this.type = type;
    this.foodBlock = new FoodBlock(foodType, type.getValue());

    if (acceptingDock.getStorage().getType() != foodBlock.getType()) {
      throw new DockTypeMismatch("Error while assigning acceptingDock",
        acceptingDock.getStorage().getType(), foodBlock.getType());
    }

    this.acceptingDock = acceptingDock;
    this.narrowChannel = narrowChannel;

    this.logger = (Logger) LoggerFactory
      .getLogger(this.toString());
    logger.setLevel(Level.INFO);
  }

  public Type getType() {
    return type;
  }

  public FoodBlock getFoodBlock() {
    return foodBlock;
  }

  @Override
  public synchronized void run() {
    logger.info("Forward towards adventure!");

    // First of all tread tries to push himself to narrowChannel
    try {
      if (narrowChannel.offer(this, ModelSettings.SHIP_CHANNEL_AWAIT_TIME.getValue(),
        TimeUnit.MILLISECONDS)) {
        logger.info("Successfully joined in narrow channel. Narrow channel: {} out of {}",
          narrowChannel.size(), ModelSettings.NARROW_CHANNEL_CAPACITY.getValue() - 1);
      } else {
        logger.warn("Await time expired, drowning ...");
        return;
      }
    } catch (InterruptedException ex) {
      logger.error(ex.getMessage(), ex);
    }

    // Than thread stops it waits until DocksDistributor assigns it to
    // specific Dock and then the Dock after unloading unlocks it
    try {
      wait();
    } catch (InterruptedException ex) {
      logger.error(ex.getMessage(), ex);
    }

    logger.info("Bye everyone. I go back home!");
  }

  public String getShipName() {
    return shipName;
  }

  @Override
  public String toString() {
    return String.format("%s (%s, %s)", shipName, type, foodBlock.getType());
  }

}
