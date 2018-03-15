package milfont.com.tezosj_android.data;

import org.bitcoinj.crypto.MnemonicCode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.libsodium.jni.Sodium;

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

    final String DEFAULT_PROVIDER = "https://tezrpc.me/api";
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

    public JSONObject sign(byte[] bytes, String sk)
    {
        JSONObject response = new JSONObject();

        int[] lengths = {32};
        byte[] sig = new byte[32];
        Sodium.crypto_sign_detached(sig, lengths, bytes, bytes.length, sk.getBytes());

        byte[] myPrefixEdsig = {9, (byte) 245, (byte) 205, (byte) 134, 18};
        byte[] prefixedSig = new byte[37];
        System.arraycopy(myPrefixEdsig, 0, prefixedSig, 0, 5);
        System.arraycopy(sig, 0, prefixedSig, 5, 32);

        String edsig = Base58.encode(prefixedSig);

        byte[] signedBytes = new byte[64];
        System.arraycopy(bytes, 0, signedBytes, 0, 32);
        System.arraycopy(sig, 0, signedBytes, 32, 32);
        String sbytes = Base58.encode(signedBytes);

        try
        {
            response.put("bytes", bytes);
            response.put("sig", sig);
            response.put("edsig", edsig);
            response.put("sbytes", sbytes);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

        return response;
    }


    public Boolean verify(Byte[] bytes, String signature, String pk)
    {
        // TODO : Implement this feature.

        return false;
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

    public JSONObject query(String endpoint, String data)
    {

        JSONObject result = null;

        RequestBody body = RequestBody.create(textPlainMT, DEFAULT_PROVIDER + endpoint);

        if (data != null)
        {
            body = RequestBody.create(JSON, data);
        }

        Request request = new Request.Builder()
                .url(DEFAULT_PROVIDER + endpoint)
                .post(body)
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
                result = new JSONObject(response.body().string());
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
        return query("/blocks/head", null);
    }


    public JSONObject sendOperation(JSONObject operation, JSONObject keys, Integer fee)
    {
        JSONObject result = new JSONObject();

        JSONObject head = new JSONObject();
        Integer counter = 0;
        String pred_block = "";
        JSONObject sopbytes = new JSONObject();
        JSONObject returnedContracts = new JSONObject();
        JSONArray operations = new JSONArray();

        head = query("/blocks/head", null);

        try
        {

            pred_block = head.get("predecessor").toString();

            JSONObject opOb = new JSONObject();
            opOb.put("branch", pred_block);
            opOb.put("source", keys.get("pkh"));
            operations.put(operation);
            opOb.put("operations", operations);

            if (fee != null)
            {
                result = query("/blocks/prevalidation/proto/context/contracts/" + keys.get("pkh") + "/counter", null);

                counter = Integer.parseInt(result.get("ok").toString()) + 1;

                opOb.put("fee", fee);
                opOb.put("counter", counter);
                opOb.put("public_key", keys.get("pk"));

                result = (JSONObject) query("/blocks/prevalidation/proto/helpers/forge/operations", opOb.toString());

                JSONObject resultOperation = (JSONObject) result.get("ok");
                byte[] opbytes = resultOperation.get("operation").toString().getBytes();
                JSONObject signed = new JSONObject();
                String strSk = keys.get("sk").toString();
                signed = sign(opbytes, strSk);

                byte[] myPrefixOp = {(byte) 5, (byte) 116};

                byte[] prefixedOpHash = new byte[66];
                System.arraycopy(myPrefixOp, 0, prefixedOpHash, 0, 2);
                System.arraycopy(Base58.decode((String) signed.get("sbytes")), 0, prefixedOpHash, 2, 64);

                String oh = Base58.encode(MyCryptoGenericHash.cryptoGenericHash(prefixedOpHash, 34));

                JSONObject myOperation = new JSONObject();
                myOperation.put("pred_block", pred_block);
                myOperation.put("operation_hash", oh);
                myOperation.put("forged_operation", opbytes);
                myOperation.put("signature", signed.get("edsig"));

                result = (JSONObject) query("/blocks/prevalidation/proto/helpers/apply_operation", myOperation.toString());

                returnedContracts = (JSONObject) result.get("contracts");
                result = query("/inject_operation", "\"signedOperationContents\" : " + signed);

                result.put("contracts", returnedContracts);

                return result;

            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }


        JSONObject jsonObject = new JSONObject();
        return jsonObject;

    }


/*
  sendOperation : function(operation, keys, fee)
  {
    var head, counter, pred_block, sopbytes, returnedContracts;
    var promises = []
    promises.push(node.query('/blocks/head'));
    if (typeof fee != 'unfedined')
    {
      promises.push(node.query('/blocks/prevalidation/proto/context/contracts/'+keys.pkh+'/counter'));
    }

    return Promise.all(promises).then(function(f)
    {
      head = f[0];
      pred_block = head.predecessor;
      var opOb =
       {
          "branch": pred_block,
          "source": keys.pkh,
          "operations": [operation]
      }
      if (typeof fee != 'unfedined')
      {
        counter = f[1]+1;
        opOb['fee'] = fee;
        opOb['counter'] = counter;
        opOb['public_key'] = keys.pk;
      }
      return node.query('/blocks/prevalidation/proto/helpers/forge/operations', opOb);
    })
    .then(function(f)
    {
      var opbytes = f.operation;
      var signed = crypto.sign(opbytes, keys.sk);
      sopbytes = signed.sbytes;
      var oh = utility.b58cencode(library.sodium.crypto_generichash(32, utility.hex2buf(sopbytes)), prefix.o);
      return node.query('/blocks/prevalidation/proto/helpers/apply_operation',
       {
          "pred_block": pred_block,
          "operation_hash": oh,
          "forged_operation": opbytes,
          "signature": signed.edsig
      });
    })
    .then(function(f)
    {
      returnedContracts = f.contracts;
      return node.query('/inject_operation',
      {
         "signedOperationContents" : sopbytes,
      });
    })
    .then(function(f)
    {
      f['contracts'] = returnedContracts;
      return f
    });
}

 */


    public JSONObject getBalance(String address) throws Exception
    {
        JSONObject result = query("/blocks/prevalidation/proto/context/contracts/" + address + "/balance", null);

        return result;
    }

    public JSONObject transfer(JSONObject keys, String from, String to, BigDecimal amount, Integer fee)
    {

        BigDecimal roundedAmount = amount.setScale(2, BigDecimal.ROUND_HALF_UP);

        JSONObject operation = new JSONObject();
        JSONObject myKeys = new JSONObject();

        try
        {
            // Prepares operation.
            operation.put("kind", OPERATION_KIND_TRANSACTION);
            operation.put("amount", roundedAmount.multiply(new BigDecimal(100)));
            operation.put("destination", to);

            // Prepares myKeys.
            myKeys.put("pk", keys.get("pk"));
            myKeys.put("pkh", from);
            myKeys.put("sk", keys.get("sk"));

        }
        catch (Exception e)
        {
        }

        return sendOperation(operation, myKeys, fee);
    }


    // Contract methods.

    public void originate(String address)
    {
        // TODO : Implement this feature.
        // originate(address);
    }

    public JSONObject storage(String contractAddress)
    {
        JSONObject response = null;

        // TODO : Implement this feature.
        // storage(contractAddress);

        return response;
    }

    public String load(String contractAddress)
    {
        String response = "";

        // TODO : Implement this feature.
        // load(contractAddress);

        return response;
    }

    public JSONArray watch(String contractAddress, Integer interval)
    {
        JSONArray response = null;

        // TODO : Implement this feature.
        // watch(contractAddress, interval);

        return response;
    }

    public JSONObject send(String contractAddress, String[] keys, Integer amount, String parameter, Integer fee)
    {
        JSONObject response = null;

        // TODO : Implement this feature.
        // send(contractAddress, keys, amount, parameter, fee);

        return response;
    }


}