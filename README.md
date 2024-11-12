# Quick Start Guide

## Run the Application

1. Start the application using Docker Compose:
```bash
docker-compose up
```

2. Access Swagger Documentation:
    - Open your web browser
    - Go to: `http://localhost:8080/swagger-ui/index.html`

That's it! You can now use the API through the Swagger interface.

To stop the application, press `Ctrl+C` or run:
```bash
docker-compose down
```

## Run the Test

To run the tests using Gradle, follow these steps:

- Open a terminal or command prompt.
- Navigate to the root directory of your project. 
- Execute the following command:
```bash
./gradlew test
```