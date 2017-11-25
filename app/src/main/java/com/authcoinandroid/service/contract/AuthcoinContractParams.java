package com.authcoinandroid.service.contract;

import java.math.BigDecimal;

class AuthcoinContractParams {
    static final String AUTHCOIN_CONTRACT_ADDRESS = "afc30d83b70ff1fc1336a3568fb0c4b38fe0fe1e";

    static final int FUNCTION_GAS_LIMIT = 1000000; // 1 million
    static final int GAS_LIMIT = 25000;
    static final int GAS_PRICE = 40;
    static final BigDecimal FEE_PER_KB = BigDecimal.valueOf(0.004);
    static final BigDecimal FEE = BigDecimal.valueOf(0.1);
}