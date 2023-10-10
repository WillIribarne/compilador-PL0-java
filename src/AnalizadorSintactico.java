import java.io.IOException;

public class AnalizadorSintactico {

    private final AnalizadorLexico aLex;
    private final AnalizadorSemantico aSem;
    private final IndicadorDeErrores indicadorErrores;
    private final GeneradorDeCodigo genCod;
    private int contadorVariablesEnBytes;

    public AnalizadorSintactico(AnalizadorLexico aLex, AnalizadorSemantico aSem, IndicadorDeErrores indicadorErrores, GeneradorDeCodigo genCod) {
        this.aLex = aLex;
        this.aSem = aSem;
        this.indicadorErrores = indicadorErrores;
        this.genCod = genCod;
        this.contadorVariablesEnBytes = 0; //cada vez que se declara una nueva,
    }

    public void parser() throws IOException {
        programa();
    }

    public void programa() throws IOException {
        Terminal s = usaScannerYDevuelveSimbolo();
        if (s != null){
            genCod.cargarByte(0xBF); //carga BF en 1792
            genCod.cargarInt(0); //carga 0 en 1793, 94, 95 y 96 = futuramente se va a cambiar por el valor de EDI
            s = bloque(0, s);
        } else {
            indicadorErrores.mostrarError(-1, null, null);
        }
        if (s == Terminal.PUNTO){
            s = usaScannerYDevuelveSimbolo();
            if (s == Terminal.EOF){
                compilacionSatisfactoria();
                genCod.FixUpFinal();
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
                while (s != Terminal.PUNTO_Y_COMA){
                    s = usaScannerYDevuelveSimbolo();
                    if (s == Terminal.IDENTIFICADOR){
                        nombre = aLex.getCad();
                        if (aSem.obtenerIndiceTabla(base + desplazamiento - 1, base, nombre) != -1){
                            indicadorErrores.mostrarError(501, Terminal.CONST, nombre);
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
                    while (s != Terminal.PUNTO_Y_COMA){
                        s = usaScannerYDevuelveSimbolo();
                        if (s == Terminal.IDENTIFICADOR){
                            nombre = aLex.getCad();
                            if (aSem.obtenerIndiceTabla(base + desplazamiento - 1, base, nombre) != -1){
                                indicadorErrores.mostrarError(501, Terminal.VAR, nombre);
                            }
                            aSem.guardarEnTabla(base + desplazamiento, nombre, Terminal.VAR, contadorVariablesEnBytes);
                            contadorVariablesEnBytes += 4;
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
                            indicadorErrores.mostrarError(501, Terminal.PROCEDURE, nombre);
                        }
                        aSem.guardarEnTabla(base + desplazamiento, nombre, Terminal.PROCEDURE, nombre.hashCode());
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
                nombre = aLex.getCad();
                pos = aSem.obtenerIndiceTabla(base + desplazamiento - 1, 0, nombre);
                if (pos == -1){
                    indicadorErrores.mostrarError(502, null, nombre);
                }
                if (aSem.obtenerTipo(pos) != Terminal.VAR){
                    indicadorErrores.mostrarError(503, aSem.obtenerTipo(pos), nombre);
                }
                s = usaScannerYDevuelveSimbolo();
                if (s == Terminal.ASIGNACION){
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
                        indicadorErrores.mostrarError(502, null, nombre);
                    }
                    if (aSem.obtenerTipo(pos) != Terminal.PROCEDURE){
                        indicadorErrores.mostrarError(504, aSem.obtenerTipo(pos), nombre);
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
                            indicadorErrores.mostrarError(502, null, nombre);
                        }
                        if (aSem.obtenerTipo(pos) != Terminal.VAR){
                            indicadorErrores.mostrarError(503, aSem.obtenerTipo(pos), nombre);
                        }
                        s = usaScannerYDevuelveSimbolo();
                        while (s != Terminal.CIERRA_PARENTESIS){
                            if (s == Terminal.COMA){
                                s = usaScannerYDevuelveSimbolo();
                                if (s == Terminal.IDENTIFICADOR){
                                    nombre = aLex.getCad();
                                    pos = aSem.obtenerIndiceTabla(base + desplazamiento - 1, 0, nombre);
                                    if (pos == -1){
                                        indicadorErrores.mostrarError(502, null, nombre);
                                    }
                                    if (aSem.obtenerTipo(pos) != Terminal.VAR){
                                        indicadorErrores.mostrarError(503, aSem.obtenerTipo(pos), nombre);
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
            genCod.cargarByte(0x58);
            genCod.cargarByte(0xA8);
            genCod.cargarByte(0x01);
            genCod.cargarByte(0x7B);
            genCod.cargarByte(0x05);
            genCod.cargarByte(0xE9);
            genCod.cargarInt(0);
        } else {
            s = expresion(base, desplazamiento, s);
            if (s == Terminal.IGUAL || s == Terminal.DISTINTO || s == Terminal.MENOR || s == Terminal.MENOR_IGUAL || s == Terminal.MAYOR || s == Terminal.MAYOR_IGUAL){
                Terminal signo = s;
                s = usaScannerYDevuelveSimbolo();
                s = expresion(base, desplazamiento, s);
                if (signo == Terminal.IGUAL){
                    genCod.cargarByte(0x74);
                } else if (signo == Terminal.DISTINTO){
                    genCod.cargarByte(0x75);
                } else if (signo == Terminal.MENOR){
                    genCod.cargarByte(0x7C);
                } else if (signo == Terminal.MENOR_IGUAL){
                    genCod.cargarByte(0x7E);
                } else if (signo == Terminal.MAYOR){
                    genCod.cargarByte(0x7F);
                } else {
                    genCod.cargarByte(0x7D);
                }
                genCod.cargarByte(0x05);
            } else {
                indicadorErrores.mostrarError(301, s, aLex.getCad());
            }
        }
        return s;
    }

    private Terminal expresion (int base, int desplazamiento, Terminal s) throws IOException {
        Terminal signo = null;
        if (s == Terminal.MAS || s == Terminal.MENOS){
            signo = s;
            s = usaScannerYDevuelveSimbolo();
        }
        s = termino(base, desplazamiento, s);
        if (signo == Terminal.MENOS){
            genCod.cargarByte(0x58);
            genCod.cargarByte(0xF7);
            genCod.cargarByte(0xD8);
            genCod.cargarByte(0x50);
        }
        while (s == Terminal.MENOS || s == Terminal.MAS){
            signo = s;
            s = usaScannerYDevuelveSimbolo();
            s = termino(base, desplazamiento, s);
            genCod.cargarByte(0x58);
            genCod.cargarByte(0x5B);
            if (signo == Terminal.MENOS){
                genCod.cargarByte(0x93);
                genCod.cargarByte(0x29);
                genCod.cargarByte(0xD8);
                genCod.cargarByte(0x50);
            } else {
                genCod.cargarByte(0x01);
                genCod.cargarByte(0xD8);
                genCod.cargarByte(0x50);
            }
        }
        return s;
    }

    private Terminal termino(int base, int desplazamiento, Terminal s) throws IOException {
        s = factor(base, desplazamiento, s);
        while (s == Terminal.POR || s == Terminal.DIVIDIDO){
            Terminal signo = s;
            s = usaScannerYDevuelveSimbolo();
            s = factor(base, desplazamiento, s);
            genCod.cargarByte(0x58);
            genCod.cargarByte(0x5B);
            if (signo == Terminal.POR){
                genCod.cargarByte(0xF7);
                genCod.cargarByte(0xEB);
            } else {
                genCod.cargarByte(0x93);
                genCod.cargarByte(0x99);
                genCod.cargarByte(0xF7);
                genCod.cargarByte(0xFB);
            }
            genCod.cargarByte(0x50);
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
                    indicadorErrores.mostrarError(502, null, nombre);
                }
                if (aSem.obtenerTipo(pos) != Terminal.VAR && aSem.obtenerTipo(pos) != Terminal.CONST){
                    indicadorErrores.mostrarError(505, aSem.obtenerTipo(pos), nombre);
                }
                if (aSem.obtenerTipo(pos) == Terminal.VAR){
                    genCod.cargarByte(0xB8);
                    genCod.cargarInt(aSem.obtenerValor(pos));
                    genCod.cargarByte(0x50);
                } else if (aSem.obtenerTipo(pos) == Terminal.CONST){
                    genCod.cargarByte(0x8B);
                    genCod.cargarByte(0x87);
                    genCod.cargarInt(aSem.obtenerValor(pos));
                    genCod.cargarByte(0x50);
                }
            } else {
                genCod.cargarByte(0xB8);
                genCod.cargarInt(Integer.parseInt(aLex.getCad()));
                genCod.cargarByte(0x50);
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
