package com.fedya.run;

import com.fedya.stuff.Dock;
import com.fedya.stuff.FoodBlock.Type;
import com.fedya.thread.Ship;
import com.fedya.utils.ShipGenerator;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class Main {

  public static void main(String[] args) {
    Dock breadDock = new Dock("Bread dock", Type.BREAD);
    Dock sausageDock = new Dock("Sausage dock", Type.SAUSAGE);
    Dock mayoDock = new Dock("Mayo dock", Type.MAYO);

    BlockingQueue<Ship> narrowChannel =
      new ArrayBlockingQueue<Ship>(ModelSettings.NARROW_CHANNEL_CAPACITY.getValue());

    ShipGenerator shipGenerator =
      new ShipGenerator("Generator", narrowChannel, breadDock, sausageDock, mayoDock);
    shipGenerator.start();

    try {
      shipGenerator.join();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
}
