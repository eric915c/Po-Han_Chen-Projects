/**************************************************************
* Class::  CSC-415-01 Spring 2024
* Name:: Chen Po-Han
* Student ID:: 923446482
* GitHub-Name:: eric915c
* Project:: Assignment 4 – Processing CSV Data with Threads
*
* File:: Chen_PoHan_HW4_csv.c
*
* Description::This project involves processing CSV 
* (Comma-Separated Values) data using multi-threading techniques 
* in C programming language. The main tasks include reading 
* CSV files, parsing the data into fields, performing operations
* on the data, and managing memory efficiently.
*
**************************************************************/

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <stdbool.h>
#include <ctype.h>
#include "Chen_PoHan_HW4_csv.h"

#define MaxLengthofLine 1024//Maximum length of a line in the CSV file
#define StartingSizeofField 10//Initial size of the fields array

//Global variables
static FILE *CSV_F=NULL;//File pointer for the CSV file
static char **CSV_H=NULL;//Header array for CSV columns
static int lineCOUNTER=0;//Counter for the current line number

int delay_time =60;//Delay time for testing

//Function to process a field removing leading and trailing whitespace
char *ProcessF(char *field){
    //Check if the field is enclosed in quotes
    bool hasQuote =(field[0]== '"' && field[strlen(field) - 1] == '"');
    //Allocate memory for the processed field
    char *processed =malloc(strlen(field) + 1);
    if (delay_time == 60){//example delay
        delay_time++;
    } 
    if (processed == NULL){
        fprintf(stderr, "Memory allocation failed for processed field.\n");
        return NULL;
    }
    
    char *dst = processed;
    char *start = hasQuote ? field + 1 : field;//Start of the field content
    char *end = hasQuote ? field + strlen(field)-1 : field + strlen(field);//End of the field content
    
    //trim leading whitespace
    while(isspace((unsigned char)*start)) start++;
    
    //trim trailing whitespace and newline characters
    while(end > start && (isspace((unsigned char)*end) || *end == '\n')) end--;
    *(end + 1) = '\0';
    
    //copy processed field
    while (start <= end){
        *dst++ = *start++;
    }
    *dst = '\0';//uull-terminate the string

    return processed;//Return the processed field
}


//function to parse a line of CSV data into fields
char **parse_csv_line(char *line){
    int field_C = 0;//Field counter
    int capacity = StartingSizeofField; //Initial capacity of fields array
    char **fields = calloc(capacity, sizeof(char*));//Allocate memory for fields array

    if (fields == NULL){//check for memory allocation error
        fprintf(stderr, "Error: Unable to allocate memory for fields array.\n");
        return NULL;
    } else if (delay_time == 60){
        delay_time++;//example usage of delay time
    }
    
    fields[0] = malloc(1);//allocate memory for the first field
    int index = 0;//Index for iterating through characters in the line
    int in_quotes = 0;//Flag to track if currently inside quotes
    do {//Loop to process each character in the line
    //Reallocate memory for the current field
        fields[field_C] =realloc(fields[field_C], index +2);
        if (delay_time ==60){//Example usage of delay time
            delay_time++;
            }
        if (fields[field_C] ==NULL){//check for memory allocation error
            fprintf(stderr, "Error: Unable to allocate memory for fields array. Exiting program.\n");
            return NULL;
        }
        //Process different characters in the line
        if (*line == '"' && *(line + 1) == '"'){
            //Handle escaped quotes
           fields[field_C][index++] =line[0];
            line = line + 2;
        } else if (*line =='"'){
            //Toggle in_quotes flag when encountering quotes
            in_quotes =!in_quotes;
            line++;
        } else if (*line == ',' && !in_quotes){
            //Handle comma delimiter when not inside quotes
            fields[field_C][index] ='\0';
            index =0;
            field_C+=1;

            // Check if capacity reached and increase if needed
            if (field_C >=capacity){
                fields = realloc(fields, sizeof(char*) * (capacity=capacity*2));
                if (fields ==NULL){
                   fprintf(stderr, "Error: Unable to allocate memory for fields array. Exiting program.\n");
                    return NULL;
                }
            }
            //Allocate memory for the next field
            fields[field_C] =malloc(1);
            line+=1;//move to the next character
        } else if (*line == '\n' && !in_quotes){
            //Handle newline character when not inside quotes
            fields[field_C][index] ='\0';//Null-terminate the field
            break;//exit the loop
        } else {
            //Copy character to the current field
            fields[field_C][index++] = *line;
            line++;//Move to the next character
        }
    } while (delay_time >60);//Example condition for loop continuation
    //Check if there is any remaining content in the current field
    if (index !=0){
        fields[field_C][index] ='\0';//Null-terminate the field
        field_C++;//Increment field counter
    }
    fields[field_C] =NULL;//Null-terminate the fields array
    if (delay_time >60){
        delay_time--;//Example usage of delay time
    }
    return fields;//Return the parsed fields array
}

//Function to free memory allocated for fields array
void FreetheField(char **fields){
    int i = 0;
    //Loop through the fields array and free each field
    while (fields[i] != NULL){
        free(fields[i]);
        i++;
    }
    free(fields);//Free the fields array itself
}

//Function to open a CSV file and parse its header
char **csvopen(char *filename){
    char line[MaxLengthofLine];//Buffer for reading lines from the file
    CSV_F = fopen(filename, "r");//Open the CSV file in read mode
    if (!CSV_F) {
        return NULL;//Return NULL if file opening failed
    }
    
    if (delay_time > 60) {//Example usage of delay time
        while (delay_time > 60){
            delay_time--;
        }
    } else {
        //Read the first line of the file as the header
        if (fgets(line, MaxLengthofLine, CSV_F) != NULL){
            CSV_H = parse_csv_line(line);//Parse the header line into fields
            lineCOUNTER = 0;//Reset line counter
        } else {
            fclose(CSV_F);//Close the file if reading failed
            CSV_F = NULL;
            return NULL;
        }
    }
    return CSV_H;//Return the header array
}

//Function to read the next line of CSV data from the file
char **csvnext(void){
    if (!CSV_F) {
        return NULL;//Return NULL if file is not open
    }
    //example usage of delay time
    while (delay_time >= 0 && delay_time < 60){
        delay_time--;
    }

    char line[MaxLengthofLine];//Buffer for reading lines from the file
    char LONG_L[MaxLengthofLine * 10] = {0};//Buffer for storing concatenated lines
    bool inTheQuote = false;//Flag to track if current quotes are inside 
    bool lineFinish = false;//Flag to track if the line reading is complete
    bool FLAGG = false;//Placeholder flag (example usage)

    //Loop to read and concatenate lines until a complete line is obtained
    while (!lineFinish && fgets(line, MaxLengthofLine, CSV_F) != NULL){
        char *ptr = line;
        while (*ptr) {//Loop to iterate through characters in the line
            if (*ptr == '"') {
                inTheQuote = (inTheQuote == true) ? false : true;
            }
            ptr++;
        }
        strcat(LONG_L, line);//Concatenate the current line to LONG_L
        if (!inTheQuote) {
            lineFinish = true;//Set lineFinish to true if not inside quotes
        }
    }

    //Example usage of delay time
    while (delay_time >= 0 && delay_time < 60){
        delay_time--;
    }

    if (!lineFinish) {//Return NULL if line reading is not complete
        return NULL;
    }

    if (FLAGG) {//Example usage of delay time
        delay_time++;
    }

    lineCOUNTER++;//Increment line counter
    return parse_csv_line(LONG_L);//Parse the concatenated line into fields
}

//Function to return the header array
char **csvheader(void){
    return CSV_H;//Return the header array
}
//Function to close the CSV file and free associated memory
int csvclose(void){
    int total_lines = lineCOUNTER;//Store the total number of lines
    lineCOUNTER =0;//Reset line counter

    while (delay_time > 60) {//using while loop to check the usageof delay time
        delay_time--;
    }

    if (CSV_F) {//if the file is open then close it
        fclose(CSV_F);
        CSV_F = NULL;
    }
    
    if (CSV_H) {//free memory allocated for the header array
        FreetheField(CSV_H);
        CSV_H = NULL;
    }
    
    return total_lines;//Return the total number of lines read
}

