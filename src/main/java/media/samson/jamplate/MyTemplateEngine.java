package media.samson.jamplate;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.stream.Collectors;

public class MyTemplateEngine {
    private String template;
    private HashMap<String,String> variables;

    /**
     *
     * @param template
     *
     * String template = "Hello {{world}}";
     *
     *  HashMap<String, String> variables = new HashMap<String, String>();
     *  variables.put("k1","v1");
     *  variables.put("k2","v2");
     *
     *  MyTemplateEngine myTemplateEngine = MyTemplateEngine();
     *
     *  Either:
     *  myTemplateEngine.setTemplate(template);
     *
     *  Or:
     *  myTemplateEngine.loadTemplateFromResource(template);
     *
     *  String doc = myTemplateEngine.build(varibles);
     *
     */
    public void setTemplate(String template) {
        this.template = template;
    }

    public void loadTemplateFromResource(String resourcePath) throws Exception {
        try (InputStream is = getClass().getResourceAsStream(resourcePath);
             BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            this.template = reader.lines().collect(Collectors.joining("\n"));
        }
    }

    public String build(HashMap<String,String> variables) {
        String document = template;

        for (var entry : variables.entrySet()) {
            var variableName = entry.getKey();
            var value = entry.getValue();
            document = document.replaceAll("\\{\\{\\$"+variableName+"\\}\\}", value);
        }

        return document;
    }
}
