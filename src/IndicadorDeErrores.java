public class IndicadorDeErrores { //cada error se identifica con un numero entero
    public void mostrarError(int n){
        switch (n) { //100 -> errores de [bloque] | 200 -> erroes de [proposicion]
            case 1 -> msjError("'PROGRAMA'", "'.'");
            case 101 -> msjError("'CONST'", "'ident'");
            case 102 -> msjError("'CONST' -> 'ident'", "'='");
            case 103 -> msjError("'CONST' -> 'ident' -> '='", "'numero'");
            case 104 -> msjError("'CONST' -> 'ident' -> '=' -> 'numero'", "',' o ';'");
            case 105 -> msjError("'VAR'", "'ident'");
            case 106 -> msjError("'VAR' -> 'ident'", "',' o ';'");
            case 107 -> msjError("'PROCEDURE'", "'ident'");
            case 108 -> msjError("'PROCEDURE' -> 'ident'", "';'");
            case 109 -> msjError("'PROCEDURE' -> 'ident' -> ';' -> [bloque]", "';'");
            case 201 -> System.out.println("Exploto");
            case 202 -> System.out.println("Exploto");
            case 203 -> System.out.println("Exploto");
        }
    }
    void msjError(String en, String falta){
        System.out.println("ERROR EN " + en + " // FALTA " + falta);
    }
}
