package com.fedya.stuff;

import com.fedya.exception.FoodBlockJoinTypesMismatch;

public class FoodBlock {
  public enum Type {
    BREAD,
    SAUSAGE,
    MAYO
  }

  private Type type;
  private int count;

  public FoodBlock(Type type, int count) {
    if (count < 0) {
      throw new IllegalArgumentException("Can't initialize FoodBlock with negative count");
    }
    this.type = type;
    this.count = count;
  }

  public Type getType() {
    return type;
  }

  public int getCount() {
    return count;
  }

  public boolean empty() {
    return count == 0;
  }

  public void extractItems(int amount) {
    if (amount < 0 || count - amount < 0) {
      throw new IllegalArgumentException("Invalid amount of products to extract");
    }
    count -= amount;
  }

  public void addItems(int amount) {
    if (amount < 0) {
      throw new IllegalArgumentException("Invalid amount of products to add");
    }
    count += amount;
  }

  public void joinBlock(FoodBlock other) {
    if (type != other.type) {
      throw new FoodBlockJoinTypesMismatch(type, other.type);
    }
    count += other.count;
  }

  @Override
  public String toString() {
    return String.format("<%s: %d>", type.name(), count);
  }
}
