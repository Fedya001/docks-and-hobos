package com.fedya.stuff;

import java.util.concurrent.Phaser;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

// This class represents a cookery place.
// After each synchronized with Phaser iteration all hobo-threads strive to get a cookery place.
// Otherwise hobo will do more difficult job: steal and carry food
public class CookeryPlace {

  private Lock lock;

  public CookeryPlace() {
    lock = new ReentrantLock();
  }

  public boolean grab(Phaser phaser) {
    if (!lock.tryLock()) {
      return false;
    }
    phaser.arriveAndAwaitAdvance();
    lock.unlock();
    return true;
  }

}
