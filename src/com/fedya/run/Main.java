package com.fedya.run;

import com.fedya.stuff.BurningBarrel;
import com.fedya.stuff.Dock;
import com.fedya.stuff.FoodBlock.Type;
import com.fedya.thread.DocksDistributor;
import com.fedya.thread.Hobo;
import com.fedya.thread.Ship;
import com.fedya.utils.ShipGenerator;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Phaser;

public class Main {

  // This are just random names, all matches are random
  private static final List<String> HOBOS_NAMES = new ArrayList<String>() {{
    add("Paddington");
    add("Patric");
    add("Jake");
    add("Mike");
    add("Vlad");
    add("Huston");
    add("Martin");
    add("Lora");
  }};

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

    List<Hobo> hobos = new ArrayList<Hobo>();

    Phaser hoboPhaser = new Phaser();
    BurningBarrel burningBarrel = new BurningBarrel(hoboPhaser);
    for (int hoboIndex = 0; hoboIndex < ModelSettings.HOBOS_NUMBER.getValue(); ++hoboIndex) {
      Hobo hobo = new Hobo(HOBOS_NAMES.get(hoboIndex), hoboPhaser, burningBarrel,
        breadDock, sausageDock, mayoDock);
      hobos.add(hobo);
      hobo.start();
    }

    // Main thread waits for a while generator creates at least one ship
    try {
      Thread.sleep(50);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
}
