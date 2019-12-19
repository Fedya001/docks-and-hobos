package com.fedya.thread;

import com.fedya.stuff.FoodBlock;

public class Ship {

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
  private Type shipType;
  private FoodBlock foodBlock;

  public Ship(String shipName, Type shipType, FoodBlock.Type foodType) {
    this.shipName = shipName;
    this.shipType = shipType;
    this.foodBlock = new FoodBlock(foodType, shipType.getValue());
  }

  public Type getShipType() {
    return shipType;
  }

  public FoodBlock getFoodBlock() {
    return foodBlock;
  }

  public String getShipName() {
    return shipName;
  }

  @Override
  public String toString() {
    return String.format("%s (%s, %s)", shipName, shipType, foodBlock.getType());
  }

}
