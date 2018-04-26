package uk.ac.ebi.uniprot.uniprotsubcell;

import uk.ac.ebi.uniprot.uniprotsubcell.services.SubcellularService;

import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class UniprotSubcellApplication {

    private final static Logger LOG = LoggerFactory.getLogger(UniprotSubcellApplication.class);

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(UniprotSubcellApplication.class, args);

        // 1st parameter is to import the file
        if (args.length > 1) {
            try {
                context.getBean(SubcellularService.class).importEntriesFromFileIntoDb(args[0]);
            } catch (IOException e) {
                LOG.error("import failed but REST api will continue serving");
                LOG.error("Import failed due to ", e);
            }
        }
    }
}
