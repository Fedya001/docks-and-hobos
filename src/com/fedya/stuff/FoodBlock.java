package com.fedya.stuff;

import com.fedya.exception.InvalidFoodBlockState;

public class FoodBlock {
  public enum Type {
    BREAD,
    SAUSAGE,
    MAYO
  }

  private Type type;
  private int count;

  public FoodBlock(Type type, int count) {
    this.type = type;
    this.count = count;
    if (count < 0) {
      throw new InvalidFoodBlockState("Can't initialize FoodBlock with negative count", this);
    }
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

  public void extractItem() {
    if (count <= 0) {
      throw new InvalidFoodBlockState("FoodBlock is empty, can't extract item", this);
    }
    --count;
  }

  public void addItem() {
    ++count;
  }

  @Override
  public String toString() {
    return String.format("<%s: %d>", type.name(), count);
  }
}
