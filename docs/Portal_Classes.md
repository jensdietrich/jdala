## Portal Classes

Portal classes are allow isolated objects to travel from one thread to another.


```json
{
  "className": "java.util.concurrent.BlockingQueue",
  "entryMethods": ["offer", "put", "add"],
  "exitMethods": ["poll", "take", "remove"],
  "includeSubClasses": true
}
```

Users can also create their own portal classes that may meet this requirement. For this reason there is a way to add to this list.
All portal classes are stored in [portal-classes.json](../jdala-core/src/main/resources/portal-classes.json) can be used to
move isolated object from one thread to another.
