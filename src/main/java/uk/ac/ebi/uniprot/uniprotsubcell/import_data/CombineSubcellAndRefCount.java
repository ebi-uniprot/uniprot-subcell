package uk.ac.ebi.uniprot.uniprotsubcell.import_data;

import uk.ac.ebi.uniprot.uniprotsubcell.domains.Subcellular;
import uk.ac.ebi.uniprot.uniprotsubcell.dto.SubcellReferenceCount;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CombineSubcellAndRefCount {

    private static final Logger LOG = LoggerFactory.getLogger(CombineSubcellAndRefCount.class);

    public List<Subcellular> readFileImportAndCombine(String subcellFilePath, String referenceCountFilePath) {
        subcellFilePath = subcellFilePath == null ? "" : subcellFilePath.trim();
        referenceCountFilePath = referenceCountFilePath == null ? "" : referenceCountFilePath.trim();

        List<String> allLines = null;
        LOG.debug("File: {} to import subcell location data set ", subcellFilePath);
        try {
            allLines = Files.readAllLines(Paths.get(subcellFilePath));
        } catch (IOException e) {
            LOG.error("Exception Handle gracefully: Failed to read file {} ", subcellFilePath, e);
            allLines = Collections.emptyList();
        }
        LOG.debug("total {} lines found in file ", allLines.size());

        final ParseSubCellLines parser = new ParseSubCellLines();
        List<Subcellular> subcellList = parser.parseLines(allLines);
        LOG.info("total {} entries found in file ", subcellList.size());

        LOG.debug("File: {} to import subcell reference count ", referenceCountFilePath);
        final ParseReferenceCountLines refParser = new ParseReferenceCountLines();
        Collection<SubcellReferenceCount> referenceCountList;
        try {
            referenceCountList = refParser.parseLinesFromReader(new FileReader(referenceCountFilePath));
        } catch (IOException e) {
            LOG.error("Exception Handle gracefully: Failed to read file {} ", referenceCountFilePath, e);
            referenceCountList = Collections.emptyList();
        }
        LOG.info("total {} Reference count found in file ", referenceCountList.size());

        updateSubcellWithReferenceCount(subcellList, referenceCountList);

        return subcellList;
    }

    private void updateSubcellWithReferenceCount(List<Subcellular> subcellList, Collection<SubcellReferenceCount>
            referenceCountList) {
        // Loop on reference count list
        referenceCountList.forEach(
                // Find the subcell from subcell list
                rc -> subcellList.stream().filter(d -> d.getIdentifier().equalsIgnoreCase(rc.getIdentifier()))
                        .findFirst()
                        .ifPresent(
                                //subcell found from list
                                seubcell -> {
                                    //Updating subcell count from reference count object
                                    seubcell.setSwissProtCount(rc.getSwissProtCount());
                                    seubcell.setTremblCount(rc.getTremblCount());
                                }
                        )

        );
    }
}
