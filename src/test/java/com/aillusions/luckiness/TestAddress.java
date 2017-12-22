package com.aillusions.luckiness;

import com.aillusions.luckiness.web.RestController;
import junit.framework.TestCase;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.params.MainNetParams;

import java.math.BigInteger;

/**
 * @author aillusions
 */
public class TestAddress extends TestCase {

    public void testHex() {
        BigInteger converted = new BigInteger("01f272ad2d099521ce2e653d1398c1016bf0e01c81809b94f2c1227ed273cb1a", 16);
        ECKey key = RestController.getNewECKey(converted);

        System.out.println(RestController.getBtcAddress(key));
    }

    // https://en.bitcoin.it/wiki/Wallet_import_format
    public void testBase58() {

        {
            String privateKeyHex = "0C28FCA386C7A227600B2FE50B7CAE11EC86D3BF1FBE471BE89827E19D72AA1D";
            BigInteger privateKeyBigInt = new BigInteger(privateKeyHex, 16);
            ECKey key = RestController.getNewECKey(privateKeyBigInt);
            String wifBase58 = key.getPrivateKeyAsWiF(MainNetParams.get());
            System.out.println("wifBase58: " + wifBase58);
            assertEquals("5HueCGU8rMjxEXxiPuD5BDku4MkFqeZyd4dZ1jvhTVqvbTLvyTJ", wifBase58);
        }

        //

        {
            ECKey key = RestController.getKeyFromWIFBase58("5HueCGU8rMjxEXxiPuD5BDku4MkFqeZyd4dZ1jvhTVqvbTLvyTJ");
            String privateKeyHex = key.getPrivateKeyAsHex();
            assertEquals("0C28FCA386C7A227600B2FE50B7CAE11EC86D3BF1FBE471BE89827E19D72AA1D", privateKeyHex.toUpperCase());
        }
    }

    public void testBase58ForReal() {
        ECKey key = RestController.getKeyFromWIFBase58("5JdeC9P7Pbd1uGdFVEsJ41EkEnADbbHGq6p1BwFxm6txNBsQnsw");
        String address = RestController.getBtcAddress(key);
        assertEquals("12AKRNHpFhDSBDD9rSn74VAzZSL3774PxQ", address);
    }

}
