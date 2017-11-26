package com.authcoinandroid;

import com.subgraph.orchid.encoders.Hex;
import org.junit.Test;
import org.web3j.crypto.Hash;

import static com.authcoinandroid.util.ContractUtil.bytesToBytes32;
import static org.junit.Assert.assertEquals;
import static org.web3j.utils.Numeric.cleanHexPrefix;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        String message = "0000000000000000000000000000000000000000000000000000000000000020000000000000000000000000000000000000000000000000000000000000005b3059301306072a8648ce3d020106082a8648ce3d0301070342000469a52a2aed12fa786e6d3c4e17fc6ab517129aec7dd60f21fd17d210e721d94282e25989a9b735d72bd1fad19069002861248e9d32366d0068bfa215a9135eb70000000000";
//        message = "5b3059301306072a8648ce3d020106082a8648ce3d0301070342000469a52a2aed12fa786e6d3c4e17fc6ab517129aec7dd60f21fd17d210e721d94282e25989a9b735d72bd1fad19069002861248e9d32366d0068bfa215a9135eb7";
//        message = "0000000000000000000000000000000000000000000000000000000000000020000000000000000000000000000000000000000000000000000000000000005b3059301306072a8648ce3d020106082a8648ce3d03010703420004f5e114c1883e1e5a2c083a515606898c2860dc59c20261814f6a6886e23a3f2d9952ca83d7fec54840860b81437c3e70d6af6599d83ff8a090408fee9d9962b00000000000";
//        message = "4235436453";
        message = "3059301306072a8648ce3d020106082a8648ce3d03010703420004f5e114c1883e1e5a2c083a515606898c2860dc59c20261814f6a6886e23a3f2d9952ca83d7fec54840860b81437c3e70d6af6599d83ff8a090408fee9d9962b0";
//        String hash = new Keccak().getHash(message, Parameters.KECCAK_256);
//        System.out.println(hash);
//        System.out.println(Runtime.class.getPackage().getImplementationVersion());
        System.out.println(bytesToBytes32(Hex.decode(cleanHexPrefix(Hash.sha3(message)))));
        System.out.println(cleanHexPrefix(Hash.sha3(message)).getBytes().length);
        assertEquals(4, 2 + 2);
    }
}

// 3059301306072a8648ce3d020106082a8648ce3d03010703420004f5e114c1883e1e5a2c083a515606898c2860dc59c20261814f6a6886e23a3f2d9952ca83d7fec54840860b81437c3e70d6af6599d83ff8a090408fee9d9962b0