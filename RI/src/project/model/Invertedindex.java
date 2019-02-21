package project.model;

public class Invertedindex {
    Integer docId;
    Double poids;
    Pair<Boolean,Boolean> balise;

    public Invertedindex(Integer docId, Double poids, Pair<Boolean, Boolean> balise) {
        this.docId = docId;
        this.poids = poids;
        this.balise = balise;
    }

    public Integer getDocId() {
        return docId;
    }

    public void setDocId(Integer docId) {
        this.docId = docId;
    }

    public Double getPoids() {
        return poids;
    }

    public void setPoids(Double poids) {
        this.poids = poids;
    }

    public Pair<Boolean, Boolean> getBalise() {
        return balise;
    }

    public void setBalise(Pair<Boolean, Boolean> balise) {
        this.balise = balise;
    }
}
