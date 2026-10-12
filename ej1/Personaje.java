public class Personaje {
    private final String nombre;
    private int vida, ataque, defensa, alcance;

    public Personaje(String nombre, int vida, int ataque, int defensa, int alcance) {
        if (nombre == null || nombre.trim().isEmpty() || nombre.matches(".*[;\\r\\n].*")) {
            throw new IllegalArgumentException("El nombre no puede estar vacio ni contener ; o saltos de linea.");
        }
        positivo(vida); positivo(ataque); positivo(defensa); positivo(alcance);
        this.nombre = nombre.trim();
        this.vida = vida; this.ataque = ataque; this.defensa = defensa;
        this.alcance = alcance;
    }

    private static void positivo(int valor) {
        if (valor <= 0) throw new IllegalArgumentException("Los atributos deben ser enteros mayores que cero.");
    }

    public String getNombre() { return nombre; }
    public int getVida() { return vida; }
    public int getAtaque() { return ataque; }
    public int getDefensa() { return defensa; }
    public int getAlcance() { return alcance; }
    public void setVida(int valor) { positivo(valor); vida = valor; }
    public void setAtaque(int valor) { positivo(valor); ataque = valor; }
    public void setDefensa(int valor) { positivo(valor); defensa = valor; }
    public void setAlcance(int valor) { positivo(valor); alcance = valor; }

    public Personaje copia() {
        return new Personaje(nombre, vida, ataque, defensa, alcance);
    }

    public String aRegistro() {
        return nombre + ";" + vida + ";" + ataque + ";" + defensa + ";" + alcance;
    }

    public static Personaje desdeRegistro(String linea) {
        String[] datos = linea.split(";", -1);
        if (datos.length != 5) {
            throw new IllegalArgumentException("Se esperan nombre;vida;ataque;defensa;alcance.");
        }
        return new Personaje(datos[0], Integer.parseInt(datos[1].trim()),
            Integer.parseInt(datos[2].trim()), Integer.parseInt(datos[3].trim()),
            Integer.parseInt(datos[4].trim()));
    }

    @Override public String toString() {
        return String.format("%s | vida=%d, ataque=%d, defensa=%d, alcance=%d",
            nombre, vida, ataque, defensa, alcance);
    }
}
