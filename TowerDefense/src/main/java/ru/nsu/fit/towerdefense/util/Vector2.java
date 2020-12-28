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

  public static Vector2<Double> negate(Vector2<Double> vector) {
    return new Vector2<>(-vector.getX(), -vector.getY());
  }

  public static Vector2<Double> add(Vector2<Double> a, Vector2<Double> b) {
    return new Vector2<>(a.getX() + b.getX(), a.getY() + b.getY());
  }

  public static Vector2<Double> subtract(Vector2<Double> a, Vector2<Double> b) {
    return new Vector2<>(a.getX() - b.getX(), a.getY() - b.getY());
  }

  public static double dotProduct(Vector2<Double> a, Vector2<Double> b) {
    return a.getX() * b.getX() + a.getY() * b.getY();
  }

  public static double distance(Vector2<Double> a, Vector2<Double> b) {
    return norm(subtract(a, b));
  }

  public static double norm(Vector2<Double> a) {
    return Math.sqrt(a.getX() * a.getX() + a.getY() * a.getY());
  }

  public static Vector2<Double> multiply(double a, Vector2<Double> v) {
    return new Vector2<>(a * v.getX(), a * v.getY());
  }
}
