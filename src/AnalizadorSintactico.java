import java.io.IOException;

public class AnalizadorSintactico { //me confundí el getS con el escanear... oops (hay q arreglar practicamente todo)
    private final AnalizadorLexico aLex;
    private IndicadorDeErrores indicadorErrores;

    public AnalizadorSintactico(AnalizadorLexico aLex) {
        this.aLex = aLex;
    }

    public void analizar() throws IOException {
        programa();
    }

    private void programa() throws IOException {
        Terminal s = aLex.getS();
        s = bloque(aLex.getS());
        if (s == Terminal.PUNTO){
            aLex.escanear(); //solo se escanea en la regla 7 (cuando hay un circulito)
            if (aLex.getS() == Terminal.EOF){
                compilacionSatisfactoria();
            }
        } else {
            indicadorErrores.mostrarError(1);
        }
    }

    private Terminal bloque(Terminal s){ // puede terminar en . o ; (. para fin de programa, ; para fin de ciclo PROCEDURE)
        switch(s){
            case CONST:
                while (s != Terminal.PUNTO_Y_COMA){
                    if (aLex.getS() == Terminal.IDENTIFICADOR){
                        if (aLex.getS() == Terminal.IGUAL){
                            if (aLex.getS() == Terminal.NUMERO){
                                s = aLex.getS();
                                if (s != Terminal.PUNTO_Y_COMA && s != Terminal.COMA){
                                    indicadorErrores.mostrarError(104);
                                }
                            } else {
                                indicadorErrores.mostrarError(103);
                            }
                        } else {
                            indicadorErrores.mostrarError(102);
                        }
                    } else {
                        indicadorErrores.mostrarError(101);
                    }
                }
                s = aLex.getS();
                if (s != Terminal.VAR && s != Terminal.PROCEDURE){
                    break;
                }
            case VAR:
                if (s == Terminal.VAR){
                    while (s != Terminal.PUNTO_Y_COMA){
                        if (aLex.getS() == Terminal.IDENTIFICADOR){
                            s = aLex.getS();
                            if (s != Terminal.PUNTO_Y_COMA && s != Terminal.COMA){
                                indicadorErrores.mostrarError(106);
                            }
                        } else {
                            indicadorErrores.mostrarError(105);
                        }
                    }
                    s = aLex.getS();
                    if (s != Terminal.PROCEDURE){
                        break;
                    }
                }
            case PROCEDURE:
                if (aLex.getS() == Terminal.IDENTIFICADOR){
                    if (aLex.getS() == Terminal.PUNTO_Y_COMA){
                        bloque(s);
                        s = aLex.getS();
                        if (s != Terminal.PUNTO_Y_COMA){
                            indicadorErrores.mostrarError(109);
                        }
                    } else {
                        indicadorErrores.mostrarError(108);
                    }
                } else {
                    indicadorErrores.mostrarError(107);
                }
                break;
        }
        return proposicion(s);
    }

    private Terminal proposicion(Terminal s){
        switch (s){
            case IDENTIFICADOR:
                if (aLex.getS() == Terminal.ASIGNACION){
                    s = expresion(); // fijarse aca si no hay que usar el valor de retorno de la funcion... | PARTIR DESDE ACA SI HAY ERRORES -> ) o ;
                } else {
                    indicadorErrores.mostrarError(201);
                }
                break;
            case CALL:
                if (aLex.getS() != Terminal.IDENTIFICADOR){
                    indicadorErrores.mostrarError(202);
                }
                break;
            case BEGIN:
                proposicion(s);
                s = aLex.getS();
                while (s != Terminal.END){
                    if (s == Terminal.PUNTO_Y_COMA){
                        proposicion(s);
                        s = aLex.getS();
                        if (s != Terminal.END && s != Terminal.PUNTO_Y_COMA){
                            indicadorErrores.mostrarError(204);
                        }
                    } else {
                        indicadorErrores.mostrarError(203);
                    }
                }
                break;
            case IF:
                condicion();
                if (aLex.getS() == Terminal.THEN){
                    proposicion(s);
                } else {
                    indicadorErrores.mostrarError(205);
                }
                break;
            case WHILE:
                condicion();
                if (aLex.getS() == Terminal.DO){
                    proposicion(s);
                } else {
                    indicadorErrores.mostrarError(206);
                }
        }
        return s;
    }

    private void condicion(){
        Terminal s = aLex.getS();
        if (s == Terminal.ODD){
            expresion();
        } else {
            expresion();
            s = aLex.getS();
            if (s == Terminal.IGUAL || s == Terminal.DISTINTO || s == Terminal.MENOR || s == Terminal.MENOR_IGUAL || s == Terminal.MAYOR || s == Terminal.MAYOR_IGUAL){
                expresion();
            } else {
                indicadorErrores.mostrarError(301);
            }
        }
    }

    private Terminal expresion(){
        Terminal s = aLex.getS();
        if (s == Terminal.MAS || s == Terminal.MENOS){
            s = aLex.getS();
        }
        do {
            if (s == Terminal.MENOS || s == Terminal.MAS){
                s = termino(s);
            }
        } while (s == Terminal.MENOS || s == Terminal.MAS);
        return s;
    }

    private Terminal termino(Terminal s){ //termino(): para ver si tiene que loopear o no, tiene que adelantar a aLex en 1 simbolo hacia adelante, cuidado con eso.
        do {
            if (s == Terminal.POR || s == Terminal.MAS){
                s = aLex.getS();
            }
            factor(s);
            s = aLex.getS();
        } while (s == Terminal.POR || s == Terminal.MAS);

        return s;
    }

    private void factor(Terminal s){
        if (s == Terminal.IDENTIFICADOR || s == Terminal.NUMERO || s == Terminal.ABRE_PARENTESIS){
            if (s == Terminal.ABRE_PARENTESIS){
                s = expresion();
                if (s != Terminal.CIERRA_PARENTESIS){
                    indicadorErrores.mostrarError(402);
                }
            }
        } else {
            indicadorErrores.mostrarError(401);
        }
    }

    private void compilacionSatisfactoria(){
        System.out.println("El programa ha compilado correctamente.");
    }
}
