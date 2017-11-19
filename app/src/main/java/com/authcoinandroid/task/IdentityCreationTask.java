package com.authcoinandroid.task;

import android.content.Context;
import android.os.AsyncTask;
import com.authcoinandroid.model.Identity;
import com.authcoinandroid.service.identity.IdentityService;
import com.authcoinandroid.task.response.IdentityCreationResponse;
import com.authcoinandroid.task.result.AsyncTaskResult;
import com.authcoinandroid.task.result.IdentityCreationResult;
import org.bitcoinj.wallet.UnreadableWalletException;

import java.io.IOException;

public class IdentityCreationTask extends AsyncTask<Object, Object, AsyncTaskResult<Identity>> {

    private Context context;
    private String password;
    private IdentityCreationResponse identityCreationResponse;

    public IdentityCreationTask(Context context, String password, IdentityCreationResponse identityCreationResponse) {
        this.context = context;
        this.password = password;
        this.identityCreationResponse = identityCreationResponse;
    }

    @Override
    protected AsyncTaskResult<Identity> doInBackground(Object... objects) {
        try {
            Identity identity = IdentityService.getInstance().createIdentity(context, this.password);
            return new IdentityCreationResult<>(identity);
        } catch (IOException | UnreadableWalletException e) {
            return new IdentityCreationResult<>(e);
        }
    }

    @Override
    protected void onPostExecute(AsyncTaskResult<Identity> o) {
        identityCreationResponse.processFinish(o);
    }
}