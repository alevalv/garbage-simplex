/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package garbagesimplex;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import net.sf.javailp.*;

/**
 *
 * @author alevalv
 */
public class GarbageSimplex extends JFrame implements ActionListener{

    /**
     * @param args the command line arguments
     */
    private JMenuBar barraMenu;
    private JMenu archivo,acercaDe;
    private JMenuItem cargar,salir,autores;
    private SolverFactory factory; // use lp_solve
    private Reader text = new Reader();
    private Problem problem = null;
    private JFileChooser selector;
    private JButton resolver;
    private JLabel xBasurero,yBasurero,distancia;
    private JPanel pDatos,grilla;
    private DecimalFormat redondear = new DecimalFormat("#.##");
    private JLayeredPane tablero;
    private JPanel[][] casillas;
    private int regionSize;
    
    public GarbageSimplex(){
        super("EcoReg - Proyecto de Optimizacion");
        
        tablero = new JLayeredPane();
        tablero.setPreferredSize(new Dimension(500,500));
        tablero.setBackground(Color.white);
        
        factory = new SolverFactoryLpSolve();
        factory.setParameter(Solver.VERBOSE, 0);
        factory.setParameter(Solver.TIMEOUT, 100); // set timeout to 100 seconds
        
        pDatos = new JPanel(new GridLayout(4,1));
        xBasurero = new JLabel("Coordenada X: ");
        yBasurero = new JLabel("Coordenada Y: ");
        distancia = new JLabel("Distancia : ");
        resolver = new JButton("Resolver Problema");
        resolver.setEnabled(false);
        resolver.addActionListener(GarbageSimplex.this);
        
        pDatos.add(xBasurero);
        pDatos.add(yBasurero);
        pDatos.add(distancia);
        pDatos.add(resolver);
        
        selector = new JFileChooser(".");
        
        Container contenedor = getContentPane();
        contenedor.setLayout(new FlowLayout());
        
        barraMenu = new JMenuBar();
        
        archivo = new JMenu("Archivo");
        cargar = new JMenuItem("Cargar Problema");
        cargar.addActionListener(GarbageSimplex.this);
        salir = new JMenuItem("Salir");
        salir.addActionListener(GarbageSimplex.this);
        
        archivo.add(cargar);
        archivo.add(salir);
        
        acercaDe = new JMenu("Acerca de");
        autores = new JMenuItem("Autores");
        autores.addActionListener(GarbageSimplex.this);
        acercaDe.add(autores);
        
        barraMenu.add(archivo);
        barraMenu.add(acercaDe);
        
        contenedor.add(tablero);
        contenedor.add(pDatos);
        
        setJMenuBar(barraMenu);
        pack();
        setVisible(true);
        setLocationRelativeTo(null);
    }
    
    public static void main(String[] args) {
        
        setDefaultLookAndFeelDecorated(true);
        SwingUtilities.invokeLater(new Runnable() {
            @Override
             public void run() {
                  try {
                      UIManager.setLookAndFeel("org.pushingpixels.substance.api.skin.SubstanceSaharaLookAndFeel");
//                      UIManager.setLookAndFeel("org.pushingpixels.substance.api.skin.SubstanceRavenLookAndFeel");
                  } catch (Exception e) {
                      System.out.println("Look and Feel failed to initialize");
                  }
                    GarbageSimplex garbage = new GarbageSimplex();
                    garbage.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            }
        });
        
        
        
        /*
         * Constructing a Problem: Maximize: 143x+60y Subject to: 
         * 120x+210y <= 15000
         * 110x+30y <= 4000
         * x+y <= 75
         *         
         * With x,y being integers
         *         
         
        Problem problem = new Problem();

        Linear linear = new Linear();
        linear.add(143, "x");
        linear.add(60, "y");

        problem.setObjective(linear, OptType.MAX);

        linear = new Linear();
        linear.add(120, "x");
        linear.add(210, "y");

        problem.add(linear, "<=", 15000);

        linear = new Linear();
        linear.add(110, "x");
        linear.add(30, "y");

        problem.add(linear, "<=", 4000);

        linear = new Linear();
        linear.add(1, "x");
        linear.add(1, "y");

        problem.add(linear, "<=", 75);

        problem.setVarType("x", Integer.class);
        problem.setVarType("y", Integer.class);
*/
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource().equals(cargar)){
            int valor = selector.showOpenDialog(GarbageSimplex.this);
            if(valor == JFileChooser.APPROVE_OPTION){
                File archivoProblema = selector.getSelectedFile();
                try {
                    problem = text.readProblem(archivoProblema.getAbsolutePath());
                } catch (IOException ex) {
                    Logger.getLogger(GarbageSimplex.class.getName()).log(Level.SEVERE, null, ex);
                }
                tablero.removeAll();
                grilla = new JPanel();
                tablero.add(grilla, JLayeredPane.DEFAULT_LAYER);
                regionSize = text.getTableroSize();
                int grillaSize = regionSize+1;
                casillas = new JPanel[grillaSize][grillaSize];
                grilla.setLayout(new GridLayout(grillaSize,grillaSize));
                grilla.setPreferredSize(tablero.getPreferredSize());
                grilla.setBounds(0, 0, tablero.getPreferredSize().width, tablero.getPreferredSize().height);
                ArrayList<ArrayList<Integer>> ciudades = text.getCiudades();
                for (int i = 0; i < grillaSize; i++) {
                    for (int j = 0; j < grillaSize; j++) {
                        casillas[i][j] = new JPanel();
                        casillas[i][j].setBorder(BorderFactory.createLineBorder(Color.black));
                        casillas[i][j].setToolTipText("("+j+","+(regionSize-i)+")");
                        grilla.add(casillas[i][j]);
                    }
                    
                }
                
                for (int i = 0; i < ciudades.size(); i++) {
                    int x = (ciudades.get(i).get(0));
                    int y = regionSize - ciudades.get(i).get(1);
                    JPanel ciudad = casillas[y][x];
                    JLabel numero = new JLabel(String.valueOf(i+1));
                    ciudad.add(numero);
                    ciudad.setBackground(Color.green);
                }
                validate();
                resolver.setEnabled(true);
            }
        }
        
        if(e.getSource().equals(resolver)) {
            Solver solver = factory.get(); // you should use this solver only once for one problem
            Result result = solver.solve(problem);
            xBasurero.setText("Coordenada X: " + redondear.format(result.get("Bx")));
            yBasurero.setText("Coordenada Y: " + redondear.format(result.get("By")));
            casillas[regionSize - Integer.parseInt(redondear.format(result.get("By").intValue()))][Integer.parseInt(redondear.format(result.get("Bx").intValue()))].setBackground(Color.red);
            distancia.setText("Distancia : " + redondear.format(result.getObjective()));
            pack();
            System.out.println(result);
        }
        
        if(e.getSource().equals(autores)){
            JOptionPane.showMessageDialog(GarbageSimplex.this,"Alejandro Valdes\nPaola Andrea Garcia\nMaria Cristina Bustos\nJorge Ivan Morales", "Integrantes", JOptionPane.INFORMATION_MESSAGE);
        }
        
        if(e.getSource().equals(salir)){
            System.exit(0);
        }
    }
}
