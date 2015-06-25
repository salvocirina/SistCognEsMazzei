/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


/**
 *
 * @author salvatore
 */
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
 
public class Planner {
 
    // espressioni regolari
    public static String verbExp = "\\w+\\(\\w+,*\\w*,*\\w*\\)";
    public static String existExp = "exists \\w+\\.\\(\\w+\\(\\w+\\) \\&";
    public static String allExp = "all \\w+\\.\\(exists \\w+\\.\\(.*->";
    public static String conjExp = verbExp + "\\)* &" + "\\ +" + verbExp;
 
    public Planner() {
         
    }
 
    // creo l'albero AS e gli inserisco un nodo root su cui analizzare la semantica
    // e popolare tale albero
    public Tree<String> createTree(String formula) throws IOException {
 
        Node<String> root = new Node<String>("S", "S");
 
        root = createNode(formula, root);
 
        Tree<String> T = new Tree<String>(root);
        return T;
    }
 
    // creo i nodi in modo ricorsivo analizzando la semantica e restituisco
    // il nodo con i relativi children.
    private Node<String> createNode(String sem, Node<String> node) {
 
        // controllo se siano presenti quantificatori universali
        // se presenti inserisco il pattern in un arraylist e ne creo tanti figli (ricorsione)
        // al nodo per quanti sono i pattern trovati ed elimino il pattern trovato 
        // dalla semantica per proseguire la ricerca
        ArrayList<String> all = findPattern(sem, allExp);
        if (!all.isEmpty()) {
            sem = sem.replaceAll(allExp, "");
            for (String a : all) {
                Node<String> allNode = new Node<String>("adj", "all");
                node.addChild(createNode(a.replace("all", ""), allNode));
            }
        }
         
        // controllo se siano presenti quantificatori esistenziali
        // se presenti inserisco il pattern in un arraylist e ne creo tanti figli (ricorsione)
        // al nodo per quanti sono i pattern trovati ed elimino il pattern trovato 
        // dalla semantica per proseguire la ricerca
        ArrayList<String> exist = findPattern(sem, existExp);
        if (!exist.isEmpty()) {
            sem = sem.replaceAll(existExp, "");
            for (String e : exist) {
                Node<String> existNode = new Node<String>("det", "exists");
                node.addChild(createNode(e.replace("exists", ""), existNode));
            }
        }
         
        // controllo se siano presenti congiunzioni
        // se presenti inserisco il pattern in un arraylist e ne creo tanti figli (ricorsione)
        // al nodo per quanti sono i pattern trovati ed elimino il pattern trovato 
        // dalla semantica per proseguire la ricerca
        ArrayList<String> conj = findPattern(sem, conjExp);
        if (!conj.isEmpty()) {
            if (conj.size() > 1) {
                Node<String> conjNode = new Node<String>("conj", "&");
                for (String c : conj) {
                    createNode(c, conjNode);
                }
                node.addChild(conjNode);
                sem = sem.replaceAll(conjExp, "");
            }
            else{
                Node<String> conjNode = new Node<String>("conj", "&");
                String temp = conj.get(0).replaceAll("&", ","); 
                conj.set(0, temp);
                createNode(conj.get(0),conjNode);
                node.addChild(conjNode);
                sem = sem.replaceAll(conjExp, "");
            }
        }
         
        // controllo se siano presenti predicati
        // se presenti inserisco il pattern in un arraylist e ne creo tanti figli (ricorsione)
        // al nodo per quanti sono i pattern trovati, distinguendo tra subj, obj e pp
        // ed elimino il pattern trovato dalla semantica per proseguire la ricerca
        ArrayList<String> verb = findPattern(sem, verbExp);
        if (!verb.isEmpty())
            for (String v : verb) {
                String[] words = v.split("\\W");
                Node<String> verbNode = new Node<String>("pred", words[0]);
                node.addChild(verbNode);
                for (int i=1; i < words.length; i++) {
                    String pos = "";
                    switch (i) {
                    case 1:
                        pos = "subj"; 
                        break;
                    case 2:
                        pos = "obj"; 
                        break;
                    case 3:
                        pos = "pp"; 
                        break;
                    }
                    verbNode.addChild(new Node<String>(pos, words[i]));
                }
 
            }
        return node;
    }
 
    // metodo per trovare i pattern relativi all'espressione regolare nella semantica
    // restituisce un ArrayList<String> con i pattern trovati.
    private ArrayList<String> findPattern(String s, String regx) {
        Pattern p = Pattern.compile(regx);
        Matcher m = p.matcher(s);
        ArrayList<String> l = new ArrayList<String>();
        while (m.find()) {
            l.add(m.group());
        }
        return l;
    }
     
    // stampa l'albero sotto forma di sentence plan in preOrderTraversal
    public void printTree(Tree<String> T)
    {
        List<Node<String>> L = T.getPreOrderTraversal();
        ArrayList<ArrayList<Node<String>>> paths = T.getPathsFromRootToAnyLeaf();
        Iterator<ArrayList<Node<String>>> itr = paths.iterator();
        System.out.println(L);
        while (itr.hasNext())
            System.out.println("\t" + itr.next().toString());
    }
     
}
