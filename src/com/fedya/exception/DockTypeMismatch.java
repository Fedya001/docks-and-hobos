package com.fedya.exception;

import com.fedya.stuff.FoodBlock;

public class DockTypeMismatch extends RuntimeException {

  public DockTypeMismatch(String message, FoodBlock.Type dockFoodType,
    FoodBlock.Type shipFoodType) {
    super(String.format("%s\nDock can't accept food from ship: %s != %s",
      message, dockFoodType, shipFoodType));
  }
}
