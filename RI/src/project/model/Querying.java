package project.model;

import org.bson.Document;
import java.util.*;
import static java.util.stream.Collectors.toMap;


public class Querying extends Indexing implements DuplicateCounting{
   private ArrayList<String> terms;
    ArrayList<Document> documentList;
    Map<String,Integer> duplicates =null;
    Double poidsQuery;  /*  sum of all term frequency in the query */
    Map<Integer,Double> score; /* list of couples (docID, WIGHT) */
    HashSet<String> hashSet;
    Map<Integer,Double> poids= null;

    public Querying(String query) {
        if(query.isEmpty()){
        }
        else {
            poids= null;
            terms =  normalisation(eliminateEmptyTerm(token(query)));
            documentList = Database.getInstance().getDocuments(terms);
            diceIndex();
        }
    }

    /* COUNTING DICE INDEX AND SORTING RESULTS OF SCORES*/
    public void diceIndex(){
        score = new TreeMap<>();
        hashSet = new HashSet<>();
        hashSet.addAll(terms);
        poidsQuery  = getWeightQuery();
        /* <docID, freqTerm> */

        for (Document doc:documentList){
            for (Document document1:(ArrayList<Document>) doc.get(Database.DOCS)){
                Integer docId = (Integer) document1.get(Database.DOCID);
                if(!score.containsKey(docId)){
                    Double a= getWeightDocQuery(docId)*2.0;
                    Double b = poidsQuery + getPoidsDocument(docId);
                    score.put(docId,a/b);
                }
            }
        }

        /* LAMBDA EXPRESSION SORTING */
        score = score
                .entrySet()
                .stream()
                .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                .collect(
                        toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2,
                                LinkedHashMap::new));


    }

    /* GET DOCS THAT MATCH SCORE(DOC) > 0 FROM DATABASE */
    public List<DocFx> getDocFx(){
        List<DocFx> list = new ArrayList<>();
        List<Document> docs = new ArrayList<>();
        for (Map.Entry<Integer, Double> entry : score.entrySet()) {
//            System.out.println(entry.getKey()+" "+entry.getValue());
            docs.addAll( Database.getInstance().getCorpus(entry.getKey()));
        }
        if (docs.isEmpty()){  list.add(new DocFx(" No matching document found","")); return list;}
        for (Document document :docs){
            list.add(new DocFx(document.get(Database.TITRE).toString().replaceAll("^-",""),
                    document.get(Database.ABSTRACT).toString().replaceAll("^\n-","")));
        }
        return list;
    }

    @Override
    public Integer countingDuplicates(String match) {
        if(duplicates == null) {
            duplicates = new HashMap<>();
            for (String term : terms) {
                if (duplicates.containsKey(term)) {
                    duplicates.put(term, duplicates.get(term) + 1);
                } else {
                    duplicates.put(term, 1);
                }
            }
        }
        return duplicates.get(match);
    }

    private Double getWeightQuery()
    {
        Double a =0.0;
        for (String term:hashSet){
            a += Math.pow(countingDuplicates(term),2);
        }
        return a;
    }


    private Double getPoidsDocument(Integer doc){
        return  Database.getInstance().getTermsByDocId(doc.toString());
    }

    private Double getWeightDocQuery(Integer docId){
        Double a =0.0;
        for (Document doc :documentList){
            if(hashSet.contains(doc.get(Database.TERM))){
                for (Document document:(ArrayList<Document>) doc.get(Database.DOCS)){
                    if(Integer.valueOf((Integer) document.get(Database.DOCID)).equals(docId)){
                        Double b = Double.valueOf((Double) document.get(Database.POID));
                        a +=  b* Double.valueOf(countingDuplicates((String) doc.get(Database.TERM)));
                        break;
                    }
                }
            }
        }
        return a ;
    }
}
