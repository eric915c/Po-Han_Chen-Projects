/***************************************************************
* Class: CSC-415-01 Spring 2024
* Name: Tsai Hsin Ying, Hsueh-Ta Lu, Po-Han Chen, Nathan Lee
* Student IDs: 923503916, 923409640, 923446482, 923407911
* GitHub-Name: Golden1018, darren816, eric915c, nlty0122
* Group-Name: RCJ
* Project: Basic File System
*
* File: b_io.c
*
* Description:: Basic File System - Key File I/O Operations
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
#include <fsLow.h>
#include <b_io.h>
#include "mfs.c"

#define MAXFCBS 20
#define B_CHUNK_SIZE 512
#define BLOCK_COUNT 5
#define BLOCK_SIZE 8

typedef struct b_fcb
	{
	/** TODO add al the information you need in the file control block **/
	char * buf;		//holds the open file buffer
	int index;		//holds the current position in the buffer
	int buflen;		//holds how many valid bytes are in the buffer
	int block; 		//holds how many blocks 
	} b_fcb;
	
b_fcb fcbArray[MAXFCBS];

int startup = 0;	//Indicates that this has not been initialized

//Method to initialize our file system
void b_init ()
	{
	//init fcbArray to all free
	for (int i = 0; i < MAXFCBS; i++)
		{
		fcbArray[i].buf = NULL; //indicates a free fcbArray
		}
		
	startup = 1;
	}

//Method to get a free FCB element
b_io_fd b_getFCB ()
	{
	for (int i = 0; i < MAXFCBS; i++)
		{
		if (fcbArray[i].buf == NULL)
			{
			return i;		//Not thread safe (But do not worry about it for this assignment)
			}
		}
	return (-1);  //all in use
	}
	
// Interface to open a buffered file
// Modification of interface for this assignment, flags match the Linux flags for open
// O_RDONLY, O_WRONLY, or O_RDWR
b_io_fd b_open (char * filename, int flags)
	{
	b_io_fd returnFd;

	//*** TODO ***:  Modify to save or set any information needed
	//
	//
	
	if (startup == 0) b_init();  //Initialize our system

	// get our own file descriptor
	returnFd = b_getFCB();
	int index = 0;
	char * buf;
	b_fcb * fcb;
	//create a tempDE for O_CREAT, 0_TRUNC, AND O_APPEND
	dir_entry *tempDE = malloc(sizeof(DE));
	if(O_CREAT){
		//create a new DE
		tempDE->size = BLOCK_SIZE*BLOCK_COUNT;
		tempDE->blocks = BLOCK_COUNT;
		tempDE->dir = 0;
		tempDE->location = index;
		time(&tempDE[index].last_accessed);
		time(&tempDE[index].last_modified);
	}
	//opens the file for writing  
	if(O_TRUNC){
		//TO DO
		//write our returnfd and then the new tempDE with LBAwrite
		LBAwrite(fcbArray[returnFd].buf,fcbArray[returnFd].block,fcbArray[returnFd].index);
		LBAwrite(tempDE,tempDE[0].blocks,tempDE[0].location);
	}
	if(O_APPEND){
		//opens file for updates at the end
		b_seek(fcbArray->index,fcbArray->buflen,SEEK_END);
	}

	// check for error - all used FCB's
	buf = malloc(B_CHUNK_SIZE);
	if(buf == NULL){
		return (-1);
	}
	//return the return fd
	returnFd = b_getFCB;
	fcbArray[returnFd].buf = buf;
	fcbArray[returnFd].buflen = 0;
	fcbArray[returnFd].index = 0;
	fcbArray[returnFd].block = 0;

	return (returnFd);						// all set
	}


// Interface to seek function	
int b_seek (b_io_fd fd, off_t offset, int whence)
	{
	if (startup == 0) b_init();  //Initialize our system

	// check that fd is between 0 and (MAXFCBS-1)
	if ((fd < 0) || (fd >= MAXFCBS))
		{
		return (-1); 					//invalid file descriptor
		}
	//Set position of the file to the new offset
	if(SEEK_SET){
		fcbArray[fd].index = offset;
	}
	//set position of the file to the end of the offset
	if(SEEK_END){
		fcbArray[fd].index -= offset;
	}	
	//set position of the file to the current position of the offset
	if(SEEK_CUR){
		fcbArray[fd].index += offset;
	}

		
	return fcbArray[fd].index; //Change this
	}



// Interface to write function	
int b_write (b_io_fd fd, char * buffer, int count)
	{
	int bytesWrote;
	int bytesReturned;
	int remainingBytes;
	int numOfBlocksToCopy;
	int part1;
	int part2;
	int part3;

	if (startup == 0) b_init();  //Initialize our system

	// check that fd is between 0 and (MAXFCBS-1)
	if ((fd < 0) || (fd >= MAXFCBS))
		{
		return (-1); 					//invalid file descriptor
		}
	
	//read the buffer
	LBAread(buffer,count,fcbArray[fd].index);
	int index = 0;
	fcbArray[fd].buf = buffer;
	fcbArray[fd].block = count;
	fcbArray[fd].index = index;
	fcbArray[fd].buflen = B_CHUNK_SIZE/BLOCK_SIZE;
	//if we have enough we set part1 to count and return it
	remainingBytes = fcbArray[fd].buflen - fcbArray[fd].index;
	if(remainingBytes >= count){
		part1 = count;
		part2 = 0;
		part3 = 0;
	//otherwise set part 1 to the bytes left
	}else{
		part1 = remainingBytes;
		part3 = count - remainingBytes;

		numOfBlocksToCopy = part3/B_CHUNK_SIZE;
		part2 = numOfBlocksToCopy*B_CHUNK_SIZE;
		part3 = part3-part2;
	}
	//if part one isn't empty copy the buffer to memoray and update the index
	if(part1 > 0){
		memcpy(buffer,fcbArray[fd].buf+fcbArray[fd].index,part1);
		fcbArray[fd].index = fcbArray[fd].index + part1;
	}
	//if part 2 isn't empty perform a LBAwrite on part2
	if(part2 > 0){
		bytesWrote = LBAwrite(buffer+part1,numOfBlocksToCopy,fcbArray[fd].index);
		part2 = bytesWrote;
	}
	//if part 3 ins't empty perform a LBAwrite() on part3 and update the index and buflen
	if(part3 > 0){
		bytesWrote =  LBAwrite(fcbArray[fd].buf,1,fcbArray[fd].index);
		fcbArray[fd].index = 0;
		fcbArray[fd].buflen = bytesWrote;
	}
	bytesReturned = part1+part2+part3;
	return (bytesReturned);	
		
	}



// Interface to read a buffer

// Filling the callers request is broken into three parts
// Part 1 is what can be filled from the current buffer, which may or may not be enough
// Part 2 is after using what was left in our buffer there is still 1 or more block
//        size chunks needed to fill the callers request.  This represents the number of
//        bytes in multiples of the blocksize.
// Part 3 is a value less than blocksize which is what remains to copy to the callers buffer
//        after fulfilling part 1 and part 2.  This would always be filled from a refill 
//        of our buffer.
//  +-------------+------------------------------------------------+--------+
//  |             |                                                |        |
//  | filled from |  filled direct in multiples of the block size  | filled |
//  | existing    |                                                | from   |
//  | buffer      |                                                |refilled|
//  |             |                                                | buffer |
//  |             |                                                |        |
//  | Part1       |  Part 2                                        | Part3  |
//  +-------------+------------------------------------------------+--------+
int b_read (b_io_fd fd, char * buffer, int count)
	{

	int bytesRead;
	int bytesReturned;
	int remainingBytes;
	int numOfBlocksToCopy;
	int part1;
	int part2;
	int part3;

	if (startup == 0) b_init();  //Initialize our system

	// check that fd is between 0 and (MAXFCBS-1)
	if ((fd < 0) || (fd >= MAXFCBS))
		{
		return (-1); 					//invalid file descriptor
		}
	//get the remainingBytes in the buffer.
	remainingBytes = fcbArray[fd].buflen - fcbArray[fd].index;
	//if we have enough we set part1 to count and return it
	if(remainingBytes >= count){
		part1 = count;
		part2 = 0;
		part3 = 0;
	//otherwise set part 1 to the bytes left
	}else{
		part1 = remainingBytes;
		part3 = count - remainingBytes;

		numOfBlocksToCopy = part3/B_CHUNK_SIZE;
		part2 = numOfBlocksToCopy*B_CHUNK_SIZE;
		part3 = part3-part2;
	}
	//if part one isn't empty copy the buffer to memoray and update the index
	if(part1 > 0){
		memcpy(buffer,fcbArray[fd].buf+fcbArray[fd].index,part1);
		fcbArray[fd].index = fcbArray[fd].index + part1;
	}
	//if part 2 isn't empty perform a LBAread() on part2
	if(part2 > 0){
		bytesRead = LBAread(buffer+part1,numOfBlocksToCopy,fcbArray[fd].index);
		part2 = bytesRead;
	}
	//if part 3 ins't empty perform a LBAread() on part3 and update the index and buflen
	if(part3 > 0){
		bytesRead =  LBAread(fcbArray[fd].buf,1,fcbArray[fd].index);
		fcbArray[fd].index = 0;
		fcbArray[fd].buflen = bytesRead;
	}
	bytesReturned = part1+part2+part3;
	return (bytesReturned);	
	}
	
// Interface to Close the file	
int b_close (b_io_fd fd)
	{
		free(fcbArray->buf);
		fcbArray->buf = NULL;
	}
