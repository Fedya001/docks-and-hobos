package com.fedya.exception;

import com.fedya.stuff.FoodBlock;

public class InvalidFoodBlockState extends RuntimeException {

  public InvalidFoodBlockState(String message, FoodBlock foodBlock) {
    super(String.format("%s\n%s", message, foodBlock));
  }

}
