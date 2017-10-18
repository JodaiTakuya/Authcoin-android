package com.authcoinandroid.task.response;

import com.authcoinandroid.model.Identity;
import com.authcoinandroid.task.result.AsyncTaskResult;

public interface IdentityCreationResponse {
    void processFinish(AsyncTaskResult<Identity> output);
}