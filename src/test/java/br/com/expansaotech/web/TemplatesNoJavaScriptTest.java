package br.com.expansaotech.web;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;

class TemplatesNoJavaScriptTest {
    @Test
    void templatesNaoIncluemJavaScript() throws IOException {
        try (Stream<Path> templates = Files.walk(Path.of("src/main/resources/templates"))) {
            for (Path template : templates.filter(Files::isRegularFile).toList()) {
                assertHtmlSemScripts(template);
            }
        }
        assertHtmlSemScripts(Path.of("index.html"));
    }

    private void assertHtmlSemScripts(Path file) throws IOException {
        String html = Files.readString(file, StandardCharsets.UTF_8).toLowerCase();
        assertThat(html).doesNotContain("<script");
        assertThat(html).doesNotContain("javascript:");
    }
}
