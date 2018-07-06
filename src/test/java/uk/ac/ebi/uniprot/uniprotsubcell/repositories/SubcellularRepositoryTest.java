package uk.ac.ebi.uniprot.uniprotsubcell.repositories;

import java.util.regex.Pattern;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.neo4j.DataNeo4jTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit4.SpringRunner;

import uk.ac.ebi.uniprot.uniprotsubcell.domains.Subcellular;
import uk.ac.ebi.uniprot.uniprotsubcell.dto.SubcellularAutoComplete;
import uk.ac.ebi.uniprot.uniprotsubcell.import_data.ParseSubCellLines;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@DataNeo4jTest
public class SubcellularRepositoryTest {
    private static final String IDENTIFIER_PROP = "identifier";

    private static List<Subcellular> subcellList;

    @Autowired
    private SubcellularRepository repo;

    @Before
    public void setUp() throws IOException, URISyntaxException {
        if (SubcellularRepositoryTest.subcellList == null) {
            final ParseSubCellLines obj = new ParseSubCellLines();
            final Path sampleDataFilePath = Paths.get(ClassLoader.getSystemResource("sample-data.txt").toURI());
            SubcellularRepositoryTest.subcellList = obj.parseLines(Files.readAllLines(sampleDataFilePath));
        }

        repo.saveAll(SubcellularRepositoryTest.subcellList);
    }

    /**
     * Test of findByAccession method, of class SubcellularRepository.
     */
    @Test
    public void testFindByAccession() {
        final String accession = "SL-0004";
        final List<Subcellular> retList = repo.findByAccession(accession);
        assertNotNull("Return list can not be null", retList);
        assertFalse("Should return at least one element", retList.isEmpty());

        Subcellular sl0004 = retList.get(0);
        assertEquals("First element should always be the one which we searched for ", "SL-0004", sl0004.getAccession());

        // Relationship test
        assertNull("No is a relationship for SL-0004", sl0004.getIsA());
        assertEquals("Only one part of sl0004", 1, sl0004.getPartOf().size());

        Subcellular sl0006 = sl0004.getPartOf().get(0);
        assertEquals("sl0004 part of sl006", "SL-0006", sl0006.getAccession());

        Subcellular sl0162 = sl0006.getIsA().get(0).getIsA().get(0).getIsA().get(0);
        assertEquals("sl006 is sl0245 is sl0089 is sl0162", "SL-0162", sl0162.getAccession());
    }

    /**
     * Test of findByIdentifierIgnoreCaseLike method, of class SubcellularRepository.
     */
    @Test
    public void testFindByIdentifierIgnoreCaseLike() {
        final String id = "*MemBrAnE*";
        final Collection<Subcellular> result = repo.findByIdentifierIgnoreCaseLike(id);
        assertNotNull(result);
        assertEquals("membrance should be find in result set ", 6, result.size());
    }

    @Test
    public void testFindByIdentifierCaseShoudBeIgnore() {
        final String identifier = "cell junction";
        final List<Subcellular> retList = repo.findByIdentifier(identifier);

        assertNotNull("Return list can not be null", retList);
        assertFalse("Should return exact one element", retList.size() != 1);

        Subcellular sl0038 = retList.get(0);
        assertEquals("First element should always be the one which we searched for ", "SL-0038", sl0038.getAccession());

        // Relationship test
        assertNull("No is a relationship for SL-0038", sl0038.getIsA());
        assertNull("No part of SL-0038", sl0038.getPartOf());
    }

    @Test
    public void
    testFindByIdentifierIgnoreCaseLikeOrAccessionIgnoreCaseLikeOrContentIgnoreCaseLikeOrKeywordIgnoreCaseLikeOrSynonymsIgnoreCaseLikeOrNoteIgnoreCaseLikeOrDefinitionIgnoreCaseLike() {
        final String input = "*memBRane*";
        final Collection<Subcellular> retCol = repo
                .findByIdentifierIgnoreCaseLikeOrAccessionIgnoreCaseLikeOrContentIgnoreCaseLikeOrKeywordIgnoreCaseLikeOrSynonymsIgnoreCaseLikeOrNoteIgnoreCaseLikeOrDefinitionIgnoreCaseLike(
                        input, input, input, input, input, input, input);
        assertNotNull(retCol);
        assertEquals("Size of In identifier", 11, retCol.size());
    }

    @Test
    public void identifierWholeWordShouldMatch() {
        Pattern p = Pattern.compile("(?i).*\\bendomembrane\\b.*");
        Collection<Subcellular> result = repo.findByIdentifierRegex(p);
        assertThat(result).isNotNull().hasSize(1);

        //when identifier is not complete (endomembrane)
        p = Pattern.compile("(?i).*\\bendomembran\\b.*");
        result = repo.findByIdentifierRegex(p);
        assertThat(result).isNotNull().hasSize(0);
    }

    @Test
    public void identifierRegexResultShouldGetRelationsAndGoMappings() {
        final Pattern p = Pattern.compile("(?i).*\\bacrosome membrane\\b.*");
        final Collection<Subcellular> result = repo.findByIdentifierRegex(p);

        final Subcellular k = result.stream().findFirst().orElse(null);
        assertThat(k.getIsA()).isNotNull().hasSize(1);
        assertThat(k.getGoMappings().get(0)).isNotNull();
        assertThat(k.getPartOf()).isNotNull().hasSize(2);
    }

    @Test
    public void wordMatchInIdentifierAccessionContentKeywordNoteDefinitionSynonyms() {
        Pattern p = Pattern.compile("(?i).*\\bVESICLe\\b.*");
        Collection<Subcellular> retCol =
                repo.findByIdentifierRegexOrAccessionRegexOrContentRegexOrKeywordRegexOrSynonymsRegexOrNoteRegexOrDefinitionRegex(
                        p, p, p, p, p, p, p);
        assertNotNull(retCol);
        assertEquals(10, retCol.size());

        p = Pattern.compile("(?i).*\\bVESICL\\b.*");
        retCol =
                repo.findByIdentifierRegexOrAccessionRegexOrContentRegexOrKeywordRegexOrSynonymsRegexOrNoteRegexOrDefinitionRegex(
                        p, p, p, p, p, p, p);
        assertNotNull(retCol);
        assertEquals(0, retCol.size());
    }

    @Test
    public void autoCompleteAndPaginationChecks() {
        final String id = "*mem*";
        List<SubcellularAutoComplete> result = repo.findProjectedByIdentifierIgnoreCaseLike(id, PageRequest.of(0, 1)).getContent();
        assertThat(result).isNotNull().hasSize(1);

        result = repo.findProjectedByIdentifierIgnoreCaseLike(id, PageRequest.of(0, 10)).getContent();
        assertThat(result).isNotNull().hasSize(6);

        assertThat(result).extracting(IDENTIFIER_PROP).contains("Cytoplasmic vesicle membrane","Endomembrane system",
                "Secretory vesicle membrane", "Membrane", "Acrosome membrane", "Acrosome inner membrane");
    }
}
