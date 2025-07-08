/**************************************************************
* Class::  CSC-415-01 Spring 2024
* Name::   Chen Po-Han
* Student ID:: 923446482
* GitHub-Name:: eric915c
* Project:: Assignment 6 – Coding A Device Driver
*
* File:: encryption_driver.c
*
* Description::
*   A Linux kernel module is developed for a simple 
*   encryption device driver that can encrypt and decrypt
*   messages using the Caesar cipher algorithm. The driver 
*   is registered as a character device with read, write, 
*   and ioctl operations. 
*
**************************************************************/
//kernel module initialization
#include <linux/init.h>

//core header for loading LKMs into the kernel
#include <linux/module.h>

//header for the Linux file system support
#include <linux/fs.h>

//required for the copy to/from user functions
#include <linux/uaccess.h>

#define DEVICE_NAME "encryption_device" //device name
#define BUF_LEN 256 //maximum buffer length

MODULE_LICENSE("GPL"); //module license

//major number assigned to our device driver
static int major_number;

//buffer to store the message
static char message[BUF_LEN];

//size of the message stored in the buffer
static short size_of_message;

//count of how many times the device has been opened
static int device_open_count = 0;

//count of how many times the device has been opened
static int is_device_open = 0;


//caesar encryption algorithm
int caesarEncr(char *text, int shift) {
    int i;
    for (i = 0; text[i] != '\0'; ++i) {
        //Encrypt lowercase letters
        if (text[i] >= 'a' && text[i] <= 'z') {
            text[i] = 'a' + (text[i] - 'a' + shift) % 26;
            //Encrypt uppercase letters
        } else if (text[i] >= 'A' && text[i] <= 'Z') {
            text[i] = 'A' + (text[i] - 'A' + shift) % 26;
        }
    }
    return 0;
}

//caesar decryption algorithm
int caesarDecr(char *text, int shift) {
    return caesarEncr(text, 26 - shift); //use the opposite shift value for decryption
}

//function prototypes
//device open function
static int deviceOpen(struct inode *, struct file *);

//device release function
static int deviceRelease(struct inode *, struct file *);

//device read function
static ssize_t deviceRead(struct file *, char *, size_t, loff_t *);

 //device write function
static ssize_t deviceWrite(struct file *, const char *, size_t, loff_t *);

//device ioctl function
static long deviceIoctl(struct file *, unsigned int, unsigned long);

//file operations structure
static struct file_operations fops = {
    .open = deviceOpen,//pointer to the device open function
    .read = deviceRead,//pointer to the device read function
    .write = deviceWrite,//pointer to the device write function
    .release = deviceRelease,//pointer to the device release function
    .unlocked_ioctl = deviceIoctl,//pointer to the device ioctl function
};


//Module initialization function
static int __init encrypDriverInit(void) {
    //print initialization message
    printk(KERN_INFO "Encryption driver: Initialization started\n");

    //register the character device
    major_number = register_chrdev(0, DEVICE_NAME, &fops);
    //check if registration was successful
    if (major_number < 0) {
        //print error message if registration failed
        printk(KERN_ALERT "Encryption driver failed to register a major number\n");
        //return the error code
        return major_number;
    }
    printk(KERN_INFO "Encryption driver: registered correctly with major number %d\n", major_number);

    //return success status
    return 0;
}


//Module exit
static void __exit encrypDriverExit(void) {
    //unregister character device
    unregister_chrdev(major_number, DEVICE_NAME);
    //print exit message
    printk(KERN_INFO "Encryption driver: Goodbye from the LKM!\n");
}

//open device function
static int deviceOpen(struct inode *inode, struct file *file) {
    //check if the device is already open
    if (is_device_open) {
        return -EBUSY; //If device is already open, return busy error code
    }

    //Increment device open count and mark the device as open
    device_open_count++;
    is_device_open = 1;
    //print device opened message
    printk(KERN_INFO "Encryption driver: Device opened\n");
    return 0;
}


//release device function
static int deviceRelease(struct inode *inode, struct file *file) {
    //decrement device open count and mark the device as closed
    device_open_count--;
    is_device_open = 0;
    //print device closed message
    printk(KERN_INFO "Encryption driver: Device closed\n");
    return 0;
}


//read from device function
static ssize_t deviceRead(struct file *filp, char *buffer, size_t length, loff_t *offset) {
    int bytes_read = 0;

    //check if end of message is reached or exceeded message length, return 0
    if (*message == 0 || *offset >= size_of_message) {
        return 0;
    }

    //copy message from kernel space to user space
    while (length && *offset < size_of_message) {
        put_user(message[*offset], buffer++); //copy one byte of message to user space
        length--; //decrease remaining length
        bytes_read++; //increase bytes read count
        (*offset)++; //move offset to next character in message
    }

    return bytes_read; //return number of bytes read
}


//write to device function
static ssize_t deviceWrite(struct file *filp, const char *buffer, size_t length, loff_t *offset) {
    int i = 0;

    //if message buffer size exceeded, return error
    if (length > BUF_LEN) {
        printk(KERN_ALERT "Encryption driver: The message is too long for the buffer\n");
        return -EINVAL;
    }

    //copy data from user space to kernel space
    while (i < length) {
        get_user(message[i], buffer + i); //get one byte of data from user space
        i++; //move to next character in buffer
    }

    size_of_message = length;//update the size of the message
    printk(KERN_INFO "Encryption driver: Received %zu characters from the user\n", length);

    return length; //return number of bytes written
}


//handle ioctl commands function
static long deviceIoctl(struct file *file, unsigned int ioctl_num, unsigned long ioctl_param) {
    int shift = 3; //caesar cipher shift key

    switch (ioctl_num) {
        case 0: //Encrypt
            caesarEncr(message, shift); //call encryption function
            break;
        case 1: //Decrypt
            caesarDecr(message, shift); //call decryption function
            break;
        default:
            printk(KERN_ALERT "Encryption driver: Invalid ioctl command\n");
            return -EINVAL; //return error for invalid command
    }

    printk(KERN_INFO "Encryption driver: Message processed\n"); // Log message processed
    return 0; //return success
}


module_init(encrypDriverInit); //set module initialization function
module_exit(encrypDriverExit); //set module exit function



//How to load device

//  sudo insmod encryption_driver.ko
//  lsmod | grep encryption_driver
//  sudo dmesg | tail

//  sudo chmod 666 /dev/encryption_device
//  ls -l /dev/encryption_device

//How to unload device

//  sudo rmmod encryption_driver
