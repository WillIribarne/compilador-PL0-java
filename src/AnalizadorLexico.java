import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;

public class AnalizadorLexico {
    private final BufferedReader br;
    private Terminal s;
    private String cad;
    private String listado;
    private String restante;
    private int numLinea = 1;
    private final IndicadorDeErrores indicadorErrores;
    private HashMap<String, Terminal> nodosTerminales;

    public AnalizadorLexico(BufferedReader br, IndicadorDeErrores indicadorErrores) {
        this.br = br;
        this.indicadorErrores = indicadorErrores;
        this.listado = "";
        cargaHashMapNodosTerminales();
    }

    public void scanner() throws IOException {
        if ((restante == null && s != Terminal.EOF) || restante.isBlank()){
            do {
                restante = br.readLine();
                if (restante == null){
                    break;
                }
                //System.out.println(numLinea + ": " + restante); /* linea util para debuggear aSin y aSem */
                listado = listado + numLinea + ": " + restante + "\n";
                numLinea++;
            } while (restante.isBlank());

            if (restante == null){
                s = Terminal.EOF;
                cad = "";
            }
        }
        if (restante != null && !restante.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            cad = "";
            char c = construyeCadYDevuelveChar(sb);
            if (Character.isLetter(c)){
                while (!restante.isEmpty() && Character.isLetterOrDigit(restante.charAt(0))){
                    construyeCadYDevuelveChar(sb);
                }
            } else if (Character.isDigit(c)){
                while (!restante.isEmpty() && Character.isDigit(restante.charAt(0))){
                    construyeCadYDevuelveChar(sb);
                }
            } else if (c == '\''){
                do {
                    c = construyeCadYDevuelveChar(sb);
                } while ((!restante.isEmpty() && c != '\''));
                if (restante.isEmpty() && c != '\''){
                    indicadorErrores.mostrarError(3, null, null);
                } else if (restante.charAt(0) == '\'' && cad.length() > 1){
                    construyeCadYDevuelveChar(sb);
                }
            } else if (c == '<'){
                if (restante.charAt(0) == '='){
                    construyeCadYDevuelveChar(sb);
                } else if (restante.charAt(0) == '>'){
                    construyeCadYDevuelveChar(sb);
                }
            } else if (c == '>'){
                if (restante.charAt(0) == '='){
                    construyeCadYDevuelveChar(sb);
                }
            } else if (c == ':'){
                if (restante.charAt(0) == '='){
                    construyeCadYDevuelveChar(sb);
                }
            }
            s = nodosTerminales.get(cad.toUpperCase());
            if (s == null){
                c = cad.charAt(0);
                if (c == '\'' && cad.charAt(cad.length() - 1) == '\''){
                    s = Terminal.CADENA_LITERAL;
                } else if (Character.isDigit(c) && !(c == 0 && cad.length() > 1)){
                    s = Terminal.NUMERO;
                } else if (Character.isLetter(c)){
                    s = Terminal.IDENTIFICADOR;
                } else {
                    s = Terminal.NULO;
                }
            }
        }
    }

    public Terminal getS(){
        return s;
    }

    public void cortarRestante(){
        if (restante.length() == 1){
            restante = "";
        } else {
            restante = restante.substring(1);
        }
    }

    public String getCad() {
        return cad;
    }

    private void cargaHashMapNodosTerminales(){
        this.nodosTerminales = new HashMap<>();
        nodosTerminales.put("IF", Terminal.IF);
        nodosTerminales.put("CALL", Terminal.CALL);
        nodosTerminales.put("WRITELN", Terminal.WRITELN);
        nodosTerminales.put("WRITE", Terminal.WRITE);
        nodosTerminales.put("READLN", Terminal.READLN);
        nodosTerminales.put("CONST", Terminal.CONST);
        nodosTerminales.put("VAR", Terminal.VAR);
        nodosTerminales.put("PROCEDURE", Terminal.PROCEDURE);
        nodosTerminales.put("BEGIN", Terminal.BEGIN);
        nodosTerminales.put("END", Terminal.END);
        nodosTerminales.put("THEN", Terminal.THEN);
        nodosTerminales.put("DO", Terminal.DO);
        nodosTerminales.put("WHILE", Terminal.WHILE);
        nodosTerminales.put("ODD", Terminal.ODD);
        nodosTerminales.put("=", Terminal.IGUAL);
        nodosTerminales.put(">", Terminal.MAYOR);
        nodosTerminales.put("<", Terminal.MENOR);
        nodosTerminales.put(">=", Terminal.MAYOR_IGUAL);
        nodosTerminales.put("<=", Terminal.MENOR_IGUAL);
        nodosTerminales.put("<>", Terminal.DISTINTO);
        nodosTerminales.put("(", Terminal.ABRE_PARENTESIS);
        nodosTerminales.put(")", Terminal.CIERRA_PARENTESIS);
        nodosTerminales.put("+", Terminal.MAS);
        nodosTerminales.put("-", Terminal.MENOS);
        nodosTerminales.put("*", Terminal.POR);
        nodosTerminales.put("/", Terminal.DIVIDIDO);
        nodosTerminales.put(".", Terminal.PUNTO);
        nodosTerminales.put(";", Terminal.PUNTO_Y_COMA);
        nodosTerminales.put(",", Terminal.COMA);
        nodosTerminales.put(":=", Terminal.ASIGNACION);
    }

    private char construyeCadYDevuelveChar(StringBuilder sb){
        char c = restante.charAt(0);
        while ((c == ' ' || c == 9)){
            if (cad.length() >= 1){
                if (cad.charAt(0) == '\''){
                    break;
                }
            }
            cortarRestante();
            c = restante.charAt(0);
        }
        sb.append(c);
        cad = sb.toString();
        cortarRestante();
        return c;
    }

    public String getListado(){
        return listado;
    }
}
