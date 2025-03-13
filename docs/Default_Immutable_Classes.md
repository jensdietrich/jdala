## Default Immutable Classes

Immutable classes are defined by the fact that they can't be modified, this includes all objects that they contain and so on.
By definition this means that primitives like int, long, double etc are all immutable and so JDala recognizes them as such. 

Classes where all fields contained (including all stored inside any contained object's fields) are final can also be considered immutable.
In the standard library some examples of classes that meet this requirement are:
```
java.lang.String
java.lang.Integer
java.lang.Long
java.lang.Boolean
java.lang.Double
java.time.LocalDate
java.math.BigInteger
java.math.BigDecimal
```

Users can also create their own classes that may meet this requirement. For this reason there is a way to add to this list.
All objects created by any of the classes that are contained in [immutable-classes.txt](../jdala-core/src/main/resources/immutable-classes.txt) will
be treated as immutable.


