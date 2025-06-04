# Arrow Extension Function Generator Example

This example project demonstrates the use of the Arrow Extension Function Generator plugin to generate Arrow extension functions for Jackson ObjectMapper.

## What's Included

1. **JacksonUtils.kt** - A utility class with Jackson ObjectMapper functions that might throw exceptions
2. **User.kt** - A simple data class for testing JSON serialization/deserialization
3. **UserService.kt** - A service class that demonstrates the use of generated Arrow extensions in a real-world scenario
4. **JacksonArrowExtensionsTest.kt** - A test class that demonstrates the use of the generated Arrow extensions
5. **UserServiceTest.kt** - A test class for the UserService that shows how to use the generated extensions in a more complex scenario

## How It Works

The plugin is configured in `build.gradle.kts` to generate Arrow extensions for Jackson ObjectMapper:

```kotlin
arrowGen {
    // Include Jackson ObjectMapper package
    includePackages.set(listOf(
        "com.fasterxml.jackson.databind.**"
    ))

    // Enable both raise and either generation
    raise.set(true)
    either.set(true)
}
```

This generates extension functions in the `com.fasterxml.jackson.databind.arrow` package that wrap the original functions with Arrow's error handling capabilities.

## Generated Extensions

For each function in the Jackson ObjectMapper that matches the package filter, the plugin generates:

1. **Raise Extensions** - Extension functions for Arrow's `Raise<E>` API that allow for Railway-oriented programming
2. **Either Extensions** - Functions that return `Either<E, T>` for explicit error handling

## Example Usage

### Using Raise Extensions

```kotlin
val result = either<JsonProcessingException, User> {
    mapper.parseJson<User>(validUserJson)
}
```

### Using Either Extensions

```kotlin
val userEither: Either<JsonProcessingException, User> = mapper.parseJsonEither(validUserJson)
userEither.fold(
    { error -> handleError(error) },
    { user -> processUser(user) }
)
```

## Running the Tests

The tests demonstrate that the IDE correctly resolves all symbols from the generated extensions and that the code works as expected.

To run the tests:

```
./gradlew :example:test
```
