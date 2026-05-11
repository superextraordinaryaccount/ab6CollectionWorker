package enviroment.workerclass;

import java.io.Serializable;

/**
 * Координаты работника.
 */
public class Coordinates implements Serializable {
    private Double x; // Поле не может быть null
    private Float y; // Максимальное значение поля: 289, не null

    public Coordinates(Double x, Float y) {
        setX(x);
        setY(y);
    }

    public Double getX() {
        return x;
    }

    public void setX(Double x) {
        if (x == null) throw new IllegalArgumentException("Нет информации о координате по X");
        this.x = x;
    }

    public Float getY() {
        return y;
    }

    public void setY(Float y) {
        if (y == null) throw new IllegalArgumentException("Нет информации о координате по X");
        if (y > 289) throw new IllegalArgumentException("y превышает значение 289");
        this.y = y;
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }
}
