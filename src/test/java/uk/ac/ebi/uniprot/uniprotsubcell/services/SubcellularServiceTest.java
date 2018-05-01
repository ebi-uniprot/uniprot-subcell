package uk.ac.ebi.uniprot.uniprotsubcell.services;

import org.mockito.ArgumentMatchers;
import uk.ac.ebi.uniprot.uniprotsubcell.domains.Location;
import uk.ac.ebi.uniprot.uniprotsubcell.domains.Orientation;
import uk.ac.ebi.uniprot.uniprotsubcell.domains.Subcellular;
import uk.ac.ebi.uniprot.uniprotsubcell.domains.Topology;
import uk.ac.ebi.uniprot.uniprotsubcell.repositories.SubcellularRepository;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.*;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class SubcellularServiceTest {

    private SubcellularService subcellularService;
    private SubcellularRepository subcellularRepositoryMock;
    private static List<Subcellular> data;

    @BeforeClass
    public static void data() {
        final Subcellular l = new Location("Cell tip", "SL-0456", "Cell tip");
        final Subcellular o = new Orientation("Cytoplasmic side", "SL-9910", "Cytoplasmic side");
        final Subcellular t = new Topology("Lipid-anchor", "SL-9901", "Lipid-anchor");
        data = Arrays.asList(l, o, t);
    }

    @Before
    public void setUp() {
        subcellularRepositoryMock = Mockito.mock(SubcellularRepository.class);
        subcellularService = new SubcellularService(subcellularRepositoryMock);
    }

    @Test
    public void testFindByAccession() {
        when(subcellularRepositoryMock.findByAccession(eq("SL-0456"))).thenReturn(data);
        Subcellular found = subcellularService.findByAccession("SL-0456");
        assertNotNull(found);
        assertEquals("Cell tip", found.getIdentifier());
    }

    @Test
    public void testFindByIdentifier() {
        when(subcellularRepositoryMock.findByIdentifier(eq("Lipid-anchor")))
                .thenReturn(Arrays.asList(data.get(2), data.get(0), data.get(1)));

        Subcellular found = subcellularService.findByIdentifier("Lipid-anchor");
        assertNotNull(found);
        assertEquals("SL-9901", found.getAccession());
    }

    @Test
    public void testFindByIdentifierIgnoreCaseLike() {
        when(subcellularRepositoryMock.findByIdentifierIgnoreCaseLike(eq("*i*"))).thenReturn(data);
        Collection<Subcellular> retCol = subcellularService.findByIdentifierIgnoreCaseLike("i");
        assertNotNull(retCol);
        assertEquals(3, retCol.size());
    }

    @Test
    public void testImportEntriesFromFileIntoDb() throws IOException {
        doAnswer(returnsFirstArg()).when(subcellularRepositoryMock).saveAll(ArgumentMatchers.anyCollection());
        subcellularService
                .importEntriesFromFileIntoDb(getClass().getClassLoader().getResource("sample-data.txt").getPath());
    }
}
