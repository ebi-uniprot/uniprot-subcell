package uk.ac.ebi.uniprot.uniprotsubcell.domains;

public class Location extends Subcellular {

    private static final String category = "Cellular component";

    public Location(String identifier, String accession, String content) {
        super(identifier, accession, content);
    }

    @SuppressWarnings("unused")
    private Location() {
        super();
    }

    @Override
    public String getCategory() {
        // Currently in web-site Location category = Cellular component so we need to provide same behaviour
        return category;
    }
}
