### Build local 
docker build -f docker/Dockerfile -t subcell_api https://github.com/ebi-uniprot/uniprot-subcell.git

### Multi-build Dockerfile
1. complie source code and create target jar
1. copy jar file, get datafile -> import into database and create entry point

### Actions
1. Above command let latest code from repo
2. Complie it
3. Get current lastest datafile from EBI file server for subcell location
4. create database and import data in to database from file
5. expose port 8080 inside container to listen and entertain user request

### Clean up
> If you are uing above command to build image locally, because of multi build it will create and extra image of size aprox 1 GB with repository none. You can optionally delete that image. One good way to check your images before and after run of above command

Need any help regarding git commands see [git](https://github.com/rizwan-ishtiaq/wiki/blob/master/commands/docker.txt) for quick reference.
