public class Empleado {
    private int numero;
    private String nombre;
    private double sueldo;

    public Empleado(int numero, String nombre, double sueldo) {
        setNumero(numero); setNombre(nombre); setSueldo(sueldo);
    }
    public int getNumero() { return numero; }
    public String getNombre() { return nombre; }
    public double getSueldo() { return sueldo; }
    public void setNumero(int numero) {
        if (numero <= 0) throw new IllegalArgumentException("El numero debe ser positivo.");
        this.numero = numero;
    }
    public void setNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty() || nombre.matches(".*[;\\r\\n].*"))
            throw new IllegalArgumentException("El nombre no puede estar vacio ni contener ; o saltos de linea.");
        this.nombre = nombre.trim();
    }
    public void setSueldo(double sueldo) {
        if (!Double.isFinite(sueldo) || sueldo < 0)
            throw new IllegalArgumentException("El sueldo debe ser un numero finito mayor o igual que cero.");
        this.sueldo = sueldo;
    }
    public String aRegistro() { return numero + ";" + nombre + ";" + sueldo; }
    @Override public String toString() {
        return String.format(java.util.Locale.ROOT, "Numero: %d | Nombre: %s | Sueldo: %.2f", numero, nombre, sueldo);
    }
}
