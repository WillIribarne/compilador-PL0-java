/*import java.io.IOException;

public class Draft_AnalizadorSintactico { // usar valores de retorno y parametros de funcion para verificar el contenido de s || si s tiene el necesario, s = null, si no, escanear();

    private final AnalizadorLexico aLex;
    private final IndicadorDeErrores indicadorErrores = new IndicadorDeErrores();

    public Draft_AnalizadorSintactico(AnalizadorLexico aLex) {
        this.aLex = aLex;
    }

    public void analizar() throws IOException {
        programa();
    }

    private void programa() throws IOException {
        Terminal s;
        aLex.scanner();
        s = aLex.getS();
        s = bloque(s);
        if (s == Terminal.PUNTO){
            aLex.scanner();
            s = aLex.getS();
            if (s == Terminal.EOF){
                compilacionSatisfactoria();
            }
        } else {
            indicadorErrores.mostrarError(1);
        }
    }

    private Terminal bloque(Terminal s) throws IOException {
        switch(s){
            case CONST:
                while (s != Terminal.PUNTO_Y_COMA){
                    aLex.scanner();
                    s = aLex.getS();
                    if (s == Terminal.IDENTIFICADOR){
                        aLex.scanner();
                        s = aLex.getS();
                        if (s == Terminal.IGUAL){
                            aLex.scanner();
                            s = aLex.getS();
                            if (s == Terminal.NUMERO){
                                aLex.scanner();
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
                aLex.scanner();
                s = aLex.getS();
                if (s != Terminal.VAR && s != Terminal.PROCEDURE){
                    break;
                }
            case VAR:
                if (s == Terminal.VAR){
                    while (s != Terminal.PUNTO_Y_COMA){
                        aLex.scanner();
                        s = aLex.getS();
                        if (s == Terminal.IDENTIFICADOR){
                            aLex.scanner();
                            s = aLex.getS();
                            if (s != Terminal.PUNTO_Y_COMA && s != Terminal.COMA){
                                indicadorErrores.mostrarError(106);
                            }
                        } else {
                            indicadorErrores.mostrarError(105);
                        }
                    }
                    aLex.scanner();
                    s = aLex.getS();
                    if (s != Terminal.PROCEDURE){
                        break;
                    }
                }
            case PROCEDURE:
                aLex.scanner();
                s = aLex.getS();
                if (s == Terminal.IDENTIFICADOR){
                    aLex.scanner();
                    s = aLex.getS();
                    if (s == Terminal.PUNTO_Y_COMA){
                        aLex.scanner();
                        s = aLex.getS();
                        s = bloque(s); // rechequear
                        if (s != Terminal.PUNTO_Y_COMA){
                            indicadorErrores.mostrarError(109);
                        }
                        aLex.scanner();
                        s = aLex.getS();
                    } else {
                        indicadorErrores.mostrarError(108);
                    }
                } else {
                    indicadorErrores.mostrarError(107);
                }
                break;
        }
        s = proposicion(s);
        return s;
    }

    private Terminal proposicion(Terminal s) throws IOException {
        boolean entroSwitch = false;
        switch(s){
            case IDENTIFICADOR:
                entroSwitch = true;
                aLex.scanner();
                s = aLex.getS();
                if (s == Terminal.ASIGNACION){
                    aLex.scanner();
                    s = aLex.getS();
                    s = expresion(s);
                } else {
                    indicadorErrores.mostrarError(214);
                }
                break;
            case CALL:
                entroSwitch = true;
                aLex.scanner();
                s = aLex.getS();
                if (s != Terminal.IDENTIFICADOR){
                    indicadorErrores.mostrarError(213);
                } else {
                    aLex.scanner();
                    s = aLex.getS();
                }
                break;
            case BEGIN:
                entroSwitch = true;
                aLex.scanner();
                s = aLex.getS();
                s = proposicion(s);
                while (s != Terminal.END){
                    if (s == Terminal.PUNTO_Y_COMA){
                        aLex.scanner();
                        s = aLex.getS();
                        if (s != Terminal.END){
                            s = proposicion(s);
                        }
                        if (s != Terminal.END && s != Terminal.PUNTO_Y_COMA){
                            indicadorErrores.mostrarError(201);
                        }
                    } else {
                        indicadorErrores.mostrarError(202);
                    }
                }
                aLex.scanner();
                s = aLex.getS();
                break;
            case IF:
                entroSwitch = true;
                aLex.scanner();
                s = aLex.getS();
                s = condicion(s);
                if (s == Terminal.THEN){
                    aLex.scanner();
                    s = aLex.getS();
                    s = proposicion(s);
                } else {
                    indicadorErrores.mostrarError(203);
                }
                break;
            case WHILE:
                entroSwitch = true;
                aLex.scanner();
                s = aLex.getS();
                s = condicion(s);
                if (s == Terminal.DO){
                    aLex.scanner();
                    s = aLex.getS();
                    s = proposicion(s); //??????????????????????????? sospechoso que no haya scanner
                } else {
                    indicadorErrores.mostrarError(204);
                }
                break;
            case READLN:
                entroSwitch = true;
                aLex.scanner();
                s = aLex.getS();
                if (s == Terminal.ABRE_PARENTESIS){
                    aLex.scanner();
                    s = aLex.getS();
                    if (s == Terminal.IDENTIFICADOR){
                        aLex.scanner();
                        s = aLex.getS();
                    } else {
                        indicadorErrores.mostrarError(205);
                    }
                    while(s != Terminal.CIERRA_PARENTESIS){
                        if (s == Terminal.COMA){
                            aLex.scanner();
                            s = aLex.getS();
                            if (s == Terminal.IDENTIFICADOR){
                                aLex.scanner();
                                s = aLex.getS();
                            } else {
                                indicadorErrores.mostrarError(206);
                            }
                        } else {
                            indicadorErrores.mostrarError(207);
                        }
                    }
                    aLex.scanner();
                    s = aLex.getS();
                } else {
                    indicadorErrores.mostrarError(208);
                }
                break;
            case WRITELN:
                entroSwitch = true;
                aLex.scanner();
                s = aLex.getS();
                if (s != Terminal.ABRE_PARENTESIS){
                    break;
                }
            case WRITE:
                entroSwitch = true;
                if (s == Terminal.WRITE){
                    aLex.scanner();
                    s = aLex.getS();
                }
                if (s == Terminal.ABRE_PARENTESIS){
                    aLex.scanner();
                    s = aLex.getS();
                    if (s == Terminal.CADENA_LITERAL){
                        aLex.scanner();
                        s = aLex.getS();
                    } else {
                        s = expresion(s);
                    }
                    while (s != Terminal.CIERRA_PARENTESIS){
                        if (s == Terminal.COMA){
                            aLex.scanner();
                            s = aLex.getS();
                            if (s == Terminal.CADENA_LITERAL){
                                aLex.scanner();
                                s = aLex.getS();
                            } else {
                                s = expresion(s);
                            }
                        } else {
                            indicadorErrores.mostrarError(209);
                        }
                    }
                    aLex.scanner();
                    s = aLex.getS();
                } else {
                    indicadorErrores.mostrarError(210);
                }
                break;
        }
        if (!entroSwitch){
            aLex.scanner();
            s = aLex.getS();
        }
        return s;
    }

    private Terminal condicion(Terminal s) throws IOException {
        if (s == Terminal.ODD){
            aLex.scanner();
            s = aLex.getS();
            s = expresion(s);
        } else {
            s = expresion(s);
            if (s == Terminal.IGUAL || s == Terminal.DISTINTO || s == Terminal.MENOR || s == Terminal.MENOR_IGUAL || s == Terminal.MAYOR || s == Terminal.MAYOR_IGUAL){
                aLex.scanner();
                s = aLex.getS();
                s = expresion(s);
            }
        }
        return s;
    }

    private Terminal expresion(Terminal s) throws IOException {
        if (s == Terminal.MAS || s ==Terminal.MENOS){
            aLex.scanner();
            s = aLex.getS();
        }
        s = termino(s);
        while (s == Terminal.MENOS || s == Terminal.MAS){
            aLex.scanner();
            s = aLex.getS();
            s = termino(s);
        }
        return s;
    }

    private Terminal termino(Terminal s) throws IOException {
        factor(s);
        aLex.scanner();
        s = aLex.getS();
        while (s == Terminal.POR || s == Terminal.DIVIDIDO){
            aLex.scanner();
            s = aLex.getS();
            factor(s);
            aLex.scanner();
            s = aLex.getS();
        }
        return s;
    }

    private void factor(Terminal s) throws IOException {
        if (s == Terminal.IDENTIFICADOR || s == Terminal.NUMERO || s == Terminal.ABRE_PARENTESIS){
            if (s == Terminal.ABRE_PARENTESIS){
                aLex.scanner();
                s = aLex.getS();
                s = expresion(s);
                if (s != Terminal.CIERRA_PARENTESIS){
                    indicadorErrores.mostrarError(211);
                }
            }
        } else {
            indicadorErrores.mostrarError(212);
        }
    }

    private void compilacionSatisfactoria(){
        System.out.println("El programa ha compilado correctamente.");
    }
}
 */