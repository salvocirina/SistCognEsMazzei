/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sistcognescky;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
 
///**
// *
// * @author salvatore
// */

public class CKYAlgorithm {
 
    static HashMap<String, String> grammar;
    static ArrayList<String> sentences;
 
    public static void main(String[] args) {
        // TODO Auto-generated method stub
        grammar = new HashMap<String, String>();
        readGrammar();
        sentences = readSentences();
        printCKY();
    }
 
    // algoritmo CKY 
    // divide in chunk la frase
    // crea una tabella NxN con N numero di parole
    // inserisco il simbolo non terminale, relativo alla regola con simbolo terminale, nella diagonale
    // controllo se c'è matching di regole inserendo nella posizione [j][i] il simbolo
    // a sinistra della regola per tute le celle della mezza tabella superiore
    // restituisco la tabella
    public static String[][] cky(String sentence) {
        String[] word = sentence.split(" ");
        String[][] table = new String[word.length][word.length];
        for (int j = 0; j < word.length; j++) {
            if (grammar.containsKey(word[j]))
                table[j][j] = grammar.get(word[j]);
            for (int i = j - 1; i >= 0; i--) {
                for (int k = i; k < j; k++) {
                    if (table[k][i] != null && table[j][k + 1] != null) {
                        String non_term = table[k][i].concat(" ").concat(
                                table[j][k + 1]);
                        if (grammar.containsKey(non_term))
                            table[j][i] = grammar.get(non_term);
                    }
                }
            }
        }
        return table;
    }
     
    // stampo la frase da parsificare, la parsifico e stampo la tabella relativa
    public static void printCKY(){
        for (String sentence : sentences) {
            String[][] table = cky(sentence);
            System.out.println("sentence: " + sentence);
            System.out.println("");
            for (int j = 0; j < table.length; j++) {
                for (int i = 0; i < table.length; i++)
                    if (table[i][j] != null)
                        System.out.print("["+table[i][j]+"]\t");
                    else
                        System.out.print("\t");
                System.out.println();
            }
            System.out.println("");
        }
    }
 
    // leggo le frasi da file
    public static ArrayList<String> readSentences() {
        try {
            ArrayList<String> lines = (ArrayList<String>) Files.readAllLines(
                    Paths.get("../../Punto1/Sentences.txt"), Charset.forName("CP850"));
            return lines;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }
 
    // leggo la grammatica da file, la divido in chunk e la inserisco in
    // un Hashmap con chiave i simboli terminali o non terminali a destra di una regola
    // e come valore il simbolo non terminale a sinistra della regola.
    public static void readGrammar() {
        try {
            ArrayList<String> lines = (ArrayList<String>) Files.readAllLines(
                    Paths.get("../../Punto1/CFG1.txt"), Charset.forName("CP850"));
            for (String line : lines) {
                String[] split = line.split(":=");
                if (split[0].endsWith(" ")) {
                    split[0] = split[0].substring(0, split[0].length() - 1);
                }
                if (split[1].contains("|")) {
                    String[] key = split[1].split("[\\W][\\s]");
                    for (String val : key) {
                        if (val.startsWith(" ") && val.endsWith(" "))
                            grammar.put(val.substring(1, val.length() - 1),
                                    split[0]);
                        else if (val.endsWith(" "))
                            grammar.put(val.substring(0, val.length() - 1),
                                    split[0]);
                        else if (val.startsWith(" "))
                            grammar.put(val.substring(1), split[0]);
 
                        else
                            grammar.put(val, split[0]);
                    }
                } else {
                    if (split[1].startsWith(" ") && split[1].endsWith(" "))
                        grammar.put(
                                split[1].substring(1, split[1].length() - 1),
                                split[0]);
                    else if (split[1].endsWith(" "))
                        grammar.put(
                                split[1].substring(0, split[1].length() - 1),
                                split[0]);
                    else if (split[1].startsWith(" "))
                        grammar.put(split[1].substring(1), split[0]);
                    else
                        grammar.put(split[1], split[0]);
 
                }
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
 
}