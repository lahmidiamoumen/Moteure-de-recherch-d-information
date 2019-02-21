package project.model;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.StringReader;
import java.util.*;

public class Parsing extends Indexing implements DuplicateCounting
{
    /**
     * This class is used to ADD ALL FILES TO DATABASE IF THEY ARE NOT ALREADY EXISTED "INCLUDING INVERTED INDEX".
     * @param stopList_file This is the source of stop list file
     * @param docs_file  This is the source of corpus file
     * @param term_freqs  This is the list of all terms with there freq,positions,docId,...
     * @param duplicate  This var is used to find the frequencies of  terms
     * @param corpus  This var is represent a list of docs of corpus file
     * {@code}
     **/

    private static String stopList_file = "src/files/stoplist.txt";
    private static String docs_file = "src/files/Corpus_OHSUMED.txt";
    private List<Core> pondred;
    private Map<String, Integer> duplicates = null;
    private Map<Integer,Map<String,String>> corpus = null;
    private Map<Integer,Double> cleFreq = null;


    public Parsing(){
        System.out.println("-----------Starting-------");
        Database.getInstance().addStopList(parseFile(stopList_file)); // add stopList to database if not exists
        if (Database.getInstance().corpusIsEmpty()){ // add corpus to database if not exists
            corpus = new TreeMap<>();
            startParsing();
            Database.getInstance().addCorpusDoc(corpus);
        }
        if(Database.getInstance().invertedIsEmpty()){ // add  invertedIndex to database if not exists
            if(corpus == null )
                startParsing();
            invertedToDatabase();
        }

        System.out.println("----------------------DONE-WITH-FILES------------------");
    }

    /* CONVERTING CORPUS FILE TO LIST OF TERMS */
    private void startParsing(){
        pondred = new ArrayList<>();
        //extract docs from corpus
        for (String xml:parseDocsFile(docs_file))
        {
            try {
                pondred.add(loadXMLFromString(xml));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        System.out.println("Ending parsing");
        for (Core pon:pondred){
            for (String string:pon.abstractTerm){
                Double tf = pon.getAbstractFerq(string)/pon.getAbstractSize();
                Double idf = Math.log10(Core.count/countingDuplicates(string));
                pon.abstractFrequence.put(string,tf*idf);
            }
            pon.finish();
        }
    }

    /* INSERTING INVERTED INDEX TO DATABASE */
    private void invertedToDatabase(){
        cleFreq = new HashMap<>();
        System.out.println("Strat collecting");
        Map<String,List<Invertedindex>> collects = collecting();
        System.out.println("Ending collecting");
        Database.getInstance().invertedIndex(collects);
        Database.getInstance().addPoids(cleFreq);
    }


    /* PARSING FROM XML TAGS */
    private Core loadXMLFromString(String xml)
            throws Exception
    {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc =  builder.parse(new InputSource(new StringReader(xml)));
        XPathFactory xpathFactory = XPathFactory.newInstance();
        XPath xpath = xpathFactory.newXPath();
        Integer docno = Integer.valueOf(xpath.evaluate("/DOC/DOCNO", doc));
        String title = xpath.evaluate("/DOC/TITLE", doc);
        String abstra = xpath.evaluate("/DOC/ABSTRACT", doc);
        if(corpus != null){
            Map<String,String> map = new HashMap<>();
            map.put(title,abstra);
            corpus.put(docno,map);
        }
        return new Core(
                docno,
                normalisation(eliminateEmptyTerm(token(title))),
                normalisation(eliminateEmptyTerm(token(abstra))));
    }

    /* READ CORPUS FILE */
    private ArrayList<String> parseDocsFile(String file_name){
        // extract each <DOC> ... </DOC> from file AS string (corpus)
        ArrayList<String> list = new ArrayList<>();
        Scanner scanner = null;
        try{
            scanner = new Scanner(new BufferedReader(new FileReader(file_name)));
            scanner.useDelimiter("</DOC>"); // END of Next()
            while (scanner.hasNext())
            {
                list.add(scanner.next()+"</DOC>");
            }
            list.remove(list.size()-1);
        }
        catch (Exception e ){ System.out.println("Error reading file -> "+e.getMessage()); }
        finally {
            if(scanner != null ){scanner.close();} // close the file
        }
        return  list;
    }

    /* READ STOP LIST FILE */
    private ArrayList<String> parseFile(String file_name){    // extract each line from file AS string (stopList)
        ArrayList<String> list = new ArrayList<>();
        Scanner scanner = null;
        try{
            scanner = new Scanner(new BufferedReader(new FileReader(file_name)));
            while (scanner.hasNextLine())
            {
                list.add(scanner.nextLine());
            }
        }
        catch (Exception e ){ System.out.println("Error reading file -> "+e.getMessage()); }
        finally {
            if(scanner != null ){scanner.close();} // close the file
        }
        return  list;
    }


    // CALCULATING THE NUMBER OF DUPLICATES TERMS IN ALL CORPUS TO USE THEM IN IDF
    @Override
    public Integer countingDuplicates(String match) {
        if(duplicates == null) { // if it's not already located
            duplicates = new HashMap<>();
            for (Core str : pondred) {
                for (String term:str.abstractTerm){
                    if (duplicates.containsKey(term) )
                            duplicates.put(term, duplicates.get(term) + 1);
                    else {
                        duplicates.put(term, 1);
                    }
                }
            }
        }
        return duplicates.get(match);
    }

    /* COLLECTING ALL KIND OF FIELDS TO CREATE AN INVERTED INDEX */
   private Map<String,List<Invertedindex>> collecting() //
   {
         Map<String,List<Invertedindex>> maps = new TreeMap<>();
         for (Core pon:pondred){
             for(Map.Entry<String,Double> term:pon.allTerms.entrySet()){
                 if (maps.containsKey(term.getKey())){
                     List<Invertedindex> index = maps.get(term.getKey());
                     index.add(new Invertedindex(pon.m_cle,term.getValue(),pon.getBalise(term.getKey())));
                     maps.put(term.getKey(), index);
                 }else {
                     List<Invertedindex> index = new ArrayList<>();
                     index.add(new Invertedindex(pon.m_cle,term.getValue(),pon.getBalise(term.getKey())));
                     maps.put(term.getKey(), index);
                 }
                 if(cleFreq.containsKey(pon.m_cle))
                      cleFreq.put(pon.m_cle,cleFreq.get(pon.m_cle)+term.getValue());
                 else cleFreq.put(pon.m_cle,term.getValue());

             }
         }
       return maps;
   }
}