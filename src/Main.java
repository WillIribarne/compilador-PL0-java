import java.io.*;

public class Main {
    public static void main(String[] args) throws IOException {
        Terminal s;
        File f = new File("C:\\Users\\Usuario\\Desktop\\Facultad\\Sistemas de Computacion I\\CompiladorWin32\\BIEN-01.PL0");
        BufferedReader br = new BufferedReader(new FileReader(f));
        AnalizadorLexico aLex = new AnalizadorLexico(br);
        do {
            aLex.escanear(); //el propósito de escanear es que despues el getS te devuelva el terminal
            s = aLex.getS();
            if (s != null){
                System.out.println(s + " " + aLex.getCad());
            }
        } while (s != Terminal.EOF);

    }
}