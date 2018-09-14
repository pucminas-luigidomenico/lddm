package pucminas.computacao.luigi.agenda;

import android.support.annotation.NonNull;

/**
 * Person class that represents Guest or Organizer.
 */
public class Person implements Comparable<Person>, Cloneable {
    private String  name;
    private String  email;
    private String  phone;
    private String  personType;

    public Person(String name, String email, String phone, String personType) {
        this.name       = name;
        this.email      = email;
        this.phone      = phone;
        this.personType = personType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    String getEmail() {
        return email;
    }

    String getPhone() {
        return phone;
    }

    String getPersonType() {
        return personType;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        super.clone();
        return new Person(name, email, phone, personType);
    }

    @Override
    public int compareTo(@NonNull Person person) {
        return this.name.compareTo(person.getName());
    }
}
