package com.lyr.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

import com.lyr.TestUtil;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;

class FileUtilsTest {

    private static final String resourceFilePath = "someResourceFile.txt";
    private static final String expectedFileContent = "Hello World!";

    @Test
    void testThatFileUtilReturnsAnInputStreamOfAResourceSuccessfully() {
        // Given
        // When
        try (var inputStream = FileUtils.getInputStreamFromResource(resourceFilePath)) {
            var fileContents = IOUtils.toString(inputStream, StandardCharsets.UTF_8);

            // Then
            assertThat(fileContents).isEqualTo(expectedFileContent);

        } catch (IOException e) {
            fail("Resource file could not be read. Error:" + e.getMessage());
        }
    }

    @Test
    void testThatFileUtilReturnsAnInputStreamOfASystemFileGivenAnAbsolutePathSuccessfully() {
        // Given
        final var resourceAbsolutePath = TestUtil.getAbsoluteFilePathOfResource(resourceFilePath);
        // When
        try (var inputStream = FileUtils.getInputStreamFromSystem(resourceAbsolutePath)) {
            var fileContents = IOUtils.toString(inputStream, StandardCharsets.UTF_8);

            // Then
            assertThat(fileContents).isEqualTo(expectedFileContent);

        } catch (IOException e) {
            fail("Resource file could not be read. Error:" + e.getMessage());
        }
    }

    @Test
    void testThatFileUtilReturnsAnInputStreamOfASystemFileGivenAnRelativePathSuccessfully() {
        // Given
        final var resourceRelativePath = "src/test/resources/someResourceFile.txt";
        // When
        try (var inputStream = FileUtils.getInputStreamFromSystem(resourceRelativePath)) {
            var fileContents = IOUtils.toString(inputStream, StandardCharsets.UTF_8);

            // Then
            assertThat(fileContents).isEqualTo(expectedFileContent);

        } catch (IOException e) {
            fail("Resource file could not be read. Error:" + e.getMessage());
        }
    }
}
