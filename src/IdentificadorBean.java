public class IdentificadorBean {
    private String nombre;
    private Terminal tipo;
    private int valor;

    public String getNombre() {
        return nombre;
    }

    public Terminal getTipo() {
        return tipo;
    }

    public int getValor() {
        return valor;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setTipo(Terminal tipo) {
        this.tipo = tipo;
    }

    public void setValor(int valor) {
        this.valor = valor;
    }
}
