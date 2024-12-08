# JDala
an experimental implementation of DALA on top of Java


## Immutable

## Isolated

## Local

There are a few different ways that this can be implemented. The current way implemented only the object that is created is bound to be local, NOT the variable. Take the example below the 3 line java example is broken down to view each line and the restrictions on each.

Orange highlights the current implementation. Green & orange highlights another possible approach 

```java
    @Local Box a = new Box("foo");
    Box b = a;
    a = new Box("bar");
```

```mermaid
flowchart TD
    localA["@Local Box a = new Box('foo')"] --> varA[Box a]:::possibleLocalClass
    localA --> boxFoo["Box('foo')"]:::localclass
    classDef localclass fill:#b63
    classDef possibleLocalClass fill:#494
```

```mermaid
flowchart TD
    b["Box b = a;"] --> varB[Box b]
    b --> boxFoo["Box('foo')"]:::localclass
    classDef localclass fill:#b63
```

```mermaid
flowchart TD
    a["a = new Box('bar')"] --> varA[Box a]:::possibleLocalClass
    a --> boxBar["Box('Bar')"]:::possibleLocalClass
    classDef possibleLocalClass fill:#494
```

Currently, (highlighted in orange)
- Box a doesn't have any local requirement on it
- Box("foo") is restricted to be local only
- Box b doesn't have a requirement to be local
- Box('Bar') is not restricted to be local only