package uk.ac.ebi.uniprot.uniprotsubcell.domain;

public class Topology extends Subcellular {

    public Topology(String identifier, String accession, String content) {
        super(identifier, accession, content);
    }

    @SuppressWarnings("unused")
    private Topology() {
        super();
    }

}
