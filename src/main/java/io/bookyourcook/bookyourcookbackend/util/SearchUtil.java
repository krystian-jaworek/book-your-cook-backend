package io.bookyourcook.bookyourcookbackend.util;

import java.text.Normalizer;
import java.util.regex.Pattern;

/**
 * Utility class for search-related text processing.
 */
public class SearchUtil {

    private static final Pattern DIACRITICS_PATTERN = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");

    /**
     * Normalizes a string by removing diacritical marks (e.g., "naleśnik" -> "nalesnik")
     * and converting it to lower case.
     * @param text The input string.
     * @return The normalized string.
     */
    public static String normalize(String text) {
        if (text == null || text.isBlank()) {
            return text;
        }
        // 1. Decompose characters into base letters and combining diacritical marks (e.g., 'ś' -> 's' + '´')
        String normalizedText = Normalizer.normalize(text, Normalizer.Form.NFD);
        // 2. Remove the diacritical marks
        String withoutDiacritics = DIACRITICS_PATTERN.matcher(normalizedText).replaceAll("");
        // 3. Convert to lower case
        return withoutDiacritics.toLowerCase();
    }
}
