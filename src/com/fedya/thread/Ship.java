package com.fedya.thread;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import com.fedya.exception.DockTypeMismatch;
import com.fedya.stuff.Dock;
import com.fedya.stuff.FoodBlock;
import java.util.concurrent.BlockingQueue;
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

  private Type type;
  private FoodBlock foodBlock;

  private Dock acceptingDock;
  private BlockingQueue<Ship> narrowChannel;

  private Logger logger;

  public Ship(
    String name, Type type, FoodBlock.Type foodType,
    Dock acceptingDock, BlockingQueue<Ship> narrowChannel
  ) {
    super(name);

    this.type = type;
    this.foodBlock = new FoodBlock(foodType, type.getValue());

    if (acceptingDock.getStorage().getType() != foodBlock.getType()) {
      throw new DockTypeMismatch("Error while assigning acceptingDock",
        acceptingDock.getStorage().getType(), foodBlock.getType());
    }

    this.acceptingDock = acceptingDock;
    this.narrowChannel = narrowChannel;

    this.logger = (Logger) LoggerFactory.getLogger(this.getName());
    logger.setLevel(Level.INFO);
  }

  public Type getType() {
    return type;
  }

  public FoodBlock getFoodBlock() {
    return foodBlock;
  }

  public void acceptDock(Dock dock) {
    dock.unloadShip(this);
  }

  @Override
  public void run() {

  }

}
