package enviroment.workerclass;
import java.io.Serializable;
import java.time.LocalDate;

/**
 * Личные данные работника.
 */
public class Person implements Serializable {
    private LocalDate birthday; // Поле не может быть null
    private Color eyeColor; // Поле может быть null
    private Color hairColor; // Поле не может быть null
    private Country nationality; // Поле не может быть null

    public Person(LocalDate birthday, Color eyeColor, Color hairColor, Country nationality) {
        setBirthday(birthday);
        setEyeColor(eyeColor);
        setHairColor(hairColor);
        setNationality(nationality);
    }

    public LocalDate getBirthday() {
        return birthday;
    }

    public void setBirthday(LocalDate birthday) {
        if (birthday == null) throw new IllegalArgumentException("Не указан день рождения");
        this.birthday = birthday;
    }

    public Color getEyeColor() {
        return eyeColor;
    }

    public void setEyeColor(Color eyeColor) {
        this.eyeColor = eyeColor;
    }

    public Color getHairColor() {
        return hairColor;
    }

    public void setHairColor(Color hairColor) {
        if (hairColor == null) throw new IllegalArgumentException("Не указан в");
        this.hairColor = hairColor;
    }

    public Country getNationality() {
        return nationality;
    }

    public void setNationality(Country nationality) {
        if (nationality == null) throw new IllegalArgumentException("nationality не может быть null");
        this.nationality = nationality;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Person person = (Person) obj;
        return birthday.equals(person.birthday) &&
                eyeColor == person.eyeColor &&
                hairColor == person.hairColor &&
                nationality == person.nationality;
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(birthday, eyeColor, hairColor, nationality);
    }

    @Override
    public String toString() {
        return String.format("Person{birthday=%s, eyeColor=%s, hairColor=%s, nationality=%s}",
                birthday, eyeColor, hairColor, nationality);
    }
}
