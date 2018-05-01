package uk.ac.ebi.uniprot.uniprotsubcell.domains;

import java.util.List;
import javax.validation.constraints.NotNull;
import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.Index;

/**
 * Can't make this class abstract because we have add label "Subcellular" on Subcellular Entry node in neo4j
 * 
 * @author Rizwan Ishtiaq
 *
 */
public class Subcellular {

    @Id
    @GeneratedValue
    protected Long id;

    @NotNull
    @Index(unique = true)
    protected String identifier;

    // AC Accession (SL-xxxx) Once
    @NotNull
    @Index
    protected String accession;

    // DE Definition Once or more
    protected String definition;

    // SY Synonyms Optional; Once or more
    protected List<String> synonyms;

    // SL Content of subc. loc. lines Once
    @NotNull
    protected String content;

    // HI Hierarchy ('is-a') Optional; Once or more
    protected List<Subcellular> isA;

    // HP Hierarchy ('part-of') Optional; Once or more
    protected List<Subcellular> partOf;

    // KW Associated keyword (accession) Optional; Once
    protected String keyword;

    // GO Gene ontology (GO) mapping Optional; Once or more
    protected List<GeneOntology> goMappings;

    // AN Annotation note Optional; Once or more
    protected String note;

    // RX Interesting references Optional; Once or more
    protected List<String> references;

    // WW Interesting links Optional; Once or more
    protected List<String> links;

    /**
     * No client/user should create this class directly
     * 
     * @param identifier
     * @param accession
     * @param content
     */
    protected Subcellular(@NotNull String identifier, @NotNull String accession, @NotNull String content) {
        this.identifier = identifier;
        this.accession = accession;
        this.content = content;
    }

    /**
     * No argument constructor is mandatory for OGM (Object Graph Model) to construct object. Making it
     * private/protected because according to business knowledge subcellular entry must have identifier, accession and
     * content
     */
    protected Subcellular() {

    }

    // Public getters will be needed while send result as JSON
    public String getCategory() {
        return this.getClass().getSimpleName();
    }

    public Long getId() {
        return id;
    }

    public String getIdentifier() {
        return identifier;
    }

    public String getAccession() {
        return accession;
    }

    public String getDefinition() {
        return definition;
    }

    public List<String> getSynonyms() {
        return synonyms;
    }

    public String getContent() {
        return content;
    }

    public List<Subcellular> getIsA() {
        return isA;
    }

    public List<Subcellular> getPartOf() {
        return partOf;
    }

    public String getKeyword() {
        return keyword;
    }

    public List<GeneOntology> getGoMappings() {
        return goMappings;
    }

    public String getNote() {
        return note;
    }

    public List<String> getReferences() {
        return references;
    }

    public List<String> getLinks() {
        return links;
    }

    // Public setters, used in creating object while parsing file
    public void setDefinition(String definition) {
        this.definition = definition;
    }

    public void setSynonyms(List<String> synonyms) {
        this.synonyms = synonyms;
    }

    public void setIsA(List<Subcellular> isA) {
        this.isA = isA;
    }

    public void setPartOf(List<Subcellular> partOf) {
        this.partOf = partOf;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public void setGoMappings(List<GeneOntology> goMappings) {
        this.goMappings = goMappings;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public void setReferences(List<String> references) {
        this.references = references;
    }

    public void setLinks(List<String> links) {
        this.links = links;
    }

    @Override
    public String toString() {
        return "Subcellular [id=" + id + ", identifier=" + identifier + ", accession=" + accession + ", definition="
                + definition + ", synonyms=" + synonyms + ", content=" + content + ", isA=" + isA + ", partOf=" + partOf
                + ", keyword=" + keyword + ", goMappings=" + goMappings + ", note=" + note + ", references="
                + references + ", links=" + links + "]";
    }

}
