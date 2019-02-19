package project.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//  collect is a row of InvertedIndex : <term> <nbdoc> <doc1 pos1,po2,...,po3 poids balise>
public class Collect {
    String term;
    Integer nbDoc ;
    List<Map<Integer,List<Integer>>> docs_positions; //<dci><po1,po2,...>
    List<Double> poids;
    List<Map<Boolean,Boolean>> balise; // titre-abstract


    public Collect(String term,Integer doc,Integer positions,Map<Boolean,Boolean> balise,Double poids) {
        finish(doc,poids);
        this.term = term;
        this.nbDoc = 1;
        Map<Integer,List<Integer>> hashMap = new HashMap<>();
        docs_positions = new ArrayList<>();
        List<Integer> position;
        position = new ArrayList<>();
        position.add(positions);
        hashMap.put(doc,position);
        docs_positions.add(hashMap);
        this.poids = new ArrayList<>();
        this.poids.add(poids);
        this.balise = new ArrayList<>();
        this.balise.add(balise);
        System.out.println(term);

    }

    public void add(Integer doc,Integer pos,Double poids,Map<Boolean,Boolean> balise) {
        System.out.println(term);
        finish(doc,poids);
        int i=0;
        for (Map<Integer, List<Integer>> entry:docs_positions){
            if(entry.containsKey(doc)){
                docs_positions.get(i).get(doc).add(pos);  /* add position poj only related to document <doci><pos1,...,poj><poidsi>*/
                this.poids.add(poids);
                return;
            }
            i++;
        }
        Map<Integer,List<Integer>> hashMap = new HashMap<>();
        List<Integer> position;
        position = new ArrayList<>();
        position.add(pos);
        this.poids.add(poids);
        this.balise.add(balise);
        hashMap.put(doc,position);
        docs_positions.add(hashMap);
        this.nbDoc++;
    }

    private void finish(Integer docID,Double poid){
                Database.getInstance().addPoids(docID.toString(),Math.pow(poid,2));
    }

    void toStrings() {
        int i=0;
        Boolean titr;
        Boolean abstr;
        if (!term.equals("1-adrenergic")) return;
        for (Map<Integer, List<Integer>> v: this.docs_positions) {
            for (Map.Entry<Integer, List<Integer>> entry:v.entrySet()){
                System.out.print("\n"+term+
                        " nbrDoc<"+nbDoc+"> docs ");
                Map.Entry<Boolean,Boolean> entry1 = balise.get(i).entrySet().iterator().next();
                titr = entry1.getKey();
                abstr = entry1.getValue();
                System.out.println(entry.getKey()+" poids<"+poids.get(i)+"> balise:"+(titr ? "(1,": "(0,")+(abstr ? "1)": "0)")+" pos: "+docs_positions.toString());
            }
            i++;
        }
    }
}
