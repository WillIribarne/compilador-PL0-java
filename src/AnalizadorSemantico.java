public class AnalizadorSemantico { //base + desplazamiento - 1
    private final int maxIdent = 25;
    private final IdentificadorBean[] tabla = new IdentificadorBean[maxIdent];

    public int obtenerIndiceTabla(int inicio, int fin, String nombre){ //fin = base en scope local, o 0 en scope global
        int i = inicio;
        while (i >= fin){
            if (tabla[i].getNombre().equals(nombre)){
                break;
            }
            i--;
        }
        if (i < fin){
            i = -1;
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
