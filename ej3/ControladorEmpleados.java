import java.io.IOException;
import java.util.NoSuchElementException;

public class ControladorEmpleados {
    private final ModeloEmpleados modelo;
    private final VistaEmpleados vista;
    public ControladorEmpleados(ModeloEmpleados modelo, VistaEmpleados vista) {
        this.modelo = modelo; this.vista = vista;
    }
    public void iniciar() {
        try {
            vista.mensaje("Empleados almacenados al iniciar:");
            vista.listar(modelo.leerEmpleados());
        } catch (IOException e) { vista.mensaje("Error al leer el archivo: " + e.getMessage()); }
        boolean continuar = true;
        while (continuar) {
            try {
                switch (vista.menu()) {
                    case 1:
                        vista.listar(modelo.leerEmpleados()); vista.mensaje("Consulta completada."); break;
                    case 2:
                        Empleado nuevo = new Empleado(vista.pedirEntero("Numero: "), vista.pedir("Nombre: "), vista.pedirSueldo());
                        vista.mensaje(modelo.agregarEmpleado(nuevo) ? "Empleado agregado y guardado." : "Ya existe un empleado con ese numero."); break;
                    case 3:
                        Empleado encontrado = modelo.buscarEmpleado(vista.pedirEntero("Numero: "));
                        vista.mensaje(encontrado == null ? "Empleado no encontrado." : "Empleado encontrado: " + encontrado); break;
                    case 4:
                        vista.mensaje(modelo.eliminarEmpleado(vista.pedirEntero("Numero: ")) ? "Empleado eliminado y archivo actualizado." : "Empleado no encontrado."); break;
                    case 5: continuar = false; vista.mensaje("Programa finalizado."); break;
                    default: vista.mensaje("Opcion invalida.");
                }
            } catch (IllegalArgumentException e) { vista.mensaje("Entrada invalida: " + e.getMessage()); }
            catch (IOException e) { vista.mensaje("Error de archivo: " + e.getMessage()); }
            catch (NoSuchElementException e) { continuar = false; }
        }
    }
}
