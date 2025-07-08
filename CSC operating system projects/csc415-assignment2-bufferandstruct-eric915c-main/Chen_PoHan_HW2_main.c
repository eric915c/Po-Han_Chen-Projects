/**************************************************************
* Class::  CSC-415-0# Spring 2024
* Name::Chen Po-Han
* Student ID::923446482
* GitHub-Name::eric915c
* Project:: Assignment 2 – Stucture in Memory and Buffering
*
* File:: Chen_PoHan_HW2_main.c
*
* Description:: The assignment 2 - Struct and Buffer provides 
* an opportunity to apply structures, pointers, 
* character strings, enumerated types, bitmap fields, and the 
* buffering of data into blocks.
*
**************************************************************/
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "assignment2.h" // Including the header file.

#define BLOCK_SIZE 256

int main(int argc, char *argv[]) {

    // Allocating memory dynamically for a structure called personalInfo using the malloc function.
    personalInfo *info = malloc(sizeof(personalInfo));
    if (info == NULL) { // checks if the memory allocation was successful. 
        printf("Memory allocation failed");
        return 1;
    }

    // Assigning values to the members of the personalInfo structure.
    info->firstName=argv[1];
    info->lastName=argv[2];
    info->studentID = 923446482;
    info->level = JUNIOR;
    info->languages = (KNOWLEDGE_OF_C | KNOWLEDGE_OF_JAVASCRIPT | KNOWLEDGE_OF_OBJECTIVE_C 
    | KNOWLEDGE_OF_SQL | KNOWLEDGE_OF_FORTRAN);

    // Copy message to info->message, ensuring it fits within the buffer size.
    strncpy(info->message, argv[3], 100);
    
    // Using "writePersonalInfo" function to save my personal information.
    writePersonalInfo(info);

    char buffer[BLOCK_SIZE];
    const char * nextString;
    int bufferIndex = 0;
    int strP;
    
    // Reading strings and committing blocks.
    while ((nextString = getNext()) != NULL) { // Loop until the getNext() function returns NULL, indicating no more strings to process.

    size_t nextLen = strlen(nextString);
    strP = 0; // The position in the current string.
    
     while (nextLen > 0) {
        size_t copySize = nextLen < (BLOCK_SIZE - bufferIndex) ? nextLen : (BLOCK_SIZE - bufferIndex);
        // Determine the size of the substring to copy to the buffer.
        // It's either the remaining length of nextString or the remaining space in the buffer, whichever is smaller.
        
        memcpy(buffer + bufferIndex, nextString + strP, copySize); 
        // Copy a portion of nextString to the buffer starting from position strP.

        bufferIndex += copySize;
        strP += copySize;
        nextLen -= copySize;

        if (bufferIndex >= BLOCK_SIZE) {
            commitBlock(buffer);  // Commit the current block of data in the buffer for further processing.
            memset(buffer, 0, BLOCK_SIZE); // Clear the buffer by setting all its contents to 0
            bufferIndex = 0; 
        }
    }
}



    // If there are remaining data in the buffer that has not been committed
    // Commit the remaining data in the buffer for further processing
    if (bufferIndex != 0) {
        commitBlock(buffer);
    }

    free(info); // Free the dynamically allocated memory for the info structure

    // Call checkIt and exit
    int checkResult = checkIt();
    return checkIt();
}
