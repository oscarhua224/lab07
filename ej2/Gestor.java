import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

public class Gestor {
    private final Path archivo;
    private Map<String, Personaje> personajes = new LinkedHashMap<>();

    public Gestor(Path archivo) throws IOException {
        this.archivo = archivo.toAbsolutePath();
        if (Files.exists(this.archivo)) personajes = leerArchivo(this.archivo);
        else guardar(personajes);
    }

    private static String clave(String nombre) {
        return nombre.trim().toLowerCase(Locale.ROOT);
    }

    private static Map<String, Personaje> leerArchivo(Path ruta) throws IOException {
        Map<String, Personaje> resultado = new LinkedHashMap<>();
        try (BufferedReader lector = Files.newBufferedReader(ruta, StandardCharsets.UTF_8)) {
            String linea; int numeroLinea = 0;
            while ((linea = lector.readLine()) != null) {
                numeroLinea++;
                if (linea.trim().isEmpty()) continue;
                try {
                    Personaje p = Personaje.desdeRegistro(linea);
                    String k = clave(p.getNombre());
                    if (resultado.containsKey(k)) throw new IllegalArgumentException("Nombre duplicado: " + p.getNombre());
                    resultado.put(k, p);
                } catch (IllegalArgumentException e) {
                    throw new IOException("Registro invalido en la linea " + numeroLinea + ": " + e.getMessage(), e);
                }
            }
        }
        return resultado;
    }

    private void guardar(Map<String, Personaje> datos) throws IOException {
        Path temporal = Files.createTempFile(archivo.getParent(), "personajes-", ".tmp");
        try {
            try (BufferedWriter escritor = Files.newBufferedWriter(temporal, StandardCharsets.UTF_8)) {
                for (Personaje p : datos.values()) {
                    escritor.write(p.aRegistro()); escritor.newLine();
                }
            }
            try {
                Files.move(temporal, archivo, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            } catch (AtomicMoveNotSupportedException e) {
                Files.move(temporal, archivo, StandardCopyOption.REPLACE_EXISTING);
            }
        } finally { Files.deleteIfExists(temporal); }
    }

    private void confirmar(Map<String, Personaje> nuevos) throws IOException {
        guardar(nuevos);
        personajes = nuevos;
    }

    public boolean agregar(Personaje p) throws IOException {
        String k = clave(p.getNombre());
        if (personajes.containsKey(k)) return false;
        Map<String, Personaje> nuevos = new LinkedHashMap<>(personajes);
        nuevos.put(k, p.copia()); confirmar(nuevos); return true;
    }

    public List<Personaje> listar() {
        List<Personaje> resultado = new ArrayList<>();
        for (Personaje p : personajes.values()) resultado.add(p.copia());
        return resultado;
    }

    public boolean modificar(String nombre, int vida, int ataque, int defensa, int alcance) throws IOException {
        Personaje actual = personajes.get(clave(nombre));
        if (actual == null) return false;
        Personaje nuevo = new Personaje(actual.getNombre(), vida, ataque, defensa, alcance, actual.getNivel());
        reemplazar(nuevo); return true;
    }

    private void reemplazar(Personaje p) throws IOException {
        Map<String, Personaje> nuevos = new LinkedHashMap<>(personajes);
        nuevos.put(clave(p.getNombre()), p); confirmar(nuevos);
    }

    public boolean borrar(String nombre) throws IOException {
        String k = clave(nombre);
        if (!personajes.containsKey(k)) return false;
        Map<String, Personaje> nuevos = new LinkedHashMap<>(personajes);
        nuevos.remove(k); confirmar(nuevos); return true;
    }

    public List<Personaje> ordenar(String atributo) {
        new Personaje("Validacion", 1, 1, 1, 1).atributo(atributo);
        List<Personaje> resultado = listar();
        resultado.sort(Comparator.comparingInt((Personaje p) -> p.atributo(atributo)).reversed()
            .thenComparing(Personaje::getNombre));
        return resultado;
    }

    public List<Personaje> filtrar(String atributo, int minimo) {
        List<Personaje> resultado = ordenar(atributo);
        resultado.removeIf(p -> p.atributo(atributo) < minimo);
        return resultado;
    }

    public boolean actualizarAtributo(String nombre, String atributo, int valor) throws IOException {
        Personaje actual = personajes.get(clave(nombre));
        if (actual == null) return false;
        Personaje nuevo = actual.copia();
        switch (atributo.toLowerCase(Locale.ROOT)) {
            case "vida": nuevo.setVida(valor); break;
            case "ataque": nuevo.setAtaque(valor); break;
            case "defensa": nuevo.setDefensa(valor); break;
            case "alcance": nuevo.setAlcance(valor); break;
            default: throw new IllegalArgumentException("Use vida, ataque, defensa o alcance.");
        }
        reemplazar(nuevo); return true;
    }

    public boolean subirNivel(String nombre) throws IOException {
        Personaje actual = personajes.get(clave(nombre));
        if (actual == null) return false;
        Personaje nuevo = actual.copia(); nuevo.subirNivel(); reemplazar(nuevo); return true;
    }

    public int importar(Path origen) throws IOException {
        Map<String, Personaje> importados = leerArchivo(origen);
        Map<String, Personaje> nuevos = new LinkedHashMap<>(personajes);
        int cantidad = 0;
        for (Map.Entry<String, Personaje> entrada : importados.entrySet()) {
            if (!nuevos.containsKey(entrada.getKey())) {
                nuevos.put(entrada.getKey(), entrada.getValue()); cantidad++;
            }
        }
        if (cantidad > 0) confirmar(nuevos);
        return cantidad;
    }

    public void cargarPredefinidos() throws IOException {
        Map<String, Personaje> nuevos = new LinkedHashMap<>(personajes);
        Personaje[] iniciales = {
            new Personaje("Caballero", 4, 2, 4, 2),
            new Personaje("Guerrero", 2, 4, 2, 4),
            new Personaje("Arquero", 2, 4, 1, 8)
        };
        for (Personaje p : iniciales) nuevos.putIfAbsent(clave(p.getNombre()), p);
        confirmar(nuevos);
    }

    public String estadisticas() {
        if (personajes.isEmpty()) return "Total: 0. No hay personajes para calcular promedios.";
        long vida = 0, ataque = 0, defensa = 0, alcance = 0, nivel = 0;
        for (Personaje p : personajes.values()) {
            vida += p.getVida(); ataque += p.getAtaque(); defensa += p.getDefensa();
            alcance += p.getAlcance(); nivel += p.getNivel();
        }
        int n = personajes.size();
        return String.format(Locale.ROOT,
            "Total: %d%nPromedios: vida=%.2f, ataque=%.2f, defensa=%.2f, alcance=%.2f, nivel=%.2f",
            n, vida / (double)n, ataque / (double)n, defensa / (double)n, alcance / (double)n, nivel / (double)n);
    }

    private static String pedir(Scanner entrada, String mensaje) {
        System.out.print(mensaje); return entrada.nextLine().trim();
    }
    private static int entero(Scanner entrada, String mensaje) { return Integer.parseInt(pedir(entrada, mensaje)); }
    private static void mostrar(List<Personaje> lista) {
        if (lista.isEmpty()) System.out.println("No hay personajes.");
        else lista.forEach(System.out::println);
    }
    private static void informar(boolean exito, String mensaje) {
        System.out.println(exito ? mensaje : "No se encontro el personaje.");
    }

    public static void main(String[] args) {
        try (Scanner entrada = new Scanner(System.in, "UTF-8")) {
            Path ruta = Paths.get("personajes.txt");
            boolean primeraEjecucion = !Files.exists(ruta);
            Gestor gestor = new Gestor(ruta);
            if (primeraEjecucion) gestor.cargarPredefinidos();
            
            boolean continuar = true;
            while (continuar) {
                System.out.println("\n1 Listar | 2 Agregar | 3 Modificar | 4 Borrar | 5 Ordenar");
                System.out.println("6 Filtrar | 7 Actualizar atributo | 8 Estadisticas | 9 Importar | 10 Subir nivel | 0 Salir");
                try {
                    int opcion = entero(entrada, "Opcion: ");
                    String nombre, atributo;
                    switch (opcion) {
                        case 1: mostrar(gestor.listar()); break;
                        case 2:
                            nombre = pedir(entrada, "Nombre: ");
                            Personaje nuevo = new Personaje(nombre, entero(entrada, "Vida: "), entero(entrada, "Ataque: "),
                                entero(entrada, "Defensa: "), entero(entrada, "Alcance: "));
                            System.out.println(gestor.agregar(nuevo) ? "Personaje agregado y guardado." : "Ese nombre ya existe."); break;
                        case 3:
                            nombre = pedir(entrada, "Nombre: ");
                            informar(gestor.modificar(nombre, entero(entrada, "Vida: "), entero(entrada, "Ataque: "),
                                entero(entrada, "Defensa: "), entero(entrada, "Alcance: ")), "Personaje modificado y guardado."); break;
                        case 4: informar(gestor.borrar(pedir(entrada, "Nombre: ")), "Personaje eliminado y archivo actualizado."); break;
                        case 5: mostrar(gestor.ordenar(pedir(entrada, "Atributo: "))); break;
                        case 6:
                            atributo = pedir(entrada, "Atributo: ");
                            mostrar(gestor.filtrar(atributo, entero(entrada, "Valor minimo: "))); break;
                        case 7:
                            nombre = pedir(entrada, "Nombre: "); atributo = pedir(entrada, "Atributo: ");
                            informar(gestor.actualizarAtributo(nombre, atributo, entero(entrada, "Nuevo valor: ")), "Atributo actualizado y guardado."); break;
                        case 8: System.out.println(gestor.estadisticas()); break;
                        case 9: System.out.println("Importados: " + gestor.importar(Paths.get(pedir(entrada, "Ruta del archivo: ")))); break;
                        case 10: informar(gestor.subirNivel(pedir(entrada, "Nombre: ")), "Nivel y atributos actualizados y guardados."); break;
                        case 0: continuar = false; break;
                        default: System.out.println("Opcion invalida.");
                    }
                } catch (IOException | IllegalArgumentException | ArithmeticException e) {
                    System.out.println("Error: " + e.getMessage());
                } catch (NoSuchElementException e) { continuar = false; }
            }
        } catch (IOException e) { System.out.println("No se pudo iniciar el gestor: " + e.getMessage()); }
    }
}
