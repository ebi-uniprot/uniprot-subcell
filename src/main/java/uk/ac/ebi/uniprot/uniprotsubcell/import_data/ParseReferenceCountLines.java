package uk.ac.ebi.uniprot.uniprotsubcell.import_data;

import uk.ac.ebi.uniprot.uniprotsubcell.dto.SubcellReferenceCount;

import java.io.IOException;
import java.io.Reader;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ParseReferenceCountLines {
    private static final int SWISSPROT = 0;
    private static final int TREMBL = 1;
    private static final Logger LOG = LoggerFactory.getLogger(ParseReferenceCountLines.class);

    public Collection<SubcellReferenceCount> parseLinesFromReader(final Reader reader) throws IOException {

        //Default is RFC4180
        final CSVFormat format = CSVFormat.DEFAULT.withCommentMarker('#');
        //initialize the CSVParser object
        final CSVParser parser = new CSVParser(reader, format);

        final Map<String, SubcellReferenceCount> tempMap = new HashMap<>(550, 1);

        for (CSVRecord record : parser) {
            if (isValidRecord(record)) {
                final String identifier = record.get(0).trim();
                final int type = Integer.parseInt(record.get(1).trim());
                final int count = Integer.parseInt(record.get(2).trim());

                tempMap.merge(identifier, getSubcellRefCountObject(identifier, type, count), this::merge);
            } else {
                LOG.info("Ignoring Record/line {} while parsing", record);
            }
        }

        return tempMap.values();
    }

    private SubcellReferenceCount merge(SubcellReferenceCount old, SubcellReferenceCount val) {
        final SubcellReferenceCount retObj = new SubcellReferenceCount(val.getIdentifier());
        retObj.setSwissProtCount(Math.max(old.getSwissProtCount(), val.getSwissProtCount()));
        retObj.setTremblCount(Math.max(old.getTremblCount(), val.getTremblCount()));
        return retObj;
    }

    private SubcellReferenceCount getSubcellRefCountObject(final String identifier, final int type, final int count) {
        final SubcellReferenceCount retObj = new SubcellReferenceCount(identifier);
        if (type == SWISSPROT) {
            retObj.setSwissProtCount(count);
        } else if (type == TREMBL) {
            retObj.setTremblCount(count);
        }
        return retObj;
    }

    private boolean isValidRecord(CSVRecord record) {

        if (record.size() < 3) {
            return false;
        }

        if (record.get(0).trim().isEmpty()) {
            return false;
        } else if (record.get(1).trim().isEmpty() || !("0".equals(record.get(1).trim()) || "1".equals(record.get(1)
                .trim()))) {
            return false;
        } else {
            try {
                Integer.parseInt(record.get(2).trim());
            } catch (NumberFormatException e) {
                return false;
            }
        }

        return true;
    }
}
