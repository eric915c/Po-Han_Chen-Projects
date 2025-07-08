/***************************************************************
* Class::  CSC-415-01 Spring 2024
* Name::   Chen Po-Han
* Student ID:: 923446482
* GitHub-Name:: eric915c
* Project:: Assignment 6 – Coding A Device Driver
*
* File:: Chen_PoHan_HW6_main.c
*
* Description::
*   This program serves as a user-space application to interact
*   with the encryption device driver. It opens the encryption
*   device, prompts the user to enter a message to encrypt, 
*   sends the message to the device, reads the encrypted 
*   message from the device, and decrypts the message manually.
*   The encrypted message is then displayed. The program 
*   utilizes system calls to communicate with the device file 
*   and also makes use of ioctl to send commands to the driver
*   for encryption.
*
***************************************************************/
#include <stdio.h>      //standard Input/Output library
#include <stdlib.h>     //standard library
#include <fcntl.h>      //file control options
#include <unistd.h>     //symbolic constants and types
#include <string.h>     //string manipulation functions
#include <sys/ioctl.h> //input/Output Control operations

//Path to the device file
#define DEVICE_PATH "/dev/encryption_device"

int main(){
    int fd; //file descriptor for the device
    char message[256]; //buffer to store the user message

    //open the device
    fd = open(DEVICE_PATH, O_RDWR); //open the device file with read/write access
    if (fd < 0) { //check if the file descriptor is valid
        perror("Failed to open device file"); //print error message if failed to open
        return -1;  //return error code
    }

    //get message from user
    printf("Enter a message to encrypt: "); //prompt user to enter a message
    scanf("%s", message); //read the message from user input

    //write message to device
    write(fd, message, strlen(message) + 1); //write the message to the device

    //Encrypt message
    if (ioctl(fd, 0, 0) < 0) {  //send ioctl command to encrypt the message
        perror("Failed to send encrypt command"); //print error message if ioctl fails
        return -1; //return error code
    }

    //read encrypted message from device
    read(fd, message, sizeof(message)); //read the encrypted message from the device

    //display encrypted message
    printf("Encrypted message: %s\n", message); //print the encrypted message

    //Decrypt message
    printf("\nDecrypted message: "); //print label for decrypted message
    for (int i = 0; message[i] != '\0'; ++i) { //use for loop through each character of the message
        if (message[i] >= 'a' && message[i] <= 'z') { //check if character is lowercase
            message[i] = 'a' + (message[i] - 'a' + 23) % 26; //Decrypt lowercase character
        } else if (message[i] >= 'A' && message[i] <= 'Z') { //check if character is uppercase
            message[i] = 'A' + (message[i] - 'A' + 23) % 26; //Decrypt uppercase character
        }
        printf("%c", message[i]); //print decrypted character
    }
    printf("\n"); //move to the next line after printing the decrypted message

    //close the device
    close(fd); //close the file descriptor associated with the device

    return 0; //return success code
}

