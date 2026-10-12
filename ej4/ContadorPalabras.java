import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import javax.swing.JFileChooser;
import javax.swing.SwingUtilities;

public class ContadorPalabras {
    public static class Resultado {
        public long lineas, palabras, caracteres;
        public final Map<String, Long> frecuencias = new TreeMap<>();
        public double promedio() { return lineas == 0 ? 0 : palabras / (double) lineas; }
        public List<Map.Entry<String, Long>> masFrecuentes() {
            List<Map.Entry<String, Long>> lista = new ArrayList<>(frecuencias.entrySet());
            lista.sort(Map.Entry.<String, Long>comparingByValue().reversed().thenComparing(Map.Entry.comparingByKey()));
            if (lista.isEmpty()) return lista;
            long maximo = lista.get(0).getValue();
            lista.removeIf(entrada -> entrada.getValue() != maximo);
            return lista;
        }
    }

    public static Resultado contar(Path archivo) throws IOException {
        Resultado resultado = new Resultado();
        try (BufferedReader lector = Files.newBufferedReader(archivo, StandardCharsets.UTF_8)) {
            String linea;
            while ((linea = lector.readLine()) != null) {
                resultado.lineas++;

                resultado.caracteres += linea.codePointCount(0, linea.length());
                StringBuilder palabra = new StringBuilder();
                for (int i = 0; i < linea.length();) {
                    int caracter = linea.codePointAt(i);
                    i += Character.charCount(caracter);
                    if (Character.isLetterOrDigit(caracter)) palabra.appendCodePoint(caracter);
                    else registrar(palabra, resultado);
                }
                registrar(palabra, resultado);
            }
        }
        return resultado;
    }

    private static void registrar(StringBuilder palabra, Resultado resultado) {
        if (palabra.length() == 0) return;
        String texto = palabra.toString().toLowerCase(Locale.ROOT);
        resultado.palabras++;
        resultado.frecuencias.merge(texto, 1L, Long::sum);
        palabra.setLength(0);
    }

    public static void mostrar(Resultado resultado) {
        System.out.println("Total de lineas: " + resultado.lineas);
        System.out.println("Total de palabras: " + resultado.palabras);
        System.out.println("Total de caracteres: " + resultado.caracteres);
        System.out.printf(Locale.ROOT, "Promedio de palabras por linea: %.2f%n", resultado.promedio());
        System.out.println("Palabras mas frecuentes (todas las empatadas en el maximo):");
        if (resultado.frecuencias.isEmpty()) System.out.println("No hay palabras.");
        else for (Map.Entry<String, Long> entrada : resultado.masFrecuentes())
            System.out.println(entrada.getKey() + ": " + entrada.getValue());
    }

    private static Path elegirArchivo() throws Exception {
        final Path[] seleccionado = new Path[1];
        SwingUtilities.invokeAndWait(() -> {
            JFileChooser selector = new JFileChooser();
            selector.setDialogTitle("Seleccione un archivo de texto UTF-8");
            selector.setFileSelectionMode(JFileChooser.FILES_ONLY);
            if (selector.showOpenDialog(null) == JFileChooser.APPROVE_OPTION)
                seleccionado[0] = selector.getSelectedFile().toPath();
        });
        return seleccionado[0];
    }

    public static void main(String[] args) {
        while (true) {
            try {
                Path archivo = elegirArchivo();
                if (archivo == null) { System.out.println("Seleccion cancelada."); return; }
                if (!Files.isRegularFile(archivo) || !Files.isReadable(archivo)) {
                    System.out.println("El archivo no existe o no se puede leer. Seleccione otro."); continue;
                }
                mostrar(contar(archivo)); return;
            } catch (IOException e) {
                System.out.println("No se pudo leer el archivo como texto UTF-8: " + e.getMessage() + ". Seleccione otro");
            } catch (Exception e) {
                System.out.println("No se pudo abrir el selector: " + e.getMessage()); return;
            }
        }
    }
}
