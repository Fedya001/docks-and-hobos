package com.fedya.stuff;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import com.fedya.observer.EventObserver;
import com.fedya.observer.ObservableEvent;
import com.fedya.run.ModelSettings;
import com.fedya.thread.Ship;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.Phaser;
import java.util.concurrent.ThreadPoolExecutor;
import org.slf4j.LoggerFactory;

public class BurningBarrel implements ObservableEvent {

  private Phaser phaser;

  // CountDownLatch is used for synchronized notification of subscribers
  private CountDownLatch breadLatch;
  private CountDownLatch sausageLatch;
  private CountDownLatch mayoLatch;

  private List<EventObserver> observers;
  private static final String MEAL_THREAD_NAME = "Meal";

  private ThreadPoolExecutor notifyingThreadPool;
  private Logger logger;

  private class NotifyTask implements Runnable {
    @Override
    public void run() {
      try {
        breadLatch.await();
        sausageLatch.await();
        mayoLatch.await();
      } catch (InterruptedException e) {
        e.printStackTrace();
      }

      notifyObservers();
      logger.info("Notified observers. Sandwich #{}", phaser.getPhase());
    }
  }

  public BurningBarrel(Phaser phaser) {
    this.notifyingThreadPool = (ThreadPoolExecutor) Executors.newFixedThreadPool(1);
    this.observers = new ArrayList<EventObserver>();
    this.reset();

    this.logger = (Logger) LoggerFactory.getLogger(MEAL_THREAD_NAME);
    logger.setLevel(Level.INFO);
  }

  public void reset() {
    breadLatch = new CountDownLatch(ModelSettings.HOBOS_NUMBER.getValue());
    sausageLatch = new CountDownLatch(ModelSettings.HOBOS_NUMBER.getValue());
    mayoLatch = new CountDownLatch(ModelSettings.HOBOS_NUMBER.getValue());
    notifyingThreadPool.execute(new NotifyTask());
  }

  public CountDownLatch getLatchByType(FoodBlock.Type type) {
    switch (type) {
      case BREAD:
        return breadLatch;
      case SAUSAGE:
        return sausageLatch;
      case MAYO:
        return mayoLatch;
    }
    return null;
  }

  @Override
  public void registerObserver(EventObserver observer) {
    observers.add(observer);
  }

  @Override
  public void unregisterObserver(EventObserver observer) {
    observers.add(observer);
  }

  @Override
  public void notifyObservers() {
    for (EventObserver observer : observers) {
      observer.updateState();
    }
  }
}
