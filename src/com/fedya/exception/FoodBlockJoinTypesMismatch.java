package com.fedya.exception;

import com.fedya.stuff.FoodBlock;

public class FoodBlockJoinTypesMismatch extends RuntimeException {
  public FoodBlockJoinTypesMismatch(String message, FoodBlock.Type lhs, FoodBlock.Type rhs) {
    super(String.format("%s\nCan't join: %s != %s", message, lhs, rhs));
  }
}
