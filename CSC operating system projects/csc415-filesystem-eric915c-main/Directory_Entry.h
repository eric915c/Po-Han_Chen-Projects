/***************************************************************
* Class: CSC-415-01 Spring 2024
* Name: Tsai Hsin Ying, Hsueh-Ta Lu, Po-Han Chen, Nathan Lee
* Student IDs: 923503916, 923409640, 923446482, 923407911
* GitHub-Name: Golden1018, darren816, eric915c, nlty0122
* Group-Name: RCJ
* Project: Basic File System
*
* File: Directory_Entry.h
*
* Description::
*   Defines directory entry structures and constants.
*
**************************************************************/
#ifndef DIR_ENTRY_H
#define DIR_ENTRY_H

#include <time.h>
#define NUMBER_OF_DE 50
#define DE_SIZE 60

typedef struct{
    char name[50];
    char author[20];
    long size;
    int location;
    time_t last_accessed;
    time_t date_created;
    time_t last_modified;
    int permission;
    char description[255];
    int dir;
    int blocks;
    unsigned char fileType;
} dir_entry;

extern dir_entry DE[NUMBER_OF_DE];  // Declare DE as extern.

typedef struct{
    int deAdress;
    int deIndex;
} locationDE;

#endif // DIR_ENTRY_H
