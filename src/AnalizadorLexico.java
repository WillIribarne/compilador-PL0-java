import java.io.BufferedReader;
import java.io.IOException;

public class AnalizadorLexico {
    private BufferedReader br;
    private Terminal s;
    private String cad;
    private String listado;
    private String restante;
    private int numLinea;


    public AnalizadorLexico(BufferedReader br) {
        this.br = br;
    }

    public void escanear() throws IOException {
        if (restante == null || restante.isEmpty()){
            restante = br.readLine();
            if (restante == null){
                s = Terminal.EOF;
                cad = "";
            } else {
                s = null;
                numLinea++;
                listado = listado + numLinea + ": " + restante + "\n";
            }
        }
        if (restante != null && !restante.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            char c;
            c = restante.charAt(0);
            while (c == ' ' || c == 9){
                cortarRestante();
                c = restante.charAt(0);
            }
            sb.append(c);
            cad = sb.toString();
            cortarRestante();
            if (Character.isLetter(c)){
                while (!restante.isEmpty() && Character.isLetter(restante.charAt(0))){
                    c = restante.charAt(0);
                    sb.append(c);
                    cad = sb.toString();
                    cortarRestante();
                }
                if (cad.equalsIgnoreCase("IF")){ //modificar por un hashmap
                    s = Terminal.IF;
                } else if (cad.equalsIgnoreCase("CALL")){
                    s = Terminal.CALL;
                } else if (cad.equalsIgnoreCase("WRITELN")){
                    s = Terminal.WRITELN;
                } else if (cad.equalsIgnoreCase("WRITE")){
                    s = Terminal.WRITE;
                } else if (cad.equalsIgnoreCase("READLN")){
                    s = Terminal.READLN;
                } else if (cad.equalsIgnoreCase("CONST")){
                    s = Terminal.CONST;
                } else if (cad.equalsIgnoreCase("VAR")){
                    s = Terminal.VAR;
                } else if (cad.equalsIgnoreCase("PROCEDURE")){
                    s = Terminal.PROCEDURE;
                } else if (cad.equalsIgnoreCase("BEGIN")){
                    s = Terminal.BEGIN;
                } else if (cad.equalsIgnoreCase("END")){
                    s = Terminal.END;
                } else if (cad.equalsIgnoreCase("THEN")){
                    s = Terminal.THEN;
                } else if (cad.equalsIgnoreCase("WHILE")){
                    s = Terminal.WHILE;
                } else if (cad.equalsIgnoreCase("DO")){
                    s = Terminal.DO;
                } else if (cad.equalsIgnoreCase("ODD")){
                    s = Terminal.ODD;
                } else {
                    s = Terminal.IDENTIFICADOR;
                }
            } else if (Character.isDigit(c)){
                while (!restante.isEmpty() && Character.isDigit(restante.charAt(0))){
                    c = restante.charAt(0);
                    sb.append(c);
                    cad = sb.toString();
                    cortarRestante();
                }
                if (cad.charAt(0) == '0' && cad.length() > 1){
                    s = Terminal.NULO; // numero empieza con 0 (ej 056)
                } else {
                    s = Terminal.NUMERO;
                }
            } else if (c == '\''){
                while ((!restante.isEmpty() && restante.charAt(0) != '\'')){
                    c = restante.charAt(0);
                    sb.append(c);
                    cad = sb.toString();
                    cortarRestante();
                }
                if (restante.charAt(0) == '\'' && cad.length() > 1){
                    c = restante.charAt(0);
                    sb.append(c);
                    cad = sb.toString();
                    cortarRestante();
                    s = Terminal.CADENA_LITERAL;
                } else {
                    s = Terminal.NULO; //empieza con ' pero nunca se cierran
                }
            } else if (c == '='){
                s = Terminal.IGUAL;
            } else if (c == '<'){
                if (restante.charAt(0) == '='){
                    s = Terminal.MENOR_IGUAL;
                    c = restante.charAt(0);
                    sb.append(c);
                    cad = sb.toString();
                    cortarRestante();
                } else if (restante.charAt(0) == '>'){
                    s = Terminal.DISTINTO;
                    c = restante.charAt(0);
                    sb.append(c);
                    cad = sb.toString();
                    cortarRestante();
                } else {
                    s = Terminal.MENOR;
                }
            } else if (c == '>'){
                if (restante.charAt(0) == '='){
                    s = Terminal.MAYOR_IGUAL;
                    c = restante.charAt(0);
                    sb.append(c);
                    cad = sb.toString();
                    cortarRestante();
                } else {
                    s = Terminal.MAYOR;
                }
            } else if (c == '('){
                s = Terminal.ABRE_PARENTESIS;
            } else if (c == ')'){
                s = Terminal.CIERRA_PARENTESIS;
            } else if (c == '+') {
                s = Terminal.MAS;
            } else if (c == '-'){
                s = Terminal.MENOS;
            } else if (c == '*'){
                s = Terminal.POR;
            } else if (c == '/'){
                s = Terminal.DIVIDIDO;
            } else if (c == '.'){
                s = Terminal.PUNTO;
            } else if (c == ';'){
                s = Terminal.PUNTO_Y_COMA;
            } else if (c == ','){
                s = Terminal.COMA;
            } else if (c == ':'){
                if (restante.charAt(0) == '='){
                    s = Terminal.ASIGNACION;
                    c = restante.charAt(0);
                    sb.append(c);
                    cad = sb.toString();
                    cortarRestante();
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
}
