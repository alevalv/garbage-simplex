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
import net.sf.javailp.Result;
import net.sf.javailp.Solver;
import net.sf.javailp.SolverFactory;
import net.sf.javailp.SolverFactoryLpSolve;
import net.sf.javailp.VarType;

/**
 *
 * @author alevalv
 */
public class Reader {
    
    public final static String FILE_NAME = "prueba2"; //TODO
    private final static Charset ENCODING = StandardCharsets.UTF_8;
    private int tableroSize;

    public int getTableroSize() {
        return tableroSize;
    }

    public int getNumeroCiudades() {
        return numeroCiudades;
    }

    public ArrayList<ArrayList<Integer>> getCiudades() {
        return ciudades;
    }
    private int numeroCiudades;
    private ArrayList<ArrayList<Integer>> ciudades;

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
        tableroSize = toInt(lineas.get(0));
        numeroCiudades = toInt(lineas.get(1));
        ciudades = new ArrayList<>();
        for(int i=2;i<numeroCiudades+2;i++){
            ArrayList<String> valoresCiudad = splitString(lineas.get(i));
            ArrayList<Integer> ciudad = new ArrayList<>(2);
            ciudad.add(toInt(valoresCiudad.get(1)));
            ciudad.add(toInt(valoresCiudad.get(2)));
            ciudades.add(ciudad);
        }
        int M = 5000;
        //definición del modelo
        
        Problem problem = new Problem();
        //ecuación a maximizar
        Linear linear = new Linear();
        linear.add(1, "DIMIN");
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
            
            Linear igualdad1 = new Linear();
            
            Linear menorCiudad = new Linear();
            
            distanciaXi1.add(-1, "Bx");
            distanciaXi1.add(1, "Dx"+i);
            distanciaXi1.add(-M, "Nx"+i);
            problem.add(distanciaXi1, ">=", (-ciudades.get(i).get(0) - M));
            
            distanciaXi2.add(-1, "Bx");
            distanciaXi2.add(1, "Dx"+i);
            distanciaXi2.add(M, "Nx"+i);
            problem.add(distanciaXi2, "<=", (-ciudades.get(i).get(0) + M));
            
            distanciaXi3.add(1, "Bx");
            distanciaXi3.add(1, "Dx"+i);
            distanciaXi3.add(M, "Nx"+i);
            problem.add(distanciaXi3, ">=", (ciudades.get(i).get(0)));
            
            distanciaXi4.add(1, "Bx");
            distanciaXi4.add(1, "Dx"+i);
            distanciaXi4.add(-M, "Nx"+i);
            problem.add(distanciaXi4, "<=", (ciudades.get(i).get(0)));
            
            distanciaYi1.add(-1, "By");
            distanciaYi1.add(1, "Dy"+i);
            distanciaYi1.add(-M, "Ny"+i);
            problem.add(distanciaYi1, ">=", (-ciudades.get(i).get(1) - M));
            
            distanciaYi2.add(-1, "By");
            distanciaYi2.add(1, "Dy"+i);
            distanciaYi2.add(M, "Ny"+i);
            problem.add(distanciaYi2, "<=", (-ciudades.get(i).get(1) + M));
            
            distanciaYi3.add(1, "By");
            distanciaYi3.add(1, "Dy"+i);
            distanciaYi3.add(M, "Ny"+i);
            problem.add(distanciaYi3, ">=", (ciudades.get(i).get(1)));
            
            distanciaYi4.add(1, "By");
            distanciaYi4.add(1, "Dy"+i);
            distanciaYi4.add(-M, "Ny"+i);
            problem.add(distanciaYi4, "<=", (ciudades.get(i).get(1)));
            
            igualdad1.add(1, "Dx"+i);
            igualdad1.add(1, "Dy"+i);
            igualdad1.add(-1, "D"+i);
            problem.add(igualdad1, "=", 0);
            
            menorCiudad.add(1, "DIMIN");
            menorCiudad.add(-1, "D"+i);
            problem.add(menorCiudad, "<=", 0);
            
            problem.setVarType("D"+i, VarType.REAL);
            problem.setVarType("Dx"+i, VarType.REAL);
            problem.setVarType("Dy"+i, VarType.REAL);
            problem.setVarType("Bx", VarType.REAL);
            problem.setVarType("By", VarType.REAL);
            problem.setVarType("DIMIN", VarType.REAL);
            problem.setVarType("Nx"+i, VarType.BOOL);
            problem.setVarType("Ny"+i, VarType.BOOL);
            
            problem.setVarLowerBound("D"+i, 0);
            problem.setVarLowerBound("Dx"+i, 0);
            problem.setVarLowerBound("Dy"+i, 0);
            problem.setVarLowerBound("Bx", 0);
            problem.setVarLowerBound("By", 0);
            problem.setVarLowerBound("DIMIN", 0);
            problem.setVarLowerBound("Nx"+i, 0);
            problem.setVarLowerBound("Ny"+i, 0);
            problem.setVarUpperBound("Bx", tableroSize);
            problem.setVarUpperBound("By", tableroSize);
        }
        return problem;
    }
    
    public static void main(String[] args) throws IOException{
        BranchAndBound mibyb = new BranchAndBound();
        double[]  salida = mibyb.runBranchAndBound(new Reader().readProblem("prueba1"), 4);
        System.out.println(salida[0]);
        System.out.println(salida[1]);
    }    
}
