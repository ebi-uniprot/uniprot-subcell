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
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.stream.Stream;
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

        final List<String> argList =
                Stream.of(args).filter(s -> !s.equalsIgnoreCase("--stopserver")).collect(Collectors.toList());

        // user wants to import new data, Delete existing database
        if (!argList.isEmpty()) {
            deleteExistingDatabase();
        }

        final ApplicationContext context = SpringApplication.run(UniprotSubcellApplication.class, args);
        final SubcellularService subService = context.getBean(SubcellularService.class);

        // Import only subcell and create new database
        if (argList.size() == 1) {
            // 1st parameter is to import the subcell file
            subService.importSubcellEntriesFromFileIntoDb(argList.get(0));
        }

        // Import subcell, reference count and create new database
        if (argList.size() > 1) {
            //1st file is diseases, 2nd is reference
            subService.importSubcellAndReferenceCountFromFilesIntoDb(argList.get(0), argList.get(1));
        }

        // Stop server if user just want to import data, using while creating docker image
        if (Stream.of(args).anyMatch(s -> s.equalsIgnoreCase("--stopserver"))) {
            ((ConfigurableApplicationContext) context).close();
            System.exit(0);
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
                LOG.info("Deleting Database from {}", rootPath);
                try (Stream<Path> paths = Files.walk(rootPath, FileVisitOption.FOLLOW_LINKS)) {
                    paths.sorted(Comparator.reverseOrder())
                            .map(Path::toFile)
                            .forEach(File::delete);
                }
            }

        } catch (IOException e) {
            LOG.error("Deleting old database failed, but ignoring it and starting spring context");
            LOG.error("delete database failed due to ", e);
        }
    }
}
