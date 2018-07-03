package uk.ac.ebi.uniprot.uniprotsubcell.import_data;

import uk.ac.ebi.uniprot.uniprotsubcell.dto.SubcellReferenceCount;

import java.io.IOException;
import java.io.StringReader;
import java.util.Arrays;
import java.util.Collection;
import org.assertj.core.groups.Tuple;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;

public class ParseReferenceCountLinesTest {

    private static final String IDENTIFIER_PROP = "identifier";
    private static final String SWISS_PROT_COUNT_PROP = "swissProtCount";
    private static final String TREMBL_COUNT_PROP = "tremblCount";
    private static final ParseReferenceCountLines obj = new ParseReferenceCountLines();

    @Test
    public void emptyStringAsInputLineParse() throws IOException {
        final Collection<SubcellReferenceCount> retCol = obj.parseLinesFromReader(new StringReader(""));
        assertTrue(retCol.isEmpty());
    }

    @Test
    public void spacesInLineMustIgnoreByParser() throws IOException {
        final Collection<SubcellReferenceCount> retCol = obj.parseLinesFromReader(new StringReader("        "));
        assertTrue(retCol.isEmpty());
    }

    @Test
    public void lineStartingWithHashIgnoreByParser() throws IOException {
        final Collection<SubcellReferenceCount> retCol =
                obj.parseLinesFromReader(new StringReader("# hello this is test"));
        assertTrue(retCol.isEmpty());
    }

    @Test
    public void lineStartingWithSpaceFollowedByHashIgnoreByParser() throws IOException {
        final Collection<SubcellReferenceCount> retCol =
                obj.parseLinesFromReader(new StringReader("      #    any line"));
        assertTrue(retCol.isEmpty());
    }

    @Test(expected = IllegalArgumentException.class)
    public void passingNullToParser() throws IOException {
        obj.parseLinesFromReader(null);
    }

    @Test
    public void lessThenThreeTokensShouldBeIgnore() throws IOException {
        final Collection<SubcellReferenceCount> retCol = obj.parseLinesFromReader(new StringReader("one\none,two"));
        assertTrue(retCol.isEmpty());
    }

    @Test
    public void firstTokenAlwaysBeString() throws IOException {
        final Collection<SubcellReferenceCount> retCol = obj.parseLinesFromReader(new StringReader("1,0,0"));
        assertFalse(retCol.isEmpty());
        assertEquals("1", retCol.iterator().next().getIdentifier());
    }

    @Test
    public void firstTokenEmptyNotValid() throws IOException {
        final Collection<SubcellReferenceCount> retCol = obj.parseLinesFromReader(new StringReader(",0,0"));
        assertTrue(retCol.isEmpty());
    }

    @Test
    public void secondTokenShoudBeParseableAsInteger() throws IOException {
        final Collection<SubcellReferenceCount> retCol = obj.parseLinesFromReader(new StringReader("one,two,three"));
        assertTrue(retCol.isEmpty());
    }

    @Test
    public void secondTokenOtherThanZeroOrOneNotValid() throws IOException {
        final Collection<SubcellReferenceCount> retCol =
                obj.parseLinesFromReader(new StringReader("one,-1,three\none,2,three"));
        assertTrue(retCol.isEmpty());
    }

    @Test
    public void thirdTokenShoudBeParseableAsInteger() throws IOException {
        final Collection<SubcellReferenceCount> retCol =
                obj.parseLinesFromReader(new StringReader("one,0,three\none,0,"));
        assertTrue(retCol.isEmpty());
    }

    @Test
    public void emptyLinesShouldBeIgnore() throws IOException {
        final Collection<SubcellReferenceCount> retCol =
                obj.parseLinesFromReader(new StringReader("# this is header\n"));
        assertTrue(retCol.isEmpty());
    }

    @Test
    public void identifierWithCommasShouldReturnAsStingValue() throws IOException {
        final String input = "\"46,XX sex reversal 2\",0,0";
        final Collection<SubcellReferenceCount> retCol = obj.parseLinesFromReader(new StringReader(input));
        assertThat(retCol).extracting(IDENTIFIER_PROP).contains("46,XX sex reversal 2");
    }

    @Test
    public void identifierWithDoubleQoutesShouldReturnInStingValue() throws IOException {
        final String input = "sex \"reversal\" 2,0,0";
        final Collection<SubcellReferenceCount> retCol = obj.parseLinesFromReader(new StringReader(input));
        assertThat(retCol).extracting(IDENTIFIER_PROP).contains("sex \"reversal\" 2");
    }

    @Test
    public void validRecordWithSwissProtCount() throws IOException {
        final Collection<SubcellReferenceCount> retCol = obj.parseLinesFromReader(new StringReader("DI-04240,0,2098"));
        assertFalse(retCol.isEmpty());
        assertThat(retCol).extracting(SWISS_PROT_COUNT_PROP).contains(2098);
    }

    @Test
    public void validRecordWithTrEMBLCount() throws IOException {
        final Collection<SubcellReferenceCount> retCol =
                obj.parseLinesFromReader(new StringReader("DI-03673,1,118900"));
        assertFalse(retCol.isEmpty());
        assertThat(retCol).extracting(TREMBL_COUNT_PROP).contains(118900);
    }

    @Test
    public void validRecordMergeAndCount() throws IOException {
        final Collection<SubcellReferenceCount> retCol =
                obj.parseLinesFromReader(new StringReader("DI-00002,0,2098\nDI-00002,1,118900"));
        assertFalse(retCol.isEmpty());
        assertThat(retCol).hasSize(1).extracting(IDENTIFIER_PROP, SWISS_PROT_COUNT_PROP, TREMBL_COUNT_PROP)
                .contains(new Tuple("DI-00002", 2098, 118900));
    }
}
