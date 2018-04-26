package uk.ac.ebi.uniprot.uniprotsubcell.domain;

public class Orientation extends Subcellular {

    public Orientation(String identifier, String accession, String content) {
        super(identifier, accession, content);
    }

    @SuppressWarnings("unused")
    private Orientation() {
        super();
    }

}
