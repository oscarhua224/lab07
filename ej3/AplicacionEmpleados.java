import java.nio.file.Paths;
import java.util.Scanner;

public class AplicacionEmpleados {
    public static void main(String[] args) {
        try (Scanner entrada = new Scanner(System.in, "UTF-8")) {
            ModeloEmpleados modelo = new ModeloEmpleados(Paths.get("empleados.txt"));
            VistaEmpleados vista = new VistaEmpleados(entrada);
            new ControladorEmpleados(modelo, vista).iniciar();
        }
    }
}
