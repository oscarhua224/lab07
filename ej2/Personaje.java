public class Personaje {
    private final String nombre;
    private int vida, ataque, defensa, alcance, nivel;

    public Personaje(String nombre, int vida, int ataque, int defensa, int alcance) {
        this(nombre, vida, ataque, defensa, alcance, 1);
    }

    public Personaje(String nombre, int vida, int ataque, int defensa, int alcance, int nivel) {
        if (nombre == null || nombre.trim().isEmpty() || nombre.matches(".*[;\\r\\n].*")) {
            throw new IllegalArgumentException("El nombre no puede estar vacio ni contener ; o saltos de linea.");
        }
        positivo(vida); positivo(ataque); positivo(defensa); positivo(alcance); positivo(nivel);
        this.nombre = nombre.trim();
        this.vida = vida; this.ataque = ataque; this.defensa = defensa;
        this.alcance = alcance; this.nivel = nivel;
    }

    private static void positivo(int valor) {
        if (valor <= 0) throw new IllegalArgumentException("Los atributos y el nivel deben ser enteros mayores que cero.");
    }

    public String getNombre() { return nombre; }
    public int getVida() { return vida; }
    public int getAtaque() { return ataque; }
    public int getDefensa() { return defensa; }
    public int getAlcance() { return alcance; }
    public int getNivel() { return nivel; }
    public void setVida(int valor) { positivo(valor); vida = valor; }
    public void setAtaque(int valor) { positivo(valor); ataque = valor; }
    public void setDefensa(int valor) { positivo(valor); defensa = valor; }
    public void setAlcance(int valor) { positivo(valor); alcance = valor; }

    public int atributo(String nombreAtributo) {
        switch (nombreAtributo.toLowerCase(java.util.Locale.ROOT)) {
            case "vida": return vida;
            case "ataque": return ataque;
            case "defensa": return defensa;
            case "alcance": return alcance;
            case "nivel": return nivel;
            default: throw new IllegalArgumentException("Atributo desconocido.");
        }
    }

    public Personaje copia() { return new Personaje(nombre, vida, ataque, defensa, alcance, nivel); }

    public void subirNivel() {
        int nuevaVida = Math.addExact(vida, 2);
        int nuevoAtaque = Math.addExact(ataque, 1);
        int nuevaDefensa = Math.addExact(defensa, 1);
        int nuevoAlcance = Math.addExact(alcance, 1);
        int nuevoNivel = Math.addExact(nivel, 1);
        vida = nuevaVida; ataque = nuevoAtaque; defensa = nuevaDefensa;
        alcance = nuevoAlcance; nivel = nuevoNivel;
    }

    public String aRegistro() {
        return nombre + ";" + vida + ";" + ataque + ";" + defensa + ";" + alcance + ";" + nivel;
    }

    public static Personaje desdeRegistro(String linea) {
        String[] datos = linea.split(";", -1);
        if (datos.length != 5 && datos.length != 6) {
            throw new IllegalArgumentException("Se esperan nombre;vida;ataque;defensa;alcance[;nivel].");
        }
        return new Personaje(datos[0], Integer.parseInt(datos[1].trim()),
            Integer.parseInt(datos[2].trim()), Integer.parseInt(datos[3].trim()),
            Integer.parseInt(datos[4].trim()), datos.length == 6 ? Integer.parseInt(datos[5].trim()) : 1);
    }

    @Override public String toString() {
        return String.format("%s | vida=%d, ataque=%d, defensa=%d, alcance=%d, nivel=%d",
            nombre, vida, ataque, defensa, alcance, nivel);
    }
}
