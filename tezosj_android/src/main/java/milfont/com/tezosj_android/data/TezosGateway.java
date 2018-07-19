////////////////////////////////////////////////////////////////////
// WARNING - This software uses the real Tezos Betanet blockchain.
//           Use it with caution.
////////////////////////////////////////////////////////////////////

package milfont.com.tezosj_android.data;

import org.bitcoinj.crypto.MnemonicCode;
import org.json.JSONArray;
import org.json.JSONObject;
import org.libsodium.jni.NaCl;

import static org.libsodium.jni.encoders.Encoder.HEX;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import milfont.com.tezosj_android.helper.Base58;
import milfont.com.tezosj_android.helper.Base58Check;
import milfont.com.tezosj_android.helper.KeyPair;
import milfont.com.tezosj_android.helper.MyCryptoGenericHash;
import milfont.com.tezosj_android.helper.Sha256Hash;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class TezosGateway
{

    final String DEFAULT_PROVIDER = "https://rpc.tezrpc.me";
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    public static final MediaType textPlainMT = MediaType.parse("text/plain; charset=utf-8");
    public static final Integer HTTP_TIMEOUT = 20;
    public static String OPERATION_KIND_TRANSACTION = "transaction";


    // Crypto methods.

    public JSONObject generateKeys(String mnemonic, String passphrase)
    {
        JSONObject response = new JSONObject();

        try
        {
            MnemonicCode mc = new MnemonicCode();
            String cleanMnemonic = mnemonic.replace("[", "");
            cleanMnemonic = cleanMnemonic.replace("]", "");

            List<String> items = Arrays.asList(cleanMnemonic.split("\\s*,\\s*"));
            byte[] src_seed = mc.toSeed(items, passphrase);
            byte[] seed = Arrays.copyOfRange(src_seed, 0, 32);

            KeyPair key = new KeyPair(seed);
            byte[] sodiumPublicKey = key.getPublicKey().toBytes();
            byte[] sodiumPrivateKey = key.getPrivateKey().toBytes();

            // These are our prefixes
            byte[] edpkPrefix = {(byte) 13, (byte) 15, (byte) 37, (byte) 217};
            byte[] edskPrefix = {(byte) 43, (byte) 246, (byte) 78, (byte) 7};
            byte[] tz1Prefix = {(byte) 6, (byte) 161, (byte) 159};

            // Create Tezos PK.
            byte[] prefixedPubKey = new byte[36];
            System.arraycopy(edpkPrefix, 0, prefixedPubKey, 0, 4);
            System.arraycopy(sodiumPublicKey, 0, prefixedPubKey, 4, 32);

            byte[] firstFourOfDoubleChecksum = Sha256Hash.hashTwiceThenFirstFourOnly(prefixedPubKey);
            byte[] prefixedPubKeyWithChecksum = new byte[40];
            System.arraycopy(prefixedPubKey, 0, prefixedPubKeyWithChecksum, 0, 36);
            System.arraycopy(firstFourOfDoubleChecksum, 0, prefixedPubKeyWithChecksum, 36, 4);

            String TezosPkString = Base58.encode(prefixedPubKeyWithChecksum);

            // Create Tezos SK.
            byte[] prefixedSecKey = new byte[68];
            System.arraycopy(edskPrefix, 0, prefixedSecKey, 0, 4);
            System.arraycopy(sodiumPrivateKey, 0, prefixedSecKey, 4, 64);

            firstFourOfDoubleChecksum = Sha256Hash.hashTwiceThenFirstFourOnly(prefixedSecKey);
            byte[] prefixedSecKeyWithChecksum = new byte[72];
            System.arraycopy(prefixedSecKey, 0, prefixedSecKeyWithChecksum, 0, 68);
            System.arraycopy(firstFourOfDoubleChecksum, 0, prefixedSecKeyWithChecksum, 68, 4);

            String TezosSkString = Base58.encode(prefixedSecKeyWithChecksum);

            //create tezos PKHash
            byte[] genericHash = new byte[20];
            genericHash = MyCryptoGenericHash.cryptoGenericHash(sodiumPublicKey, genericHash.length);

            byte[] prefixedGenericHash = new byte[23];
            System.arraycopy(tz1Prefix, 0, prefixedGenericHash, 0, 3);
            System.arraycopy(genericHash, 0, prefixedGenericHash, 3, 20);

            firstFourOfDoubleChecksum = Sha256Hash.hashTwiceThenFirstFourOnly(prefixedGenericHash);
            byte[] prefixedPKhashWithChecksum = new byte[27];
            System.arraycopy(prefixedGenericHash, 0, prefixedPKhashWithChecksum, 0, 23);
            System.arraycopy(firstFourOfDoubleChecksum, 0, prefixedPKhashWithChecksum, 23, 4);

            String pkHash = Base58.encode(prefixedPKhashWithChecksum);

            // Builds JSON to return.
            response.put("mnemonic", items);
            response.put("passphrase", passphrase);
            response.put("sk", TezosSkString);
            response.put("pk", TezosPkString);
            response.put("pkh", pkHash);

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return response;
    }

    public JSONObject sign(byte[] bytes, String sk)
    {
        JSONObject response = new JSONObject();

        int[] lengths = {64};
        byte[] sig = new byte[64];

        try
        {
            byte[] byteDecodedSk = Base58Check.decode(sk);
            byte[] slicedSig = new byte[64];

            for (int i = (slicedSig.length - 1); i >= 0; i--)
            {
                slicedSig[i] = byteDecodedSk[i + 4];
            }

            int r = NaCl.sodium().crypto_sign_detached(sig, lengths, bytes, bytes.length, slicedSig);

            byte[] edsigPrefix = {9, (byte) 245, (byte) 205, (byte) 134, 18};
            int totalArraySize = sig.length + edsigPrefix.length;
            byte[] byteEdsig = new byte[totalArraySize];

            System.arraycopy(edsigPrefix, 0, byteEdsig, 0, 5);

            for (int i = 0; i < sig.length; i++)
            {
                byteEdsig[i + 5] = sig[i];
            }

            String edsig = Base58Check.encode(byteEdsig);
            String sbytes = HEX.encode(bytes) + HEX.encode(sig);

            // Now, with all needed values ready, creates the response.
            response.put("bytes", HEX.encode(bytes));
            response.put("sig", HEX.encode(sig));
            response.put("edsig", edsig);
            response.put("sbytes", sbytes);

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return response;
    }

    public String generateMnemonic()
    {
        String result = "";

        try
        {
            MnemonicCode mc = new MnemonicCode();
            byte[] bytes = new byte[20];
            new java.util.Random().nextBytes(bytes);
            List<String> code = mc.toMnemonic(bytes);
            result = code.toString();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return result;
    }

    public Boolean checkAddress(String address)
    {
        Base58Check base58Check = new Base58Check();

        try
        {
            byte[] result = base58Check.decode(address);
            return true;
        }
        catch (Exception e)
        {
            return false;
        }

    }


    public JSONObject query(String endpoint, String data)
    {

        JSONObject result = new JSONObject();

        RequestBody body = RequestBody.create(textPlainMT, DEFAULT_PROVIDER + endpoint);

        if (data != null)
        {
            body = RequestBody.create(JSON, data);
        }

        Request request = new Request.Builder()
                .url(DEFAULT_PROVIDER + endpoint)
                .build();


        try
        {
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(HTTP_TIMEOUT, TimeUnit.SECONDS)
                    .writeTimeout(HTTP_TIMEOUT, TimeUnit.SECONDS)
                    .readTimeout(HTTP_TIMEOUT, TimeUnit.SECONDS)
                    .build();

            Response response = client.newCall(request).execute();

            try
            {
                result.put("ok", response.body().string());
            }
            catch (Exception e)
            {
            }

        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return result;
    }


    // RPC methods.

    public JSONObject getHead() throws Exception
    {
        return query("/chains/main/blocks/head", null);
    }

    public JSONObject getBalance(String address) throws Exception
    {
        JSONObject result = query("/chains/main/blocks/head/context/contracts/" + address + "/balance", null);

        return result;
    }




    //////////////////////////////////////////////////////////
    //
    // TODO features...
    //
    //////////////////////////////////////////////////////////

    public JSONObject sendOperation(JSONObject operation, JSONObject keys, Integer fee)
    {

        // TODO : Implement this feature.
        JSONObject jsonObject = new JSONObject();
        return jsonObject;
    }

    public void transfer(JSONObject keys, String from, String to, BigDecimal amount, Integer fee)
    {
        // TODO : Implement this feature.
    }


    // Contract methods.

    public void originate(String address)
    {
        // TODO : Implement this feature.
    }

    public JSONObject storage(String contractAddress)
    {
        JSONObject response = null;

        // TODO : Implement this feature.

        return response;
    }

    public String load(String contractAddress)
    {
        String response = "";

        // TODO : Implement this feature.

        return response;
    }

    public JSONArray watch(String contractAddress, Integer interval)
    {
        JSONArray response = null;

        // TODO : Implement this feature.

        return response;
    }

    public JSONObject send(String contractAddress, String[] keys, Integer amount, String parameter, Integer fee)
    {
        JSONObject response = null;

        // TODO : Implement this feature.

        return response;
    }

    public JSONObject generateKeysNoSeed()
    {
        JSONObject response = null;

        // TODO : Implement this feature.

        return response;
    }

    public JSONObject generateKeysSalted(String mnemonic, String passphrase)
    {
        JSONObject response = null;

        // TODO : Implement this feature.

        return response;
    }

    public JSONObject generateKeysFromSeedMulti(String mnemonic, String passphrase, Integer n)
    {
        JSONObject response = null;

        // TODO : Implement this feature.

        return response;
    }


    public Boolean verify(Byte[] bytes, String signature, String pk)
    {
        // TODO : Implement this feature.

        return false;
    }

    // Node methods.

    public void setProvider(String provider)
    {
        // TODO : Implement this feature.
        // setProvider(provider);
    }

    public void resetProvider()
    {
        // TODO : Implement this feature.
        // resetProvider(DEFAULT_PROVIDER);
    }


}