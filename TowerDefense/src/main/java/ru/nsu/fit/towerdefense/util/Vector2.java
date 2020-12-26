package ru.nsu.fit.towerdefense.util;

public class Vector2<T> {
  private T x;
  private T y;

  public Vector2(T x, T y) {
    this.x = x;
    this.y = y;
  }

  /**
   * Copies another Vector2 (creates a Vector2 with same coordinates)
   *
   * @param other the vector to be copied
   */
  public Vector2(Vector2<T> other) {
    this(other.getX(), other.getY());
  }

  public T getX() {
    return x;
  }

  public void setX(T x) {
    this.x = x;
  }

  public T getY() {
    return y;
  }

  public void setY(T y) {
    this.y = y;
  }

  @Override
  public String toString() {
    return "(" + x + ":" + y + ")";
  }
}
