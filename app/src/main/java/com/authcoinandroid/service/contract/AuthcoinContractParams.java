package com.authcoinandroid.service.contract;

import java.math.BigDecimal;

class AuthcoinContractParams {
    static final String CONTRACT_ADDRESS = "c740d9cf5a7ca6e13f2a34bf5a661180c74d2161";

    static final int GAS_LIMIT = 25000;
    static final int GAS_PRICE = 40;
    static final BigDecimal FEE_PER_KB = BigDecimal.valueOf(0.004);
    static final BigDecimal FEE = BigDecimal.valueOf(0.1);
}