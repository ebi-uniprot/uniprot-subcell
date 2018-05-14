//Using the plural for packages with homogeneous contents and the singular for packages with heterogeneous contents.
package uk.ac.ebi.uniprot.uniprotsubcell;

import uk.ac.ebi.uniprot.uniprotsubcell.services.SubcellularService;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.io.support.PropertiesLoaderUtils;

@SpringBootApplication
public class UniprotSubcellApplication {

    private final static Logger LOG = LoggerFactory.getLogger(UniprotSubcellApplication.class);

    public static void main(String[] args) {

        // user wants to import new data, Delete existing database
        if (args.length >= 1) {
            deleteExistingDatabase();
        }

        ApplicationContext context = SpringApplication.run(UniprotSubcellApplication.class, args);

        // 1st parameter is to import the file
        if (args.length >= 1) {
            try {

                // Import and create new database
                context.getBean(SubcellularService.class).importEntriesFromFileIntoDb(args[0]);

                // Stop server if user just want to import data, using while creating docker image
                if (args.length >= 2 && "stopserver".equalsIgnoreCase(args[1])) {
                    ((ConfigurableApplicationContext) context).close();
                    System.exit(0);
                }
            } catch (IOException e) {
                LOG.error("import failed but REST api will continue serving");
                LOG.error("Import failed due to ", e);
            }
        }
    }

    private static void deleteExistingDatabase() {
        try {
            final Properties appProp = PropertiesLoaderUtils.loadAllProperties("application.properties");
            final String databaseUrl = appProp.getProperty("spring.data.neo4j.uri");

            Path rootPath = null;
            if (databaseUrl != null && !databaseUrl.isEmpty() && databaseUrl.startsWith("file")) {
                final String dbdir = databaseUrl.substring("file://".length());
                rootPath = Paths.get(dbdir);
            }

            // Start delete if only path exists
            if (rootPath != null && Files.exists(rootPath)) {
                Files.walk(rootPath, FileVisitOption.FOLLOW_LINKS)
                        .sorted(Comparator.reverseOrder())
                        .map(Path::toFile)
                        .forEach(File::delete);
            }

        } catch (IOException e) {
            LOG.error("Deleting old database failed, but ignoring it and starting spring context");
            LOG.error("delete database failed due to ", e);
        }
    }
}
