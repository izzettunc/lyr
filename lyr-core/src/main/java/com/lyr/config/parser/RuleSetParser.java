package com.lyr.config.parser;

import com.google.common.annotations.VisibleForTesting;
import com.lyr.exception.config.RuleSetParseException;
import com.lyr.util.file.FileUtils;
import com.lyr.util.file.InputStreamProvider;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.file.NoSuchFileException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.exc.MismatchedInputException;
import tools.jackson.databind.exc.UnrecognizedPropertyException;
import tools.jackson.dataformat.yaml.YAMLMapper;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class RuleSetParser {
    private static final String DEFAULT_RULESET_CONFIG_PATH = "defaultRuleSet.yaml";
    private static final YAMLMapper YAML_MAPPER = YAMLMapper.builder()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true)
            .build();

    public static RuleSet parseRuleSet(final String path) {
        return parseRuleSetFrom(FileUtils::getInputStreamFromSystem, path);
    }

    public static RuleSet parseDefaultRuleSet() {
        return parseRuleSetFrom(FileUtils::getInputStreamFromResource, getDefaultRuleSetPath());
    }

    private static RuleSet parseRuleSetFrom(final InputStreamProvider inputStreamProvider, final String path) {
        try (InputStream ruleSetInputStream = inputStreamProvider.getInputStream(path)) {
            return YAML_MAPPER.readValue(ruleSetInputStream, RuleSet.class);
        } catch (final UnrecognizedPropertyException unrecognizedPropertyException) {
            throw new RuleSetParseException(
                    String.format(
                            "Failed to parse the ruleset due to an unrecognized property. Property: %s, ExceptionType: %s, Exception: %s, Path: %s",
                            unrecognizedPropertyException.getPropertyName(),
                            unrecognizedPropertyException.getClass().getName(),
                            unrecognizedPropertyException.getMessage(),
                            path),
                    unrecognizedPropertyException);
        } catch (final MismatchedInputException mismatchedInputException) {
            throw new RuleSetParseException(
                    String.format(
                            "Failed to parse ruleset as it is not in expected format. ExceptionType: %s, Exception: %s, Path: %s",
                            mismatchedInputException.getClass().getName(), mismatchedInputException.getMessage(), path),
                    mismatchedInputException);
        } catch (final NoSuchFileException | FileNotFoundException fileDoesNotExistException) {
            throw new RuleSetParseException(
                    String.format(
                            "Failed to parse ruleset as the file doesn't exist. ExceptionType: %s, Path: %s",
                            fileDoesNotExistException.getClass().getName(), fileDoesNotExistException.getMessage()),
                    fileDoesNotExistException);
        } catch (final Exception exception) {
            throw new RuleSetParseException(
                    String.format(
                            "Failed to parse ruleset due to unknown error. ExceptionType: %s, Exception: %s, Path: %s",
                            exception.getClass().getName(), exception.getMessage(), path),
                    exception);
        }
    }

    @VisibleForTesting
    static String getDefaultRuleSetPath() {
        return DEFAULT_RULESET_CONFIG_PATH;
    }
}
