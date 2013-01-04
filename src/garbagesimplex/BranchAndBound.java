/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package garbagesimplex;

import java.util.Stack;
import net.sf.javailp.*;
/**
 *
 * @author Cristina
 */
public class BranchAndBound {
    private Stack<Nodo> stack;
    private double Bx;
    private double By;
    private double cota;
    public BranchAndBound(){
        stack = new Stack<>();
        cota=Double.NEGATIVE_INFINITY;
        Bx=-1;
        By=-1;
    }
    
    public double[] runBranchAndBound(Problem problem, int ciudadesSize){
        Nodo nodoInicial = new Nodo(problem);
        stack.push(nodoInicial);
        while(!stack.isEmpty()){
            Nodo nodoActual = stack.pop();
            double Z;
            double[] Nx = new double[ciudadesSize];
            double[] Ny = new double[ciudadesSize];
            Z = nodoActual.getZ();
            for(int i=0;i<ciudadesSize;i++){
                Nx[i]=nodoActual.getNxi(i);
                Ny[i]=nodoActual.getNyi(i);
            }
            int variablesNoEnteras=0;
            double epsilon = 0.0000001;
            for(int i=0;i<ciudadesSize;i++){
                if(!(Math.abs(Math.floor(Nx[i]) - Nx[i]) <= epsilon)){
                    variablesNoEnteras++;
                }
                if(!(Math.abs(Math.floor(Ny[i]) - Ny[i]) <= epsilon)){
                    variablesNoEnteras++;
                }
            }
            if(Z!=-1 || Z<cota || variablesNoEnteras==0){
                if(variablesNoEnteras==0 && Z>cota){
                    cota = Z;
                    Bx=nodoActual.getBx();
                    By=nodoActual.getBy();
                }
            }
            else{
                int variable=-1;
                boolean isX=false;
                for(int i=0;i<ciudadesSize;i++){
                    if(!(Math.abs(Math.floor(Nx[i]) - Nx[i]) <= epsilon)){
                    isX=true;
                    variable=i;
                    i=ciudadesSize;
                }
                if(!(Math.abs(Math.floor(Ny[i]) - Ny[i]) <= epsilon)){
                    isX=false;
                    variable=i;
                    i=ciudadesSize;
                    }
                }
                Problem p1 = nodoActual.getProblem();
                Problem p2 = nodoActual.getProblem();
                if(variable!=-1){
                    if(isX){
                        double valueFloor = Math.floor(Nx[variable]);
                        double valueCeil = Math.ceil(Nx[variable]);
                        p1.setVarLowerBound("Nx"+variable, valueFloor);
                        p2.setVarUpperBound("Nx"+variable, valueCeil);
                        Nodo nodoNuevo1 = new Nodo(p1);
                        Nodo nodoNuevo2 = new Nodo(p2);
                        stack.add(nodoNuevo1);
                        stack.add(nodoNuevo2);
                    }
                    else{
                        double valueFloor = Math.floor(Ny[variable]);
                        double valueCeil = Math.ceil(Ny[variable]);
                        p1.setVarLowerBound("Ny"+variable, valueFloor);
                        p2.setVarUpperBound("Ny"+variable, valueCeil);
                        Nodo nodoNuevo1 = new Nodo(p1);
                        Nodo nodoNuevo2 = new Nodo(p2);
                        stack.add(nodoNuevo1);
                        stack.add(nodoNuevo2);
                    }
                }        
            }
        }
        double[] salida = new double[3];
        salida[0] = Bx;
        salida[1] = By;
        salida[2] = cota;
        return salida;
    }
}
