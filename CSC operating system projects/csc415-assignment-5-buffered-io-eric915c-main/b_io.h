/**************************************************************
* Class::  CSC-415-01 Spring 2024
* Name::   Chen Po-Han
* Student ID:: 923446482
* GitHub-Name:: eric915c
* Project:: Assignment 5 – Buffered I/O read
*
* File:: b_io.h
*
* Description:: Definitino of b_io_fd type and the prototypes
*               of the functions accessible by user applications
*
**************************************************************/

#ifndef _B_IO_H
#define _B_IO_H

typedef int b_io_fd;

b_io_fd b_open (char * filename, int flags);
int b_read (b_io_fd fd, char * buffer, int count);
int b_close (b_io_fd fd);

#endif

