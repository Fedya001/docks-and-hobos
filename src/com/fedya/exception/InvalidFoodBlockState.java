package com.fedya.exception;

import com.fedya.stuff.FoodBlock;

public class InvalidFoodBlockState extends Exception {

  public InvalidFoodBlockState(String message, FoodBlock foodBlock) {
    super(message + '\n' + foodBlock);
  }

}
