package uk.ac.ebi.uniprot.uniprotsubcell.services;

import uk.ac.ebi.uniprot.uniprotsubcell.domains.Subcellular;
import uk.ac.ebi.uniprot.uniprotsubcell.import_data.ParseSubCellLines;
import uk.ac.ebi.uniprot.uniprotsubcell.repositories.SubcellularRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SubcellularService {
    private static final Logger LOG = LoggerFactory.getLogger(SubcellularService.class);

    private final SubcellularRepository subcellularRepository;

    public SubcellularService(SubcellularRepository subcellularRepository) {
        this.subcellularRepository = subcellularRepository;
    }

    @Transactional
    public void importEntriesFromFileIntoDb(final String filePath) throws IOException {
        LOG.debug("File: {} to import data set ", filePath);
        final ParseSubCellLines parser = new ParseSubCellLines();
        List<String> allLines = Files.readAllLines(Paths.get(filePath));
        LOG.debug("total {} lines found in file ", allLines.size());
        List<Subcellular> subCellList = parser.parseLines(allLines);
        LOG.info("total {} entries found in file ", subCellList.size());
        LOG.debug("saving entries into file");
        subcellularRepository.saveAll(subCellList);
        LOG.info("Import finished and entries saved into database successfully");
    }

    @Transactional(readOnly = true)
    public Subcellular findByAccession(final String accession) {
        List<Subcellular> result = subcellularRepository.findByAccession(accession);
        return result.isEmpty() ? null : result.get(0);
    }

    @Transactional(readOnly = true)
    public Subcellular findByIdentifier(final String identifier) {
        List<Subcellular> result = subcellularRepository.findByIdentifier(identifier);
        return result.isEmpty() ? null : result.get(0);
    }

    @Transactional(readOnly = true)
    public Collection<Subcellular> findByIdentifierIgnoreCaseLike(final String identifier) {
        return subcellularRepository.findByIdentifierIgnoreCaseLike("*" + identifier + "*");
    }

    @Transactional(readOnly = true)
    public Collection<Subcellular> findAllByKeyWords(String input) {
        Set<String> words =
                Stream.of(input.split("\\s+")).map(s -> "*" + s.toLowerCase() + "*").collect(Collectors.toSet());
        // Database will be embedded so we can query multiple times with minimum performance hit
        // We could build dynamic query to save DB hits, but that will increase code also load on DB to scan for huge
        // set
        return words.stream().flatMap(i -> subcellularRepository
                .findByIdentifierIgnoreCaseLikeOrAccessionIgnoreCaseLikeOrContentIgnoreCaseLikeOrKeywordIgnoreCaseLikeOrSynonymsIgnoreCaseLikeOrNoteIgnoreCaseLikeOrDefinitionIgnoreCaseLike(
                        i, i, i, i, i, i, i)
                .stream()).collect(Collectors.toSet());
    }
}
