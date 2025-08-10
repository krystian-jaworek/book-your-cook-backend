# Krok 1: Użyj oficjalnego, lekkiego obrazu z Javą 17
FROM openjdk:17-slim

# Krok 2: Ustaw katalog roboczy wewnątrz kontenera
WORKDIR /app

# Krok 3: Skopiuj opakowanie Mavena i plik pom.xml
# Robimy to w osobnych krokach, aby wykorzystać buforowanie warstw Dockera
COPY .mvn/ .mvn
COPY mvnw pom.xml ./

# Krok 4: Pobierz wszystkie zależności projektu
RUN ./mvnw dependency:go-offline

# Krok 5: Skopiuj resztę kodu źródłowego aplikacji
COPY src ./src

# Krok 6: Zbuduj aplikację, tworząc plik .jar
RUN ./mvnw clean package -DskipTests

# Krok 7: Zdefiniuj komendę startową dla aplikacji
CMD ["java", "-jar", "target/book-your-cook-backend-0.0.1-SNAPSHOT.jar"]