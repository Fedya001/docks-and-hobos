package com.fedya.run;

public enum ModelSettings {
  NARROW_CHANNEL_CAPACITY(5),
  HOBOS_NUMBER(8),
  COOKING_HOBOS_NUMBER(2),
  SHIP_UNLOAD_SPEED(5_000),
  STEAL_DURATION(3_000);

  private int value;

  ModelSettings(int value) {
    this.value = value;
  }

  public int getValue() {
    return value;
  }
}
