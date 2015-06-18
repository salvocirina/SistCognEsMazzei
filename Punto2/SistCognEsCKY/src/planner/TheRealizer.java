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
import java.util.HashMap;
import java.util.List;
 
import simplenlg.framework.CoordinatedPhraseElement;
import simplenlg.framework.NLGElement;
import simplenlg.framework.NLGFactory;
import simplenlg.lexicon.Lexicon;
import simplenlg.phrasespec.NPPhraseSpec;
import simplenlg.phrasespec.PPPhraseSpec;
import simplenlg.phrasespec.SPhraseSpec;
import simplenlg.phrasespec.VPPhraseSpec;
import simplenlg.realiser.english.Realiser;
 
 
public class TheRealizer {
 
    Lexicon lexicon;
    NLGFactory nlgFactory;
    Realiser realiser;
     
    public TheRealizer(){
        this.lexicon = Lexicon.getDefaultLexicon();
        this.nlgFactory = new NLGFactory(lexicon);
        this.realiser = new Realiser(lexicon);
    }
     
    public String getSentence(Tree<String> tree){
         
        // creo delle hashmap temporali in cui memorizzare le informazioni
        // words per le parole, che possono ripetersi
        // sentence per la frase da creare
        HashMap<String, NLGElement> words = new HashMap<String, NLGElement>(); 
        HashMap<String, NLGElement> sentence = new HashMap<String, NLGElement>();
        NPPhraseSpec np;
        PPPhraseSpec pp;
        SPhraseSpec s = nlgFactory.createClause();
        CoordinatedPhraseElement coord = null;
        // ottengo la lista dei nodi dell'albero in pre-order traversal
        List<Node<String>> nodes = tree.getPreOrderTraversal();
        for(int i = 0; i < nodes.size() ; i++)
        {
            Node<String> current = nodes.get(i);
            // controllo se il nodo sia una congiunzione
            // inizializzo un sintagma di coordinazione ed elimino i nodi duplicati
            if(current.getPos().equalsIgnoreCase("conj")){
                coord = nlgFactory.createCoordinatedPhrase();
                nodes = removeDuplicate(nodes, i);
            }
             
            // controllo se il nodo sia un quantificatore esistenziale
            // inizializzo un sintagma nominale gli aggiungo lo specificatore
            // imposto il terminale nel sintagma lo inserisco nella hashmap delle parole
            if(current.getPos().equalsIgnoreCase("det"))
            {
                np = nlgFactory.createNounPhrase();
                np.setSpecifier("a");
                np.setNoun(nodes.get(++i).getData());
                words.put(nodes.get(++i).getData(), np);
            }
             
            // controllo se il nodo sia un quantificatore universale
            // inizializzo un sintagma universale e gli aggiungo il quantificatore
            // imposto il terminale nel sintagma, gli imposto la forma plurale
            // e lo inserisco nella hashmap delle parole
            if(current.getPos().equalsIgnoreCase("adj")){   
                np = nlgFactory.createNounPhrase();
                np.setPreModifier(current.getData());
                nodes.get(++i);
                np.setNoun(nodes.get(++i).getData());
                nodes.get(++i);
                np.setPlural(true);
                words.put(nodes.get(++i).getData(), np);
            }
             
            // controllo se il nodo sia un predicato
            // inizializzo un sintagma verbale e gli aggiungo il terminale
            // lo inserisco nella hashmap della frase
            // esamino, se esiste, il nodo successivo e controllo se esista già nella hashmap
            // e lo inserisco nella hashmap della frase
            // esamino, se esiste, il nodo successivo e controllo se esista già nella hashmap
            // se è un PP allora fa parte del predicato altrimenti si tratta di elementi di una congiunzione
            // e verranno inseriti nella hashmap della frase come soggetti coordinati
            if(current.getPos().equalsIgnoreCase("pred")){
                VPPhraseSpec verb = nlgFactory.createVerbPhrase(current.getData());
                sentence.put("verb", verb);
                 
                String temp = nodes.get(++i).getData();
                np = nlgFactory.createNounPhrase();
                if(words.containsKey(temp))
                    np.setNoun(words.get(temp));
                else
                    np.setNoun(temp);
                sentence.put("subj", np);
 
                if(++i<nodes.size()){
                    temp = nodes.get(i).getData();
                    np = nlgFactory.createNounPhrase(temp);
                    if(words.containsKey(temp))
                        np.setNoun(words.get(temp));
                    else
                        np.setNoun(temp);
 
                    sentence.put("obj", np);
                }
                 
                while(++i<nodes.size()){
                    Node<String> n = nodes.get(i);
                    temp = n.getData();
                    np = nlgFactory.createNounPhrase(temp);
                    if(words.containsKey(temp))
                        np.setNoun(words.get(temp));
                    else
                        np.setNoun(temp);
                     
                    switch(n.getPos()){
                    case "pp":{
                        pp = nlgFactory.createPrepositionPhrase();
                        pp.addComplement(np);
                        pp.setPreposition("to");
                        sentence.put("pp", pp);
                        break;
                    }
                    case "subj":{
                        if(coord!=null){
                            coord = nlgFactory.createCoordinatedPhrase();
                            coord.addCoordinate(sentence.get("subj"));
                            coord.addCoordinate(np);
                            sentence.put("subjc", coord);
                        }
                        break;
                    }
                    case "obj":{
                        if(coord!=null){
                            coord = nlgFactory.createCoordinatedPhrase();
                            coord.addCoordinate(sentence.get("obj"));
                            coord.addCoordinate(np);
                            sentence.put("objc", coord);
                        }
                        break;
                    }
                    }
                }
            }
        }
         
        // inserisco nell' SPhraseSpec le specifiche di costruzione della frase 
        s.setSubject(sentence.get("subj"));
        s.setVerb(sentence.get("verb"));
        s.setObject(sentence.get("obj"));
        if(sentence.containsKey("pp"))
            s.addComplement(sentence.get("pp"));
        if(sentence.containsKey("subjc"))
            s.setSubject(sentence.get("subjc"));
        if(sentence.containsKey("objc"))
            s.setObject(sentence.get("objc"));
         
        // restituisco la frase realizzata
        return realiser.realiseSentence(s);
         
    }
     
    // esamino tutti i nodi dell'albero ed elimino eventuali duplicati
    private static List<Node<String>> removeDuplicate(List<Node<String>> L, int index)
    {
        for(; index<L.size(); index++)
            for(int j = index+1; j<L.size(); j++)
                if(L.get(index).equals(L.get(j)))
                    L.remove(j);
         
        return L;
    }
     
}
