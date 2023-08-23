import java.io.*;

public class Main {
    public static void main(String[] args) throws IOException {
        File f = new File("C:\\Users\\Usuario\\Desktop\\Facultad\\Sistemas de Computacion I\\CompiladorWin32\\BIEN-00.PL0");
        BufferedReader br = new BufferedReader(new FileReader(f));
        AnalizadorLexico aLex = new AnalizadorLexico(br);
        AnalizadorSintactico aSin = new AnalizadorSintactico(aLex);

        aSin.analizar();
    }
}