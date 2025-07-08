# Credentials Folder

## The purpose of this folder is to store all credentials needed to log into your server and databases. This is important for many reasons. But the two most important reasons is
    1. Grading , servers and databases will be logged into to check code and functionality of application. Not changes will be unless directed and coordinated with the team.
    2. Help. If a class TA or class CTO needs to help a team with an issue, this folder will help facilitate this giving the TA or CTO all needed info AND instructions for logging into your team's server. 


# Below is a list of items required. Missing items will causes points to be deducted from multiple milestone submissions.

1. Server URL or IP: 3.143.17.129
2. SSH username: ubuntu
3. SSH password or key: see Team7Server.pem
4. Database URL or IP and port used: team7db.cb26y46s85ai.us-east-2.rds.amazonaws.com
5. Database username: class_ceo
6. Database password: Teamcsc007
7. Database name (basically the name that contains all your tables): Team7DB
8. Instructions on how to use the above information: <br>
    Connecting to the server: <br>
    Windows:<br>
      a. Open MobaXterm<br>
      b. Click 'Session' in top left corner.<br>
      c. Select "SSH"<br>
      d. In place of "Remote Host" give 18.116.65.3 and "Specify username" give ubuntu. Next click "Advanced SSH settings" then tick "Use private key" button and upload .pem file.<br>
      e. Click "Ok" now you are connected to server.<br>
      f. You can go to html folder by using cd var/www/html command to check files of Team7 website.<br><br>
    macOS: <br>
      a. Using the credentials folder as the working directory, use terminal command ```ssh -i Team7Server.pem ubuntu@3.143.17.129```.<br><br>
    Connecting to the database: <br>
    Windows/Intel Macs: <br>
        a. Open "MySQL Workbench".<br>
        b. click "+" button to open new connection.<br>
        c. Give "Hostname" - team7db.cb26y46s85ai.us-east-2.rds.amazonaws.com<br>
        d. username - class_ceo<br>
        e. Click "Store in vault" and enter the password - Teamcsc007<br>
        d. You will be connected to Database server. Open script and run "Show databases;" command to check databases. Database - 'team7DB' should be visible. <br><br>
    Apple Silicon Macs:<br>
        a. Open DBeaver. <br>
        b. Select "Database -> New Database Connection" and select "MySQL."<br>
        c. Select "URL" and give the following: team7db.cb26y46s85ai.us-east-2.rds.amazonaws.com<br>
        d. Username: class_ceo<br>
        e. Password: Teamcsc007 and click "Save password."<br>
        f. Make sure drivers say "MySQL" and click "Test Connection." If successful, click "Finish." The Team7DB will be visible in the Database Navigator. <br>



# Most important things to Remember
## These values need to kept update to date throughout the semester. <br>
## <strong>Failure to do so will result it points be deducted from milestone submissions.</strong><br>
## You may store the most of the above in this README.md file. DO NOT Store the SSH key or any keys in this README.md file.
