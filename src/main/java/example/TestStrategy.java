package example;
import example.domain.game.Location;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class TestStrategy {
    public static void main(String[] args) {
        String filePath = "src/main/java/example/cave.txt";

        try {
            // Wczytaj wszystkie linie z pliku
            List<String> lines = Files.readAllLines(Paths.get(filePath));

            // Ustal rozmiary tablicy na podstawie danych z pliku
            int rows = lines.size();
            int columns = lines.get(0).length();

            // Utwórz tablicę map
            char[][] map = new char[rows][columns];

            // Przepisz zawartość pliku do tablicy
            for (int i = 0; i < rows; i++) {
                map[i] = lines.get(i).toCharArray();
            }

            // Wyświetlenie tablicy map (opcjonalne)
            for (char[] row : map) {
                System.out.println(new String(row));
            }
            Location currentLocation = new Location(findRowWithP(map),findColWithP(map));
            System.out.println(TestSubs.dijkstry(map,currentLocation));
        } catch (IOException e) {
            System.err.println("Błąd podczas wczytywania pliku: " + e.getMessage());
        }




    }

    public static int findRowWithP(char[][] map) {
        // Iteracja po wierszach
        for (int row = 0; row < map.length; row++) {
            // Iteracja po kolumnach w wierszu
            for (int col = 0; col < map[row].length; col++) {
                if (map[row][col] == 'O') {
                    return row; // Zwróć indeks wiersza
                }
            }
        }
        return -1; // Jeśli 'P' nie zostało znalezione
    }
    public static int findColWithP(char[][] map) {
        // Iteracja po wierszach
        for (int row = 0; row < map.length; row++) {
            // Iteracja po kolumnach w wierszu
            for (int col = 0; col < map[row].length; col++) {
                if (map[row][col] == 'O') {
                    return row; // Zwróć indeks wiersza
                }
            }
        }
        return -1; // Jeśli 'P' nie zostało znalezione
    }
}
