package com.comp2042.help;

import com.comp2042.config.ResourceManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Loads the Markdown help content and exposes parsed shortcuts for reuse.
 */
public final class HelpContentProvider {

    private static final String SHORTCUTS_HEADING = "Shortcuts";

    private final String markdown;
    private final List<String> shortcutSummaries;

    private HelpContentProvider() {
        markdown = loadResourceMarkdown();
        shortcutSummaries = Collections.unmodifiableList(extractSection(markdown, SHORTCUTS_HEADING));
    }

    public static HelpContentProvider getInstance() {
        return Holder.INSTANCE;
    }

    public String getMarkdown() {
        return markdown;
    }

    public List<String> getShortcutSummaries() {
        return shortcutSummaries;
    }

    private static String loadResourceMarkdown() {
        try (InputStream in = ResourceManager.openStream(ResourceManager.Asset.HELP_MARKDOWN)) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
                StringBuilder builder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line).append(System.lineSeparator());
                }
                return builder.toString().trim();
            }
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to read help content.", ex);
        }
    }

    private static List<String> extractSection(String markdown, String heading) {
        List<String> lines = new ArrayList<>();
        boolean inSection = false;
        try (BufferedReader reader = new BufferedReader(new StringReader(markdown))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String trimmed = line.trim();
                if (trimmed.startsWith("#")) {
                    String header = trimmed.replaceFirst("^#+\\s*", "").toLowerCase();
                    inSection = header.equals(heading.toLowerCase());
                    continue;
                }
                if (inSection) {
                    if (trimmed.startsWith("- ")) {
                        lines.add(trimmed.substring(2).trim());
                    } else if (!trimmed.isEmpty()) {
                        lines.add(trimmed);
                    }
                }
            }
        } catch (IOException ex) {
            return lines;
        }
        return lines;
    }

    private static final class Holder {
        private static final HelpContentProvider INSTANCE = new HelpContentProvider();
    }
}
