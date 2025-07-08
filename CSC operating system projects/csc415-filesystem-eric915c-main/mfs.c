/***************************************************************
* Class: CSC-415-01 Spring 2024
* Name: Tsai Hsin Ying, Hsueh-Ta Lu, Po-Han Chen, Nathan Lee
* Student IDs: 923503916, 923409640, 923446482, 923407911
* GitHub-Name: Golden1018, darren816, eric915c, nlty0122
* Group-Name: RCJ
* Project: Basic File System
*
* File: mfs.c
*
* Description: The primary purpose of this file is to provide
* an interface for interacting with the file system, including 
* creating, deleting, and navigating through directories,
* as well as performing operations on files such as creating, 
* deleting, and checking their attributes. 
* 
*
**************************************************************/

#include "mfs.h"
#include "Directory_Entry.c"
#include "fsLow.h"


fdDir * inodes;

char requestedFilePath[255];
int currentDirectoryPathArraySize = 0;
int requestedFilePathArraySize = 0;
char requestedFilePathArray[10][255];
char currentDirectoryPathArray[10][255];


vcb *mainvcb;


char** parse(const char *pathname){
    dir_entry *tempDir = malloc(sizeof(dir_entry) * NUMBER_OF_DE);
    //create a temporary directory to store each directory that will be accessed during parse
    char pathCopy[256];
    strcpy(pathCopy, pathname);//copy pathname into pathCopy
    char **tokens = malloc(50 * sizeof(char*));//allocate some space for the parsed values
    char *token;
    int i = 1;
    char *delim = "/";
    token = strtok(pathCopy, delim);
    do{//separate the pathname into paths
        tokens[i] = token;
        i++;
        token = strtok(NULL, delim);
    }while(token != NULL);
    tokens[i] = '\0';
    if(tokens[0] == "abs"){//check if path is absolue
        LBAread(tempDir, mainvcb->rootsize/mainvcb->sizeofBlock, mainvcb->rootDir);
        //load the directory from the root since it is an absolute path
    }else{
        ("-------User Pased relative path------------");
        int blocksNeeded = ((sizeof(dir_entry) * NUMBER_OF_DE)/mainvcb->sizeofBlock)+1;
        LBAread(tempDir, blocksNeeded, currentDirLocation);
        //load the directory from the current directory location since path is relative
    }
    int count = 1;
    int foundDe = 1;//flag to signal if path is found or not
    while(tokens[count] != NULL){//iterate through each path
        int deindex = 0;
        do{//iterate through the current directory entry looking for the path
            if(strcmp(tempDir[deindex].name, tokens[count])){
            //if the name of the directory entry matches the name of the path that we are 
            //lookign for
            //then load the directory entry
                foundDe = 0;
            // deLocation->deAddress = cofused about how to get the adress(number in memory)
            // deLocation->deIndex = deindex;
            //return n-1 DE
            //index in the DE

            }
            deindex++;
        }while (deindex < NUMBER_OF_DE);
        if(foundDe == 1){//path not found
            printf("DirEntry not found");
            return NULL;
        }
        count++;
    }
    return NULL;
}

void parseFilePath(const char * pathname) {

    /* Clear previous value and count. */
    requestedFilePath[0] = '\0';
    requestedFilePathArraySize = 0;

    /* Make mutable copy of pathname. */
    char _pathname[255];
    strcpy(_pathname, pathname);

    /* Setup tokenizer. */
    char * savePointer;
    char * token = strtok_r(_pathname, "/", & savePointer);

    /* Categorize the pathname. */
    int isAbsolute = pathname[0] == '/';
    int isSelfRelative = !strcmp(token, ".");
    int isParentRelative = !strcmp(token, "..");
    // Fourth case: relative to cwd

    if (token && !isAbsolute) {
        int maxLevel = isParentRelative ? currentDirectoryPathArraySize
         - 1 : currentDirectoryPathArraySize;
        for (int i = 0; i < maxLevel; i++) {
            strcpy(requestedFilePathArray[i], currentDirectoryPathArray[i]);
            sprintf(requestedFilePath, "%s/%s", requestedFilePath, currentDirectoryPathArray[i]);
            requestedFilePathArraySize++;
        }
    }

    /* Discard '.' or '..'. */
    if (isSelfRelative || isParentRelative) {
        token = strtok_r(0, "/", & savePointer);
    }

    while(token && requestedFilePathArraySize < 10) {

        strcpy(requestedFilePathArray[requestedFilePathArraySize], token);
        sprintf(requestedFilePath, "%s/%s", requestedFilePath, token);

        /* Printf for debug. */
        printf("\t%s\n", token);

        requestedFilePathArraySize++;
        token = strtok_r(0, "/", & savePointer);

    }
    printf("Output: %s\n", requestedFilePath);

}

fdDir * getInode(const char * pathname) {
    // Loop over inodes, find requested node, return that node, if does not exist return NULL

    for (size_t i = 0; i < 6; i++) {
        if (strcmp(inodes[i].path, pathname) == 0) {
            return &inodes[i];
        }
    }

    return NULL;

}

int fs_mkdir(const char *pathname, mode_t mode){
    //parse the pathname
    char *DirectEntry = malloc(strlen(pathname)+1);
    strcpy(DirectEntry,pathname);
    char **newPath = parse(DirectEntry);
    //make sure path exists
    if(newPath ==NULL){
            return -1;
        }
    time_t DE_current_time;
    time ( &DE_current_time );
    //make a new Directy entry
    dir_entry *newDE = malloc(sizeof(dir_entry));
    //set new values for copied DE
    strcpy(newDE->name,"newName");
    newDE->last_accessed = DE_current_time;
    newDE->last_modified = DE_current_time;

    return 1;

}

int fs_rmdir(const char *pathname) {
    //parse the pathname
    char **newPath = parse(pathname);
    
    //make sure path exists
    if (newPath == NULL) {
        return -1;
    }
    
    //make a copy of the DE
    dir_entry *copyDE = malloc(sizeof(dir_entry));
    
    //set new values for copied DE
    time_t current_time;
    time(&current_time);
    strcpy(copyDE->name, "newName");
    copyDE->last_accessed = current_time;
    copyDE->last_modified = current_time;
    
    return 1;
}


char currentDirectoryPath[255];

char * fs_getcwd(char * buf, size_t size) {
    if (strlen(currentDirectoryPath) > size) {
        
        return NULL;
    }
    strcpy(buf, currentDirectoryPath);
    return buf;
}

int fs_setcwd(char * buf) {

    parseFilePath(buf);

    /* Check if inode exists. */
    fdDir * inode = getInode(requestedFilePath);
    if (!inode) {
        printf("Directory '%s' does not exist.\n", requestedFilePath);
        printf("----------------------------------------------------------------\n");
        return 1;
    }

    /* Clear previous cwd. */
    currentDirectoryPath[0] = '\0';
    currentDirectoryPathArraySize = 0;

    /* Copy parsed pathname to currentDirectoryPathArray. */
    for (int i = 0; i < requestedFilePathArraySize; i++) {
        strcpy(currentDirectoryPathArray[i], requestedFilePathArray[i]);
        sprintf(currentDirectoryPath, "%s/%s", currentDirectoryPath, requestedFilePathArray[i]);
        currentDirectoryPathArraySize++;
    }


    return 0;

}

int fs_stat(const char *path, struct fs_stat *buf){
    return 1;
}


int fs_delete(char* filename) {
    // Make sure filename is not NULL
    if (filename == NULL) {
        printf("Filename cannot be NULL\n");
        return -1;
    }

    //parse the filename
    char **DirectEntry;
    DirectEntry = parse(filename);

    char tempDE[50];
    int i = 0; // Counter to iterate through directory entries

    //iterate through every directory entry
    while(i < NUMBER_OF_DE){
        //find the directory entry with the name that was passed in
        if (strcmp(tempDE, DE[i].name) == 0){
            //set the values to 0 or NULL to delete
            DE[i].last_accessed = 0;
            DE[i].date_created = 0;
            DE[i].last_modified = 0;
            DE[i].location = 0;
            DE[i].size = 0;
            strcpy(DE[i].name, "NULL");
            break; //exit loop once entry is found and deleted
        }
        i++; //move to the next directory entry
    }

    //If cannot find the directory entry, return error
    if (i == NUMBER_OF_DE){
        printf("Could not find Directory entry to delete\n");
        return -1;
    }

    return 1;
}

fdDir * fs_opendir(const char *pathname){
    char **DirectEntry;
    DirectEntry = parse(pathname);
    // int isDir = fs_isDir(pathname);
    int isDir;

    switch (isDir){
        case 0:
            return NULL;
        case 1: {
            fdDir *fd = malloc(sizeof(fdDir));
            // load directory into memory
            dir_entry *tempDE = malloc(sizeof(dir_entry));
            fd->DirectoryEntryPosition = 0;
            fd->DirectoryStartLocation = tempDE->location;
            fd->d_reclen = tempDE->size;
            return fd;
        }
        default:
            //handle unexpected values of isDir
            printf("Unexpected value of isDir: %d\n", isDir);
            return NULL;
    }
}

struct fs_diriteminfo *fs_readdir(fdDir *dirp){
    //make a struct for the diriteminfo
    struct fs_diriteminfo fd;
    struct fs_diriteminfo *returnfd = &fd;

    //Initialize DirectoryEntryPosition if it's negative
    if (dirp->DirectoryEntryPosition < 0) 
        dirp->DirectoryEntryPosition = 0;

    do {
        //check if DirectoryEntryPosition is within bounds
        if (dirp->DirectoryEntryPosition < NUMBER_OF_DE) {
            //find the dir and perform a strcpy
            strcpy(DE[dirp->DirectoryEntryPosition].name, fd.d_name);
            fd.fileType = DE[dirp->DirectoryEntryPosition].fileType;
            fd.d_reclen = DE[dirp->DirectoryEntryPosition].size;
            dirp->DirectoryEntryPosition++;
            return returnfd;
        }
    } while (0); //loop runs only once

    //reset DirectoryEntryPosition to negative value
    dirp->DirectoryEntryPosition = -1;

    return returnfd;
}


int fs_closedir(fdDir *dirp){
    //check if the directory pointer exists
    if(dirp != NULL){
        free(dirp);
        dirp == NULL;
    }
    return 0;
}
int fs_isFile(char * filename){
    char** file = parse(filename);
    if(file < 0){//file doesn't exist
        return -1;
    }
    return 1;
    //load file and check dir
}
int fs_isDir(char *pathname){
    //check if it is a file and return the result
    return !fs_isFile(pathname);
}



