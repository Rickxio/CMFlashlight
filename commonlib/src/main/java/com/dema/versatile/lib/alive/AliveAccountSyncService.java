package com.dema.versatile.lib.alive;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Service;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SyncResult;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.IBinder;
import android.text.TextUtils;

import com.dema.versatile.lib.R;
import com.dema.versatile.lib.utils.AliveHelp;
import com.dema.versatile.lib.utils.UtilsLog;

public class AliveAccountSyncService extends Service {
    private static final Object mObjectLock = new Object();
    private static SyncAdapter mSyncAdapter = null;

    public static void startAccountSync(Context context) {
        try {
            if (null == context || context.getResources() == null)
                return;
            Resources resources = context.getResources();
            String strAccountName = resources.getString(R.string.app_name);
            String strAccountType = resources.getString(R.string.alive_account_type);
            String strAuthority = resources.getString(R.string.alive_authority);
            if (TextUtils.isEmpty(strAccountName) || TextUtils.isEmpty(strAccountType) || TextUtils.isEmpty(strAuthority)) {
                return;
            }
            AccountManager am = (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);
            Account[] arrayAccount = am.getAccountsByType(strAccountType);
            if (null != arrayAccount && arrayAccount.length > 0)
                return;

            Account account = new Account(strAccountName, strAccountType);
            if (am.addAccountExplicitly(account, null, null)) {
                ContentResolver.setIsSyncable(account, strAuthority, 1);
                ContentResolver.setSyncAutomatically(account, strAuthority, true);
                ContentResolver.addPeriodicSync(account, strAuthority, new Bundle(), 60 * 5);
            }
        } catch (Exception | Error e) {
        }
    }

    public static void requestAccountSync(Context context, String strAccountName, String strAccountType, String strAuthority) {
        if (null == context || TextUtils.isEmpty(strAccountName) || TextUtils.isEmpty(strAccountType) || TextUtils.isEmpty(strAuthority))
            return;

        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);

        Account account = new Account(strAccountName, strAccountType);
        ContentResolver.requestSync(account, strAuthority, bundle);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        synchronized (mObjectLock) {
            if (mSyncAdapter == null) {
                mSyncAdapter = new SyncAdapter(getApplicationContext(), true);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mSyncAdapter.getSyncAdapterBinder();
    }

    class SyncAdapter extends AbstractThreadedSyncAdapter {
        public SyncAdapter(Context context, boolean autoInitialize) {
            super(context, autoInitialize);
        }

        @Override
        public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
            UtilsLog.aliveLog("account", null);
            UtilsLog.send();
            AliveHelp.startPull("account");
        }
    }
}
