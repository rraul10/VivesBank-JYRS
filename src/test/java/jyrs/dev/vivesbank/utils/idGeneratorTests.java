package jyrs.dev.vivesbank.utils;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.HashSet;
import java.util.Set;

class IdGeneratorTest {

    @Test
    void generateHash_ShouldReturnStringOfExpectedLength() {
        // Act
        String hash = IdGenerator.generateHash();

        // Assert
        assertNotNull(hash);
        assertEquals(11, hash.length());
    }

    @Test
    void generateHash_ShouldNotContainInvalidCharacters() {
        // Act
        String hash = IdGenerator.generateHash();

        // Assert
        assertFalse(hash.contains("+"));
        assertFalse(hash.contains("/"));
    }

    @Test
    void generateHash_ShouldReturnUniqueHashes() {
        // Arrange
        Set<String> uniqueHashes = new HashSet<>();
        int numberOfHashes = 1000;

        // Act
        for (int i = 0; i < numberOfHashes; i++) {
            String hash = IdGenerator.generateHash();
            uniqueHashes.add(hash);
        }

        // Assert
        assertEquals(numberOfHashes, uniqueHashes.size());
    }
}
