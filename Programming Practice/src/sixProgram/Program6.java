package sixProgram;

/**
 * Created by binlix26 on 9/03/17.
 */
public class Program6 {
    public static void main(String[] args) {
        Animal an = new Animal("Dog");
        an.eating();
        an.screaming();

        System.out.println();

        Animal cat = new Cat("Sophie");
        cat.setName("Robin");
        cat.screaming();
        cat.eating();
    }
}

class Animal {
    private String name;

    Animal(String name) {
        this.name = name;
    }

    public void screaming() {
        System.out.println("I am an Animal!");
    }

    public void eating() {
        System.out.println("I eat everything!");
    }

    public void setName(String name) {
        this.name = name;
        System.out.println("Name is changed to: "+this.name);
    }
}

class Cat extends Animal {
    Cat(String name) {
        super(name);
    }

    @Override
    public void screaming() {
        System.out.println("I am a cat!");
    }

    @Override
    public void eating() {
        System.out.println("I like fish!");
    }
}
