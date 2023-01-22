package net.datafaker;

import net.datafaker.service.FakeValuesService;
import net.datafaker.service.FakerContext;
import net.datafaker.service.RandomService;
import net.datafaker.service.openai.OpenAIFakeValuesService;
import net.datafaker.service.openai.model.OpenAIModel;
import org.junit.jupiter.api.Test;

import java.util.Locale;

public class FakerTest {

    public static final String OPENAI_API_KEY = System.getenv("OPENAI_API_KEY");

    @Test
    void useBasicOpenAICodex() {
        OpenAIFakeValuesService openAICodexService = new OpenAIFakeValuesService(OPENAI_API_KEY);

        Faker openAIFaker = new Faker(openAICodexService, new FakerContext(new Locale("en", "US"), new RandomService()));

        System.out.println("Faker: " + openAIFaker.getFaker().getContext().getLocale().getDisplayLanguage());

        // This loop demonstrates the cache usage. Setting amountOfItemsToGenerate=1 will effectively disable the cache.
        for (int i = 0; i < 15; i++) {
            System.out.println(System.currentTimeMillis());
            System.out.println("firstname: " + openAIFaker.name().firstName());
        }

        System.out.println("firstname: " + openAIFaker.name().firstName());
        System.out.println("lastname: " + openAIFaker.name().lastName());
        System.out.println("address: " + openAIFaker.address().fullAddress());
        System.out.println();

        openAICodexService.setUseFullKey(true);
        System.out.println("aquateen character: " + openAIFaker.aquaTeenHungerForce().character());

        System.out.println("movie: " + openAIFaker.movie().quote());
    }

    @Test
    void useOpenAICodex() {
        FakeValuesService openAICodexService = new OpenAIFakeValuesService(OPENAI_API_KEY, OpenAIModel.CODE_DAVINCI_002, 100, 0.7);

        Faker openAIFaker = new Faker(openAICodexService, new FakerContext(new Locale("en", "US"), new RandomService()));

        System.out.println("Faker: " + openAIFaker.getFaker().getContext().getLocale().getDisplayLanguage());
        System.out.println("firstname: " + openAIFaker.name().firstName());
        System.out.println("lastname: " + openAIFaker.name().lastName());
        System.out.println("address: " + openAIFaker.address().fullAddress());
        System.out.println();
        System.out.println("aquateen character: " + openAIFaker.aquaTeenHungerForce().character());
        System.out.println("movie: " + openAIFaker.movie().quote());
    }
}
