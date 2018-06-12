package edu.agh.utils;

public class Pair<T, K> {

    private T leftValue;
    private K rightValue;

    public Pair(T leftValue, K rightValue) {
        this.leftValue = leftValue;
        this.rightValue = rightValue;
    }

    public T getLeftValue() {
        return leftValue;
    }

    public void setLeftValue(T leftValue) {
        this.leftValue = leftValue;
    }

    public K getRightValue() {
        return rightValue;
    }

    public void setRightValue(K rightValue) {
        this.rightValue = rightValue;
    }

    @Override
    public String toString() {
        return "[" + leftValue + ", " + rightValue + "]";
    }
}
