/***************************************************************
* Class: CSC-415-01 Spring 2024
* Name: Tsai Hsin Ying, Hsueh-Ta Lu, Po-Han Chen, Nathan Lee
* Student IDs: 923503916, 923409640, 923446482, 923407911
* GitHub-Name: Golden1018, darren816, eric915c, nlty0122
* Group-Name: RCJ
* Project: Basic File System
*
* File: mfs.h
*
* Description:: This is the file system interface.
* This is the interface needed by the driver to interact with
* your filesystem.
*
**************************************************************/
#ifndef _MFS_H
#define _MFS_H
#include <sys/types.h>
#include <unistd.h>
#include <time.h>
#include <string.h>
#include <stddef.h>
#include <stdio.h>
#include <stdlib.h>
#include "fsLow.h"
#include <volume_control_block.h>
#include "Directory_Entry.h"
#include <dirent.h>
#define FT_REGFILE	DT_REG
#define FT_DIRECTORY DT_DIR
#define FT_LINK	DT_LNK

#ifndef uint64_t
typedef u_int64_t uint64_t;
#endif
#ifndef uint32_t
typedef u_int32_t uint32_t;
#endif

typedef enum { I_FILE, I_DIR, I_UNUSED } InodeType;

// This structure is returned by fs_readdir to provide the caller with information
// about each file as it iterates through a directory
struct fs_diriteminfo
	{
    unsigned short d_reclen;    /* length of this record */
    unsigned char fileType;    
    char d_name[256]; 			/* filename max filename is 255 characters */
	};

// This is a private structure used only by fs_opendir, fs_readdir, and fs_closedir
// Think of this like a file descriptor but for a directory - one can only read
// from a directory.  This structure helps you (the file system) keep track of
// which directory entry you are currently processing so that everytime the caller
// calls the function readdir, you give the next entry in the directory
typedef struct
	{
	/*****TO DO:  Fill in this structure with what your open/read directory needs  *****/
	uint64_t id; // holds the index of the inode in the inodes array 
	int inUse; // holds 0 if inode is free and 1 if it is in use
	InodeType type; // holds the type of the inode I_FILE or I_DIR
	char parent[225];  // holds the parenr path
	char NameOfChild[64][20]; // an array that holds names of the children
	int NumberOfChild; // holds number of children in a DIR
	char name[225];  // holds the file name
	char path[225];  // holds the path of the file/folder
	time_t LastAccessTime; // holds time last accessed 
	time_t LastModificationTime; //  holds time last modifed 
	blkcnt_t SizeInBlocks; // holds the size of the file by block, 512 each block
	off_t SizeInBytes; // holds the size of the file in bytes
	

	unsigned short  d_reclen;		/*length of this record */
	unsigned short	DirectoryEntryPosition;	/*which directory entry position, like file pos */
	uint64_t	DirectoryStartLocation;		/*Starting LBA of directory */
	} fdDir;

// Key directory functions
int fs_mkdir(const char *pathname, mode_t mode);
int fs_rmdir(const char *pathname);

// Directory iteration functions
fdDir * fs_opendir(const char *pathname);
struct fs_diriteminfo *fs_readdir(fdDir *dirp);
int fs_closedir(fdDir *dirp);

// Misc directory functions
char * fs_getcwd(char *pathname, size_t size);
int fs_setcwd(char *pathname);   //linux chdir
int fs_isFile(char * filename);	//return 1 if file, 0 otherwise
int fs_isDir(char *pathname);		//return 1 if directory, 0 otherwise
int fs_delete(char* filename);	//removes a file


// This is the strucutre that is filled in from a call to fs_stat
struct fs_stat
	{
	off_t     st_sizeinbytes;    		/* total size, in bytes */
	blksize_t st_blocksize; 		/* blocksize for file system I/O */
	blkcnt_t  st_blocks;  		/* number of 512B blocks allocated */
	time_t    st_accesstime;   	/* time of last access */
	time_t    st_modificationtime;   	/* time of last modification */
	time_t    st_createtime;   	/* time of last status change */
	
	/* add additional attributes here for your file system */
	};

int fs_stat(const char *path, struct fs_stat *buf);

extern int currentDirLocation;

#endif

