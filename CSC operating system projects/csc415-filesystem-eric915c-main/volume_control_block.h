/***************************************************************
* Class: CSC-415-01 Spring 2024
* Name: Tsai Hsin Ying, Hsueh-Ta Lu, Po-Han Chen, Nathan Lee
* Student IDs: 923503916, 923409640, 923446482, 923407911
* GitHub-Name: Golden1018, darren816, eric915c, nlty0122
* Group-Name: RCJ
* Project: Basic File System
*
* File: volume_control_block.h
*
* Description:: This is file for the volume control block.
*
*
**************************************************************/

#include <stdio.h>
#include <unistd.h>
#include <stdlib.h>			// for malloc
#include <string.h>			// for memcpy
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <stdint.h>

typedef struct {
    int signature;
    // A ‘magic number’ where the first couple of bytes indicate what type of file the file is
    int totalBlocks;//the total number of blocks. Free or not free.
    int sizeofBlock;//the size of the block
    int freeBlocks;//free block pointer casted to int
    int rootDir; //Block number pointing to where the root directory is
    int firstBlock; //First block of the volume
    int rootsize;
    int rootLocation;
    
} vcb ;

extern vcb *mainvcb;
// void initVCB(vcb *controlBlock, uint64_t numberOfBlocks, uint64_t blockSize){
//     controlBlock->totalBlocks = numberOfBlocks;
//     controlBlock->sizeofBlock = blockSize;
//     controlBlock->signature = 777;//magic number
// }

