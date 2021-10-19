package no.unit.nva.testutils;

import com.github.javafaker.Faker;
import java.net.URI;
import java.util.Collection;
import java.util.Random;
import org.apache.commons.lang3.RandomStringUtils;

public class RandomDataGenerator {

    public static final int MIN_RANDOM_STRING_LENGTH = 10;
    public static final int MAX_RANDOM_STRING_LENGTH = 20;
    public static final Random RANDOM = new Random();
    public static final Faker FAKER = Faker.instance();

    public static String randomString() {
        return RandomStringUtils.randomAlphanumeric(MIN_RANDOM_STRING_LENGTH, MAX_RANDOM_STRING_LENGTH);
    }

    public static Integer randomInteger() {
        return randomInteger(Integer.MAX_VALUE);
    }

    public static Integer randomInteger(int bound) {
        return RANDOM.nextInt(bound);
    }

    public static URI randomUri() {
        return URI.create("https://www.example.com/" + randomString());
    }

    public static String randomIsbn10() {
        return FAKER.code().isbn10();
    }

    public static String randomIsbn13() {
        return FAKER.code().isbn13();
    }

    public static <T> T randomElement(T... elements) {
        return elements[RANDOM.nextInt(elements.length)];
    }

    @SuppressWarnings({"unchecked"})
    public static <T> T randomElement(Collection<T> elements) {
        Object element = randomElement(elements.toArray());
        return (T) element;
    }
}
