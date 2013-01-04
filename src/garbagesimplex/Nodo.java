/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package garbagesimplex;

import net.sf.javailp.*;

/**
 *
 * @author alevalv
 */
public class Nodo {

    private Problem problem;
    private Result result;
    private static SolverFactory factory;

    public Nodo(Problem problem) {
        this.problem = problem;
        if (factory == null) {
            factory = new SolverFactoryLpSolve(); // use lp_solve
            factory.setParameter(Solver.VERBOSE, 0);
            factory.setParameter(Solver.TIMEOUT, 100); // set timeout to 100 seconds
        }
        Solver solver = factory.get(); // you should use this solver only once for one problem
        Result miResult = solver.solve(problem);
        result = miResult;

    }

    public Problem getProblem() {
        return problem;
    }

    public double getZ() {
        if (result != null) {
            return result.getObjective().doubleValue();
        } else {
            return -1;
        }

    }

    public double getNyi(int i) {
        if (result != null) {
            return result.get("Ny" + i).doubleValue();
        } else {
            return -1;
        }
    }

    public double getNxi(int i) {
        if (result != null) {
            return result.get("Nx" + i).doubleValue();
        } else {
            return -1;
        }
    }
    
    public double getBx(){
        if (result != null) {
            return result.get("Bx").doubleValue();
        } else {
            return -1;
        }
    }
    
    public double getBy(){
        if (result != null) {
            return result.get("By").doubleValue();
        } else {
            return -1;
        }
    }
}
