import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

public class ModeloEmpleados {
    private final Path archivo;
    public ModeloEmpleados(Path archivo) { this.archivo = archivo.toAbsolutePath(); }

    public List<Empleado> leerEmpleados() throws IOException {
        List<Empleado> empleados = new ArrayList<>();
        if (!Files.exists(archivo)) return empleados;
        Set<Integer> numeros = new HashSet<>();
        try (BufferedReader lector = Files.newBufferedReader(archivo, StandardCharsets.UTF_8)) {
            String linea; int numeroLinea = 0;
            while ((linea = lector.readLine()) != null) {
                numeroLinea++;
                if (linea.trim().isEmpty()) continue;
                try {
                    String[] datos = linea.split(";", -1);
                    if (datos.length != 3) throw new IllegalArgumentException("Formato esperado: numero;nombre;sueldo.");
                    Empleado empleado = new Empleado(Integer.parseInt(datos[0].trim()), datos[1], Double.parseDouble(datos[2].trim()));
                    if (!numeros.add(empleado.getNumero())) throw new IllegalArgumentException("Numero duplicado.");
                    empleados.add(empleado);
                } catch (IllegalArgumentException e) {
                    throw new IOException("Registro invalido en la linea " + numeroLinea + ": " + e.getMessage(), e);
                }
            }
        }
        return empleados;
    }

    public boolean agregarEmpleado(Empleado empleado) throws IOException {
        List<Empleado> empleados = leerEmpleados();
        for (Empleado actual : empleados) if (actual.getNumero() == empleado.getNumero()) return false;
        empleados.add(empleado); guardar(empleados); return true;
    }

    public Empleado buscarEmpleado(int numero) throws IOException {
        for (Empleado empleado : leerEmpleados()) if (empleado.getNumero() == numero) return empleado;
        return null;
    }

    public boolean eliminarEmpleado(int numero) throws IOException {
        List<Empleado> empleados = leerEmpleados();
        boolean eliminado = empleados.removeIf(e -> e.getNumero() == numero);
        if (eliminado) guardar(empleados);
        return eliminado;
    }

    private void guardar(List<Empleado> empleados) throws IOException {
        
        Path temporal = Files.createTempFile(archivo.getParent(), "empleados-", ".tmp");
        try {
            try (BufferedWriter escritor = Files.newBufferedWriter(temporal, StandardCharsets.UTF_8)) {
                for (Empleado empleado : empleados) {
                    escritor.write(empleado.aRegistro()); escritor.newLine();
                }
            }
            try {
                Files.move(temporal, archivo, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            } catch (AtomicMoveNotSupportedException e) {
                Files.move(temporal, archivo, StandardCopyOption.REPLACE_EXISTING);
            }
        } finally { Files.deleteIfExists(temporal); }
    }
}
