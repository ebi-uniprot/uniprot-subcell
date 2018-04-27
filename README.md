# uniprot-subcell
This is a REST API for [uniprot subcellular locations](https://www.uniprot.org/locations)

## Technologies
* Java 8
* Spring boot
* Embedded neo4j
* Maven

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

## Code Explanation
1. Main Class uk.ac.ebi.uniprot.uniprotsubcell.UniprotSubcellApplication
1. Single Controller for API uk.ac.ebi.uniprot.uniprotsubcell.controller.DefaultController
1. Controller interacting with service and service interacting with repository
1. Import/Parse file logic is in uk.ac.ebi.uniprot.uniprotsubcell.importData.ParseSubCellLines
