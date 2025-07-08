/**************************************************************
* Class::  CSC-415-01 Spring 2024
* Name:: Chen Po-Han
* Student ID::923446482
* GitHub-Name::eric915c
* Project:: Assignment 3 – Simple Shell with Pipes
*
* File:: Chen_PoHan_HW3_main.c
*
* Description:: Creating a simple shell program for Linux 
* that's capable of executing commands, parsing user input,
* handling errors, and supporting piping. The shell reads lines
* of user input, tokenizes them, and executes commands by creating 
* new processes. It waits for child processes to complete before 
* printing their PID and returning the result.
*
**************************************************************/
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <string.h>
#include <sys/wait.h>

/* Executes two piped commands by creating a pipe, forking two child processes,
 * and redirecting the standard input and output for communication between them.*/
void EPipeC(char *args[], char *pipe_token) {
    int pipefd[2];//File descriptors for pipe
    pid_t pid1, pid2;//Process IDs for child processes
    int status;//Status of child processes

    if (pipe(pipefd) == -1) {//create pipe
        perror("pipe");
        exit(EXIT_FAILURE);
    }

    //Fork first child process for the first command
    pid1 = fork();
    if (pid1 == -1){
        perror("fork");
        exit(EXIT_FAILURE);
    } else if(pid1 ==0){
        // Child process for the first command
        close(pipefd[0]); //close unused read end
        dup2(pipefd[1], STDOUT_FILENO); //redirect stdout to pipe
        close(pipefd[1]); //close write end
        execvp(args[0], args);//execute the first command
        perror("execvp");//if the 'execvp' fail, print error
        exit(EXIT_FAILURE);//exit child process with failure
    }

    //Fork second child process for the second command
    pid2 =fork();
    if (pid2 ==-1) {
        perror("fork");
        exit(EXIT_FAILURE);
    } else if (pid2 == 0) {
        //child process for the second command
        close(pipefd[1]); //close unused write end
        dup2(pipefd[0], STDIN_FILENO); //redirect stdin to pipe
        close(pipefd[0]); //close read end
        execvp(pipe_token, args);//execute second command
        perror("execvp");//if the 'execvp' fail, print error
        exit(EXIT_FAILURE);//exit child process with failure
    }

    //parent process
    close(pipefd[0]); //close read end in parent
    close(pipefd[1]); //close write end in parent
    waitpid(pid1, NULL, 0); //wait for the first command to finish
    waitpid(pid2, &status, 0); //wait for the second command to finish
    printf("Child %d, exited with %d\n", pid2,WEXITSTATUS(status));
    //print the child process ID and exit status
}

/* Executes a single command by replacing the current process with
 * the specified command.*/
void ESC(char *args[]) {
    if (execvp(args[0], args) == -1) {//execute the command
        perror("execvp");//if the 'execvp' fail, print error
        exit(EXIT_FAILURE);//exit process with failure
    }
}

//Waits for the specified child process to terminate and prints its exit status.
void WaitCP(pid_t pid) {
    int status;//status of child process
    if (waitpid(pid, &status, 0) == -1) {//wait for child process to terminate
        perror("waitpid");//print error if waitpid fails
        exit(EXIT_FAILURE);//exit process with failure
    }
    //print the child process ID and exit status
    printf("Child %d, exited with %d\n", pid, WEXITSTATUS(status));
}


int main(int argc, char *argv[]) {
    char input[178];//177 characters + null terminator
    char *args[10];//Array to store command and arguments
    char *token;//Declaring a token pointer for strtok
    const char *prompt = "> ";//Default prompt string 

    /* checks if there are command-line arguments
     * If `argc` is greater than 1, it means there are 
     * additional arguments beyond the program name*/
    if (argc>1) {
        prompt= argv[1];
    }

    while (printf("%s", prompt)!=EOF) {
        fflush(stdout);//Flush output buffer

        
        if (fgets(input, sizeof(input), stdin) ==NULL){
        /* It checks if `fgets` returns `NULL`, which indicates 
         * that the end-of-file (EOF) or an error condition has been 
         * encountered while reading input.*/
            printf("\n");
            break; // EOF encountered
        }

        //Remove trailing newline
        input[strcspn(input, "\n")]= '\0';

        //tokenizes the input string `input` using the `strtok` function
        token = strtok(input, " ");
        if (token ==NULL) {/* if the input string is empty or consists only of
                            * whitespace characters*/
            fprintf(stderr,"Error: Empty command\n");
            /*an error message is 
             * printed to the standard error stream 
             * using `fprintf`, indicating that an empty 
             * command was encountered*/
            continue;
        }
        args[0]=token;//Set last element of args array to NULL

        //Tokenize input again to get arguments
        int i= 1;//initializes an index variable `i` to 1
        do {
            /*retrieves the next token from the input string using 
             * `strtok(NULL, " ")` and assigns it to the `token` variable*/
            token = strtok(NULL, " ");
            if (token != NULL) {
                args[i++] = token;/* assign the token to the `args` 
                                   * array at index `i` and increments `i`*/
            }
        } while(token!=NULL && i<10- 1);
        args[i] = NULL;/* set the next element of the `args` array to NULL to 
                        * indicate the end of the argument list*/

        //Check for pipe
        char *pipe_token;//pointer to token after pipe
        int pipe_flag = 0;//Flag to indicate if pipe is present
        
        for (int j=0;j<i;j++){
            if (strcmp(args[j], "|") ==0){
                pipe_flag = 1;
                args[j] = NULL;/* set the current element to NULL, splitting 
                                * the command into two separate commands at 
                                * the pipe symbol*/
                pipe_token =args[j + 1];
                break;
                /* break out of the loop since the first pipe 
                 * symbol encountered is enough to indicate the 
                 * presence of a pipe */
            }
        }
        /* If the result of `strcmp()` is not 0, it means that the two strings
         * are not equal, indicating that the user did not enter "exit" */
        
        /* If `exitRequested` is equal to 0, it means that the user input 
         * is equal to "exit".*/
        int exitRequested =strcmp(args[0], "exit")!=0;
        if (exitRequested==0) {
            break;
        }

        //Execute command
        pid_t pid =fork();//create child process
        if (pid==0) {
            //child process

            if (pipe_flag) {
                //If pipe is present, execute piped command
                EPipeC(args, pipe_token);
            } else {
                //If no pipe, execute non-piped command
                ESC(args);
            }

        } else if (pid > 0) {
            //parent process
            WaitCP(pid);
            //waiting for child process to finish and print its exit status
        } else {
            perror("fork"); //print error if fork fails
        }
    }

    return 0;
}
