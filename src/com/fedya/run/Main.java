package com.fedya.run;

import com.fedya.stuff.Dock;
import com.fedya.stuff.FoodBlock.Type;
import com.fedya.thread.DocksDistributor;
import com.fedya.thread.Ship;
import com.fedya.utils.ShipGenerator;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class Main {
  public static void main(String[] args) {
    Dock breadDock = new Dock("Bread dock", Type.BREAD);
    Dock sausageDock = new Dock("Sausage dock", Type.SAUSAGE);
    Dock mayoDock = new Dock("Mayo dock", Type.MAYO);

    // We create narrowChannel one size less, because one ship
    // is almost always stored in DocksDistributor
    BlockingQueue<Ship> narrowChannel =
      new ArrayBlockingQueue<Ship>(ModelSettings.NARROW_CHANNEL_CAPACITY.getValue() - 1);

    DocksDistributor distributor =
      new DocksDistributor("Docks distributor", narrowChannel, breadDock, sausageDock, mayoDock);
    distributor.start();

    ShipGenerator shipGenerator =
      new ShipGenerator("Generator", narrowChannel, breadDock, sausageDock, mayoDock);
    shipGenerator.start();

    // Main thread waits for a while generator creates at least one ship
    try {
      Thread.sleep(50);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
}
