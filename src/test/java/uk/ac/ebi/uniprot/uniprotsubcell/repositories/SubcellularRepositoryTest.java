package uk.ac.ebi.uniprot.uniprotsubcell.repositories;

import uk.ac.ebi.uniprot.uniprotsubcell.domains.Subcellular;
import uk.ac.ebi.uniprot.uniprotsubcell.import_data.ParseSubCellLines;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.neo4j.DataNeo4jTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

@RunWith(SpringRunner.class)
@DataNeo4jTest
public class SubcellularRepositoryTest {

    private static List<Subcellular> subcellList;

    @Autowired
    private SubcellularRepository repo;

    @Before
    public void setUp() throws IOException, URISyntaxException {
        if (SubcellularRepositoryTest.subcellList == null) {
            final ParseSubCellLines obj = new ParseSubCellLines();
            Path sampleDataFilePath = Paths.get(ClassLoader.getSystemResource("sample-data.txt").toURI());
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
        List<Subcellular> subcellList = repo.findByAccession(accession);
        assertNotNull("Return list can not be null", subcellList);
        assertFalse("Should return at least one element", subcellList.isEmpty());

        Subcellular sl0004 = subcellList.get(0);
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
    public void findByIdentifierIgnoreCaseLike() {
        final String id = "*MemBrAnE*";
        Collection<Subcellular> result = repo.findByIdentifierIgnoreCaseLike(id);
        assertNotNull(result);
        assertEquals("membrance should be find in result set ", 6, result.size());
    }

}
