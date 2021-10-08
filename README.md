# Goal

Provide a lightweight type safe and consistent way to access string based property collections using strongly typed properties objects. 

This library treats Properties as a first class concept. A property is defined up front in a Property class with keys, parsers, nullability and defaults all defined up front and explicitely. When you go to access the property you know exactly what you will get. You control how each property is converted and what to do if the conversion fails.  This allows you to decide if you want to handle parse exceptions or exit on parse exceptions. It allows you easily know if a key was actually provided or just populated with a default and it easily allows you to find out where the key originated.

## Basic Usage

```java
PropertyConfiguration propertyConfiguration = PropertyConfiguration.fromSystem();

BooleanProperty booleanDefaulted = new BooleanProperty("boolean.with.default", true);
Boolean booleanValue = propertyConfiguration.getValue(booleanDefaulted);

NullableBooleanProperty booleanOptional = new NullableBooleanProperty("boolean.with.no.default");
Optional<Boolean> optionalBooleanValue = propertyConfiguration.getValue(booleanOptional);

StringListProperty stringList = new StringListProperty("string.list", Collections.emptyList());
List<String> myList = propertyConfiguration.getValue(stringList);
```
## Basic Usage with Spring

```java
@Autowired
public Application(ConfigurableEnvironment environment) {
    List<PropertySource> sources = new ArrayList<>(SpringConfigurationPropertySource.fromConfigurableEnvironment(environment, true));
    PropertyConfiguration config = new PropertyConfiguration(sources);
    
    BooleanProperty booleanDefaulted = new BooleanProperty("boolean.with.default", true);
    Boolean booleanValue = propertyConfiguration.getValue(booleanDefaulted);
}
```

## Property Sources

Property sources can be created from several different property providers such as a Map, Java Properties object or Spring Configuration (using the Spring adapter). Property sources have names so the origin and source of properties can be tracked and logged. 

```java
Map<String, String> exampleMap = Map.of("property.one", "value");
MapPropertySource mapPropertySource = new MapPropertySource("in-memory", exampleMap);

JavaPropertiesPropertySource javaPropertiesPropertySource = new JavaPropertiesPropertySource("system", System.getProperties());
PropertyConfiguration propertyConfiguration = new PropertyConfiguration(mapPropertySource, javaPropertiesPropertySource);

```

## Help and logging

Each out of the box property provides information useful for logging user help. 
```java
EnumListProperty<Example> enumList = new EnumListProperty<Example>("example.enum.list", Collections.singletonList(Example.Two), Example.class);
System.out.println("Property Key: " + enumList.getKey()); //                                                Property Key: example.enum.list
System.out.println("Type: " + enumList.describeType()); //                                                  Type: Example List
System.out.println("Default: " + enumList.describeDefault()); //                                            Default: Two
System.out.println("Case Sensitive: " + enumList.isCaseSensitive()); //                                     Case Sensitive: True
System.out.println("Comma Separated: " + enumList.isCommaSeparated()); //                                   Comma Separated: True
System.out.println("Example Values: " + StringUtils.join(enumList.listExampleValues(), ",")); //  Example Values: One,Two
System.out.println("Only Example Values: " + enumList.isOnlyExampleValues()); //                            Only Example Values: True
```

## Advanced Types

The library supports several advanced enum types to handle specific property use cases. 

### Soft Enum

A soft enum lets the user specify an enum value OR any value. The benefit being help, example values and known enum types can be handled gracefully. This is particularly useful for 'enums' where you only know a subset of values and it is passed through to an external system.

```java
SoftEnumProperty<Example> property = new SoftEnumProperty<>("soft.enum", SoftEnumValue.ofEnumValue(Example.ANOTHER), Example.class);

SoftEnumValue<Example> softEnumValue = config.getValue(property);
if (softEnumValue.getEnumValue().isPresent()) {
    methodUsingActualEnum(softEnumValue.getEnumValue().get());
} else if (softEnumValue.getSoftValue().isPresent()) {
    methodUsingOtherValue(softEnumValue.getSoftValue().get());
}
```

### Extended Enum

An extended enum lets the user specify one of two enums. This is useful if you are using an enum from another library but want to add your own additional values to it. 

```java
final ExtendedEnumProperty<ExtensionEnum, BaseEnum> property = new ExtendedEnumProperty<>("enum.nullable", ExtendedEnumValue.ofExtendedValue(ExtensionEnum.EXTENDED_VALUE), ExtensionEnum.class, BaseEnum.class);

ExtendedEnumValue<ExtensionEnum, BaseEnum> value = config.getValue(property);
if (value.getExtendedValue().isPresent()) {
    handleExtendedValue(value.getExtendedValue().get());
} else if (value.getBaseValue().isPresent()) {
    handleBaseValue(value.getBaseValue().get());
}
```

### Filterable Enum

A filterable enum is an enum that also supports ALL or NONE. It is similar to Extended Enum but provides additional utilities for converting the provided values into actual enum values. 

```java
final FilterableEnumListProperty<Example> property = new FilterableEnumListProperty<>("enum.list", Collections.emptyList(), Example.class);

final List<FilterableEnumValue<Example>> value = config.getValue(property);
List<Example> values = FilterableEnumUtils.populatedValues(value, Example.class); //ALL is converted into [ELEMENT... ], NONE is converted to [], 'value1,value2' is converted to [VALUE1, VALUE2]

if (values.contains(Example.THING)) {
    doTheThing();
}
```

### Passthrough

Sometimes it is convenient to get all properties that start with a prefix, for example if you are passing through a set of values to an external system. A passthrough property provides a mechanism to get all keys with a prefix. 

```java
// Config = { example.passthrough.key1 = value1 } 
PassthroughProperty property = new PassthroughProperty("example.passthrough");
Map<String, String> passthroughMap = propertyConfiguration.getRaw(property);
//Then map contains { key1 = value1 }
```

## Configuration Validation

When a property cannot be properly coerced to it's type an InvalidPropertyException with a ValueParseException inner is thrown. Only TypedProperties will throw in this manner.

Some applicaitions may preer to throw these exceptions during a startup phase. To do this, you must query the property configuration for parse exceptions using a collection of all known properties.

```java
for (TypedProperty property : knownTypedProperties) {
    Optional<ValueParseException> exception = propertyConfiguration.getPropertyException(property);
    if (exception.isPresent()) {
        // Log exception and exit application
    }
}
```

If you would prefer to never throw parse exceptions, there are methods that do not throw that can be used to get property values but you must specify what they return in the case of an exception (either a default or empty).

```java
PropertyConfiguration config = configOf( "example.key", "some value which cannot be converted to a boolean")
BooleanProperty property = new BooleanProperty("example.key", true);

config.getValue(property); // throws InvalidPropertyException
config.getValueOrDefault(property) // returns the properties default
```

```java
PropertyConfiguration config = configOf( "example.key", "some value which cannot be converted to a boolean")
NullableBooleanProperty property = new NullableBooleanProperty("example.key");

config.getValue(property); // throws InvalidPropertyException
config.getValueOrEmpty(property) // returns Optional.empty()
```

## Property Source

It is sometimes convenient to display to the user where a property came from. The property configuration can tell you where a property was sourced and the origin of the property. 

```java
Map<String, String> firstMap = Map.of("property.first", "value");
MapPropertySource firstSource = new MapPropertySource("first-source", firstMap);

Map<String, String> secondMap = Map.of("property.second", "value");
MapPropertySource secondSource = new MapPropertySource("second-source", secondMap);

PropertyConfiguration propertyConfiguration = new PropertyConfiguration(firstSource, secondSource);
NullableBooleanProperty propertyFirst = new NullableBooleanProperty("property.first");
NullableBooleanProperty propertySecond = new NullableBooleanProperty("property.second");

propertyConfiguration.getPropertySource(propertyFirst); // first-source
propertyConfiguration.getPropertySource(propertySecond); // second-source

```

## Checking if Property Was Provided

Sometimes getting a default value is not sufficient, you need to know if the property was actually provided by the user - for example to decide whether or not you should warn that a property is deprecated. 

```java
BooleanProperty deprecatedButHasDefault = new BooleanProperty("property.key", true);

config.getValue(deprecatedButHasDefault); // we don't know if this value was provided because the property was set or if it was a default

if (config.wasPropertyProvided(deprecatedButHasDefault)) {
  System.out.println(deprecatedButHasDefault.getKey() + " is deprecated. Please do not provide it.");
}
```
