# Market Activity Monitor (Java)

Spring Boot project that reads public Polymarket data and displays top trader activity.

## Why
This is a learning project for:
- working with public APIs
- parsing JSON
- building small CLI tools
- basic data filtering and notifications (later)

## Run the Spring Boot app

This project must be run as a Maven project because Spring Boot dependencies are defined in `pom.xml`.

Requirements:
- Java 17 or newer
- Maven, or IntelliJ IDEA with Maven support enabled

### IntelliJ IDEA

1. Open the project folder.
2. Right-click `pom.xml`.
3. Choose `Add as Maven Project` or `Reload Maven Project`.
4. Wait for Maven dependencies to finish downloading.
5. Run `com.marketmonitor.MarketMonitorApplication`.
6. Open `http://localhost:8080/traders` or `http://localhost:8080/whales/view`.

If you see `java: package org.springframework.stereotype does not exist`, IntelliJ is compiling the project as a plain Java project instead of a Maven project. Reload `pom.xml` as Maven and make sure the Maven tool window shows `spring-boot-starter-web` and `spring-boot-starter-thymeleaf`.

### Terminal

Install Maven first if `mvn -version` fails.

On macOS with Homebrew:

```bash
brew install maven
```

```bash
mvn spring-boot:run
```

Then open:

```text
http://localhost:8080/traders
```

Whale Tracker page:

```text
http://localhost:8080/whales/view
```

## Important note
This project is for educational and research purposes only.
