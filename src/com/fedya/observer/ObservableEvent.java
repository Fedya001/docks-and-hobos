package com.fedya.observer;

public interface ObservableEvent {

  void registerObserver(EventObserver observer);

  void unregisterObserver(EventObserver observer);

  void notifyObservers();
}
