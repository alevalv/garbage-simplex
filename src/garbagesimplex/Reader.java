/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package garbagesimplex;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import net.sf.javailp.Linear;
import net.sf.javailp.OptType;
import net.sf.javailp.Problem;

/**
 *
 * @author alevalv
 */
public class Reader {
    
    final static String FILE_NAME = "entrada1"; //TODO
    final static Charset ENCODING = StandardCharsets.UTF_8;

    private int toInt(String aString){
        return Integer.parseInt(aString);
    }
    
    private ArrayList<String> splitString(String aString){
        StringTokenizer stk;
        stk = new StringTokenizer(aString, " ");
        ArrayList<String> salida = new ArrayList<>(3);
        while(stk.hasMoreTokens()){
            salida.add(stk.nextToken());
        }
        return salida;
    }
    
    //For smaller files
    private List<String> readTextFile(String aFileName) throws IOException {
        Path path = Paths.get(aFileName);
        return Files.readAllLines(path, ENCODING);
    }
    
    public Problem readProblem(String aFileName) throws IOException {
        List<String> lineas = readTextFile(aFileName);
        int tableroSize = toInt(lineas.get(0));
        int numeroCiudades = toInt(lineas.get(1));
        ArrayList<ArrayList<Integer>> ciudades = new ArrayList<>();
        for(int i=2;i<numeroCiudades+2;i++){
            ArrayList<String> valoresCiudad = splitString(lineas.get(i));
            ArrayList<Integer> ciudad = new ArrayList<>(2);
            ciudad.add(toInt(valoresCiudad.get(1)));
            ciudad.add(toInt(valoresCiudad.get(2)));
        }
        int M = 5000;
        //definición del modelo
        
        Problem problem = new Problem();
        //ecuación a maximizar
        Linear linear = new Linear();
        linear.add(1, "L");
        problem.setObjective(linear, OptType.MAX);
        
        //distancias ciudades
        for(int i=0;i<numeroCiudades;i++){
            Linear distanciaXi1 = new Linear();
            Linear distanciaXi2 = new Linear();
            Linear distanciaXi3 = new Linear();
            Linear distanciaXi4 = new Linear();
            
            Linear distanciaYi1 = new Linear();
            Linear distanciaYi2 = new Linear();
            Linear distanciaYi3 = new Linear();
            Linear distanciaYi4 = new Linear();
            
            distanciaXi1.add(1, "Bx");
            distanciaXi1.add(-1, "Dx"+i);
            distanciaXi1.add(M, "Nx"+i);
            problem.add(distanciaXi1, ">", (ciudades.get(i).get(0) + M));
        }
        return problem;
    }
    
    public static void main(String... aArgs) throws IOException {
        Reader text = new Reader();

        //treat as a small file
        List<String> lines = text.readTextFile(FILE_NAME);
    }
    
}
