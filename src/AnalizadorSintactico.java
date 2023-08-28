import java.io.IOException;

public class AnalizadorSintactico {

    private final AnalizadorLexico aLex;
    private final IndicadorDeErrores indicadorErrores = new IndicadorDeErrores();

    public AnalizadorSintactico(AnalizadorLexico aLex) {
        this.aLex = aLex;
    }

    public void parser() throws IOException {
        programa();
    }

    public void programa() throws IOException {
        Terminal s = usaScannerYDevuelveSimbolo();
        if (s != null){
            s = bloque(s);
        } else {
            indicadorErrores.mostrarError(-1, null, " ");
        }
        if (s == Terminal.PUNTO){
            s = usaScannerYDevuelveSimbolo();
            if (s == Terminal.EOF){
                compilacionSatisfactoria();
            } else {
                indicadorErrores.mostrarError(2, s, aLex.getCad());
            }
        } else {
            indicadorErrores.mostrarError(1, s, aLex.getCad());
        }
    }

    public Terminal bloque(Terminal s) throws IOException {
        switch(s){
            case CONST:
                while (s != Terminal.PUNTO_Y_COMA){
                    s = usaScannerYDevuelveSimbolo();
                    if (s == Terminal.IDENTIFICADOR){
                        s = usaScannerYDevuelveSimbolo();
                        if (s == Terminal.IGUAL){
                            s = usaScannerYDevuelveSimbolo();
                            if (s == Terminal.NUMERO){
                                s = usaScannerYDevuelveSimbolo();
                                if (s != Terminal.PUNTO_Y_COMA && s != Terminal.COMA){
                                    indicadorErrores.mostrarError(104, s, aLex.getCad());
                                }
                            } else {
                                indicadorErrores.mostrarError(103, s, aLex.getCad());
                            }
                        } else {
                            indicadorErrores.mostrarError(102, s, aLex.getCad());
                        }
                    } else {
                        indicadorErrores.mostrarError(101, s, aLex.getCad());
                    }
                }
                s = usaScannerYDevuelveSimbolo();
                if (s != Terminal.VAR && s != Terminal.PROCEDURE){
                    break;
                }
            case VAR:
                if (s == Terminal.VAR){
                    while (s != Terminal.PUNTO_Y_COMA){
                        s = usaScannerYDevuelveSimbolo();
                        if (s == Terminal.IDENTIFICADOR){
                            s = usaScannerYDevuelveSimbolo();
                            if (s != Terminal.PUNTO_Y_COMA && s != Terminal.COMA){
                                indicadorErrores.mostrarError(106, s, aLex.getCad());
                            }
                        } else {
                            indicadorErrores.mostrarError(105, s, aLex.getCad());
                        }
                    }
                    s = usaScannerYDevuelveSimbolo();
                    if (s != Terminal.PROCEDURE){
                        break;
                    }
                }
            case PROCEDURE:
                while (s == Terminal.PROCEDURE){
                    s = usaScannerYDevuelveSimbolo();
                    if (s == Terminal.IDENTIFICADOR){
                        s = usaScannerYDevuelveSimbolo();
                        if (s == Terminal.PUNTO_Y_COMA){
                            s = usaScannerYDevuelveSimbolo();
                            s = bloque(s);
                            if (s == Terminal.PUNTO_Y_COMA){
                                s = usaScannerYDevuelveSimbolo();
                            } else {
                                indicadorErrores.mostrarError(109, s, aLex.getCad());
                            }
                        } else {
                            indicadorErrores.mostrarError(108, s, aLex.getCad());
                        }
                    } else {
                        indicadorErrores.mostrarError(107, s, aLex.getCad());
                    }
                }
                break;
        }

        s = proposicion(s);
        return s;
    }

    private Terminal proposicion (Terminal s) throws IOException {
        switch(s){
            case IDENTIFICADOR:
                s = usaScannerYDevuelveSimbolo();
                if (s == Terminal.ASIGNACION){
                    s = usaScannerYDevuelveSimbolo();
                    s = expresion(s);
                } else {
                    indicadorErrores.mostrarError(201, s, aLex.getCad());
                }
                break;
            case CALL:
                s = usaScannerYDevuelveSimbolo();
                if (s == Terminal.IDENTIFICADOR){
                    s = usaScannerYDevuelveSimbolo();
                } else {
                    indicadorErrores.mostrarError(202, s, aLex.getCad());
                }
                break;
            case BEGIN:
                s = usaScannerYDevuelveSimbolo();
                s = proposicion(s);
                while (s != Terminal.END){
                    if (s == Terminal.PUNTO_Y_COMA){
                        s = usaScannerYDevuelveSimbolo();
                        s = proposicion(s);
                    } else {
                        indicadorErrores.mostrarError(203, s, aLex.getCad());
                    }
                }
                s = usaScannerYDevuelveSimbolo();
                break;
            case IF:
                s = usaScannerYDevuelveSimbolo();
                s = condicion(s);
                if (s == Terminal.THEN){
                    s = usaScannerYDevuelveSimbolo();
                    s = proposicion(s);
                } else {
                    indicadorErrores.mostrarError(204, s, aLex.getCad());
                }
                break;
            case WHILE:
                s = usaScannerYDevuelveSimbolo();
                s = condicion(s);
                if (s == Terminal.DO){
                    s = usaScannerYDevuelveSimbolo();
                    s = proposicion(s);
                } else {
                    indicadorErrores.mostrarError(205, s, aLex.getCad());
                }
                break;
            case READLN:
                s = usaScannerYDevuelveSimbolo();
                if (s == Terminal.ABRE_PARENTESIS){
                    s = usaScannerYDevuelveSimbolo();
                    if (s == Terminal.IDENTIFICADOR){
                        s = usaScannerYDevuelveSimbolo();
                        while (s != Terminal.CIERRA_PARENTESIS){
                            if (s == Terminal.COMA){
                                s = usaScannerYDevuelveSimbolo();
                                if (s == Terminal.IDENTIFICADOR){
                                    s = usaScannerYDevuelveSimbolo();
                                } else {
                                    indicadorErrores.mostrarError(209, s, aLex.getCad());
                                }
                            } else {
                                indicadorErrores.mostrarError(208, s, aLex.getCad());
                            }
                        }
                        s = usaScannerYDevuelveSimbolo();
                    } else {
                        indicadorErrores.mostrarError(207, s, aLex.getCad());
                    }
                } else {
                    indicadorErrores.mostrarError(206, s, aLex.getCad());
                }
                break;
            case WRITELN:
                s = usaScannerYDevuelveSimbolo();
                if (s != Terminal.ABRE_PARENTESIS){
                    break;
                }
            case WRITE:
                if (s == Terminal.WRITE){
                    s = usaScannerYDevuelveSimbolo();
                }
                if (s == Terminal.ABRE_PARENTESIS){
                    s = usaScannerYDevuelveSimbolo();
                    if (s == Terminal.CADENA_LITERAL){
                        s = usaScannerYDevuelveSimbolo();
                    } else {
                        s = expresion(s);
                    }
                    while (s != Terminal.CIERRA_PARENTESIS){
                        if (s == Terminal.COMA){
                            s = usaScannerYDevuelveSimbolo();
                            if (s == Terminal.CADENA_LITERAL){
                                s = usaScannerYDevuelveSimbolo();
                            } else {
                                s = expresion(s);
                            }
                        } else {
                            indicadorErrores.mostrarError(211, s, aLex.getCad());
                        }
                    }
                    s = usaScannerYDevuelveSimbolo();
                } else {
                    indicadorErrores.mostrarError(210, s, aLex.getCad());
                }
        }
        return s;
    }

    private Terminal condicion(Terminal s) throws IOException {
        if (s == Terminal.ODD){
            s = usaScannerYDevuelveSimbolo();
            s = expresion(s);
        } else {
            s = expresion(s);
            if (s == Terminal.IGUAL || s == Terminal.DISTINTO || s == Terminal.MENOR || s == Terminal.MENOR_IGUAL || s == Terminal.MAYOR || s == Terminal.MAYOR_IGUAL){
                s = usaScannerYDevuelveSimbolo();
                s = expresion(s);
            } else {
                indicadorErrores.mostrarError(301, s, aLex.getCad());
            }
        }
        return s;
    }

    private Terminal expresion (Terminal s) throws IOException {
        if (s == Terminal.MAS || s == Terminal.MENOS){
            s = usaScannerYDevuelveSimbolo();
        }
        s = termino(s);
        while (s == Terminal.MENOS || s == Terminal.MAS){
            s = usaScannerYDevuelveSimbolo();
            s = termino(s);
        }
        return s;
    }

    private Terminal termino(Terminal s) throws IOException {
        s = factor(s);
        while (s == Terminal.POR || s == Terminal.DIVIDIDO){
            s = usaScannerYDevuelveSimbolo();
            s = factor(s);
        }
        return s;
    }

    private Terminal factor(Terminal s) throws IOException {
        if (s == Terminal.IDENTIFICADOR || s == Terminal.NUMERO){
            s = usaScannerYDevuelveSimbolo();
        } else if (s == Terminal.ABRE_PARENTESIS){
            s = usaScannerYDevuelveSimbolo();
            s = expresion(s);
            if (s == Terminal.CIERRA_PARENTESIS){
                s = usaScannerYDevuelveSimbolo();
            } else {
                indicadorErrores.mostrarError(402, s, aLex.getCad());
            }
        } else {
            indicadorErrores.mostrarError(401, s, aLex.getCad());
        }
        return s;
    }

    private Terminal usaScannerYDevuelveSimbolo() throws IOException {
        aLex.scanner();
        return aLex.getS();
    }

    private void compilacionSatisfactoria(){
        System.out.println("El programa ha compilado correctamente.");
    }
}
