### Object Validation
#### Variable Calls
Currently, all calls of a variable have checks that see what thread it is in and if it correctly managed.
Note: this will come at a runtime performance hit.

Code before analysis:
```java
Box a = new Box("food");
@Local Box obj = new Box("foo");
Box aliasObj = obj;
obj = new Box("bar");
obj.value = "bar2";
```

Code after analysis:
```java
Box a = new Box("food");
ThreadChecker.validate(a);
Box obj = new Box("foo");
ThreadChecker.register(obj);
ThreadChecker.validate(obj);
Box aliasObj = obj;
ThreadChecker.validate(obj);
obj = new Box("bar");
ThreadChecker.validate(obj);
obj.value = "bar2";
```

This means that even `a` which can be considered `unsafe` is validated, this is to cover for cases like reflection which mean that we better be safe by checking all possible approaches

#### Thread Detection
This program uses the access of variables as the check against the conditions set by the annotations.

This means that if an object is marked with an annotation and is passed to another thread it may not throw up and error until the object is accessed. This is fine as it still doesn't allow any changes or reads to be made without validation to occur.
