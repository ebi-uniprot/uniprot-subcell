package uk.ac.ebi.uniprot.uniprotsubcell.domain;

public class Location extends Subcellular {

    public Location(String identifier, String accession, String content) {
        super(identifier, accession, content);
    }

    @SuppressWarnings("unused")
    private Location() {
        super();
    }

}
