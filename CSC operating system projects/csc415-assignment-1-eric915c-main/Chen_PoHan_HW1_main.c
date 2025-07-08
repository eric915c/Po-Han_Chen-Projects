/**************************************************************
* Class::  CSC-415-01 Spring 2024
* Name::Po-Han Chen
* Student ID::923446482
* GitHub-Name::eric915c
* Project:: Assignment 1 – Command Line Arguments
*
* File:: Chen_PoHan_HW1_main.c
*
* Description::The objective of this project is to utilize the C 
* language for command-line parameter handling. It involves 
* showcasing each received parameter on the terminal and 
* concluding by displaying the total count of parameters.
*
**************************************************************/

#include <stdio.h>
#include <stdlib.h>

int main(int argc, char *argv[]){
    // Print the number of arguments on the command line
    printf("%d arguments om command line.\n\n",argc); 
    int i=0;// Initialize a counter variable for the loop
    while(i<argc){// Use a while loop to iterate through command-line arguments
         // Print the current argument along with its index
        printf("argment%.2d:%s\n\n",i,argv[i]);
        // Increment the counter variable
        i++;
    }
    // Return 0 to indicate successful completion
    return 0;

}