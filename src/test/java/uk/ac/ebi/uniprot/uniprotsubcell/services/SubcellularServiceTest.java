package uk.ac.ebi.uniprot.uniprotsubcell.services;

import java.util.regex.Pattern;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.refEq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
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
        when(subcellularRepositoryMock.findByIdentifierRegex(refEq(Pattern.compile("(?i).*\\bi\\b.*"))))
                .thenReturn(data);
        Collection<Subcellular> retCol = subcellularService.findByIdentifierIgnoreCaseLike("i");
        assertNotNull(retCol);
        assertEquals(3, retCol.size());
    }

    @Test
    public void testImportEntriesFromFileIntoDb() throws IOException {
        doAnswer(returnsFirstArg()).when(subcellularRepositoryMock).saveAll(ArgumentMatchers.anyCollection());
        subcellularService
                .importSubcellEntriesFromFileIntoDb(getClass().getClassLoader().getResource("sample-data.txt").getPath());
    }

    @Test
    public void testKeywordSearch() {
        Pattern input = Pattern.compile("(?i).*\\btip\\b.*");
        when(subcellularRepositoryMock
                .findByIdentifierRegexOrAccessionRegexOrContentRegexOrKeywordRegexOrSynonymsRegexOrNoteRegexOrDefinitionRegex(
                        refEq(input), refEq(input), refEq(input), refEq(input), refEq(input), refEq(input),
                        refEq(input))).thenReturn(data.subList(0, 1));
        input = Pattern.compile("(?i).*\\bman\\b.*");
        when(subcellularRepositoryMock
                .findByIdentifierRegexOrAccessionRegexOrContentRegexOrKeywordRegexOrSynonymsRegexOrNoteRegexOrDefinitionRegex(
                        refEq(input), refEq(input), refEq(input), refEq(input), refEq(input), refEq(input),
                        refEq(input))).thenReturn(data.subList(1, 2));
        input = Pattern.compile("(?i).*\\bsl-9910\\b.*");
        when(subcellularRepositoryMock
                .findByIdentifierRegexOrAccessionRegexOrContentRegexOrKeywordRegexOrSynonymsRegexOrNoteRegexOrDefinitionRegex(
                        refEq(input), refEq(input), refEq(input), refEq(input), refEq(input), refEq(input),
                        refEq(input))).thenReturn(data.subList(2, 3));
        input = Pattern.compile("(?i).*\\bnot\\b.*");
        when(subcellularRepositoryMock
                .findByIdentifierRegexOrAccessionRegexOrContentRegexOrKeywordRegexOrSynonymsRegexOrNoteRegexOrDefinitionRegex(
                        refEq(input), refEq(input), refEq(input), refEq(input), refEq(input), refEq(input),
                        refEq(input))).thenReturn(data.subList(0, 1));
        Collection<Subcellular> retCol = subcellularService.findAllByKeyWords("tIp SL-9910 TIP man noT");
        assertNotNull(retCol);
        assertEquals(2, retCol.size());

        verify(subcellularRepositoryMock, times(4))
                .findByIdentifierRegexOrAccessionRegexOrContentRegexOrKeywordRegexOrSynonymsRegexOrNoteRegexOrDefinitionRegex(
                        any(), any(), any(), any(), any(), any(), any());
    }
}
