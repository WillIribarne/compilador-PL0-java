public class AnalizadorSemantico {
    private final int maxIdent = 25;
    private final IdentificadorBean[] tabla = new IdentificadorBean[maxIdent];
    private final IndicadorDeErrores indicadorErrores;

    public AnalizadorSemantico(IndicadorDeErrores indicadorErrores) {
        this.indicadorErrores = indicadorErrores;
    }

    public int obtenerIndiceTabla(int inicio, int fin, String nombre){
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
        if (pos == maxIdent){
            indicadorErrores.mostrarError(506, null, null);
        } else {
            tabla[pos] = new IdentificadorBean();
            tabla[pos].setNombre(nombre);
            tabla[pos].setTipo(tipo);
            tabla[pos].setValor(valor);
        }
    }

    public Terminal obtenerTipo(int pos){
        return tabla[pos].getTipo();
    }
}
