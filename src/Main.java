import java.io.*;

public class Main {
    public static void main(String[] args) throws IOException {
        try {
            File f = new File("C:\\Users\\Usuario\\Desktop\\Facultad\\Sistemas de Computacion I\\CompiladorWin32\\BIEN-00.PL0"); //ver el doble enter del MAL-08 y PRUEBA.PL0 (los espacios finales explotan todo)
            BufferedReader br = new BufferedReader(new FileReader(f));
            IndicadorDeErrores indicadorErrores = new IndicadorDeErrores();
            AnalizadorLexico aLex = new AnalizadorLexico(br, indicadorErrores);
            AnalizadorSemantico aSem = new AnalizadorSemantico(indicadorErrores);
            GeneradorDeCodigo genaCod = new GeneradorDeCodigo(f.getName(), indicadorErrores, f.getPath());
            AnalizadorSintactico aSin = new AnalizadorSintactico(aLex, aSem, indicadorErrores, genaCod);
            //genaCod.volcar(genaCod.getNombre());

        } catch (FileNotFoundException e) {
            throw new FileNotFoundException("El archivo no existe");
        }
    }
}