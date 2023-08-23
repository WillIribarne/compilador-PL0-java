import java.io.*;

public class Main {
    public static void main(String[] args) throws IOException {
        Terminal s;
        File f = new File("C:\\Users\\Usuario\\Desktop\\Facultad\\Sistemas de Computacion I\\CompiladorWin32\\BIEN-08.PL0");
        BufferedReader br = new BufferedReader(new FileReader(f));
        AnalizadorLexico aLex = new AnalizadorLexico(br);
        AnalizadorSintactico aSin = new AnalizadorSintactico(aLex);

        do {
            aLex.escanear();
            s = aLex.getS();
            System.out.println(s + " " + aLex.getCad());
        } while (s != Terminal.EOF);
    }
}