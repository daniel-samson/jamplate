package media.samson.jamplate;

import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;
import org.reactfx.Subscription;

import java.time.Duration;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Provides syntax highlighting for the template editor.
 * Supports HTML and PHP syntax highlighting.
 */
public class TemplateEditorSyntaxHighlighter {
    
    private final CodeArea codeArea;
    private final ExecutorService executor;
    private Subscription subscription;
    private TemplateFileType templateType;
    
    // HTML patterns
    private static final String HTML_TAG_PATTERN = "</?\\b[a-zA-Z][a-zA-Z0-9]*\\b[^>]*>|<!DOCTYPE[^>]*>|<!--[^>]*-->";
    private static final String HTML_ATTR_NAME_PATTERN = "\\s([a-zA-Z][a-zA-Z0-9_-]*)\\s*=";
    private static final String HTML_ATTR_VALUE_PATTERN = "=\\s*([\"'][^\"']*[\"']|[^\\s>]+)";
    private static final String HTML_COMMENT_PATTERN = "<!--[\\s\\S]*?-->";
    private static final String HTML_ENTITY_PATTERN = "&[a-zA-Z0-9#]+;";
    
    // PHP patterns
    private static final String PHP_TAG_PATTERN = "<\\?php|<\\?=|<\\?|\\?>";
    private static final String PHP_VARIABLE_PATTERN = "\\$[a-zA-Z_\\x7f-\\xff][a-zA-Z0-9_\\x7f-\\xff]*";
    private static final String PHP_KEYWORD_PATTERN = "\\b(and|or|xor|array|as|break|case|class|const|continue|declare|" +
                                                    "default|die|do|echo|else|elseif|empty|enddeclare|endfor|endforeach|" +
                                                    "endif|endswitch|endwhile|eval|exit|extends|final|for|foreach|function|" +
                                                    "global|goto|if|implements|include|include_once|instanceof|interface|" +
                                                    "isset|list|namespace|new|print|private|protected|public|require|" +
                                                    "require_once|return|static|switch|throw|try|unset|use|var|while|yield)\\b";
    private static final String PHP_STRING_PATTERN = "\"([^\"\\\\]|\\\\.)*\"|'([^'\\\\]|\\\\.)*'";
    private static final String PHP_COMMENT_PATTERN = "//[^\\n]*|/\\*(.|\\n)*?\\*/";
    private static final String PHP_NUMBER_PATTERN = "\\b\\d+(\\.\\d+)?\\b";
    
    // Jamplate template variable pattern
    private static final String JAMPLATE_VARIABLE_PATTERN = "\\{\\{\\$[^}]+\\}\\}";
    
    // HTML Pattern
    private static final Pattern HTML_PATTERN = Pattern.compile(
        "(?<HTMLCOMMENT>" + HTML_COMMENT_PATTERN + ")" +
        "|(?<HTMLTAG>" + HTML_TAG_PATTERN + ")" +
        "|(?<HTMLATTRNAME>" + HTML_ATTR_NAME_PATTERN + ")" +
        "|(?<HTMLATTRVALUE>" + HTML_ATTR_VALUE_PATTERN + ")" +
        "|(?<HTMLENTITY>" + HTML_ENTITY_PATTERN + ")" +
        "|(?<JAMPLATEVAR>" + JAMPLATE_VARIABLE_PATTERN + ")"
    );
    
    // PHP Pattern
    private static final Pattern PHP_PATTERN = Pattern.compile(
        "(?<PHPTAG>" + PHP_TAG_PATTERN + ")" +
        "|(?<PHPVAR>" + PHP_VARIABLE_PATTERN + ")" +
        "|(?<PHPKEYWORD>" + PHP_KEYWORD_PATTERN + ")" +
        "|(?<PHPSTRING>" + PHP_STRING_PATTERN + ")" +
        "|(?<PHPCOMMENT>" + PHP_COMMENT_PATTERN + ")" +
        "|(?<PHPNUMBER>" + PHP_NUMBER_PATTERN + ")" +
        "|(?<HTMLCOMMENT>" + HTML_COMMENT_PATTERN + ")" +
        "|(?<HTMLTAG>" + HTML_TAG_PATTERN + ")" +
        "|(?<HTMLATTRNAME>" + HTML_ATTR_NAME_PATTERN + ")" +
        "|(?<HTMLATTRVALUE>" + HTML_ATTR_VALUE_PATTERN + ")" +
        "|(?<HTMLENTITY>" + HTML_ENTITY_PATTERN + ")" +
        "|(?<JAMPLATEVAR>" + JAMPLATE_VARIABLE_PATTERN + ")"
    );
    
    /**
     * Creates a new syntax highlighter for the given CodeArea and template type.
     *
     * @param codeArea The CodeArea to apply highlighting to
     * @param templateType The type of template file
     */
    public TemplateEditorSyntaxHighlighter(CodeArea codeArea, TemplateFileType templateType) {
        this.codeArea = codeArea;
        this.templateType = templateType;
        this.executor = Executors.newSingleThreadExecutor();
    }
    
    /**
     * Initializes the syntax highlighter.
     */
    public void initialize() {
        // Initial highlighting
        highlightText();
        
        // Set up real-time highlighting
        subscription = codeArea.multiPlainChanges()
            .successionEnds(Duration.ofMillis(100))
            .retainLatestUntilLater(executor)
            .supplyTask(this::computeHighlightingAsync)
            .awaitLatest(codeArea.multiPlainChanges())
            .filterMap(t -> {
                if (t.isSuccess()) {
                    return Optional.of(t.get());
                } else {
                    t.getFailure().printStackTrace();
                    return Optional.empty();
                }
            })
            .subscribe(this::applyHighlighting);
    }
    
    /**
     * Releases resources used by the highlighter.
     */
    public void dispose() {
        if (subscription != null) {
            subscription.unsubscribe();
        }
        executor.shutdown();
    }
    
    /**
     * Sets the template file type for highlighting.
     * 
     * @param templateType The template file type
     */
    public void setTemplateType(TemplateFileType templateType) {
        System.out.println("TemplateEditorSyntaxHighlighter: Setting template type to: " + templateType);
        this.templateType = templateType;
        highlightText();
        System.out.println("TemplateEditorSyntaxHighlighter: Highlighting applied for: " + templateType);
    }
    
    /**
     * Highlights the current text based on the template type.
     */
    public void highlightText() {
        String text = codeArea.getText();
        StyleSpans<Collection<String>> highlighting = computeHighlighting(text);
        applyHighlighting(highlighting);
    }
    
    /**
     * Computes highlighting asynchronously.
     * 
     * @return A task that computes the highlighting
     */
    private javafx.concurrent.Task<StyleSpans<Collection<String>>> computeHighlightingAsync() {
        String text = codeArea.getText();
        javafx.concurrent.Task<StyleSpans<Collection<String>>> task = new javafx.concurrent.Task<>() {
            @Override
            protected StyleSpans<Collection<String>> call() {
                return computeHighlighting(text);
            }
        };
        executor.execute(task);
        return task;
    }
    
    /**
     * Applies the computed highlighting to the CodeArea.
     * 
     * @param highlighting The computed highlighting
     */
    private void applyHighlighting(StyleSpans<Collection<String>> highlighting) {
        codeArea.setStyleSpans(0, highlighting);
    }
    
    /**
     * Computes the highlighting for the given text based on the template type.
     * 
     * @param text The text to highlight
     * @return The computed highlighting
     */
    private StyleSpans<Collection<String>> computeHighlighting(String text) {
        if (templateType == TemplateFileType.PHP_FILE) {
            return computePhpHighlighting(text);
        } else if (templateType == TemplateFileType.HTML_FILE) {
            return computeHtmlHighlighting(text);
        } else {
            // Default or TXT_FILE: only highlight Jamplate variables
            return computePlainTextHighlighting(text);
        }
    }
    
    /**
     * Computes highlighting for PHP files.
     * 
     * @param text The text to highlight
     * @return The computed highlighting
     */
    private StyleSpans<Collection<String>> computePhpHighlighting(String text) {
        Matcher matcher = PHP_PATTERN.matcher(text);
        int lastKwEnd = 0;
        StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();
        
        while (matcher.find()) {
            String styleClass = 
                matcher.group("PHPTAG") != null ? "keyword" :
                matcher.group("PHPVAR") != null ? "variable" :
                matcher.group("PHPKEYWORD") != null ? "keyword" :
                matcher.group("PHPSTRING") != null ? "string" :
                matcher.group("PHPCOMMENT") != null ? "comment" :
                matcher.group("PHPNUMBER") != null ? "number" :
                matcher.group("HTMLCOMMENT") != null ? "comment" :
                matcher.group("HTMLTAG") != null ? "bracket" :
                matcher.group("HTMLATTRNAME") != null ? "attribute" :
                matcher.group("HTMLATTRVALUE") != null ? "string" :
                matcher.group("HTMLENTITY") != null ? "operator" :
                matcher.group("JAMPLATEVAR") != null ? "template-variable" :
                null;
            
            assert styleClass != null;
            
            spansBuilder.add(Collections.emptyList(), matcher.start() - lastKwEnd);
            spansBuilder.add(Collections.singleton(styleClass), matcher.end() - matcher.start());
            lastKwEnd = matcher.end();
        }
        
        spansBuilder.add(Collections.emptyList(), text.length() - lastKwEnd);
        return spansBuilder.create();
    }
    
    /**
     * Computes highlighting for HTML files.
     * 
     * @param text The text to highlight
     * @return The computed highlighting
     */
    private StyleSpans<Collection<String>> computeHtmlHighlighting(String text) {
        Matcher matcher = HTML_PATTERN.matcher(text);
        int lastKwEnd = 0;
        StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();
        
        while (matcher.find()) {
            String styleClass = 
                matcher.group("HTMLCOMMENT") != null ? "comment" :
                matcher.group("HTMLTAG") != null ? "bracket" :
                matcher.group("HTMLATTRNAME") != null ? "attribute" :
                matcher.group("HTMLATTRVALUE") != null ? "string" :
                matcher.group("HTMLENTITY") != null ? "operator" :
                matcher.group("JAMPLATEVAR") != null ? "template-variable" :
                null;
            
            assert styleClass != null;
            
            spansBuilder.add(Collections.emptyList(), matcher.start() - lastKwEnd);
            spansBuilder.add(Collections.singleton(styleClass), matcher.end() - matcher.start());
            lastKwEnd = matcher.end();
        }
        
        spansBuilder.add(Collections.emptyList(), text.length() - lastKwEnd);
        return spansBuilder.create();
    }
    
    /**
     * Computes highlighting for plain text files (only highlighting Jamplate variables).
     * 
     * @param text The text to highlight
     * @return The computed highlighting
     */
    private StyleSpans<Collection<String>> computePlainTextHighlighting(String text) {
        Pattern pattern = Pattern.compile("(?<JAMPLATEVAR>" + JAMPLATE_VARIABLE_PATTERN + ")");
        Matcher matcher = pattern.matcher(text);
        int lastKwEnd = 0;
        StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();
        
        while (matcher.find()) {
            spansBuilder.add(Collections.emptyList(), matcher.start() - lastKwEnd);
            spansBuilder.add(Collections.singleton("template-variable"), matcher.end() - matcher.start());
            lastKwEnd = matcher.end();
        }
        
        spansBuilder.add(Collections.emptyList(), text.length() - lastKwEnd);
        return spansBuilder.create();
    }
}

