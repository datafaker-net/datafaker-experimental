# Datafaker Ollama module

This module provides access to the [Ollama Models](https://ollama.com/) using [Ollam4j](https://github.com/amithkoujalgi/ollama4j)

This module is an add-on to [Datafaker](https://www.datafaker.net), and can be 
used to augment or replace the normal data generation of Datafaker. 

While Datafaker by default mostly uses YAML files with hardcoded values, this 
module aims to replace that implementation by an LLM model, which can use any
Ollama provided model using the same API as Datafaker uses, making it easy to replace the
stable implementation by the LLM model.

## Usage

Make sure `ollama` is installed. Then run the `gemma` model:

```bash
ollama run gemma
 ``` 

Then, using the following Java code, the Ollama service can be used:

```java
// Create an Ollama value generator, using the Gemma model (others might work too, but this the one I tested with)  
OllamaFakeValuesService fakeValuesService = new OllamaFakeValuesService("gemma");

// Inject the value generator in the Faker
Faker llmFaker = new Faker(fakeValuesService, new FakerContext(new Locale("en", "US"), new RandomService()));

// This value is now generated using the Gemma model 
System.out.println("firstname: " + llmFaker.name().firstName());
```

## Known issues and limitations

* The generation could be slow sometimes, depending on your machine, though items will be generated in bulk (20) and are cached.
* Not all generators are currently being intercepted by Datafaker OpenAI module, so some of them still might be using the YAML files.
