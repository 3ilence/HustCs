package homework.before_ch11;

import java.util.ArrayList;
import java.util.List;

public class Fan {
    public final int SLOW = 1;
    public final int MEDIUM = 2;
    public final int FAST = 3;
    private int speed  = SLOW;
    private boolean on = false;
    private double radius = 5.0;
    String color = "blue";

    public Fan() {
    }

    public int getSpeed() {
        return speed;
    }

    public boolean isOn() {
        return on;
    }

    public double getRadius() {
        return radius;
    }

    public String getColor() {
        return color;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public void setOn(boolean on) {
        this.on = on;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public void setColor(String color) {
        this.color = color;
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        if (on ) {
            str.append("speed: " + speed + "\ncolor: " + color + "\nradiue: " + radius);
        } else {
            str.append("fan is off\ncolor: " + color + "\nradius: " + radius  );
        }
        return str.toString();
    }

    public static void main(String[] args) {
        Fan fan1 = new Fan();
        Fan fan2 = new Fan();
        fan1.setSpeed(fan1.FAST);
        fan1.setRadius(10.0);
        fan1.setColor("yellow");
        fan1.setOn(true);
        fan2.setSpeed(fan2.MEDIUM);
        //fan2.setColor("blue);
        //fan2.setRadius(5.0);
        System.out.println("fan1: \n" + fan1.toString());
        System.out.println("fan2: \n" + fan2.toString());
    }
}

