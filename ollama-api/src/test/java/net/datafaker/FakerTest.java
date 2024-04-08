package net.datafaker;

import net.datafaker.service.FakerContext;
import net.datafaker.service.RandomService;
import net.datafaker.service.ollama.OllamaFakeValuesService;
import org.junit.jupiter.api.Test;

import java.util.Locale;

public class FakerTest {


    @Test
    void useGemmaModel() {
        OllamaFakeValuesService fakeValuesService = new OllamaFakeValuesService("gemma");

        Faker aiFaker = new Faker(fakeValuesService, new FakerContext(new Locale("en", "US"), new RandomService()));

        System.out.println("Faker: " + aiFaker.getFaker().getContext().getLocale().getDisplayLanguage());

        // This loop demonstrates the cache usage. Setting amountOfItemsToGenerate=1 will effectively disable the cache.
        for (int i = 0; i < 15; i++) {
            System.out.println(System.currentTimeMillis());
            System.out.println("firstname: " + aiFaker.name().firstName());
        }

        System.out.println("firstname: " + aiFaker.name().firstName());
        System.out.println("lastname: " + aiFaker.name().lastName());
        System.out.println("address: " + aiFaker.address().fullAddress());
        System.out.println();

        fakeValuesService.setUseFullKey(true);
        System.out.println("aquateen character: " + aiFaker.aquaTeenHungerForce().character());

        System.out.println("movie: " + aiFaker.movie().quote());

    }
}
