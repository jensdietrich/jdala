package nz.ac.wgtn.ecs.jdala.realWorldExamples;

import nz.ac.wgtn.ecs.jdala.StaticAgentTests;
import nz.ac.wgtn.ecs.jdala.annotation.Immutable;
import nz.ac.wgtn.ecs.jdala.exceptions.DalaCapabilityViolationException;
import nz.ac.wgtn.ecs.jdala.exceptions.DalaRestrictionException;
import org.junit.jupiter.api.Test;
import org.junit.rules.ExpectedException;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

public class DeepImmutabilityTests extends StaticAgentTests {

    @Test
    public void ShallowImmutabilityTest() throws InterruptedException {
        Collection<Person> people = new TreeSet<>(Comparator.comparing(Person::getName)); // Sort all objects in alphabetical order

        Person person1 = new Person("Charlotte");
        people.add(person1);
        Person person2 = new Person("Dave");
        people.add(person2);

        Collection<Person> people2 = Collections.unmodifiableCollection(people);

        // Without modification
        people2.forEach(System.out::println);

        Iterator<Person> iterator = people2.iterator();

        assertEquals(person1, iterator.next()); // Charlotte
        assertEquals(person2, iterator.next()); // Dave

        // Modification of object stored within collection
        person2.setName("Adam");

        // Print out list after name has been modified
        people2.forEach(System.out::println);
//
//        iterator = people2.iterator();
//
//        assertEquals(person2, iterator.next()); // Should be Adam
//        assertEquals(person1, iterator.next()); // Should be Charlotte
    }

    @Test
    public void DeepImmutabilityTest() throws InterruptedException {
        Collection<Person> people = new TreeSet<>(Comparator.comparing(Person::getName)); // Sort all objects in alphabetical order

        Person person1 = new Person("Charlotte");
        people.add(person1);
        Person person2 = new Person("Dave");
        people.add(person2);

        @Immutable Collection<Person> people2 = people;

        // Without modification
        people2.forEach(System.out::println);

        Iterator<Person> iterator = people2.iterator();

        assertEquals(person1, iterator.next()); // Charlotte
        assertEquals(person2, iterator.next()); // Dave

        // Modification of object stored within collection
        assertThrows(DalaCapabilityViolationException.class , () -> person2.setName("Adam"));

        // Print out list after name has been modified
        people2.forEach(System.out::println);
    }

    static class Person{
        String name;

        public Person(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return "Person{name='" + name + "'}";
        }
    }
}
