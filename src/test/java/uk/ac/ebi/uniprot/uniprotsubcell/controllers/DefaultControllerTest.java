package uk.ac.ebi.uniprot.uniprotsubcell.controllers;

import uk.ac.ebi.uniprot.uniprotsubcell.controllers.DefaultController;
import uk.ac.ebi.uniprot.uniprotsubcell.domains.Location;
import uk.ac.ebi.uniprot.uniprotsubcell.domains.Orientation;
import uk.ac.ebi.uniprot.uniprotsubcell.domains.Subcellular;
import uk.ac.ebi.uniprot.uniprotsubcell.domains.Topology;
import uk.ac.ebi.uniprot.uniprotsubcell.services.SubcellularService;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Arrays;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@WebMvcTest(DefaultController.class)
public class DefaultControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SubcellularService subcellularService;

    @Autowired
    private ObjectMapper mapper;

    @Before
    public void setup() {
        mapper = mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        final Subcellular l = new Location("Cell tip", "SL-0456", "Cell tip");
        final Subcellular o = new Orientation("Cytoplasmic side", "SL-9910", "Cytoplasmic side");
        final Subcellular t = new Topology("Lipid-anchor", "SL-9901", "Lipid-anchor");

        given(this.subcellularService.findByAccession("SL-0456")).willReturn(l);

        given(this.subcellularService.findByIdentifier("Cytoplasmic side")).willReturn(o);

        given(this.subcellularService.findByIdentifierIgnoreCaseLike("singleWord"))
                .willReturn(Arrays.asList(l, o, t));
    }

    @Test
    public void testAccessionEndPoint() throws Exception {

        MvcResult rawRes = mockMvc.perform(get("/accession/{accession}", "SL-0456"))
                .andExpect(status().isOk())
                .andReturn();

        Subcellular response = mapper.readValue(rawRes.getResponse().getContentAsString(), Location.class);
        assertThat(response.getAccession()).isEqualTo("SL-0456");
        assertThat(response.getCategory()).isEqualTo("Cellular component");
    }

    @Test
    public void testIdentifierEndPoint() throws Exception {

        MvcResult rawRes = mockMvc.perform(get("/identifier/{identifier}", "Cytoplasmic side"))
                .andExpect(status().isOk())
                .andReturn();

        Subcellular response = mapper.readValue(rawRes.getResponse().getContentAsString(), Orientation.class);
        assertThat(response.getIdentifier()).isEqualTo("Cytoplasmic side");
        assertThat(response.getCategory()).isEqualTo("Orientation");
    }

    @Test
    public void testIdentifierAllEndPoint() throws Exception {

        MvcResult rawRes = mockMvc.perform(get("/identifier/all/{singleWord}", "singleWord"))
                .andExpect(status().isOk())
                .andReturn();

        List<Subcellular> retList = mapper.readValue(rawRes.getResponse().getContentAsString(),
                mapper.getTypeFactory().constructCollectionType(List.class, Subcellular.class));
        assertThat(retList.size()).isEqualTo(3);
        List<String> accessions = Arrays.asList("SL-0456", "SL-9910", "SL-9901");
        assertThat(retList.get(0).getAccession()).isIn(accessions);
        assertThat(retList.get(1).getAccession()).isIn(accessions);
        assertThat(retList.get(2).getAccession()).isIn(accessions);

        List<String> caategories = Arrays.asList("Cellular component", "Orientation", "Topology");
        JsonNode tree = mapper.readTree(rawRes.getResponse().getContentAsString());
        assertThat(tree.get(0).at("/category").textValue()).isIn(caategories);
        assertThat(tree.get(1).at("/category").textValue()).isIn(caategories);
        assertThat(tree.get(2).at("/category").textValue()).isIn(caategories);
    }
}
