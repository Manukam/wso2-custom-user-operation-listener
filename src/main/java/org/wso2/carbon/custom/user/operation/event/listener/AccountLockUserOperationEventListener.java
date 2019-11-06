package org.wso2.carbon.custom.user.operation.event.listener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.identity.core.util.IdentityUtil;
import org.wso2.carbon.identity.mgt.constants.IdentityMgtConstants;
import org.wso2.carbon.user.core.UserStoreException;
import org.wso2.carbon.user.core.UserStoreManager;
import org.wso2.carbon.user.core.common.AbstractUserOperationEventListener;

import java.io.File;
import java.io.InputStream;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.FileInputStream;
import java.util.Arrays;
import java.util.Properties;

public class AccountLockUserOperationEventListener extends AbstractUserOperationEventListener {

    private static Log log = LogFactory.getLog(AccountLockUserOperationEventListener.class);
    public Properties properties = new Properties();

    @Override
    public int getExecutionOrderId() {

        //This listener should execute before the IdentityMgtEventListener
        //Hence the number should be < 1357 (Execution order ID of IdentityMgtEventListener)
        return 1356;
    }

    @Override
    public boolean doPreAuthenticate(String userName, Object credential, UserStoreManager userStoreManager) throws UserStoreException {
        String message;
        if (properties.isEmpty()) {
            readPropertiesFromFile();
        }
        Boolean isLockAccounts = Boolean.valueOf(this.properties.getProperty("Custom.Accounts.Lock").trim());
        if (isLockAccounts) {
            message = "Account is locked for user " + userName + " in user store. Cannot login until the " +
                    "account is unlocked.";
            String excludedUserSet = this.properties.getProperty("Custom.Accounts.Lock.Exclude.Users").trim();
            if (excludedUserSet == null || excludedUserSet.isEmpty()) {
                log.info("No users defined to be excluded from Account lock process.");
                throw new UserStoreException(message);
            }
            String[] excludedUsers = excludedUserSet.split(",");
            if (!Arrays.asList(excludedUsers).contains(userName)) {
                throw new UserStoreException(message);
            } else {
                return true;
            }
        } else {
            return true;
        }
    }

    private void readPropertiesFromFile() {
        InputStream inStream = null;
        File pipConfigXml = new File(IdentityUtil.getIdentityConfigDirPath(), IdentityMgtConstants.PropertyConfig
                .CONFIG_FILE_NAME);
        if (pipConfigXml.exists()) {
            try {
                inStream = new FileInputStream(pipConfigXml);
                properties.load(inStream);
            } catch (FileNotFoundException e) {
                log.error("Can not load identity-mgt properties file ", e);
            } catch (IOException e) {
                log.error("Can not load identity-mgt properties file ", e);
            } finally {
                if (inStream != null) {
                    try {
                        inStream.close();
                    } catch (IOException e) {
                        log.error("Error while closing stream ", e);
                    }
                }
            }
        }
    }
}
