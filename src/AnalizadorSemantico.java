public class AnalizadorSemantico { //base + desplazamiento - 1
    private final int maxIdent = 25;
    private IdentificadorBean tabla[] = new IdentificadorBean[maxIdent];
    private IndicadorDeErrores indicadorErrores;

    public AnalizadorSemantico(IndicadorDeErrores indicadorErrores) {
        this.indicadorErrores = indicadorErrores;
    }

    public int obtenerIndiceTabla(int inicio, int fin, String nombre){ //fin = base en scope local, o 0 en scope global
        int i = inicio;
        while (i >= fin){
            if (tabla[i].getNombre() == tabla[inicio].getNombre()){
                break;
            }
            i--;
        }
        return i;
    }

    public void guardarEnTabla(int pos, String nombre, Terminal tipo, int valor){
        tabla[pos] = new IdentificadorBean();
        tabla[pos].setNombre(nombre);
        tabla[pos].setTipo(tipo);
        tabla[pos].setValor(valor);
    }

    public Terminal obtenerTipo(int pos){
        return tabla[pos].getTipo();
    }
}
