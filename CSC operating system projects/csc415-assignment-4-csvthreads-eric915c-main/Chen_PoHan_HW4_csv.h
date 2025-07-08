/**************************************************************
* Class::  CSC-415-01 Spring 2024
* Name:: Chen Po-Han
* Student ID:: 923446482
* GitHub-Name:: eric915c
* Project:: Assignment 4 – Processing CSV Data with Threads
*
* File:: Chen_PoHan_HW4_csv.h
*
* Description::This project involves processing CSV 
* (Comma-Separated Values) data using multi-threading techniques 
* in C programming language. The main tasks include reading 
* CSV files, parsing the data into fields, performing operations
* on the data, and managing memory efficiently.
*
**************************************************************/

#ifndef Chen_PoHan_HW4_csv_h
#define Chen_PoHan_HW4_csv_h

#include <stdio.h>
#define COLUMNCOUNT 4


// Function prototypes for CSV processing
char **csvopen(char *filename);
char **csvnext(void);
char **csvheader(void);
int csvclose(void);

// Function prototype for data processing
void process_data(char *csv_filename, int threads, char *subfield, char *subfield_values[], int num_values);

#endif /* Chen_PoHan_HW4_csv_h */

