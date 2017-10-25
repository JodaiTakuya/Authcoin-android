package com.authcoinandroid.service.qtum;

import com.authcoinandroid.model.contract.ContractRequest;
import com.authcoinandroid.model.contract.ContractResponse;
import com.authcoinandroid.util.AuthCoinNetParams;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;

public class BlockChainService {

    private static BlockChainService blockChainService;
    private BlockChainApi blockChainApi;

    public static BlockChainService getInstance() {
        if (blockChainService == null) {
            blockChainService = new BlockChainService();
        }
        return blockChainService;
    }

    private BlockChainService() {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(AuthCoinNetParams.getUrl())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();

        blockChainApi = retrofit.create(BlockChainApi.class);
    }

    public Observable<ContractResponse> callSmartContract(String contractAddress, final ContractRequest contractRequest) {
        return blockChainApi.callSmartContract(contractAddress, contractRequest);
    }
}