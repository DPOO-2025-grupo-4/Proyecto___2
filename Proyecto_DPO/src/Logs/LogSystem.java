package Logs;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Log simple en memoria. Solo el administrador debería leerlo desde la interfaz.
 */
public final class LogSystem {

    private static final List<String> registros = new ArrayList<>();
    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private LogSystem() {
        // Evitar instancias
    }

    public static void registrar(String tipo, String descripcion) {
        String linea = String.format(
                "%s [%s] %s",
                LocalDateTime.now().format(FORMATTER),
                tipo,
                descripcion
        );
        registros.add(linea);
    }

    public static List<String> obtenerRegistros() {
        return Collections.unmodifiableList(registros);
    }
}
