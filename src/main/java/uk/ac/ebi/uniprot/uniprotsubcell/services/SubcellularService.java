package uk.ac.ebi.uniprot.uniprotsubcell.services;

import uk.ac.ebi.uniprot.uniprotsubcell.domains.Subcellular;
import uk.ac.ebi.uniprot.uniprotsubcell.dto.SubcellularAutoComplete;
import uk.ac.ebi.uniprot.uniprotsubcell.import_data.CombineSubcellAndRefCount;
import uk.ac.ebi.uniprot.uniprotsubcell.import_data.ParseSubCellLines;
import uk.ac.ebi.uniprot.uniprotsubcell.repositories.SubcellularRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static java.util.regex.Pattern.compile;

@Service
public class SubcellularService {
    private static final Logger LOG = LoggerFactory.getLogger(SubcellularService.class);

    private final SubcellularRepository subcellularRepository;

    public SubcellularService(SubcellularRepository subcellularRepository) {
        this.subcellularRepository = subcellularRepository;
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
    public Collection<Subcellular> findByIdentifierIgnoreCaseLike(String identifier) {
        identifier = identifier == null ? "" : identifier.trim();
        // Bug #DATAGRAPH-1092 in spring data can't use following
        // Pattern.compile("(?i).*\\b"+identifier.trim()+"\\b.*", Pattern.CASE_INSENSITIVE);
        Pattern p = Pattern.compile("(?i).*\\b" + identifier + "\\b.*");

        return subcellularRepository.findByIdentifierRegex(p);
    }

    @Transactional(readOnly = true)
    public Collection<Subcellular> findAllByKeyWords(String input) {
        input = input == null ? "" : input.trim();

        //Java don't override equals for Pattern
        final Comparator<Pattern> comp = (p1, p2) -> p1.pattern().compareTo(p2.pattern()) + (p1.flags() - p2.flags());

        //Make only unique quries for neo4j
        final Set<Pattern> words =
                Stream.of(input.split("\\s+")).map(String::toLowerCase).map(s -> compile("(?i).*\\b" + s + "\\b.*"))
                        .collect(Collectors.toCollection(() -> new TreeSet<>(comp)));

        //spring data neo4j return relationships as part of collection need to filter it
        final Set<Pattern> javaPatterns = Stream.of(input.split("\\s+")).map(String::toLowerCase)
                .map(s -> compile("\\b" + s + "\\b", Pattern.CASE_INSENSITIVE))
                .collect(Collectors.toCollection(() -> new TreeSet<>(comp)));

        // Database will be embedded so we can query multiple times with minimum performance hit
        // We could build dynamic query to save DB hits, but that will increase code also load on DB to scan for huge
        // set
        return words.stream().flatMap(i -> subcellularRepository
                .findByIdentifierRegexOrAccessionRegexOrContentRegexOrKeywordRegexOrSynonymsRegexOrNoteRegexOrDefinitionRegex(
                        i, i, i, i, i, i, i).stream()).filter(s -> filterRelativeSearch(s, javaPatterns))
                .collect(Collectors.toSet());
    }

    private boolean filterRelativeSearch(Subcellular s, Set<Pattern> patterns) {
        for (Pattern p : patterns) {
            if (stringPatternMatch(s.getIdentifier(), p) || stringPatternMatch(s.getAccession(), p) ||
                    stringPatternMatch(s.getContent(), p) || stringPatternMatch(s.getDefinition(), p) ||
                    stringPatternMatch(s.getKeyword(), p) || stringPatternMatch(s.getSynonyms(), p) ||
                    stringPatternMatch(s.getNote(), p)) {
                return true;
            }
        }
        return false;
    }

    private boolean stringPatternMatch(String input, Pattern p) {
        if (p == null) {
            return input == null;
        }
        input = input == null ? "" : input;
        return p.matcher(input).find();
    }

    private boolean stringPatternMatch(List<String> input, Pattern p) {
        final String s = Optional.ofNullable(input).map(Object::toString).orElse(null);
        return stringPatternMatch(s, p);
    }

    public void importSubcellEntriesFromFileIntoDb(String pathToSubcellFile) {
        importSubcellAndReferenceCountFromFilesIntoDb(pathToSubcellFile, "reference count file not given");
    }

    @Transactional
    public void importSubcellAndReferenceCountFromFilesIntoDb(String pathToSubcellFile, String pathToRefCountFile) {
        CombineSubcellAndRefCount obj = new CombineSubcellAndRefCount();
        final List<Subcellular> subCellList = obj.readFileImportAndCombine(pathToSubcellFile, pathToRefCountFile);
        subcellularRepository.saveAll(subCellList);
        LOG.info("{} SubCell locations saved into database", subCellList.size());
    }

    public List<SubcellularAutoComplete> autoCompleteSearch(String search, Integer returnSize) {
        final int limited =
                returnSize == null || returnSize == 0 ? 10 : returnSize < 0 ? Integer.MAX_VALUE : returnSize;
        search = search == null ? "" : search.trim();
        search = "*" + search + "*";
        final Pageable pageable = PageRequest.of(0, limited);
        return subcellularRepository.findProjectedByIdentifierIgnoreCaseLike(search, pageable).getContent();
    }
}
