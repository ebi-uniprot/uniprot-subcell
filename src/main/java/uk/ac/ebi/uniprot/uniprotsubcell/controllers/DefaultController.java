package uk.ac.ebi.uniprot.uniprotsubcell.controllers;

import uk.ac.ebi.uniprot.uniprotsubcell.domains.Subcellular;
import uk.ac.ebi.uniprot.uniprotsubcell.dto.SubcellularAutoComplete;
import uk.ac.ebi.uniprot.uniprotsubcell.services.SubcellularService;

import java.util.Collection;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class DefaultController {
    private final SubcellularService subcellularService;

    public DefaultController(SubcellularService subcellularService) {
        this.subcellularService = subcellularService;
    }

    @GetMapping("accession/{accession}")
    public Subcellular findByAccession(@PathVariable String accession) {
        return subcellularService.findByAccession(accession);
    }

    @GetMapping("identifier/{identifier}")
    public Subcellular findByIdentifier(@PathVariable String identifier) {
        return subcellularService.findByIdentifier(identifier);
    }

    @GetMapping("identifier/all/{identifier}")
    public Collection<Subcellular> findByidentifierLikeIgnoreCase(@PathVariable String identifier) {
        return subcellularService.findByIdentifierIgnoreCaseLike(identifier);
    }

    @GetMapping("search/{wordSeperatedBySpace}")
    public Collection<Subcellular> search(@PathVariable String wordSeperatedBySpace) {
        return subcellularService.findAllByKeyWords(wordSeperatedBySpace);
    }

    @GetMapping("like/{wordCanBeSeperatedBySpace}")
    public Collection<SubcellularAutoComplete> autoComplete(@PathVariable String wordCanBeSeperatedBySpace, Integer size) {
        return subcellularService.autoCompleteSearch(wordCanBeSeperatedBySpace, size);
    }

}
