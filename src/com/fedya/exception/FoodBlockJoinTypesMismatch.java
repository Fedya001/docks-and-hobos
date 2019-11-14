package com.fedya.exception;

import com.fedya.stuff.FoodBlock;

public class FoodBlockJoinTypesMismatch extends RuntimeException {
  public FoodBlockJoinTypesMismatch(FoodBlock.Type lhs, FoodBlock.Type rhs) {
    super(String.format("Can't join FoodBlocks: %s != %s", lhs, rhs));
  }
}
