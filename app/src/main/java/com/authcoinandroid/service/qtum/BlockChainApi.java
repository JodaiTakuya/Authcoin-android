package com.authcoinandroid.service.qtum;

import com.authcoinandroid.model.contract.ContractRequest;
import com.authcoinandroid.model.contract.ContractResponse;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Path;
import rx.Observable;

public interface BlockChainApi {
    @POST("/contracts/{addressContract}/call")
    Observable<ContractResponse> callSmartContract(@Path("addressContract") String addressContract, @Body ContractRequest contractRequest);
}