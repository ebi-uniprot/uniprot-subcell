package uk.ac.ebi.uniprot.uniprotsubcell.integration_test;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import uk.ac.ebi.uniprot.uniprotsubcell.domains.Location;
import uk.ac.ebi.uniprot.uniprotsubcell.domains.Subcellular;
import uk.ac.ebi.uniprot.uniprotsubcell.services.SubcellularService;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class DefaultControllerIT {

    private static boolean isRun = false;
    private static final String TEMP_FILE_NAME = "subcell-data-temp.txt";

    private ObjectMapper mapper;

    @Autowired
    private TestRestTemplate rest;

    @Autowired
    private SubcellularService service;

    @BeforeClass
    public static void downloadAndSaveFile() throws IOException {
        URL url = new URL(
                "ftp://ftp.uniprot.org/pub/databases/uniprot/current_release/knowledgebase/complete/docs/subcell.txt");
        URLConnection connection = url.openConnection();
        InputStream initialStream = connection.getInputStream();
        File targetFile = new File(TEMP_FILE_NAME);

        Files.copy(
                initialStream,
                targetFile.toPath(),
                StandardCopyOption.REPLACE_EXISTING);

        IOUtils.closeQuietly(initialStream);
    }

    @AfterClass
    public static void deleteFile() throws IOException {
        Files.deleteIfExists(new File(TEMP_FILE_NAME).toPath());
    }

    @Before
    public void setup() throws Exception {
        if (!isRun) {
            service.importEntriesFromFileIntoDb(new File(TEMP_FILE_NAME).getPath());
            mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            isRun = true;
        }
    }

    @Test
    public void testAccessionEndPoint() throws IOException {
        ResponseEntity<String> rawRes = rest.getForEntity("/accession/{accession}", String.class, "SL-0456");

        assertEquals("Status okay match", HttpStatus.OK, rawRes.getStatusCode());
        assertEquals("Content type check", MediaType.APPLICATION_JSON_UTF8, rawRes.getHeaders().getContentType());

        Subcellular response = mapper.readValue(rawRes.getBody(), Location.class);
        assertThat(response.getAccession()).isEqualTo("SL-0456");
        assertThat(response.getCategory()).isEqualTo("Cellular component");
    }

    @Test
    public void testAccessionAllEndPoint() throws IOException {
        Map<String, String> params = new HashMap<>();
        params.put("identifier", "inner membrane");
        ResponseEntity<String> rawRes = rest.getForEntity("/identifier/all/{identifier}", String.class, params);

        assertEquals("Status okay match", HttpStatus.OK, rawRes.getStatusCode());
        assertEquals("Content type check", MediaType.APPLICATION_JSON_UTF8, rawRes.getHeaders().getContentType());

        List<Subcellular> retCol = mapper.readValue(rawRes.getBody(), new TypeReference<List<Subcellular>>() {});
        assertEquals(15, retCol.size());
    }

    @Test
    public void testSearchEndPoint() throws IOException {
        ResponseEntity<String> rawRes = rest.getForEntity("/search/{keywords}", String.class, "Membrane iNNer");

        assertEquals("Status okay match", HttpStatus.OK, rawRes.getStatusCode());
        assertEquals("Content type check", MediaType.APPLICATION_JSON_UTF8, rawRes.getHeaders().getContentType());

        Subcellular[] retArr = mapper.readValue(rawRes.getBody(), Subcellular[].class);
        assertEquals(378, retArr.length);
    }
}
