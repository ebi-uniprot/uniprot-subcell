package uk.ac.ebi.uniprot.uniprotsubcell.importData;

import uk.ac.ebi.uniprot.uniprotsubcell.domain.GeneOntology;
import uk.ac.ebi.uniprot.uniprotsubcell.domain.Location;
import uk.ac.ebi.uniprot.uniprotsubcell.domain.Orientation;
import uk.ac.ebi.uniprot.uniprotsubcell.domain.Subcellular;
import uk.ac.ebi.uniprot.uniprotsubcell.domain.Topology;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ParseSubCellLines {

    private final String SPLIT_SPACES = "   ";

    public List<Subcellular> parseLines(List<String> lines) {
        List<SubcellularFileEntry> rawList = convertLinesIntoInMemoryObjectList(lines);
        List<Subcellular> list = parseSubcellularFileEntryList(rawList);
        updateListWithRelationShips(list, rawList);
        return list;
    }

    private List<SubcellularFileEntry> convertLinesIntoInMemoryObjectList(List<String> lines) {
        // At the time of writing code there was 513 entries per line
        List<SubcellularFileEntry> retList = new ArrayList<>(550);

        int i = 0;

        // Ignore the header lines and information
        for (; i < lines.size(); i++) {
            String lineIgnore = lines.get(i);
            if (lineIgnore.startsWith("______"))
                break;
        }

        // Ignore underscore ___ line
        i++;

        // reached entries lines
        SubcellularFileEntry entry = new SubcellularFileEntry();

        // create in memory list of objects
        while (i < lines.size()) {
            String line = lines.get(i);

            // For terminating line no need to complete loop
            if (line.equals("//")) {
                retList.add(entry);
                entry = new SubcellularFileEntry();
                i++;
                continue;
            }

            String[] tokens = line.split(SPLIT_SPACES);
            switch (tokens[0]) {
                case "ID":
                    entry.ID = tokens[1];
                    break;
                case "IT":
                    entry.IT = tokens[1];
                    break;
                case "IO":
                    entry.IO = tokens[1];
                    break;
                case "AC":
                    entry.AC = tokens[1];
                    break;
                case "DE":
                    entry.DE.add(tokens[1]);
                    break;
                case "SY":
                    entry.SY.add(tokens[1]);
                    break;
                case "SL":
                    entry.SL = tokens[1];
                    break;
                case "HI":
                    entry.HI.add(tokens[1]);
                    break;
                case "HP":
                    entry.HP.add(tokens[1]);
                    break;
                case "KW":
                    entry.KW = tokens[1];
                    break;
                case "GO":
                    entry.GO.add(tokens[1]);
                    break;
                case "AN":
                    entry.AN.add(tokens[1]);
                    break;
                case "RX":
                    entry.RX.add(tokens[1]);
                    break;
                case "WW":
                    entry.WW.add(tokens[1]);
                    break;

            }

            // read and save next line
            i++;
        }
        return retList;
    }

    private List<Subcellular> parseSubcellularFileEntryList(List<SubcellularFileEntry> list) {
        return list.stream().map(this::parseSubcellularFileEntry).collect(Collectors.toList());
    }

    /**
     * In case properties (strings or list) are empty setting it null. OGM will not insert null properties in neo4j node
     * 
     * @param entry
     * @return
     */
    private Subcellular parseSubcellularFileEntry(SubcellularFileEntry entry) {
        Subcellular retObj;
        if (entry.ID != null)
            retObj = new Location(trimSpacesAndRemoveLastDot(entry.ID), entry.AC, trimSpacesAndRemoveLastDot(entry.SL));
        else if (entry.IT != null)
            retObj = new Topology(trimSpacesAndRemoveLastDot(entry.IT), entry.AC, trimSpacesAndRemoveLastDot(entry.SL));
        else
            retObj = new Orientation(trimSpacesAndRemoveLastDot(entry.IO), entry.AC,
                    trimSpacesAndRemoveLastDot(entry.SL));

        // definition
        String def = String.join(" ", entry.DE);
        retObj.setDefinition(def.isEmpty() ? null : def);

        // Keyword is a single string will null by default
        retObj.setKeyword(entry.KW);

        // Links
        retObj.setLinks(entry.WW.isEmpty() ? null : entry.WW);

        // Notes
        String note = entry.AN.stream().collect(Collectors.joining(" "));
        retObj.setNote(note.isEmpty() ? null : note);

        // Interesting references
        List<String> refList =
                entry.RX.stream().flatMap(s -> Arrays.asList(s.split(";")).stream()).collect(Collectors.toList());
        retObj.setReferences(refList.isEmpty() ? null : refList);

        // GoMapping
        List<GeneOntology> goList = entry.GO.stream().map(this::parseGeneOntology).collect(Collectors.toList());
        retObj.setGoMappings(goList.isEmpty() ? null : goList);

        // Synonyms
        List<String> synList =
                entry.SY.stream().flatMap(s -> Arrays.asList(s.split(";")).stream())
                        .map(this::trimSpacesAndRemoveLastDot)
                        .collect(Collectors.toList());
        retObj.setSynonyms(synList.isEmpty() ? null : synList);

        return retObj;
    }

    private void updateListWithRelationShips(List<Subcellular> list, List<SubcellularFileEntry> rawList) {
        for (SubcellularFileEntry raw : rawList) {

            if (!raw.HI.isEmpty() || !raw.HP.isEmpty()) {
                final String rawId = raw.ID != null ? raw.ID : raw.IT != null ? raw.IT : raw.IO;
                Subcellular target = findByIdentifier(list, rawId);
                if (!raw.HI.isEmpty()) {
                    target.setIsA(new ArrayList<>());
                    for (String id : raw.HI) {
                        target.getIsA().add(findByIdentifier(list, id));
                    }
                }
                if (!raw.HP.isEmpty()) {
                    target.setPartOf(new ArrayList<>());
                    for (String id : raw.HP) {
                        target.getPartOf().add(findByIdentifier(list, id));
                    }
                }
            }
        }
    }

    private Subcellular findByIdentifier(List<Subcellular> list, String id) {
        return list.stream().filter(
                s -> s.getIdentifier().equals(trimSpacesAndRemoveLastDot(id)))
                .findFirst().orElse(null);
    }

    private GeneOntology parseGeneOntology(String go) {
        String[] tokens = go.split(";");
        return new GeneOntology(tokens[0], tokens[1].trim());
    }

    private String trimSpacesAndRemoveLastDot(String str) {
        if (str == null)
            return null;
        str = str.trim();
        return str.endsWith(".") ? str.substring(0, str.length() - 1) : str;
    }

}

class SubcellularFileEntry {
    String ID;
    String IT;
    String IO;
    String AC;
    List<String> DE;
    List<String> SY;
    String SL;
    List<String> HI;
    List<String> HP;
    String KW;
    List<String> GO;
    List<String> AN;
    List<String> RX;
    List<String> WW;

    SubcellularFileEntry() {
        DE = new ArrayList<>();
        SY = new ArrayList<>();
        HI = new ArrayList<>();
        HP = new ArrayList<>();
        GO = new ArrayList<>();
        AN = new ArrayList<>();
        RX = new ArrayList<>();
        WW = new ArrayList<>();
    }
}
