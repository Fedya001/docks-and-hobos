package com.fedya.run;

public enum ModelSettings {
  NARROW_CHANNEL_CAPACITY(5),
  HOBOS_NUMBER(8),
  COOKING_HOBOS_NUMBER(2),
  SHIP_UNLOAD_SPEED(2_000),
  SHIP_UNLOAD_COUNT(5),
  STEAL_DURATION(2_000),
  GENERATOR_FREQUENCY(3_000),
  SHIP_CHANNEL_AWAIT_TIME(4_000);

  private int value;

  ModelSettings(int value) {
    this.value = value;
  }

  public int getValue() {
    return value;
  }
}
