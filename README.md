# JDala
an experimental implementation of DALA on top of Java


## Licence
View software licence [here](LICENSE.md).
This code contains a shaded version of some of the classes from [Collections.java.util.concurrent](https://github.com/openjdk/jdk/tree/master) package

## Set up
The program needs to be compiled twice to run, first for the agent and second is the actual program

First run to build the java agent (and skip the tests as they need the agent to pass)
```shell
mvn clean package -DskipTests
```

Static Attachment of agent needs to be set. If using intellij this can be done by adding opting the run configuration and adding
```shell
-javaagent:target/jdala-agent.jar
```
to the VM options. In command line this can just be added as an extra flag.

## Annotations
There are three possible annotations that a local variable can have. These are:

### Immutable

```java
import nz.ac.wgtn.ecs.jdala.annotation.Immutable;

@Immutable Box var = new Box("foo");
```

### Isolated

```java
import nz.ac.wgtn.ecs.jdala.annotation.Isolated;

@Isolated Box var = new Box("foo");
```

### Local

```java
import nz.ac.wgtn.ecs.jdala.annotation.Local;

@Local Box var = new Box("foo");
```


## Implementation Details
This the decisions made and how they effect the project are stored in the [docs](./docs) folder.

- [Annotations](./docs/Annotations.md)
- [Deep Immutability](./docs/Deep_Immutability.md)
- [Isolated Behaviour](./docs/Isolated_Behaviour.md)
- [Object Validation](./docs/Object_Validation.md)
- [Default Immutable Classes](./docs/Default_Immutable_Classes.md)
- [Portal Classes](./docs/Portal_Classes.md)