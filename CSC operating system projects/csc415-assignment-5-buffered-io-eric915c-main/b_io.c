/**************************************************************
* Class::  CSC-415-01 Spring 2024
* Name::   Chen Po-Han
* Student ID:: 923446482
* GitHub-Name:: eric915c
* Project:: Assignment 5 – Buffered I/O read
*
* File:: b_io.c
*
* Description:: This project involves creating efficient
* buffered I/O functions in C to read data from files. Through
* this assignment to learn how to optimize file reading, manage
* multiple files, and handle end-of-file situations correctly.
* Using low-level APIs and ensure that the buffered I/O
* operations are performed efficiently.
*
**************************************************************/
#include <stdio.h>
#include <unistd.h>
#include <stdlib.h>
#include <string.h>
#include <fcntl.h>

#include "b_io.h"
#include "fsLowSmall.h"

#define MaxFCBS 20	//The maximum number of files open at one time

// This structure is all the information needed to maintain an open file
// It contains a pointer to a fileInfo structure and any other information
// that you need to maintain your open file.
typedef struct b_fcb
    {
    fileInfo *fi; // holds the low level systems file info

    // Add any other needed variables here to track the individual open file
    int position; // track the current position in the buffer
    char buffer[B_CHUNK_SIZE]; // buffer to store data
    } b_fcb;

// static array of file control blocks
b_fcb fcbArray[MaxFCBS];

// Indicates that the file control block array has not been initialized
int startup = 0;

// Method to initialize our file system / file control blocks
// Anything else that needs one time initialization can go in this routine
void b_init() 
    {
    if (startup)
        return; //already initialized

    //init fcbArray to all free
    for (int i = 0; i < MaxFCBS; i++){
        fcbArray[i].fi = NULL; //indicates a free fcbArray
    }

    startup = 1;
    }

// Method to get a free File Control Block FCB element
b_io_fd b_getFCB() 
    {
    for (int i = 0; i < MaxFCBS; i++){
        if (fcbArray[i].fi == NULL){
            fcbArray[i].fi = (fileInfo *) -2; // used but not assigned
            return i; //Not thread safe but okay for this project
        }
    }

    return (-1); //all in use
    }

// b_open is called by the "user application" to open a file.  This routine is
// similar to the Linux open function.
// You will create your own file descriptor which is just an integer index into an
// array of file control blocks (fcbArray) that you maintain for each open file.
// For this assignment the flags will be read only and can be ignored.

b_io_fd b_open(char *filename, int flags)
    {
    if (startup == 0) b_init(); //Initialize our system

    //*** TODO ***//  Write open function to return your file descriptor
	//				  You may want to allocate the buffer here as well
	//				  But make sure every file has its own buffer

    // This is where you are going to want to call GetFileInfo and b_getFCB
    fileInfo *file_info = GetFileInfo(filename);
        /* call 'GetFileInfo' function with given 'filename' as an argument to obtain
        information about the file. The result is stored in the pointer variable 'file_info.'*/
    if (file_info == NULL){/*if NULL means that the file was not found or 
                            there was an error obtaining its information*/
        return -1; // File not found
    }
    int fd = b_getFCB();//call 'b_getFCB()' to obtain a free file control block(FCB)
    if(fd < 0){/*check if the file descriptor `fd` is invalid*/
        return -1; // No available file descriptor

    }else if(fd >= MaxFCBS){/*check if the file descriptor `fd` is invalid*/
        return -1; // No available file descriptor
    }
    fcbArray[fd].fi = file_info;//use this line to associate the file information with the FCB.
    fcbArray[fd].position = 0; //Initialize position to beginning of the file

    return fd;/*indicating that the file was successfully opened and providing the caller
                with the index into the FCB array for this file.*/
    }

// b_read functions just like its Linux counterpart read.  The user passes in
// the file descriptor (index into fcbArray), a buffer where they want you to
// place the data, and a count of how many bytes they want from the file.
// The return value is the number of bytes you have copied into their buffer.
// The return value can never be greater then the requested count, but it can
// be less only when you have run out of bytes to read.  i.e. End of File
int b_read(b_io_fd fd, char *buffer, int count) {
    //*** TODO ***//  
	// Write buffered read function to return the data and # bytes read
	// You must use LBAread and you must buffer the data in B_CHUNK_SIZE byte chunks.
		
	if (startup == 0) b_init();  //Initialize our system

	// check that fd is between 0 and (MAXFCBS-1)
	if ((fd < 0) || (fd >= MaxFCBS))
		{
		return (-1); 					//invalid file descriptor
		}

	// and check that the specified FCB is actually in use	
	if (fcbArray[fd].fi == NULL)		//File not open for this descriptor
		{
		return -1;
		}	

    b_fcb *F_C_B = &fcbArray[fd];
    /*create a pointer 'fcb' to a file control block ('b_fcb') and assigns it
     the address of the file control block corresponding to the file descriptor
    'fd' in the 'fcbArray'*/

    int bytesRead = 0; /*initializes a counter `bytesRead` to keep 
                         track of the number of bytes read from the buffer.*/
    
    
    for (; bytesRead < count && F_C_B->position < F_C_B->fi->fileSize;){
        /*as long as `bytesRead` is less than `count` (the number of bytes requested
          to read) and the current position (`position`) in the file is less than 
          the file size (`fileSize`)*/
        
        bytesRead++;//loop iterates until the desired number of bytes is read.

        int chuInd = F_C_B->position / B_CHUNK_SIZE; /*the current position by 
                                                        the size of each chunk 
                                                        (`B_CHUNK_SIZE`) to determine
                                                        the chunk index*/

        if (F_C_B->position % B_CHUNK_SIZE == 0){
        /*If the remainder of the division of the current position by the chunk size is 0, it
         means that the current position is at the start of a new chunk*/
            
            
            LBAread(F_C_B->buffer, 1, F_C_B->fi->location + chuInd);
            /* if the remainder of division of the current position by the chunk size is 0
               means taht the current position is at the start of a new chunk*/
        }

        buffer[bytesRead] = F_C_B->buffer[F_C_B->position % B_CHUNK_SIZE]; 
        /*copy data to output buffer, by using modulo operator to the offset within the current chunk*/
        
        
        F_C_B->position++; //update current position
    }
    
   return bytesRead;//return the total number of bytes read 
}




// b_close frees and allocated memory and places the file control block back
// into the unused pool of file control blocks.
int b_close(b_io_fd fd)
    {
    //*** TODO ***//  Release any resources

    /*check if the file descriptor `fd` is valid and if it corresponds to an open file*/
    if (fd < 0){//it is considered an invalid file descriptor
        
        return -1; // Invalid file descriptor or file not open
    
    }else if(fd >= MaxFCBS){//it is considered an invalid file descriptor
       
        return -1; // Invalid file descriptor or file not open
    
    }else if(fcbArray[fd].fi == NULL){//indicate that the file is not open
        
        return -1; // Invalid file descriptor or file not open
    
    }

    // Reset file control block
    fcbArray[fd].fi =NULL;
    fcbArray[fd].position = 0;
    /*close the file and releasing any resources associated with it*/

    return 0; //successful close
    }
