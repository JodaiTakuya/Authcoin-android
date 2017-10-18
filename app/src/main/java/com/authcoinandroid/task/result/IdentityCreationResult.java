package com.authcoinandroid.task.result;

public class IdentityCreationResult<Identity> extends AsyncTaskResult<Identity> {

    public IdentityCreationResult(Identity result) {
        super(result);
    }

    public IdentityCreationResult(Exception error) {
        super(error);
    }
}