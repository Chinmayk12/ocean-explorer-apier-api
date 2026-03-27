package com.NatWest.ChinmayKarodpati.ocean_explorer_apier_api.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

// Test suite for the Command enum.
// Ensures that commands like 'F' or 'r' are properly parsed and gibberish inputs are correctly rejected.
class CommandTest {

    @ParameterizedTest(name = "'{0}' → {1}")
    @CsvSource({
            "F, F",
            "B, B",
            "L, L",
            "R, R"
    })
    @DisplayName("Should parse valid uppercase command characters")
    void fromChar_shouldParseValidUppercaseCommands(char input, Command expected) {
        assertEquals(expected, Command.fromChar(input));
    }

    @ParameterizedTest(name = "'{0}' → valid (case insensitive)")
    @CsvSource({
            "f, F",
            "b, B",
            "l, L",
            "r, R"
    })
    @DisplayName("Should parse valid lowercase command characters (case insensitive)")
    void fromChar_shouldParseLowercaseCommands(char input, Command expected) {
        assertEquals(expected, Command.fromChar(input));
    }

    @ParameterizedTest(name = "Invalid character ''{0}'' should throw")
    @ValueSource(chars = {'X', 'Z', '1', ' ', 'A', 'N'})
    @DisplayName("Should throw IllegalArgumentException for invalid characters")
    void fromChar_shouldThrowForInvalidCharacters(char invalidChar) {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> Command.fromChar(invalidChar)
        );
        assertTrue(exception.getMessage().contains(String.valueOf(invalidChar)),
                "Error message should contain the invalid character");
    }

    @Test
    @DisplayName("Should have exactly 4 command values")
    void enum_shouldHaveExactlyFourValues() {
        assertEquals(4, Command.values().length);
    }
}
