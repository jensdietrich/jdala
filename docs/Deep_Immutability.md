## Deep Immutability

This stems from [this](https://github.com/jensdietrich/jdala/issues/2#issuecomment-2533375836) issue.

The program will work using deep immutability meaning once an object is set to be immutable any objects contained by it are also considered to be immutable.

This causes an issue to when creating a new object like an array (and any objects that contain an array) and builder methods.

##### Example 1
```java
@Immutable ArrayList<String> immutableArrayList = new ArrayList<>();
```
This is rather useless as nothing can be added to the array as everything including the internal array is immutable.

To not limit the use of `@Immutable` arrays we allow objects to become immutable after creation.

```java
ArrayList<String> mutableArrayList = new ArrayList<>();
mutableArrayList.add("foo");
mutableArrayList.add("bar");
@Immutable ArrayList<String> immutableArrayList = mutableArrayList;
```

Now nothing can be added to the ArrayList.
