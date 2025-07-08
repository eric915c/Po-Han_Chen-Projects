/***************************************************************
* Class: CSC-415-01 Spring 2024
* Name: Tsai Hsin Ying, Hsueh-Ta Lu, Po-Han Chen, Nathan Lee
* Student IDs: 923503916, 923409640, 923446482, 923407911
* GitHub-Name: Golden1018, darren816, eric915c, nlty0122
* Group-Name: RCJ
* Project: Basic File System
*
* File: Free_Space.c
*
* Description:: Implements functions for managing free space.
*
**************************************************************/
#include <Free_Space.h>
#include <time.h>
//bitwise function for setting a bit to 1
void setBit(int Bitmap[],int n){
    Bitmap[n/8] |= (1 << (n%8));
}
//bitwise function for clearing a bit to 0
void clearBit(int Bitmap[],int n){
    Bitmap[n/8] &= ~(1 << (n%8));
}

