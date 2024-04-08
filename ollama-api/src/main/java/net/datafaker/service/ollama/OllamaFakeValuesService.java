package net.datafaker.service.ollama;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import io.github.amithkoujalgi.ollama4j.core.OllamaAPI;
import io.github.amithkoujalgi.ollama4j.core.models.OllamaResult;
import io.github.amithkoujalgi.ollama4j.core.types.OllamaModelType;
import io.github.amithkoujalgi.ollama4j.core.utils.OptionsBuilder;
import io.github.amithkoujalgi.ollama4j.core.utils.PromptBuilder;
import net.datafaker.Faker;
import net.datafaker.providers.base.AbstractProvider;
import net.datafaker.providers.base.BaseFaker;
import net.datafaker.providers.base.ProviderRegistration;
import net.datafaker.service.FakeValuesService;
import net.datafaker.service.FakerContext;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Type;
import java.util.List;
import java.util.function.Supplier;

public class OllamaFakeValuesService extends FakeValuesService {

    private final Gson gson = new Gson();

    private final ArrayListMultimap<String, String> cache = ArrayListMultimap.create();

    private final String modelName;

    /**
     * Keys are either the faker name + property (full:true), such as person + name, or only the property (full:false), such as "name".
     * <p>
     * Depending on your use case, one might provide better results than the other.
     */
    private boolean useFullKey = false;

    /**
     * Controls the amount of items to generate. These items are cached, so to speed up the process, set this to high amount,
     * but the higher the amount, the higher the costs (don't forget to set the maxTokens to an appropriate amount too).
     */
    private int amountOfItemsToGenerate = 20;

    /**
     * Creates a new instance with sane defaults, works for most cases.
     */
    public OllamaFakeValuesService() {
        this("gemma");
    }

    /**
     * @param modelName Name of the model to use to generate tokens.
     */
    public OllamaFakeValuesService(String modelName) {
        this.modelName = modelName;
    }

    /**
     * @param modelType A typesafe version of the model used to generate tokens.
     */
    public OllamaFakeValuesService(OllamaModelType modelType) {
        this(modelType.toString());
    }

    @Override
    public String resolve(String key, Object current, BaseFaker root, FakerContext context) {
        return super.resolve(key, current, root, context);
    }

    @Override
    public String resolve(String key, AbstractProvider provider, FakerContext context) {
        if (!cache.containsKey(key)) {
            var prompt = createPrompt(key, context);

            String host = "http://localhost:11434/";
            OllamaAPI ollamaAPI = new OllamaAPI(host);
            ollamaAPI.setRequestTimeoutSeconds(30);

            try {
                OllamaResult response = ollamaAPI.generate(modelName, prompt, new OptionsBuilder().build());

                List<String> values = cleanResponse(response.getResponse());

                // Cache response
                cache.putAll(key, values);

            } catch (Exception e) {
                return null;
            }
        }

        var faker = new Faker();

        List<String> strings = cache.get(key);
        String value = faker.options().nextElement(strings);
        cache.remove(key, value);
        return value;
    }

    private String createPrompt(String key, FakerContext context) {

        PromptBuilder promptBuilder =
                new PromptBuilder()
                        .addLine("You are an generator of fake data which looks like real data.")
                        .addLine("Given a question, answer ONLY with json outputs.")
                        .addLine("Generate a single list of " + amountOfItemsToGenerate + " items of " + formatKey(key))
                        .addLine("Generate the list in " + context.getLocale().getDisplayLanguage());

        return promptBuilder.build();
    }

    private List<String> cleanResponse(String json) {
        // Sometimes Ollama returns a list of comma or newline separated responses.
        // For now, let's ignore that and just return the first entry in the list.
        String stripped = json.strip()
                .replaceAll("\n", "")
                .replaceAll("```json", "")
                .replaceAll("```", "");

        Type listType = new TypeToken<List<String>>() {}.getType();
        return gson.fromJson(stripped, listType);
    }

    private String formatKey(String key) {

        String[] split = key.split("\\.");

        var prefix = split[0].replaceAll("_", " ");
        var postfix = split[1].replaceAll("_", " ");

        if (useFullKey) {
            return prefix + " " + splitCamelCase(postfix).replaceAll(" {2,}", " ");
        } else {
            return splitCamelCase(postfix).replaceAll(" {2,}", " ");
        }
    }

    private String splitCamelCase(String s) {
        return StringUtils.join(StringUtils.splitByCharacterTypeCamelCase(s), ' ');
    }

    @Override
    public String resolve(String key, Object current, ProviderRegistration root, Supplier<String> exceptionMessage, FakerContext context) {
        // TODO
        return super.resolve(key, current, root, exceptionMessage, context);
    }

    @Override
    protected String resolveExpression(String expression, Object current, ProviderRegistration root, FakerContext context) {
        // TODO
        return super.resolveExpression(expression, current, root, context);
    }

    public void setUseFullKey(boolean useFullKey) {
        this.useFullKey = useFullKey;
    }

    public void setAmountOfItemsToGenerate(int amountOfItemsToGenerate) {
        this.amountOfItemsToGenerate = amountOfItemsToGenerate;
    }
}