package uk.ac.ebi.uniprot.uniprotsubcell.repositories;

import uk.ac.ebi.uniprot.uniprotsubcell.domain.Subcellular;

import java.util.Collection;
import java.util.List;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;

public interface SubcellularRepository extends Neo4jRepository<Subcellular, Long> {

    // we can use here @depth(-1) but this is more optimised
    // @Query("MATCH (n:Subcellular{accession:{0}}) WITH n MATCH p=(n)-[*0..]->() RETURN p, length(p) ORDER BY length(p)
    // DESC LIMIT 1")

    @Query("MATCH (n:Subcellular{accession:{0}}) WITH n MATCH p=(n)-[*0..]->() RETURN p")
    List<Subcellular> findByAccession(String accession);

    Collection<Subcellular> findByIdentifierLike(@Param("identifier") String identifier);
}
