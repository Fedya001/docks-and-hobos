package com.fedya.run;

import com.fedya.stuff.FoodBlock;

public class Main {

  public static void main(String[] args) {
    FoodBlock block = new FoodBlock(FoodBlock.Type.BREAD, -1);
    System.out.print(block);
  }
}
