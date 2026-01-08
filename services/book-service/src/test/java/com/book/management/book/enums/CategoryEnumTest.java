package com.book.management.book.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CategoryEnum Tests")
class CategoryEnumTest {

    @Nested
    @DisplayName("getId() Tests")
    class GetIdTests {
        
        @Test
        @DisplayName("Should return correct ID for FICTION")
        void shouldReturnCorrectIdForFiction() {
            assertEquals("CAT-FIC", CategoryEnum.FICTION.getId());
        }

        @Test
        @DisplayName("Should return correct ID for NON_FICTION")
        void shouldReturnCorrectIdForNonFiction() {
            assertEquals("CAT-NF", CategoryEnum.NON_FICTION.getId());
        }

        @Test
        @DisplayName("Should return correct ID for TECHNOLOGY")
        void shouldReturnCorrectIdForTechnology() {
            assertEquals("CAT-TCH", CategoryEnum.TECHNOLOGY.getId());
        }

        @Test
        @DisplayName("Should return correct ID for SCIENCE")
        void shouldReturnCorrectIdForScience() {
            assertEquals("CAT-SCI", CategoryEnum.SCIENCE.getId());
        }

        @Test
        @DisplayName("Should return correct ID for HISTORY")
        void shouldReturnCorrectIdForHistory() {
            assertEquals("CAT-HIS", CategoryEnum.HISTORY.getId());
        }

        @Test
        @DisplayName("Should return correct ID for FANTASY")
        void shouldReturnCorrectIdForFantasy() {
            assertEquals("CAT-FAN", CategoryEnum.FANTASY.getId());
        }

        @Test
        @DisplayName("Should return correct ID for BIOGRAPHY")
        void shouldReturnCorrectIdForBiography() {
            assertEquals("CAT-BIO", CategoryEnum.BIOGRAPHY.getId());
        }

        @Test
        @DisplayName("Should return correct ID for BUSINESS")
        void shouldReturnCorrectIdForBusiness() {
            assertEquals("CAT-BUS", CategoryEnum.BUSINESS.getId());
        }

        @Test
        @DisplayName("Should return correct ID for OTHER")
        void shouldReturnCorrectIdForOther() {
            assertEquals("CAT-OTH", CategoryEnum.OTHER.getId());
        }
    }

    @Nested
    @DisplayName("fromId() Tests")
    class FromIdTests {
        
        @ParameterizedTest
        @CsvSource({
            "CAT-FIC, FICTION",
            "CAT-NF, NON_FICTION",
            "CAT-TCH, TECHNOLOGY",
            "CAT-SCI, SCIENCE",
            "CAT-HIS, HISTORY",
            "CAT-FAN, FANTASY",
            "CAT-BIO, BIOGRAPHY",
            "CAT-BUS, BUSINESS",
            "CAT-OTH, OTHER"
        })
        @DisplayName("Should return correct enum from valid ID")
        void shouldReturnCorrectEnumFromValidId(String id, CategoryEnum expected) {
            assertEquals(expected, CategoryEnum.fromId(id));
        }

        @ParameterizedTest
        @CsvSource({
            "cat-fic, FICTION",
            "Cat-Fic, FICTION",
            "CAT-FIC, FICTION"
        })
        @DisplayName("Should handle case-insensitive ID lookup")
        void shouldHandleCaseInsensitiveIdLookup(String id, CategoryEnum expected) {
            assertEquals(expected, CategoryEnum.fromId(id));
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"  ", "\t", "\n"})
        @DisplayName("Should return OTHER for null or blank ID")
        void shouldReturnOtherForNullOrBlankId(String id) {
            assertEquals(CategoryEnum.OTHER, CategoryEnum.fromId(id));
        }

        @ParameterizedTest
        @ValueSource(strings = {"INVALID", "CAT-XXX", "random", "123"})
        @DisplayName("Should return OTHER for invalid ID")
        void shouldReturnOtherForInvalidId(String id) {
            assertEquals(CategoryEnum.OTHER, CategoryEnum.fromId(id));
        }

        @Test
        @DisplayName("Should handle ID with extra whitespace")
        void shouldHandleIdWithExtraWhitespace() {
            assertEquals(CategoryEnum.FICTION, CategoryEnum.fromId("  CAT-FIC  "));
        }
    }

    @Nested
    @DisplayName("fromName() Tests")
    class FromNameTests {
        
        @ParameterizedTest
        @CsvSource({
            "FICTION, FICTION",
            "NON_FICTION, NON_FICTION",
            "TECHNOLOGY, TECHNOLOGY",
            "SCIENCE, SCIENCE",
            "HISTORY, HISTORY",
            "FANTASY, FANTASY",
            "BIOGRAPHY, BIOGRAPHY",
            "BUSINESS, BUSINESS",
            "OTHER, OTHER"
        })
        @DisplayName("Should return correct enum from valid name")
        void shouldReturnCorrectEnumFromValidName(String name, CategoryEnum expected) {
            assertEquals(expected, CategoryEnum.fromName(name));
        }

        @ParameterizedTest
        @CsvSource({
            "fiction, FICTION",
            "Fiction, FICTION",
            "FICTION, FICTION"
        })
        @DisplayName("Should handle case-insensitive name lookup")
        void shouldHandleCaseInsensitiveNameLookup(String name, CategoryEnum expected) {
            assertEquals(expected, CategoryEnum.fromName(name));
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"  ", "\t", "\n"})
        @DisplayName("Should return OTHER for null or blank name")
        void shouldReturnOtherForNullOrBlankName(String name) {
            assertEquals(CategoryEnum.OTHER, CategoryEnum.fromName(name));
        }

        @ParameterizedTest
        @ValueSource(strings = {"INVALID", "UNKNOWN", "random", "123"})
        @DisplayName("Should return OTHER for invalid name")
        void shouldReturnOtherForInvalidName(String name) {
            assertEquals(CategoryEnum.OTHER, CategoryEnum.fromName(name));
        }

        @Test
        @DisplayName("Should handle name with dashes converted to underscores")
        void shouldHandleNameWithDashes() {
            assertEquals(CategoryEnum.NON_FICTION, CategoryEnum.fromName("NON-FICTION"));
        }

        @Test
        @DisplayName("Should handle name with spaces converted to underscores")
        void shouldHandleNameWithSpaces() {
            assertEquals(CategoryEnum.NON_FICTION, CategoryEnum.fromName("NON FICTION"));
        }
    }

    @Nested
    @DisplayName("Enum Values Tests")
    class EnumValuesTests {
        
        @Test
        @DisplayName("Should have 9 category values")
        void shouldHaveNineCategoryValues() {
            assertEquals(9, CategoryEnum.values().length);
        }

        @Test
        @DisplayName("Should get enum by valueOf")
        void shouldGetEnumByValueOf() {
            assertEquals(CategoryEnum.FICTION, CategoryEnum.valueOf("FICTION"));
        }
    }
}
