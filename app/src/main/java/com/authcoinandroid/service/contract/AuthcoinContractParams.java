package com.authcoinandroid.service.contract;

import java.math.BigDecimal;

class AuthcoinContractParams {
    static final String AUTHCOIN_CONTRACT_ADDRESS = "719fe766f2e07522ce486a2673bd725983575da4";

    static final int FUNCTION_GAS_LIMIT = 1000000;
    static final int GAS_LIMIT = 25000;
    static final int GAS_PRICE = 40;
    static final BigDecimal FEE_PER_KB = BigDecimal.valueOf(0.004);
    static final BigDecimal FEE = BigDecimal.valueOf(0.4);
}