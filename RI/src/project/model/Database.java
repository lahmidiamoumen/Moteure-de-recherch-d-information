package project.model;

import com.mongodb.*;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Projections;
import org.bson.Document;
import org.bson.conversions.Bson;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;


import static com.mongodb.client.model.Accumulators.sum;
import static com.mongodb.client.model.Aggregates.group;
import static com.mongodb.client.model.Aggregates.match;
import static com.mongodb.client.model.Filters.elemMatch;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.in;
import static com.mongodb.client.model.Projections.excludeId;
import static com.mongodb.client.model.Projections.include;


public class Database
{
    /*-------------------------------------VARIABLES------------------------*/
    private String user = "moumen_admin"; // the user name
    private String database = "ri"; // the name of the database in which the user is defined
    public static String DOCS = "ptr";
    private static String inverseTest = "invertedIndex2";
    private static String collection = "poidsDoc2";
    private static String file = "corpus";
    private static String stopList = "stopList";
    char[] password = {'r','o','o','o','t'}; // the password as a character array
    static private Database instance = new Database();
    public  static  String TERM = "term";
    static  String NDOC = "ndoc";
    public static  String DOCID = "docid";
    public static  String POID = "poid";
    static  String BALISE = "balise";
    static  String CLE = "cle";
    static  String TITRE = "titre";
    static  String ABSTRACT = "abstract";
    private MongoCollection<BasicDBObject> stopList_coll;
    private MongoCollection<Document> poidsDoc;
    private MongoCollection<Document> inverse_coll;
    private MongoCollection<Document> file_coll;
    private MongoDatabase db;
    List<BasicDBObject> foundDocument = null;

    /* this is the only way to create an object of Database class */
    static public Database getInstance(){return instance;}

    private Database()
    {
        MongoCredential credential = MongoCredential.createCredential(user, database, password);
        MongoClient mongoClient = new MongoClient(new ServerAddress("localhost",27017), Arrays.asList(credential));
        try{
            db = mongoClient.getDatabase(database);
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
        poidsDoc = db.getCollection(collection,  Document.class);
        stopList_coll = db.getCollection(stopList,  BasicDBObject.class);
        inverse_coll = db.getCollection(inverseTest, Document.class);
        file_coll = db.getCollection(file, Document.class);
    }

    /* add stopList To DATABASE */
    public void addStopList(ArrayList<String> list)
    {
        List<BasicDBObject> foundDocument = stopList_coll.find().into(new ArrayList<>());
        if(foundDocument.isEmpty()){
            for (String term: list) {
                stopList_coll.insertOne(new BasicDBObject(TERM, term));
            }
        }
        else {
            System.out.println("stopList is already exists");
        }
    }
    /* ADD CORPUS FILE TO DATABASE */
    public void addCorpusDoc(Map<Integer,Map<String,String>> list){
        List<Document> foundDocument = file_coll.find().limit(1).into(new ArrayList<>());
        if(foundDocument.isEmpty()){
            foundDocument = new ArrayList<>();
            for (Map.Entry<Integer,Map<String,String>> map:list.entrySet()) {
                   for (Map.Entry<String,String> map2 : map.getValue().entrySet()){
                       foundDocument.add(new Document(CLE,map.getKey()).append(TITRE,map2.getKey()).append(ABSTRACT,map2.getValue()));
                   }
            }
            file_coll.insertMany(foundDocument);
        }
        else {
            System.out.println("Corpus is already exists");
        }
    }
    /* GET CORPUS DOCS FROM DATABASE */
    public List<Document> getCorpus(Integer docId){
        return  file_coll.find(eq(CLE,docId)).into(new ArrayList<>());
    }

    public void invertedIndex(Map<String,List<Invertedindex>> collects){
        if(!invertedIsEmpty()){ System.out.println("is not empty"); return;}
        System.out.println("Start inserting in DATABASE");
        List<Document> termRrow = new ArrayList<>();
        List<Document> docs = new ArrayList<>();
        for (Map.Entry<String,List<Invertedindex>> term:collects.entrySet()){
            for (Invertedindex in:term.getValue()){
                termRrow.add(new Document()
                        .append(DOCID, in.docId)
                        .append(POID, in.getPoids())
                        .append(BALISE, Arrays.asList(in.getBalise().getKey(), in.getBalise().getValue())));
            }
            docs.add(new Document(TERM, term.getKey()).append(NDOC, term.getValue().size()).append(DOCS,termRrow));
            termRrow= new ArrayList<>();
        }
        inverse_coll.insertMany(docs);
        System.out.println("Inserting Ended");
    }


    public boolean isStopWord(String term){
        final boolean[] bool = {false};
        if(foundDocument == null){
            foundDocument  = stopList_coll.find().into(new ArrayList<>());
        }
        Consumer<BasicDBObject> consumer = basicDBObject -> {
            if(basicDBObject.get(TERM).equals(term)){
                bool[0] = true;
            }
        };
        foundDocument.forEach(consumer);
        return  bool[0];
    }

    public Boolean invertedIsEmpty(){
        List<Document> foundDocument = inverse_coll.find().limit(1).into(new ArrayList<>());
        if(!foundDocument.isEmpty()){
            System.out.println("inverse file is already exists");
            return false;
        }else {
            return true;
        }
    }
    public Boolean corpusIsEmpty(){
        List<Document> foundDocument = file_coll.find().limit(1).into(new ArrayList<>());
        if(foundDocument.isEmpty()){
            System.out.println("corpus file is already exists");
            return true;
        }else {
            return false;
        }
    }

    public ArrayList<Document> getDocuments(ArrayList<String> terms){
              return  inverse_coll.find(in(TERM,terms)).into(new ArrayList<>());
    }

    public int count(){
        return inverse_coll.find().into(new ArrayList<>()).size();
    }

    public void addPoids(Map<Integer,Double> cleFreq) {
        List<Document> list = new ArrayList<>();
        for (Map.Entry<Integer,Double> ter:cleFreq.entrySet()){
            list.add(new Document(DOCID,ter.getKey().toString()).append(POID,ter.getValue()));
        }
            poidsDoc.insertMany(list);
    }
    public Double getTermsByDocId(String docId){
       return (Double) poidsDoc.find(eq(DOCID,docId)).iterator().next().get(POID);
    }

}

/* create user in mongodb console
db.createUser({
                user:"moumen_admin",
                pwd:"rooot",
                roles:["readWrite","dbAdmin"]
              })
*/