import java.io.IOException;

public class AnalizadorSintactico {

    private final AnalizadorLexico aLex;
    private AnalizadorSemantico aSem;
    private final IndicadorDeErrores indicadorErrores;

    public AnalizadorSintactico(AnalizadorLexico aLex, AnalizadorSemantico aSem, IndicadorDeErrores indicadorErrores) {
        this.aLex = aLex;
        this.aSem = aSem;
        this.indicadorErrores = indicadorErrores;
    }

    public void parser() throws IOException {
        programa();
    }

    public void programa() throws IOException {
        Terminal s = usaScannerYDevuelveSimbolo();
        if (s != null){
            s = bloque(0, s);
        } else {
            indicadorErrores.mostrarError(-1, null, null);
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

    public Terminal bloque(int base, Terminal s) throws IOException {
        int desplazamiento = 0;
        String nombre;
        switch(s){
            case CONST:
                while (s != Terminal.COMA){
                    s = usaScannerYDevuelveSimbolo();
                    if (s == Terminal.IDENTIFICADOR){
                        nombre = aLex.getCad();
                        if (aSem.obtenerIndiceTabla(base + desplazamiento - 1, base, nombre) != -1){
                            //error
                        }
                        s = usaScannerYDevuelveSimbolo();
                        if (s == Terminal.IGUAL){
                            s = usaScannerYDevuelveSimbolo();
                            if (s == Terminal.NUMERO){
                                aSem.guardarEnTabla(base + desplazamiento, nombre, Terminal.CONST, Integer.parseInt(aLex.getCad()));
                                desplazamiento++;
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
                    while (s != Terminal.COMA){
                        s = usaScannerYDevuelveSimbolo();
                        if (s == Terminal.IDENTIFICADOR){
                            nombre = aLex.getCad();
                            if (aSem.obtenerIndiceTabla(base + desplazamiento - 1, base, nombre) != -1){
                                //error
                            }
                            aSem.guardarEnTabla(base + desplazamiento, nombre, Terminal.CONST, nombre.hashCode());
                            desplazamiento++;
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
                        nombre = aLex.getCad();
                        if (aSem.obtenerIndiceTabla(base + desplazamiento - 1, base, nombre) != -1){
                            //error
                        }
                        aSem.guardarEnTabla(base + desplazamiento, nombre, Terminal.CONST, nombre.hashCode());
                        desplazamiento++;
                        s = usaScannerYDevuelveSimbolo();
                        if (s == Terminal.PUNTO_Y_COMA){
                            s = usaScannerYDevuelveSimbolo();
                            s = bloque((base + desplazamiento), s);
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

        s = proposicion(base, desplazamiento, s);
        return s;
    }

    private Terminal proposicion (int base, int desplazamiento, Terminal s) throws IOException {
        String nombre;
        int pos;
        switch(s){
            case IDENTIFICADOR:
                s = usaScannerYDevuelveSimbolo();
                if (s == Terminal.ASIGNACION){
                    nombre = aLex.getCad();
                    pos = aSem.obtenerIndiceTabla(base + desplazamiento - 1, 0, nombre);
                    if (pos == -1){
                        //error
                    }
                    if (aSem.obtenerTipo(pos) != Terminal.VAR){ // para factor verifica que no sea procedure, pero puede ser var y const
                        //error
                    }
                    s = usaScannerYDevuelveSimbolo();
                    s = expresion(base, desplazamiento, s);
                } else {
                    indicadorErrores.mostrarError(201, s, aLex.getCad());
                }
                break;
            case CALL:
                s = usaScannerYDevuelveSimbolo();
                if (s == Terminal.IDENTIFICADOR){
                    nombre = aLex.getCad();
                    pos = aSem.obtenerIndiceTabla(base + desplazamiento - 1, 0, nombre);
                    if (pos == -1){
                        //error
                    }
                    if (aSem.obtenerTipo(pos) != Terminal.PROCEDURE){ // para factor verifica que no sea procedure, pero puede ser var y const
                        //error
                    }
                    s = usaScannerYDevuelveSimbolo();
                } else {
                    indicadorErrores.mostrarError(202, s, aLex.getCad());
                }
                break;
            case BEGIN:
                s = usaScannerYDevuelveSimbolo();
                s = proposicion(base, desplazamiento, s);
                while (s != Terminal.END){
                    if (s == Terminal.PUNTO_Y_COMA){
                        s = usaScannerYDevuelveSimbolo();
                        s = proposicion(base, desplazamiento, s);
                    } else {
                        indicadorErrores.mostrarError(203, s, aLex.getCad());
                    }
                }
                s = usaScannerYDevuelveSimbolo();
                break;
            case IF:
                s = usaScannerYDevuelveSimbolo();
                s = condicion(base, desplazamiento, s);
                if (s == Terminal.THEN){
                    s = usaScannerYDevuelveSimbolo();
                    s = proposicion(base, desplazamiento, s);
                } else {
                    indicadorErrores.mostrarError(204, s, aLex.getCad());
                }
                break;
            case WHILE:
                s = usaScannerYDevuelveSimbolo();
                s = condicion(base, desplazamiento, s);
                if (s == Terminal.DO){
                    s = usaScannerYDevuelveSimbolo();
                    s = proposicion(base, desplazamiento, s);
                } else {
                    indicadorErrores.mostrarError(205, s, aLex.getCad());
                }
                break;
            case READLN:
                s = usaScannerYDevuelveSimbolo();
                if (s == Terminal.ABRE_PARENTESIS){
                    s = usaScannerYDevuelveSimbolo();
                    if (s == Terminal.IDENTIFICADOR){
                        nombre = aLex.getCad();
                        pos = aSem.obtenerIndiceTabla(base + desplazamiento - 1, 0, nombre);
                        if (pos == -1){
                            //error
                        }
                        if (aSem.obtenerTipo(pos) != Terminal.VAR){ // para factor verifica que no sea procedure, pero puede ser var y const
                            //error
                        }
                        s = usaScannerYDevuelveSimbolo();
                        while (s != Terminal.CIERRA_PARENTESIS){
                            if (s == Terminal.COMA){
                                s = usaScannerYDevuelveSimbolo();
                                if (s == Terminal.IDENTIFICADOR){
                                    nombre = aLex.getCad();
                                    pos = aSem.obtenerIndiceTabla(base + desplazamiento - 1, 0, nombre);
                                    if (pos == -1){
                                        //error
                                    }
                                    if (aSem.obtenerTipo(pos) != Terminal.VAR){ // para factor verifica que no sea procedure, pero puede ser var y const
                                        //error
                                    }
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
                        s = expresion(base, desplazamiento, s);
                    }
                    while (s != Terminal.CIERRA_PARENTESIS){
                        if (s == Terminal.COMA){
                            s = usaScannerYDevuelveSimbolo();
                            if (s == Terminal.CADENA_LITERAL){
                                s = usaScannerYDevuelveSimbolo();
                            } else {
                                s = expresion(base, desplazamiento, s);
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

    private Terminal condicion(int base, int desplazamiento, Terminal s) throws IOException {
        if (s == Terminal.ODD){
            s = usaScannerYDevuelveSimbolo();
            s = expresion(base, desplazamiento, s);
        } else {
            s = expresion(base, desplazamiento, s);
            if (s == Terminal.IGUAL || s == Terminal.DISTINTO || s == Terminal.MENOR || s == Terminal.MENOR_IGUAL || s == Terminal.MAYOR || s == Terminal.MAYOR_IGUAL){
                s = usaScannerYDevuelveSimbolo();
                s = expresion(base, desplazamiento, s);
            } else {
                indicadorErrores.mostrarError(301, s, aLex.getCad());
            }
        }
        return s;
    }

    private Terminal expresion (int base, int desplazamiento, Terminal s) throws IOException {
        if (s == Terminal.MAS || s == Terminal.MENOS){
            s = usaScannerYDevuelveSimbolo();
        }
        s = termino(base, desplazamiento, s);
        while (s == Terminal.MENOS || s == Terminal.MAS){
            s = usaScannerYDevuelveSimbolo();
            s = termino(base, desplazamiento, s);
        }
        return s;
    }

    private Terminal termino(int base, int desplazamiento, Terminal s) throws IOException {
        s = factor(base, desplazamiento, s);
        while (s == Terminal.POR || s == Terminal.DIVIDIDO){
            s = usaScannerYDevuelveSimbolo();
            s = factor(base, desplazamiento, s);
        }
        return s;
    }

    private Terminal factor(int base, int desplazamiento, Terminal s) throws IOException {
        String nombre;
        int pos;
        if (s == Terminal.IDENTIFICADOR || s == Terminal.NUMERO){
            if (s == Terminal.IDENTIFICADOR){
                nombre = aLex.getCad();
                pos = aSem.obtenerIndiceTabla(base + desplazamiento - 1, 0, nombre);
                if (pos == -1){
                    //error
                }
                if (aSem.obtenerTipo(pos) != Terminal.VAR && aSem.obtenerTipo(pos) != Terminal.CONST){ // para factor verifica que no sea procedure, pero puede ser var y const
                    //error
                }
            }
            s = usaScannerYDevuelveSimbolo();
        } else if (s == Terminal.ABRE_PARENTESIS){
            s = usaScannerYDevuelveSimbolo();
            s = expresion(base, desplazamiento, s);
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
