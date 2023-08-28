public class IndicadorDeErrores { //cada error se identifica con un numero entero
    public void mostrarError(int n, Terminal s, String cadenaS){
        switch (n) { //100 -> errores de [bloque] | 200 -> errores de [proposicion] | 300 -> Errores de [condicion] | 400 -> Errores de [factor]
            case -1 -> System.out.println("El programa esta vacío");
            case 1 -> msjError("'[PROGRAMA]'", "'PUNTO'", s, cadenaS);
            case 2 -> msjError("'[PROGRAMA] -> (PUNTO)'", "'EOF'", s, cadenaS);
            case 101 -> msjError("'[BLOQUE] -> (CONST)'", "'IDENTIFICADOR'", s, cadenaS);
            case 102 -> msjError("'[BLOQUE] -> (CONST) -> (IDENTIFICADOR)", "'IGUAL'", s, cadenaS);
            case 103 -> msjError("'[BLOQUE] -> (CONST) -> (IDENTIFICADOR) -> (IGUAL)'", "'NUMERO'", s, cadenaS);
            case 104 -> msjError("'[BLOQUE] -> (CONST) -> (IDENTIFICADOR) -> (IGUAL) -> (NUMERO) '", "'PUNTO_Y_COMA' o 'COMA'", s, cadenaS);
            case 105 -> msjError("'[BLOQUE] -> (VAR)'", "'IDENTIFICADOR'", s, cadenaS);
            case 106 -> msjError("'[BLOQUE] -> (VAR) -> (IDENT)'", "'PUNTO_Y_COMA' o 'COMA'", s, cadenaS);
            case 107 -> msjError("'[BLOQUE] -> (PROCEDURE)'", "'IDENTIFICADOR'", s, cadenaS);
            case 108 -> msjError("'[BLOQUE] -> (PROCEDURE) -> (IDENTIFICADOR)'", "'PUNTO_Y_COMA'", s, cadenaS);
            case 109 -> msjError("'[BLOQUE] -> (PROCEDURE) -> (IDENTIFICADOR) -> (PUNTO_Y_COMA) -> [BLOQUE]'", "'PUNTO_Y_COMA'", s, cadenaS);
            case 201 -> msjError("'[PROPOSICION] -> (IDENTIFICADOR)'", "'ASIGNACION'", s, cadenaS);
            case 202 -> msjError("'[PROPOSICION] -> (CALL)'", "'IDENTIFICADOR'", s, cadenaS);
            case 203 -> msjError("'[PROPOSICION] -> (BEGIN) -> [PROPOSICION]'", "'END' o 'PUNTO_Y_COMA'", s, cadenaS);
            case 204 -> msjError("'[PROPOSICION] -> (IF) -> [CONDICION]'", "'THEN'", s, cadenaS);
            case 205 -> msjError("'[PROPOSICION] -> (WHILE) -> [CONDICION]'", "'DO'", s, cadenaS);
            case 206 -> msjError("'[PROPOSICION] -> (READLN)'", "'ABRE_PARENTESIS'", s, cadenaS);
            case 207 -> msjError("'[PROPOSICION] -> (READLN) -> (ABRE_PARENTESIS)'", "'IDENTIFICADOR'", s, cadenaS);
            case 208 -> msjError("'[PROPOSICION] -> (READLN) -> (ABRE_PARENTESIS) -> (IDENTIFICADOR)'", "'CIERRA_PARENTESIS' o 'COMA'", s, cadenaS);
            case 209 -> msjError("'[PROPOSICION] -> (READLN) -> (ABRE_PARENTESIS) -> (IDENTIFICADOR) -> (COMA)'", "'IDENTIFICADOR'", s, cadenaS);
            case 210 -> msjError("'[PROPOSICION] -> (WRITE)'", "'ABRE_PARENTESIS'", s, cadenaS);
            case 211 -> msjError("'[PROPOSICION] -> (WRITE) -> (CADENA) o [EXPRESION]'", "'CIERRA_PARENTESIS' o 'COMA'", s, cadenaS);
            case 301 -> msjError("'[CONDICION] -> (EXPRESION)'", "'IGUAL' o 'DISTINTO' o 'MENOR' o 'MENOR_IGUAL' o 'MAYOR' o 'MAYOR_IGUAL'", s, cadenaS);
            case 401 -> msjError("'[FACTOR]'", "'IDENTIFICADOR' o 'NUMERO' o 'ABRE_PARENTESIS", s, cadenaS);
            case 402 -> msjError("'[FACTOR] -> (ABRE_PARENTESIS) -> [EXPRESION]'", "'CIERRA_PARENTESIS'", s, cadenaS);
        }
        if (n != -1){
            System.out.println("Compilacion fallida");
        }
        System.exit(n);
    }
    void msjError(String zonaError, String simboloEsperado, Terminal simboloRecibido, String cadenaS){
        System.out.println("Ocurrió un error en " + zonaError + " // Se esperaba el simbolo: '" + simboloEsperado + "', pero se recibió: '" + simboloRecibido + "' - \"" + cadenaS + "\"");
    }
}
