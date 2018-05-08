# uniprot-subcell
This is a REST API for [uniprot subcellular locations](https://www.uniprot.org/locations). Standalone application, you need java8 and maven to startup. **This project is currently under development.**

## Technologies
* Java 8
* Spring boot 2.0.1
* Embedded neo4j 3
* Maven 3.5.2
* Junit 4.12
* Mockito 2.15
* jackson 2.9.5
* assertj 3.9.1

## Getting started
1. Download subcellular data file from ftp://ftp.uniprot.org/pub/databases/uniprot/current_release/knowledgebase/complete/docs/subcell.txt on local file system
1. Download/clone the code from github `git clone https://github.com/ebi-uniprot/uniprot-subcell.git`
1. Open file *uniprot-subcell/src/main/resources/application.properties* and change the value **spring.data.neo4j.uri=file://** with the path where you want to create your neo4j database 
1. Go to uniprot-subcell directory from terminal/command prompt
1. run command `mvn package`
1. For First time only (import data into database from txt file) run command `java -jar target/uniprot-subcell-0.0.1-SNAPSHOT.jar subcell.txt`
  1. **Note:** I have downloaded *subcell.txt* file in same directory. You have give the complete path of file if it is not in same directory
1. To start server second time (without import) use `java -jar target/uniprot-subcell-0.0.1-SNAPSHOT.jar`

## Endpoints
Endpoint | Description
-------- | -----------
http://localhost:8080/accession/SL-0004 | Return the single subcellular entry with all depth relationships exact match on accession=SL-0004
http://localhost:8080/identifier/Membrane | Return single subcellular entry with all depth relationships exact match on identifier=Membrane with case-sensitive
http://localhost:8080/identifier/all/MemBrAnE | Returns the collection of subcellulars of all the matching subcellulars which contains the "membrane" after ignoring case in identifiers. Return elements in list will resolve relationships at depth 1
http://localhost:8080/search/mEmbrance iNNer | Returns the unique collection of subcellulars of all the matching subcellulars which contains the "membrane" or "inner" after ignoring case in identifier or accession or content or keyword or synonyms or note or definition. Return elements in collection will contain relationships at depth level 1

## Code Explanation
1. Package name convention, using the plural for packages with homogeneous contents and the singular for packages with heterogeneous contents.
1. Main Class uk.ac.ebi.uniprot.uniprotsubcell.UniprotSubcellApplication
1. Single Controller for API uk.ac.ebi.uniprot.uniprotsubcell.controller.DefaultController
1. Controller interacting with service and service interacting with repository
1. Import/Parse file logic is in uk.ac.ebi.uniprot.uniprotsubcell.importData.ParseSubCellLines
