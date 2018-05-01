package uk.ac.ebi.uniprot.uniprotsubcell.domains;

import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;

public class GeneOntology {

    @Id
    @GeneratedValue
    protected Long id;

    // Bellow fields can't be final because we have to provide default values to fields. I we do and when OGM will
    // create object it will not set the actual values
    private String goId;
    private String definition;

    @SuppressWarnings("unused")
    private GeneOntology() {}

    public GeneOntology(String goId, String definition) {
        this.goId = goId;
        this.definition = definition;
    }

    public String getGoId() {
        return goId;
    }

    public String getDefinition() {
        return definition;
    }

}
