package uk.ac.ebi.uniprot.uniprotsubcell.import_data;

import uk.ac.ebi.uniprot.uniprotsubcell.domains.Subcellular;
import uk.ac.ebi.uniprot.uniprotsubcell.import_data.ParseSubCellLines;

import java.util.Arrays;
import java.util.List;
import org.junit.Test;

import static org.junit.Assert.*;

public class ParseSubCellLinesTest {

    private final ParseSubCellLines obj = new ParseSubCellLines();

    @Test
    public void testSingleSubCellEntry() {
        List<String> input =
                Arrays.asList("_______________________________",
                        "ID   Cell tip.",
                        "AC   SL-0456",
                        "DE   The region at either end of the longest axis of a cylindrical or",
                        "DE   elongated cell, where polarized growth may occur.",
                        "SL   Cell tip.",
                        "GO   GO:0051286; cell tip",
                        "//");
        List<Subcellular> retList = obj.parseLines(input);

        assertEquals("Size shoud be exactly 1", 1,retList.size());
        Subcellular retObj = retList.get(0);
        assertEquals("Identifier check", "Cell tip", retObj.getIdentifier());
        assertEquals("Accession test", "SL-0456", retObj.getAccession());
        assertEquals("Definition match",
                "The region at either end of the longest axis of a cylindrical or elongated cell, where polarized growth may occur.",
                retObj.getDefinition());
        assertEquals("Content of subc. loc. lines match", "Cell tip", retObj.getContent());
        assertEquals("Type match", "Cellular component", retObj.getCategory());

        assertEquals("Size of GO mapping shoud be exactly 1", 1,retObj.getGoMappings().size());
        assertEquals("GO id check", "GO:0051286", retObj.getGoMappings().get(0).getGoId());
        assertEquals("GO definition check", "cell tip", retObj.getGoMappings().get(0).getDefinition());
    }

    @Test
    public void testRelationShip() {
        List<String> input =
                Arrays.asList("---------------------------------------------------------------------------",
                        "        UniProt - Swiss-Prot Protein Knowledgebase",
                        "        SIB Swiss Institute of Bioinformatics; Geneva, Switzerland",
                        "        European Bioinformatics Institute (EBI); Hinxton, United Kingdom",
                        "        Protein Information Resource (PIR); Washington DC, USA",
                        "---------------------------------------------------------------------------",
                        "",
                        "Description: Controlled vocabulary of subcellular locations and membrane",
                        "             topologies and orientations",
                        "Name:        subcell.txt",
                        "Release:     2018_03 of 28-Mar-2018",
                        "",
                        "---------------------------------------------------------------------------",
                        "",
                        " This document lists the UniProtKB controlled vocabulary used for the",
                        " subcellular locations (including the membrane topologies and orientations",
                        " terms) in the SUBCELLULAR LOCATION lines.",
                        "",
                        " It provides definitions of the terms as well as other relevant information",
                        " in the following format:",
                        "",
                        "  ---------  -------------------------------   ----------------------------",
                        "  Line code  Content                           Occurrence in an entry",
                        "  ---------  -------------------------------   ----------------------------",
                        "  ID         Identifier (location)             Once; starts an entry",
                        "  IT         Identifier (topology)             Once; starts a 'topology' entry",
                        "  IO         Identifier (orientation)          Once; starts an 'orientation' entry",
                        "  AC         Accession (SL-xxxx)               Once",
                        "  DE         Definition                        Once or more",
                        "  SY         Synonyms                          Optional; Once or more",
                        "  SL         Content of subc. loc. lines       Once",
                        "  HI         Hierarchy ('is-a')                Optional; Once or more",
                        "  HP         Hierarchy ('part-of')             Optional; Once or more",
                        "  KW         Associated keyword (accession)    Optional; Once",
                        "  GO         Gene ontology (GO) mapping        Optional; Once or more",
                        "  AN         Annotation note                   Optional; Once or more",
                        "  RX         Interesting references            Optional; Once or more",
                        "  WW         Interesting links                 Optional; Once or more",
                        "  //         Terminator                        Once; ends an entry",
                        "",
                        "",
                        "AN   Next free AC: SL-0501",
                        "___________________________________________________________________________",
                        "ID   Acidocalcisome membrane.",
                        "AC   SL-0003",
                        "DE   The membrane of an acidocalcisome.",
                        "SY   Acidocalcisomal membrane.",
                        "SL   Acidocalcisome membrane.",
                        "HI   Membrane.",
                        "HP   Acidocalcisome.",
                        "HP   Endomembrane system.",
                        "GO   GO:0033102; acidocalcisome membrane",
                        "//",
                        "ID   Endomembrane system.",
                        "AC   SL-0147",
                        "DE   A collection of membranous structures involved in transport within the",
                        "DE   cell. The main components of the endomembrane system are endoplasmic",
                        "DE   reticulum, Golgi apparatus, vesicles and cell membrane and nuclear",
                        "DE   envelope. The endomembrane system does not include the membranes of",
                        "DE   mitochondria or plastids.",
                        "SY   Endomembrane.",
                        "SL   Endomembrane system.",
                        "HI   Membrane.",
                        "GO   GO:0012505; endomembrane system",
                        "AN   Try to use a child/narrower/more specific term instead",
                        "//",
                        "ID   Acidocalcisome.",
                        "AC   SL-0002",
                        "DE   The acidocalcisome is an electron-dense acidic organelle which",
                        "DE   contains a matrix of pyrophosphate and polyphosphates with bound",
                        "DE   calcium and other cations. Its limiting membrane possesses a number of",
                        "DE   pumps and exchangers for the uptake and release of these elements. The",
                        "DE   acidocalcisome does not belong to the endocytic pathway and may",
                        "DE   represent a branch of the secretory pathway in trypanosomatids and",
                        "DE   apicomplexan parasites. The acidocalcisome is possibly involved in",
                        "DE   polyphosphate and cation storage and in adaptation of these",
                        "DE   microoganisms to environmental stress.",
                        "SL   Acidocalcisome.",
                        "GO   GO:0020022; acidocalcisome",
                        "RX   PubMed=15738951; DOI=10.1038/nrmicro1097;",
                        "//",
                        "ID   Membrane.",
                        "AC   SL-0162",
                        "DE   A membrane is a lipid bilayer which surrounds enclosed spaces and",
                        "DE   compartments. This selectively permeable structure is essential for",
                        "DE   effective separation of a cell or organelle from its surroundings.",
                        "DE   Membranes are composed of various types of molecules such as",
                        "DE   phospholipids, integral membrane proteins, peripheral proteins,",
                        "DE   glycoproteins, glycolipids, etc. The relative amounts of these",
                        "DE   components as well as the types of lipids are non-randomly distributed",
                        "DE   from membrane to membrane as well as between the two leaflets of a",
                        "DE   membrane.",
                        "SL   Membrane.",
                        "KW   KW-0472",
                        "GO   GO:0016020; membrane",
                        "//",
                        "-----------------------------------------------------------------------",
                        "Copyrighted by the UniProt Consortium, see https://www.uniprot.org/terms",
                        "Distributed under the Creative Commons Attribution-NoDerivs License",
                        "-----------------------------------------------------------------------");

        List<Subcellular> retList = obj.parseLines(input);
        assertEquals("Return list size ", 4, retList.size());
        assertEquals("Is a Hierarchy of parent size", 1, retList.get(0).getIsA().size());
        assertEquals("Part of Hierarchy of parent size", 2, retList.get(0).getPartOf().size());

        assertEquals("Is a Hierarchy of parent", "SL-0162", retList.get(0).getIsA().get(0).getAccession());
        assertEquals("Part of Hierarchy of parent", "Endomembrane system",
                retList.get(0).getPartOf().get(1).getIdentifier());
    }

}
