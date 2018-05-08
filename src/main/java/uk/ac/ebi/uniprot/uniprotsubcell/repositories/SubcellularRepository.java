package uk.ac.ebi.uniprot.uniprotsubcell.repositories;

import uk.ac.ebi.uniprot.uniprotsubcell.domains.Subcellular;

import java.util.Collection;
import java.util.List;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;

public interface SubcellularRepository extends Neo4jRepository<Subcellular, Long> {

    // we can use here @depth(-1) but this is more optimised
    @Query("MATCH (n:Subcellular{accession:{0}}) WITH n MATCH p=(n)-[*0..]->() RETURN p")
    List<Subcellular> findByAccession(String accession);

    @Query("MATCH (n:Subcellular{identifier:{identifier}}) WITH n MATCH p=(n)-[*0..]->() RETURN p")
    List<Subcellular> findByIdentifier(@Param("identifier") String identifier);

    Collection<Subcellular> findByIdentifierIgnoreCaseLike(@Param("identifier") String identifier);

    Collection<Subcellular>
            findByIdentifierIgnoreCaseLikeOrAccessionIgnoreCaseLikeOrContentIgnoreCaseLikeOrKeywordIgnoreCaseLikeOrSynonymsIgnoreCaseLikeOrNoteIgnoreCaseLikeOrDefinitionIgnoreCaseLike(
                    String identifier, String accession, String content, String keyword, String synonyms, String note,
                    String definition);
}
