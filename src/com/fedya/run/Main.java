package com.fedya.run;

import com.fedya.stuff.FoodBlock;

public class Main {

  public static void main(String[] args) {
    FoodBlock block = new FoodBlock(FoodBlock.Type.BREAD, 5);
    FoodBlock block2 = new FoodBlock(FoodBlock.Type.MAYO, 6);
    block2.joinBlock(block);
    System.out.print(block2);
  }
}
