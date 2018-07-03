package uk.ac.ebi.uniprot.uniprotsubcell.import_data;

import uk.ac.ebi.uniprot.uniprotsubcell.domains.Subcellular;

import java.util.List;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;

public class CombineSubcellAndRefCountTest {

    private static final String IDENTIFIER_PROP = "identifier";
    private static final String SWISS_PROT_COUNT_PROP = "swissProtCount";
    private static final String TREMBL_COUNT_PROP = "tremblCount";
    private final CombineSubcellAndRefCount obj = new CombineSubcellAndRefCount();

    @Test
    public void shouldIgnoreExceptionsWhenSubcellAndReferenceFilesNotExist() {
        final List<Subcellular> retList = obj.readFileImportAndCombine("abc", "def");
        assertNotNull(retList);
        assertThat(retList).hasSize(0);
    }

    @Test
    public void shouldReturnNothingWhenSubcellFileMissingAndReferenceFileAvailable() {
        final String referenceCountFilePath = ClassLoader.getSystemResource("sample-reference.txt").getPath();
        final List<Subcellular> retList = obj.readFileImportAndCombine(null, referenceCountFilePath);
        assertNotNull(retList);
        assertThat(retList).hasSize(0);
    }

    @Test
    public void countsShouldZeroWhenRefCountFileNotValid() {
        final String subcellFilePath = ClassLoader.getSystemResource("sample-data.txt").getPath();
        final List<Subcellular> retList = obj.readFileImportAndCombine(subcellFilePath, "tmp");

        assertNotNull(retList);
        assertThat(retList).hasSize(14);
        assertThat(retList.get(0)).extracting(IDENTIFIER_PROP, SWISS_PROT_COUNT_PROP, TREMBL_COUNT_PROP)
                .containsExactly("Acrosome inner membrane", 0, 0);
        assertThat(retList.get(3)).extracting(IDENTIFIER_PROP, SWISS_PROT_COUNT_PROP, TREMBL_COUNT_PROP)
                .containsExactly("Membrane", 0, 0);

    }

    @Test
    public void countShouldValidWhenRefFileContainEntires() {
        final String diseaseFilePath = ClassLoader.getSystemResource("sample-data.txt").getPath();
        final String referenceCountFilePath = ClassLoader.getSystemResource("sample-reference.txt").getPath();
        final List<Subcellular> retList = obj.readFileImportAndCombine(diseaseFilePath, referenceCountFilePath);

        assertNotNull(retList);
        assertThat(retList).hasSize(14);
        assertThat(retList.get(1)).extracting(IDENTIFIER_PROP, SWISS_PROT_COUNT_PROP, TREMBL_COUNT_PROP)
                .containsExactly("Cell tip", 1, 3);
        assertThat(retList.get(2)).extracting(IDENTIFIER_PROP, SWISS_PROT_COUNT_PROP, TREMBL_COUNT_PROP)
                .containsExactly("Acrosome membrane", 2, 4);
    }
}
