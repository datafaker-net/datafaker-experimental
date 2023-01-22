# Datafaker OpenAI GPT3 and Codex module

This module provides access to the [OpenAI’s API](https://openai.com/api/) which provides access to GPT-3 which performs a wide variety of natural language tasks, and Codex, which translates natural language to code.

This module is an add-on to Datafaker, and will can be used to augment or
replace the normal data generation of Datafaker. While Datafaker by default 
mostly uses YAML files with hardcoded values, this module aims to replace that
implementation by a ML model, which will use the GPT-3/Codex model to generate
data using the same API as Datafaker uses, making it easy to replace the
stable implementation by the GPT-3/Codex model.

## Usage

```java
// Create an OpenAI value generator, instantiated with your OpenAI key  
FakeValuesService openAIService = new OpenAIFakeValuesService("my-openai-key");

// Inject the value generator in the Faker
Faker openAIFaker = new Faker(openAIService, new FakerContext(new Locale("en", "US"), new RandomService()));

// This value is now generated using the OpenAI GPT-3 model
System.out.println("firstname: " + openAIFaker.name().firstName());
```

## Known issues and limitations

* The generation is slow, since for every generation, we need to do an HTTP call. This is being worked on to improve -> **UPDATE** Items are now generated per 5 (default), and are cached.
* Not all generators are currently being intercepted by Datafaker OpenAI module, so some of them still might be using the YAML files.
* Be aware that you need to use an OpenAI API key to access the model. While currently the generation is free, in the future OpenAI most likely will charge for this model.