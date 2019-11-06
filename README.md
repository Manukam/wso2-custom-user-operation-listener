# wso2-custom-user-operation-listener
 
> A Custom User operations listener for WSO2 IS 5.7.0 that locks all user accounts excluding select few users (Configurable).

## Build Setup

* Navigate to the project root directory and execute the following.
``` bash
  mvn clean install
```
* Navigate to the `/target` folder in the project directory and copy and paste the `org.wso2.carbon.custom.user.operation.event.listener-1.0.0.jar` to `IS_HOME/repository/components/dropins`

* Navigate to `IS_HOME/repository/conf/identity` and open the **identity-mgt.properties** file and add the following lines.
- To turn account locking on/off
```
Custom.Accounts.Lock=true/false
``` 
 - To define the usernames of users (Comma separated) whose accounts needs to be excluded from the account locking process. 
```
Custom.Accounts.Lock.Exclude.Users=user1,user2
``` 
* Save the file.

* Start the WSO2 IS server to observe the changes.

 
