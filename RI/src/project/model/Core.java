package project.model;

import java.util.*;

public class Core {

    Integer m_cle;
    private List<String> m_title;
    private List<String> m_abstract;
    Map<String,Integer> titleFrequence = new HashMap<>();
    Map<String,Double> abstractFrequence = new HashMap<>();
    HashSet<String> titleTerm;
    HashSet<String> abstractTerm;
    Map<String,Double> allTerms = new TreeMap<>();
    Map<String,Pair<Boolean,Boolean>> balise = new HashMap<>();

    static public  int count = 0;


    public Core(Integer m_cle, List<String> m_title, List<String> m_abstract) {
        this.m_cle = m_cle;
        this.m_title = m_title;
        this.m_abstract = m_abstract;
        titleTerm =  new HashSet<>(m_title);
        abstractTerm =  new HashSet<>(m_abstract);
        poidsTitre();
        poidsAbstract();
        count++;
//        System.out.println(count);
    }

    private void poidsTitre(){ //counting the number of each term in document title
       for (String str : titleTerm){
            titleFrequence.put(str,Collections.frequency(m_title,str));
            if(abstractTerm.contains(str))
                balise.put(str,new Pair<>(true,true));
            else balise.put(str,new Pair<>(true,false));
        }
    }

    private void poidsAbstract(){ //counting the number of each term in document abstract
        for (String str:abstractTerm) {
            abstractFrequence.put(str,Double.valueOf(Collections.frequency(m_abstract, str)));
            if(!balise.containsKey(str))
                balise.put(str,new Pair<>(false,true));
        }
    }

    public int getAbstractSize(){
        return m_abstract.size();
    }

    public boolean containTerm(String term){
        return titleTerm.contains(term) || abstractTerm.contains(term);
    }

    public Double getAbstractFerq(String str){
        return abstractFrequence.get(str);
    }

    public Pair<Boolean,Boolean> getBalise(String term){
        return balise.get(term);
    }

    public void finish(){
        for (String str:abstractTerm){
            allTerms.put(str,abstractFrequence.get(str)+(titleFrequence.containsKey(str) ? titleFrequence.get(str) : 0));
        }
    }
}
