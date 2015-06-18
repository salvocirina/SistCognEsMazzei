/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package planner;

/**
 *
 * @author salvatore
 */
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
 
 
public class MainPlanner {
 
    static Planner planner;
    static TheRealizer realizer;
    static ArrayList<String> semantics;
     
    public static void main(String[] args) {
        // TODO Auto-generated method stub
        planner = new Planner();
        realizer = new TheRealizer();
        semantics = readSemantic();
        try {
            for(String semantic : semantics){
                Tree<String> tree = planner.createTree(semantic);
                System.out.println("Semantic: " + semantic);
                System.out.println("Sentence Plan:");
                planner.printTree(tree);
                System.out.println("English sentence:");
                System.out.println(realizer.getSentence(tree) + "\n");
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
     
    public static ArrayList<String> readSemantic() {
        try {
            ArrayList<String> lines = (ArrayList<String>) Files.readAllLines(
                    Paths.get("../../Punto3/G2.txt"), Charset.forName("CP850"));
            return lines;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }
 
}
