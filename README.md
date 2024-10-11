# BiteBuddy

A Java-based recipe recommendation app that fetches recipes based on ingredients you have and displays them using a beautiful JavaFX UI.

## Features

- Search for recipes based on available ingredients
- Fetch recipes from a third-party API (e.g., Spoonacular API)
- Display recipe titles and details in a user-friendly JavaFX interface
- Filter recipes based on the ingredients you provide

## Requirements

- Java 11 or higher
- Maven
- Dependencies:
- [OkHttp](https://square.github.io/okhttp/) for HTTP requests
- [Gson](https://github.com/google/gson) for JSON parsing
- [JavaFX](https://openjfx.io/) for the UI

## Setup Instructions

### 1. Clone the Repository

```bash
git https://github.com/SauravSreejith/bitebuddy.git
cd bitebuddy
```

### 2. Get an API Key

To use the app, you will need to register for an API key from one of the following recipe APIs:

- [Spoonacular API](https://spoonacular.com/food-api)
- [Edamam Recipe Search API](https://developer.edamam.com/edamam-recipe-api)
- [TheMealDB](https://www.themealdb.com/api.php)

Once you have an API key, replace the `YOUR_API_KEY` placeholder in `RecipeService.java` with your actual API key.

### 3. Build the Project

```bash
mvn clean install
```

### 4. Run the App

```bash
mvn javafx:run
```

### 5. Usage

1. Launch the app.
2. Enter a list of ingredients in the search box (e.g., `tomato, cheese, pasta`).
3. Click the **Search Recipes** button.
4. The app will display a list of recipes based on the ingredients.

## Project Structure

```
├── src
│ ├── main
│ │ ├── java
│ │ │ ├── RecipeApp.java # Main JavaFX Application class
│ │ │ ├── RecipeService.java # Handles API requests
│ │ │ ├── RecipeParser.java # Parses API responses
│ │ │ └── Recipe.java # Recipe model class
│ └── resources
│ └── styles.css # Styles for the JavaFX UI
├── pom.xml # Maven dependencies and plugins
└── README.md # Project documentation
```

## Dependencies

In your `pom.xml`, ensure you have the following dependencies:

```xml
<dependencies>
<!-- OkHttp for making HTTP requests -->
<dependency>
<groupId>com.squareup.okhttp3</groupId>
<artifactId>okhttp</artifactId>
<version>4.9.3</version>
</dependency>

<!-- Gson for parsing JSON responses -->
<dependency>
<groupId>com.google.code.gson</groupId>
<artifactId>gson</artifactId>
<version>2.8.8</version>
</dependency>

<!-- JavaFX for the user interface -->
<dependency>
<groupId>org.openjfx</groupId>
<artifactId>javafx-controls</artifactId>
<version>17.0.1</version>
</dependency>
</dependencies>
```

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
