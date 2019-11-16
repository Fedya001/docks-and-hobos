package com.fedya.stuff;

import com.fedya.exception.FoodBlockJoinTypesMismatch;
import java.util.concurrent.atomic.AtomicInteger;

public class FoodBlock {
  public enum Type {
    BREAD,
    SAUSAGE,
    MAYO
  }

  private Type type;
  private AtomicInteger count;

  public FoodBlock(Type type, int count) {
    if (count < 0) {
      throw new IllegalArgumentException("Can't initialize FoodBlock with negative count");
    }
    this.type = type;
    this.count = new AtomicInteger(count);
  }

  public Type getType() {
    return type;
  }

  public AtomicInteger getCount() {
    return count;
  }

  public boolean empty() {
    return count.get() == 0;
  }

  public synchronized void extractItems(int amount) {
    if (amount < 0 || count.get() - amount < 0) {
      throw new IllegalArgumentException("Invalid amount of products to extract");
    }
    count.addAndGet(-amount);
  }

  public synchronized void addItems(int amount) {
    if (amount < 0) {
      throw new IllegalArgumentException("Invalid amount of products to add");
    }
    count.addAndGet(amount);
  }

  public synchronized void joinBlock(FoodBlock other) {
    if (type != other.type) {
      throw new FoodBlockJoinTypesMismatch(type, other.type);
    }
    count.addAndGet(other.count.get());
  }

  @Override
  public String toString() {
    return String.format("<%s: %d>", type.name(), count.get());
  }
}
