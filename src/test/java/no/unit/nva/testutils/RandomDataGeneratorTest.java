package no.unit.nva.testutils;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import java.net.URI;
import org.apache.commons.validator.routines.ISBNValidator;
import org.junit.jupiter.api.Test;

class RandomDataGeneratorTest {

    @Test
    void randomStringReturnsAString() {
        assertThat(RandomDataGenerator.randomString(), is(instanceOf(String.class)));
    }

    @Test
    void randomIntegerReturnsAPositiveInteger() {
        assertThat(RandomDataGenerator.randomInteger(), is(instanceOf(Integer.class)));
        assertThat(RandomDataGenerator.randomInteger(), is(greaterThanOrEqualTo(0)));
    }

    @Test
    void randomIntegerWithABoundReturnsAPositiveIntegerSmallerThanTheBound() {
        assertThat(RandomDataGenerator.randomInteger(1), is(instanceOf(Integer.class)));
        assertThat(RandomDataGenerator.randomInteger(1), is(equalTo(0)));
    }

    @Test
    void randomUriReturnsAUriWithRandomPath() {
        assertThat(RandomDataGenerator.randomUri(), is(instanceOf(URI.class)));
    }

    @Test
    void randomIsbn10ReturnsValidIsbn() {
        String isbn10 = RandomDataGenerator.randomIsbn10();
        assertThat(ISBNValidator.getInstance().isValid(isbn10), is(true));
        assertThat(isbn10, is(instanceOf(String.class)));
    }

    @Test
    void randomIsbn13ReturnsValidIsbn() {
        String isbn13 = RandomDataGenerator.randomIsbn13();
        assertThat(ISBNValidator.getInstance().isValid(isbn13), is(true));
        assertThat(isbn13, is(instanceOf(String.class)));
    }
}