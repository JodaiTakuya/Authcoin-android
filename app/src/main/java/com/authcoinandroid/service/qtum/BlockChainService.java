package com.authcoinandroid.service.qtum;

import com.authcoinandroid.util.AuthCoinNetParams;

import java.util.List;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;

public class BlockChainService implements BlockChainApi {

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

    @Override
    public Observable<ContractResponse> callContract(String contractAddress, ContractRequest contractRequest) {
        return blockChainApi.callContract(contractAddress, contractRequest);
    }

    @Override
    public Observable<SendRawTransactionResponse> sendRawTransaction(SendRawTransactionRequest sendRawTransactionRequest) {
        return blockChainApi.sendRawTransaction(sendRawTransactionRequest);
    }

    @Override
    public Observable<List<UnspentOutput>> getUnspentOutput(List<String> addresses) {
        return blockChainApi.getUnspentOutput(addresses);
    }

}