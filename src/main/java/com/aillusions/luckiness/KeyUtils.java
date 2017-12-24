package com.aillusions.luckiness;

import com.aillusions.luckiness.web.CheckBatchResponse;
import org.bitcoinj.core.*;
import org.bitcoinj.params.MainNetParams;

import java.math.BigInteger;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @author aillusions
 */
public class KeyUtils {

    private static final BigInteger CHECK_RANGE = BigInteger.valueOf(500L);

    private static final BigInteger MIN_BTC_KEY = new BigInteger("1");
    private static final BigInteger MAX_BTC_KEY = new BigInteger("115792089237316195423570985008687907852837564279074904382605163141518161494337");

    private static DormantBloomFilter bloomFilter;

    static {
        bloomFilter = new DormantBloomFilter(new DormantAddressProvider().getDormantAddresses());
    }

    private static final Set<String> LOGGED_KEYS = Collections.synchronizedSet(new HashSet<>());


    public static ECKey getNewECKey(String providedKeyValue) {
        return new CustomECKey(validateKeyValue(providedKeyValue));
    }

    public static ECKey getNewECKey(BigInteger key) {
        return new CustomECKey(validateKeyValue(key));
    }

    public static BigInteger validateKeyValue(String providedKeyValue) {
        return validateKeyValue(new BigInteger(providedKeyValue));
    }

    public static BigInteger validateKeyValue(BigInteger providedKeyValue) {
        if (providedKeyValue.compareTo(MIN_BTC_KEY) < 0) {
            throw new RuntimeException("Key assertion failure (too small): " + providedKeyValue.toString(10));
        }

        if (providedKeyValue.compareTo(MAX_BTC_KEY) > 0) {
            BigInteger diff = providedKeyValue.subtract(MAX_BTC_KEY);

            throw new RuntimeException("Key assertion failure (too big): "
                    + "\n" + providedKeyValue.toString(10)
                    + "\n" + MAX_BTC_KEY.toString(10)
                    + "\n"
                    + diff.toString(10)
                    + "\n");
        }

        return providedKeyValue;
    }

    public static String getBtcAddress(ECKey key) {

        final NetworkParameters netParams = MainNetParams.get();
        Address addressFromKey = key.toAddress(netParams);

        return addressFromKey.toBase58();
    }

    public static ECKey getKeyFromHex(String hex) {
        return getNewECKey(new BigInteger(hex, 16));
    }

    public static ECKey getKeyFromWIFBase58(String base58) {
        DumpedPrivateKey dumpedKey = DumpedPrivateKey.fromBase58(MainNetParams.get(), base58);
        return dumpedKey.getKey();
    }

    public static String keyToWif(ECKey key) {
        return key.getPrivateKeyAsWiF(MainNetParams.get());
    }

    public static CheckBatchResponse checkBatchFor(String providedKey) {

        CheckBatchResponse rv = new CheckBatchResponse();

        BigInteger origKey = new BigInteger(providedKey);

        BigInteger from = origKey.subtract(CHECK_RANGE);
        BigInteger to = origKey.add(CHECK_RANGE);

        BigInteger thisVal = from;

        do {

            if (thisVal.compareTo(MIN_BTC_KEY) < 0 || thisVal.compareTo(MAX_BTC_KEY) > 0) {
                thisVal = thisVal.add(BigInteger.ONE);
                continue;
            }

            String thisValDec = thisVal.toString(10);
            ECKey key = getNewECKey(thisVal);
            String testBtcAddress = getBtcAddress(key);

            if (bloomFilter.has(testBtcAddress)) {
                if (!KnownKeysProvider.getKnownKeys().contains(thisValDec)) {
                    logFound(key, thisValDec);
                }
                rv.getFoundKeys().add(thisValDec);
            }

            thisVal = thisVal.add(BigInteger.ONE);

        } while (thisVal.compareTo(to) <= 0);

        return rv;
    }

    private static void logFound(ECKey key, String decimalKey) {

        if (LOGGED_KEYS.contains(decimalKey)) {
            return;
        }

        String privateKeyAsHex = key.getPrivateKeyAsHex();
        String publicKeyAsHex = key.getPublicKeyAsHex();
        String testBtcAddress = getBtcAddress(key);

        System.out.println(
                "\n\n----------------------\n\n" +
                        " Found private key:\n" +
                        "  dec: " + decimalKey + "\n" +
                        "  wif: " + keyToWif(key) + "\n" +
                        "  pub: " + testBtcAddress);

        LOGGED_KEYS.add(decimalKey);
    }
}