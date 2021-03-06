package uk.ac.ebi.uniprot.uniprotsubcell.repositories;

import uk.ac.ebi.uniprot.uniprotsubcell.domains.Subcellular;
import uk.ac.ebi.uniprot.uniprotsubcell.dto.SubcellularAutoComplete;

import java.util.Collection;
import java.util.List;

import java.util.regex.Pattern;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;

public interface SubcellularRepository extends Neo4jRepository<Subcellular, Long> {

    // we can use here @depth(-1) but this is more optimised
    @Query("MATCH (n:Subcellular{accession:{0}}) WITH n MATCH p=(n)-[*0..]->() RETURN p")
    List<Subcellular> findByAccession(String accession);

    @Query("MATCH (n:Subcellular) WHERE LOWER(n.identifier) = LOWER({identifier}) WITH n MATCH p=(n)-[*0..]->() RETURN p")
    List<Subcellular> findByIdentifier(@Param("identifier") String identifier);

    Collection<Subcellular> findByIdentifierIgnoreCaseLike(@Param("identifier") String identifier);

    Collection<Subcellular>
            findByIdentifierIgnoreCaseLikeOrAccessionIgnoreCaseLikeOrContentIgnoreCaseLikeOrKeywordIgnoreCaseLikeOrSynonymsIgnoreCaseLikeOrNoteIgnoreCaseLikeOrDefinitionIgnoreCaseLike(
                    String identifier, String accession, String content, String keyword, String synonyms, String note,
                    String definition);


    @Query("MATCH (n:Subcellular) WHERE n.identifier =~ {0} OR n.accession =~ {1} OR n.content =~ {2} OR n.keyword =~" +
            " {3} OR ANY(synonym IN n.synonyms WHERE synonym =~ {4}) OR n.note =~ {5} OR n.definition =~ {6} " +
            "WITH n MATCH p=(n)-[*1..1]->() RETURN p")
    Collection<Subcellular>
    findByIdentifierRegexOrAccessionRegexOrContentRegexOrKeywordRegexOrSynonymsRegexOrNoteRegexOrDefinitionRegex(
            Pattern identifier, Pattern accession, Pattern content, Pattern keyword, Pattern synonyms, Pattern note,
            Pattern definition);

    Collection<Subcellular> findByIdentifierRegex(Pattern identifier);

    Page<SubcellularAutoComplete> findProjectedByIdentifierIgnoreCaseLike(String identifier, Pageable pageable);
}
