package org.wso2.carbon.sample.user.operation.event.listener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.identity.core.util.IdentityUtil;
import org.wso2.carbon.identity.mgt.constants.IdentityMgtConstants;
import org.wso2.carbon.user.core.UserStoreException;
import org.wso2.carbon.user.core.UserStoreManager;
import org.wso2.carbon.user.core.common.AbstractUserOperationEventListener;

import java.io.*;
import java.util.Arrays;
import java.util.Properties;

public class SampleUserOperationEventListener extends AbstractUserOperationEventListener {

    private static Log log = LogFactory.getLog(SampleUserOperationEventListener.class);
    public Properties properties = new Properties();

    @Override
    public int getExecutionOrderId() {

        //This listener should execute before the IdentityMgtEventListener
        //Hence the number should be < 1357 (Execution order ID of IdentityMgtEventListener)
        return 1356;
    }


    @Override
    public boolean doPreAuthenticate(String userName, Object credential, UserStoreManager userStoreManager) throws UserStoreException {

        if (properties.isEmpty()) {
            readPropertiesFromFile();
        }
//        for (Enumeration<?> e = properties.propertyNames(); e.hasMoreElements(); ) {
//            String name = (String) e.nextElement();
//            String value = properties.getProperty(name);
//            // now you have name and value
//            log.info(name);
//            log.info(value);
//        }
        Boolean lockAccounts = Boolean.valueOf(this.properties.getProperty("Custom.Lock.Accounts").trim());
        String[] excludedUsers = this.properties.getProperty("Custom.Account.Lock.Exclude.Users").split(",");
        log.info("Accounts Locked Turned :" + lockAccounts);
        if (lockAccounts) {
            if (!Arrays.asList(excludedUsers).contains(userName)) {
                throw new UserStoreException("User Account is temporarily blocked");
            } else {
                return true;
            }
        } else {
            return true;
        }
    }

    private void readPropertiesFromFile() {
        log.info("Reading Custom User Lock properties form file");
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
