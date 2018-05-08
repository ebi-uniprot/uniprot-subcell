package uk.ac.ebi.uniprot.uniprotsubcell.services;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import uk.ac.ebi.uniprot.uniprotsubcell.domains.Location;
import uk.ac.ebi.uniprot.uniprotsubcell.domains.Orientation;
import uk.ac.ebi.uniprot.uniprotsubcell.domains.Subcellular;
import uk.ac.ebi.uniprot.uniprotsubcell.domains.Topology;
import uk.ac.ebi.uniprot.uniprotsubcell.repositories.SubcellularRepository;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

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

    @Test
    public void testKeywordSearch() {
        String input = "*inner*";
        when(subcellularRepositoryMock
                .findByIdentifierIgnoreCaseLikeOrAccessionIgnoreCaseLikeOrContentIgnoreCaseLikeOrKeywordIgnoreCaseLikeOrSynonymsIgnoreCaseLikeOrNoteIgnoreCaseLikeOrDefinitionIgnoreCaseLike(
                        eq(input), eq(input), eq(input), eq(input), eq(input), eq(input),
                        eq(input))).thenReturn(data.subList(0, 1));
        input = "*man*";
        when(subcellularRepositoryMock
                .findByIdentifierIgnoreCaseLikeOrAccessionIgnoreCaseLikeOrContentIgnoreCaseLikeOrKeywordIgnoreCaseLikeOrSynonymsIgnoreCaseLikeOrNoteIgnoreCaseLikeOrDefinitionIgnoreCaseLike(
                        eq(input), eq(input), eq(input), eq(input), eq(input), eq(input),
                        eq(input))).thenReturn(data.subList(1, 2));
        input = "*outer*";
        when(subcellularRepositoryMock
                .findByIdentifierIgnoreCaseLikeOrAccessionIgnoreCaseLikeOrContentIgnoreCaseLikeOrKeywordIgnoreCaseLikeOrSynonymsIgnoreCaseLikeOrNoteIgnoreCaseLikeOrDefinitionIgnoreCaseLike(
                        eq(input), eq(input), eq(input), eq(input), eq(input), eq(input),
                        eq(input))).thenReturn(data.subList(2, 3));
        input = "*not*";
        when(subcellularRepositoryMock
                .findByIdentifierIgnoreCaseLikeOrAccessionIgnoreCaseLikeOrContentIgnoreCaseLikeOrKeywordIgnoreCaseLikeOrSynonymsIgnoreCaseLikeOrNoteIgnoreCaseLikeOrDefinitionIgnoreCaseLike(
                        eq(input), eq(input), eq(input), eq(input), eq(input), eq(input),
                        eq(input))).thenReturn(data.subList(0, 1));
        Collection<Subcellular> retCol = subcellularService.findAllByKeyWords("inNer Outer INNER man noT");
        assertNotNull(retCol);
        assertEquals(3, retCol.size());
    }
}
