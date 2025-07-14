import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.GsonBuilder;

import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.io.File;

public class ConversorDeMoneda {

    private static final String API_KEY = "fed9262c355a6bb44f210b58";
    private static final String API_URL = "https://v6.exchangerate-api.com/v6/" + API_KEY + "/latest/";
    private static final Scanner scanner = new Scanner(System.in);
    private static final List<String> historialConsola = new ArrayList<>();
    private static final List<conversor.Conversion> historialJSON = new ArrayList<>();
    private static final Set<String> MONEDAS = Set.of("ARS", "BOB", "BRL", "CLP", "COP", "USD");

    public static void main(String[] args) throws IOException, InterruptedException {
        System.out.println("Sea bienvenido/a al Conversor de Moneda");

        int opcion;
        do {
            mostrarMenu();
            opcion = scanner.nextInt();
            scanner.nextLine(); // limpiar buffer

            if (opcion >= 1 && opcion <= 6) {
                String origen = obtenerCodigoPorOpcion(opcion);
                System.out.println("Ingrese el código de la moneda destino (ARS, BOB, BRL, CLP, COP, USD):");
                String destino = scanner.nextLine().toUpperCase();

                if (!MONEDAS.contains(destino)) {
                    System.out.println("Moneda destino no válida.");
                    continue;
                }

                System.out.println("Ingrese el monto a convertir:");
                double monto = scanner.nextDouble();

                double tasa = obtenerTasaCambio(origen, destino);
                double resultado = monto * tasa;

                System.out.printf("Resultado: %.2f %s = %.2f %s%n", monto, origen, resultado, destino);

                registrarConversion(origen, destino, monto, resultado);
                guardarHistorialComoJSON();

            } else if (opcion == 7) {
                mostrarHistorial();
            } else if (opcion != 8) {
                System.out.println("Elija una opción válida.");
            }
        } while (opcion != 8);
    }

    private static void mostrarMenu() {
        System.out.println("\nSeleccione una opción:");
        System.out.println("1 - ARS - Peso argentino");
        System.out.println("2 - BOB - Boliviano boliviano");
        System.out.println("3 - BRL - Real brasileño");
        System.out.println("4 - CLP - Peso chileno");
        System.out.println("5 - COP - Peso colombiano");
        System.out.println("6 - USD - Dólar estadounidense");
        System.out.println("7 - Ver historial de conversiones");
        System.out.println("8 - Salir");
    }

    private static String obtenerCodigoPorOpcion(int opcion) {
        return switch (opcion) {
            case 1 -> "ARS";
            case 2 -> "BOB";
            case 3 -> "BRL";
            case 4 -> "CLP";
            case 5 -> "COP";
            case 6 -> "USD";
            default -> "";
        };
    }

    private static double obtenerTasaCambio(String base, String destino) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL + base))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        JsonObject json = JsonParser.parseString(response.body()).getAsJsonObject();
        JsonObject conversionRates = json.getAsJsonObject("conversion_rates");

        return conversionRates.get(destino).getAsDouble();
    }

    private static void registrarConversion(String origen, String destino, double monto, double resultado) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        String texto = String.format("[%s] %.2f %s -> %.2f %s", timestamp, monto, origen, resultado, destino);
        historialConsola.add(texto);

        conversor.Conversion conversion = new conversor.Conversion(timestamp, origen, destino, monto, resultado);
        historialJSON.add(conversion);
    }

    private static void mostrarHistorial() {
        if (historialConsola.isEmpty()) {
            System.out.println("No hay conversiones registradas.");
        } else {
            System.out.println("Historial de conversiones:");
            historialConsola.forEach(System.out::println);
        }
    }

    private static void guardarHistorialComoJSON() {
        try (FileWriter writer = new FileWriter("historial.json")) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(historialJSON, writer);
        } catch (IOException e) {
            System.out.println("Error al guardar el historial en archivo JSON: " + e.getMessage());
        }
    }
}


