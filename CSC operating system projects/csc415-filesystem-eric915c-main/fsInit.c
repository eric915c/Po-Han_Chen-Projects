/***************************************************************
* Class: CSC-415-01 Spring 2024
* Name: Tsai Hsin Ying, Hsueh-Ta Lu, Po-Han Chen, Nathan Lee
* Student IDs: 923503916, 923409640, 923446482, 923407911
* GitHub-Name: Golden1018, darren816, eric915c, nlty0122
* Group-Name: RCJ
* Project: Basic File System
*
* File: fsInit.c
*
* Description:: Initializes the basic file system.
*
**************************************************************/
#include <stdlib.h>
#include <unistd.h>
#include <sys/types.h>
#include <stdio.h>
#include <string.h>
#include <time.h>
#include "mfs.c"
#include "fsLow.h"
#include "Free_Space.c"
#include "mfs.h"

int currentDirLocation;

int initFileSystem (uint64_t numberOfBlocks, uint64_t blockSize)
	{
	printf ("Initializing File System with %ld blocks with a block size of %ld\n",
	numberOfBlocks, blockSize);
	/* TODO: Add any code you need to initialize your file system. */
	mainvcb = malloc(sizeof(vcb)*(numberOfBlocks*blockSize));
	LBAread(mainvcb, blockSize, 0);//read volume control block
	//check if the volume control block has been initialize
	if(mainvcb->signature != 777){
		//initVCB(mainvcb, numberOfBlocks, blockSize);//initialize the volume control block
		mainvcb->rootsize = sizeof(dir_entry)*NUMBER_OF_DE;
		currentDirLocation = 0;
		//creating bit map
		int *Bitmap;
		Bitmap = malloc(4*(numberOfBlocks*blockSize));
		//setting everything to 0
		memset(Bitmap, 0, (numberOfBlocks*blockSize) * sizeof(int));
		//setting the first 6 bytes of the 0 block to used
		for(int i=0;i<6;i++){
			setBit(Bitmap,i);
		}
		//writing to disk the bitmap blocks 1 - numberOfBlocks
		LBAwrite(Bitmap,numberOfBlocks,1);
		//setting first block where free space starts
		mainvcb->firstBlock = Bitmap[1];

		//allocating space in memoray for Directory entries
		dir_entry* DE = malloc(DE_SIZE * sizeof(dir_entry));
		//initialize each directory entry structure to be in 
		//a known free state. 
		int off_set=8;
		for(int i=0;i<NUMBER_OF_DE+1;i++){
			DE[i].location = Bitmap[i*off_set];
			setBit(Bitmap,i*off_set);
		}
		//allocate 6 bocks for the '.' dir
		for(int i=NUMBER_OF_DE+1;i<NUMBER_OF_DE+7;i++){
			if(i==NUMBER_OF_DE+1){
				DE[0].location = Bitmap[i];
				setBit(Bitmap,i*off_set);
			}else{
				setBit(Bitmap,i*off_set);
			}
		}
		//setting values for "." in the DE struct
		strcpy(DE[0].name,".");
		DE[0].size = 3060;
		//setting current time for DE
		time_t DE_current_time;
		time ( &DE_current_time );
		DE[0].date_created = DE_current_time;
		strcpy(DE[0].description,"'.' for the file system");
		DE[0].permission = 2;
		//allocate 6 bocks for the '..' dir
		for(int i=NUMBER_OF_DE+7;i<NUMBER_OF_DE+13;i++){
			if(i==NUMBER_OF_DE+7){
				DE[1].location = Bitmap[i];
				setBit(Bitmap,i*off_set);
			}else{
				setBit(Bitmap,i*off_set);
			}
		}
		//setting values for ".." in the DE struct
		strcpy(DE[1].name,"..");
		DE[1].size = 3060;
		DE[1].date_created = DE_current_time;
		strcpy(DE[1].description,"'..' for the file system");
		DE[1].permission = 2;

		for(int i=NUMBER_OF_DE+13;i<NUMBER_OF_DE+19;i++){
			if(i==NUMBER_OF_DE+13){
				DE[2].location = Bitmap[i];
				setBit(Bitmap,i*off_set);
			}else{
				setBit(Bitmap,i*off_set);
			}
		}
		//setting values for "root" in the DE struct
		strcpy(DE[2].name,"..");
		DE[2].size = 3060;
		DE[2].date_created = DE_current_time;
		strcpy(DE[2].description,"root for the file system");
		DE[2].permission = 2;
		//set the root directory in the volume control block
		mainvcb->rootDir = DE[2].location;
		LBAwrite(mainvcb, blockSize, 0);//write volume control block to disk
	}else{
		//load the de and the  free space
	}
	locationDE *deLocation = malloc(sizeof(locationDE));
	dir_entry *tempDir = malloc(sizeof(dir_entry) * NUMBER_OF_DE);
	LBAread(tempDir, mainvcb->rootsize/mainvcb->sizeofBlock, mainvcb->rootDir);
	//printf("This is the rootDir%d\n", mainvcb->rootDir);
	//printf("This is the tempDir%d", tempDir);
	fdDir fd;
	char* path = {"Test Path"};
	fs_opendir(path);

	fdDir *dirp;
	//fs_readdir(dirp);
	// printf("%d\n",Bitmap[0]);
	return 0;
	}
void exitFileSystem ()
	{
	printf ("System exiting\n");
	}



	