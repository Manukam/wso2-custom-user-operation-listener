package org.wso2.carbon.custom.user.operation.event.listener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.custom.user.operation.event.listener.internal.CustomUserOperationEventListenerDSComponent;
import org.wso2.carbon.identity.core.model.IdentityErrorMsgContext;
import org.wso2.carbon.identity.core.util.IdentityUtil;
import org.wso2.carbon.user.core.UserCoreConstants;
import org.wso2.carbon.user.core.UserStoreException;
import org.wso2.carbon.user.core.UserStoreManager;
import org.wso2.carbon.user.core.common.AbstractUserOperationEventListener;

import java.util.Arrays;

public class AccountLockUserOperationEventListener extends AbstractUserOperationEventListener {

    private static Log log = LogFactory.getLog(AccountLockUserOperationEventListener.class);

    @Override
    public int getExecutionOrderId() {

        //This listener should execute before the IdentityMgtEventListener
        //Hence the number should be < 1357 (Execution order ID of IdentityMgtEventListener)
        return 1356;
    }

    @Override
    public boolean doPreAuthenticate(String userName, Object credential, UserStoreManager userStoreManager) throws UserStoreException {
        String message;
        Boolean isLockAccounts = Boolean.valueOf(CustomUserOperationEventListenerDSComponent.properties.getProperty("Custom.Accounts.Lock").trim());
        if (isLockAccounts) {
            message = "Account is locked for user " + userName + " in user store. Cannot login until the " +
                    "account is unlocked.";
            String excludedUserSet = CustomUserOperationEventListenerDSComponent.properties.getProperty("Custom.Accounts.Lock.Exclude.Users").trim();
            if (excludedUserSet == null || excludedUserSet.isEmpty()) {
                log.warn("No users defined to be excluded from Account lock process.");
                return true;
            }
            String[] excludedUsers = excludedUserSet.split(",");
            if (!Arrays.asList(excludedUsers).contains(userName)) {
                IdentityErrorMsgContext customErrorMessageContext = new IdentityErrorMsgContext(UserCoreConstants.ErrorCode.USER_IS_LOCKED);
                IdentityUtil.setIdentityErrorMsg(customErrorMessageContext);
                throw new UserStoreException(UserCoreConstants.ErrorCode.USER_IS_LOCKED);
            } else {
                return true;
            }
        } else {
            return true;
        }
    }
}
