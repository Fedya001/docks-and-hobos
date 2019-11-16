package com.fedya.observer;

public interface EventObserver {

  // Observer with no parameters (neither push, nor pull implementation)
  // All it cares is that event happened
  void updateState();
}