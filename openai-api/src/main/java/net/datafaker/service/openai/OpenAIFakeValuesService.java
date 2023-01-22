package net.datafaker.service.openai;

import com.google.common.collect.ArrayListMultimap;
import com.google.gson.Gson;
import net.datafaker.Faker;
import net.datafaker.providers.base.AbstractProvider;
import net.datafaker.providers.base.BaseFaker;
import net.datafaker.providers.base.ProviderRegistration;
import net.datafaker.service.FakeValuesService;
import net.datafaker.service.FakerContext;
import net.datafaker.service.openai.model.OpenAIModel;
import net.datafaker.service.openai.model.Request;
import net.datafaker.service.openai.model.Response;
import org.apache.commons.lang3.StringUtils;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.function.Supplier;

public class OpenAIFakeValuesService extends FakeValuesService {

    private static final String OPEN_API_COMPLETION_ENDPOINT = "https://api.openai.com/v1/completions";
    private static final String APPLICATION_JSON = "application/json";

    private final HttpClient httpClient = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_2)
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    private final Gson gson = new Gson();

    private final ArrayListMultimap<String, String> cache = ArrayListMultimap.create();

    private final String apiKey;
    private final String modelName;
    private final Integer maxTokens;
    private final Double temperature;

    /**
     * Keys are either the faker name + property (full:true), such as person + name, or only the property (full:false), such as "name".
     * <p>
     * Depending on your use case, one might provider better results than the other.
     */
    private boolean useFullKey = false;

    /**
     * Controls the amount of items to generate. These items are cached, so to speed up the process, set this to high amount,
     * but the higher the amount, the higher the costs (don't forget to set the maxTokens to an appropriate amount too).
     */
    private int amountOfItemsToGenerate = 5;

    /**
     * Creates a new instance with sane defaults, works for most cases.
     *
     * @param apiKey The API key used for the OpenAI service.
     */
    public OpenAIFakeValuesService(String apiKey) {
        this(apiKey, OpenAIModel.TEXT_DAVINCI_003, 500, 0.5);
    }

    /**
     * @param apiKey      The API key used for the OpenAI service.
     * @param modelName   Name of the model to use to generate tokens.
     * @param maxTokens   The maximum number of tokens to generate. Requests can use up to 2,048 or 4,000 tokens
     *                    shared between prompt and completion. The exact limit varies by model.
     *                    (One token is roughly 4 characters for normal English text)
     * @param temperature Controls randomness: Lowering results in less random completions.
     *                    As the temperature approaches zero, the model will become deterministic and repetitive.
     */
    public OpenAIFakeValuesService(String apiKey, String modelName, Integer maxTokens, Double temperature) {
        this.apiKey = apiKey;
        this.modelName = modelName;
        this.maxTokens = maxTokens;
        this.temperature = temperature;
    }

    /**
     * @param apiKey      The API key used for the OpenAI service.
     * @param model       A typesafe version of the model used to generate tokens.
     * @param maxTokens   The maximum number of tokens to generate. Requests can use up to 2,048 or 4,000 tokens
     *                    shared between prompt and completion. The exact limit varies by model.
     *                    (One token is roughly 4 characters for normal English text)
     * @param temperature Controls randomness: Lowering results in less random completions.
     *                    As the temperature approaches zero, the model will become deterministic and repetitive.
     */
    public OpenAIFakeValuesService(String apiKey, OpenAIModel model, Integer maxTokens, Double temperature) {
        this(apiKey, model.getModelName(), maxTokens, temperature);
    }

    @Override
    public String resolve(String key, Object current, BaseFaker root, FakerContext context) {
        return super.resolve(key, current, root, context);
    }

    @Override
    public String resolve(String key, AbstractProvider provider, FakerContext context) {
        if (!cache.containsKey(key)) {
            var prompt = createPrompt(key, context);
            var apiRequest = new Request(modelName, prompt, maxTokens, temperature);
            System.out.println("Using prompt: " + prompt);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(OPEN_API_COMPLETION_ENDPOINT))
                    .header("Content-Type", APPLICATION_JSON)
                    .header("Authorization", "Bearer " + apiKey)
                    .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(apiRequest)))
                    .build();

            try {
                HttpResponse<String> httpResponse = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                Response jsonResponse = gson.fromJson(httpResponse.body(), Response.class);

                if (jsonResponse.getChoices().isEmpty()) {
                    return null;
                } else {
                    List<String> response = createResponse(jsonResponse.getChoices().get(0).getText());

                    // Cache response
                    cache.putAll(key, response);
                }
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
        var toJson = "Response should be a json object, with array field named \"values\"";

        return "List " + amountOfItemsToGenerate + " " + context.getLocale().getDisplayLanguage() + " " + formatKey(key) + ". " + toJson + ".";
    }

    private List<String> createResponse(String json) {
        // Sometimes OpenAPI returns a list of comma or newline separated responses.
        // For now, let's ignore that and just return the first entry in the list.

        String stripped = json.strip().replaceAll("\n", "");

        return gson.fromJson(stripped, ValueList.class).getValues();
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

class ValueList {
    private List<String> values;

    public List<String> getValues() {
        return values;
    }

    @SuppressWarnings("unused")
    public void setValues(List<String> values) {
        this.values = values;
    }
}
