import java.util.List;
import java.util.Scanner;

public class VistaEmpleados {
    private final Scanner entrada;
    public VistaEmpleados(Scanner entrada) { this.entrada = entrada; }
    public String pedir(String mensaje) {
        System.out.print(mensaje); return entrada.nextLine().trim();
    }
    public int pedirEntero(String mensaje) { return Integer.parseInt(pedir(mensaje)); }
    public double pedirSueldo() { return Double.parseDouble(pedir("Sueldo (ejemplo 1500.50): ")); }
    public void mensaje(String texto) { System.out.println(texto); }
    public void listar(List<Empleado> empleados) {
        if (empleados.isEmpty()) mensaje("No hay empleados registrados.");
        else empleados.forEach(e -> mensaje(e.toString()));
    }
    public int menu() {
        mensaje("\n1 Listar empleados\n2 Agregar empleado\n3 Buscar por numero\n4 Eliminar por numero\n5 Salir");
        return pedirEntero("Opcion: ");
    }
}
