# An Example GraphQL + JanusGraph Web App 

This repository demonstrates how to implement a graph-based web application using JanusGraph. It is written in Kotlin and exposes a GraphQL interface.

The application was written using:

- [Kotlin](https://kotlinlang.org/)
- [Janus Graph](http://janusgraph.org/)
- [Ferma OGM](http://syncleus.com/Ferma/)
- [Spring Boot](https://projects.spring.io/spring-boot/)
- [GraphQL](http://graphql.org/)
- [GraphQL Tools](https://github.com/graphql-java/graphql-java-tools)

The project is split into 2 packages, `framework` and `starwars`. `framework` adds some features to the Ferma OGM, such as providing type-safe traversals. The `starwars` package shows how any data model could plug into the `framework` package and Ferma OGM with minimal effort. The `framework` package is not currently offered as a library due to its lack of documentation and testing.


To build and run the application locally, from the repository root:
```
gradlew run
```

Then load `http://localhost:5000/graphiql.html` and start playing around in GraphiQL
