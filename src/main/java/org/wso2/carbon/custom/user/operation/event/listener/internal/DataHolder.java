package org.wso2.carbon.custom.user.operation.event.listener.internal;

import org.wso2.carbon.custom.user.operation.event.listener.AccountLockUserOperationEventListener;
import org.wso2.carbon.user.core.service.RealmService;


public class DataHolder {
    private static RealmService realmService;
    private static volatile DataHolder dataHolder;
    private static AccountLockUserOperationEventListener customUserOperationEventListener;

    private DataHolder() {

    }

    public static DataHolder getInstance() {

        if (dataHolder == null) {

            synchronized (DataHolder.class) {
                if (dataHolder == null) {
                    dataHolder = new DataHolder();
                    customUserOperationEventListener = new AccountLockUserOperationEventListener();
                }
            }
        }
        return dataHolder;
    }

    public void setRealmService(RealmService realmService) {
        this.realmService = realmService;
    }

    public RealmService getRealmService() {
        return realmService;
    }

    public AccountLockUserOperationEventListener getCustomUserOperationEventListener() {
        return customUserOperationEventListener;
    }

}
