////////////////////////////////////////////////////////////////////
// WARNING - This software uses the real TezosGateway Betanet blockchain.
//           Use it with caution.
////////////////////////////////////////////////////////////////////

package milfont.com.tezosj_android.data;

import android.security.keystore.KeyProperties;
import android.util.Base64;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.libsodium.jni.NaCl;

import static milfont.com.tezosj_android.helper.Constants.TZJ_KEY_ALIAS;
import static milfont.com.tezosj_android.helper.Constants.UTEZ;
import static org.libsodium.jni.encoders.Encoder.HEX;

import java.math.BigDecimal;
import java.security.KeyStore;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.lang.Object;

import milfont.com.tezosj_android.helper.Base58Check;
import milfont.com.tezosj_android.model.EncKeys;
import milfont.com.tezosj_android.model.SignedOperationGroup;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import org.apache.commons.lang3.ArrayUtils;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class TezosGateway
{

    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private static final MediaType textPlainMT = MediaType.parse("text/plain; charset=utf-8");
    private static final Integer HTTP_TIMEOUT = 20;


    // Sends request for Tezos node.
    private Object query(String endpoint, String data) throws Exception
    {

        JSONObject result = null;
        Boolean methodPost = false;
        Request request = null;


        final MediaType MEDIA_PLAIN_TEXT_JSON = MediaType.parse("application/json");
        String DEFAULT_PROVIDER = "https://rpc.tezrpc.me";
        RequestBody body = RequestBody.create(textPlainMT, DEFAULT_PROVIDER + endpoint);

        if (data != null)
        {
            methodPost = true;
            body = RequestBody.create(MEDIA_PLAIN_TEXT_JSON, data.getBytes());
        }

        if (methodPost == false)
        {
            request = new Request.Builder()
                    .url(DEFAULT_PROVIDER + endpoint)
                    .build();
        }
        else
        {

            request = new Request.Builder()
                    .url(DEFAULT_PROVIDER + endpoint)
                    .addHeader("Content-Type", "text/plain")
                    .post(body)
                    .build();
        }

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(HTTP_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(HTTP_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(HTTP_TIMEOUT, TimeUnit.SECONDS)
                .build();

        try
        {

            Response response = client.newCall(request).execute();
            String strResponse = response.body().string();

            if (isJSONObject(strResponse))
            {
                result = new JSONObject(strResponse);
            }
            else
            {
                if (isJSONArray(strResponse))
                {
                    JSONArray myJSONArray = new JSONArray(strResponse);
                    result = new JSONObject();
                    result.put("result", myJSONArray);
                }
                else
                {
                    // If response is not a JSONObject nor JSONArray...
                    // (can be a primitive).
                    result = new JSONObject();
                    result.put("result", strResponse);
                }
            }
        }
        catch (Exception e)
        {
            // If there is a real error...
            e.printStackTrace();
            result = new JSONObject();
            result.put("result", e.toString());
        }

        return result;
    }


    // RPC methods.


    public JSONObject getHead() throws Exception
    {
        return (JSONObject) query("/chains/main/blocks/head", null);
    }

    // Gets the balance for a given address.
    public JSONObject getBalance(String address) throws Exception
    {
        JSONObject result = (JSONObject) query("/chains/main/blocks/head/context/contracts/" + address + "/balance", null);

        return result;
    }

    // Prepares ans sends an operation to the Tezos node.
    private JSONObject sendOperation(JSONArray operations, EncKeys encKeys) throws Exception
    {
        JSONObject result = new JSONObject();

        JSONObject head = new JSONObject();
        String forgedOperationGroup = "";

        head = (JSONObject) query("/chains/main/blocks/head/header", null);
        forgedOperationGroup = forgeOperations(head, operations);

        SignedOperationGroup signedOpGroup = signOperationGroup(forgedOperationGroup, encKeys);
        String operationGroupHash = computeOperationHash(signedOpGroup);
        JSONObject appliedOp = applyOperation(head, operations, operationGroupHash, forgedOperationGroup, signedOpGroup);
        JSONObject opResult = checkAppliedOperationResults(appliedOp);

        if (opResult.get("result").toString().length() == 0)
        {
            String injectedOperation = injectOperation(signedOpGroup);
            String operation_result = (String) ((JSONObject) (((JSONObject) ((((JSONObject) (((JSONArray) (((JSONObject) ((JSONArray) (appliedOp.get("result"))).get(0)).get("contents"))).get(0))).get("metadata")))).get("operation_result"))).get("status");

            if (operation_result.equals("applied"))
            {
                operation_result = "Operation successful";
            }
            result.put("result", operation_result);

        }
        else
        {
            result.put("result", opResult.get("result").toString());
        }

        return result;
    }

    // Sends a transaction to the Tezos node.
    public JSONObject sendTransaction(String from, String to, BigDecimal amount, String fee, String gasLimit, String storageLimit, EncKeys encKeys) throws Exception
    {
        JSONObject result = new JSONObject();

        BigDecimal roundedAmount = amount.setScale(2, BigDecimal.ROUND_HALF_UP);
        JSONArray operations = new JSONArray();
        JSONObject transaction = new JSONObject();
        JSONObject head = new JSONObject();
        JSONObject account = new JSONObject();
        JSONObject parameters = new JSONObject();
        JSONArray argsArray = new JSONArray();
        Integer counter = 0;

        if (gasLimit == null)
        {
            gasLimit = "200";
        }
        else
        {
            if ((gasLimit.length() == 0) || (gasLimit.equals("0")))
            {
                gasLimit = "200";
            }
        }

        if (storageLimit == null)
        {
            storageLimit = "0";
        }

        head = new JSONObject(query("/chains/main/blocks/head/header", null).toString());
        account = getAccountForBlock(head.get("hash").toString(), from);
        counter = Integer.parseInt(account.get("counter").toString());

        transaction.put("destination", to);
        transaction.put("amount", (String.valueOf(roundedAmount.multiply(BigDecimal.valueOf(UTEZ)).toBigInteger())));
        transaction.put("storage_limit", storageLimit);
        transaction.put("gas_limit", gasLimit);
        transaction.put("counter", String.valueOf(counter + 1));
        transaction.put("fee", fee);
        transaction.put("source", from);
        String OPERATION_KIND_TRANSACTION = "transaction";
        transaction.put("kind", OPERATION_KIND_TRANSACTION);
        parameters.put("prim", "Unit");
        parameters.put("args", argsArray);
        transaction.put("parameters", parameters);

        operations.put(transaction);

        result = (JSONObject) sendOperation(operations, encKeys);


        return result;
    }

    private SignedOperationGroup signOperationGroup(String forgedOperation, EncKeys encKeys) throws Exception
    {

        SignedOperationGroup signedOperationGroup = null;

        JSONObject signed = sign(HEX.decode(forgedOperation), encKeys, "03");

        // Prepares the object to be returned.
        byte[] workBytes = ArrayUtils.addAll(HEX.decode(forgedOperation), HEX.decode((String) signed.get("sig")));
        signedOperationGroup = new SignedOperationGroup(workBytes, (String) signed.get("edsig"), (String) signed.get("sbytes"));

        return signedOperationGroup;

    }

    private String forgeOperations(JSONObject blockHead, JSONArray operations) throws Exception
    {
        JSONObject result = new JSONObject();
        result.put("branch", blockHead.get("hash"));
        result.put("contents", operations);

        return nodeForgeOperations(result.toString());
    }


    private String nodeForgeOperations(String opGroup) throws Exception
    {
        JSONObject response = (JSONObject) query("/chains/main/blocks/head/helpers/forge/operations", opGroup);
        String forgedOperation = (String) response.get("result");

        return ((forgedOperation.replaceAll("\\n", "")).replaceAll("\"", "").replaceAll("'", ""));

    }

    private JSONObject getAccountForBlock(String blockHash, String accountID) throws Exception
    {
        JSONObject result = new JSONObject();

        result = (JSONObject) query("/chains/main/blocks/" + blockHash + "/context/contracts/" + accountID, null);

        return result;
    }

    private String computeOperationHash(SignedOperationGroup signedOpGroup) throws Exception
    {

        byte[] hash = new byte[32];
        int r = NaCl.sodium().crypto_generichash(hash, hash.length, signedOpGroup.getTheBytes(), signedOpGroup.getTheBytes().length, signedOpGroup.getTheBytes(), 0);

        return Base58Check.encode(hash);
    }

    private JSONObject nodeApplyOperation(JSONArray payload) throws Exception
    {
        JSONObject response = (JSONObject) query("/chains/main/blocks/head/helpers/preapply/operations", payload.toString());

        return response;
    }

    private JSONObject applyOperation(JSONObject head, JSONArray operations, String operationGroupHash, String forgedOperationGroup, SignedOperationGroup signedOpGroup) throws Exception
    {
        JSONArray payload = new JSONArray();
        JSONObject jsonObject = new JSONObject();

        jsonObject.put("protocol", head.get("protocol"));
        jsonObject.put("branch", head.get("hash"));
        jsonObject.put("contents", operations);
        jsonObject.put("signature", signedOpGroup.getSignature());
        payload.put(jsonObject);

        return nodeApplyOperation(payload);
    }

    private JSONObject checkAppliedOperationResults(JSONObject appliedOp) throws Exception
    {
        JSONObject returned = new JSONObject();
        String error = "", status = "";

        String[] validAppliedKinds = new String[]{"activate_account", "reveal", "transaction", "origination", "delegation"};
        JSONObject firstAppliedOp = new JSONObject();
        firstAppliedOp = appliedOp;

        String firstApplyed = firstAppliedOp.toString().replaceAll("\\\\n", "").replaceAll("\\\\", "");
        JSONArray result = new JSONArray(new JSONObject(firstApplyed).get("result").toString());
        JSONObject first = (JSONObject) result.get(0);

        // Checks if the operation was applied.
        if (first.has("kind") == true)
        {
            if (Arrays.asList(validAppliedKinds).contains(first.get("kind").toString()) == false)
            {
                // Did not apply the operation.
                returned.put("result", "Could not apply operation because: " + first.get("id"));
            }
            else
            {
                // Success.
                returned.put("result", "");
            }
        }
        else
        {
            if (first.has("contents"))
            {
                // The operation was applied, so gets the operation_results.
                JSONObject operation_result = (JSONObject) ((JSONObject) (((JSONObject) (((JSONArray) first.get("contents")).get(0))).get("metadata"))).get("operation_result");

                if (operation_result.has("status"))
                {
                    status = (String) ((JSONObject) ((JSONObject) (((JSONObject) (((JSONArray) first.get("contents")).get(0))).get("metadata"))).get("operation_result")).get("status");

                    if (status.equals("applied"))
                    {
                        // Success.
                        returned.put("result", "");
                    }
                    else if (operation_result.has("errors"))
                    {
                        error = (String) ((JSONObject) ((JSONArray) ((JSONObject) ((JSONObject) (((JSONObject) (((JSONArray) first.get("contents")).get(0))).get("metadata"))).get("operation_result")).get("errors")).get(0)).get("id");
                        returned.put("result", "Error: " + operation_result);
                    }
                    else
                    {
                        returned.put("result", "Error");
                    }

                }
                else
                {
                    returned.put("result", "Error");
                }
            }
            else
            {
                returned.put("result", "Error");
            }
        }
        return returned;
    }


    private String injectOperation(SignedOperationGroup signedOpGroup) throws Exception
    {
        String payload = signedOpGroup.getSbytes();
        return nodeInjectOperation("\"" + payload + "\"");
    }

    private String nodeInjectOperation(String payload) throws Exception
    {
        JSONObject result = (JSONObject) query("/injection/operation?chain=main", payload);
        String injectedOperation = result.get("result").toString();

        return injectedOperation;
    }

    public JSONObject sign(byte[] bytes, EncKeys keys, String watermark) throws Exception
    {
        // Access wallet keys to have authorization to perform the operation.
        byte[] byteSk = keys.getEncPrivateKey();
        byte[] decSkBytes = decryptBytes(byteSk, getEncryptionKey(keys));

        StringBuilder builder = new StringBuilder();
        for (byte decSkByte : decSkBytes)
        {
            builder.append((char) (decSkByte));
        }

        // First, we remove the edsk prefix from the decoded private key bytes.
        byte[] edskPrefix = {(byte) 43, (byte) 246, (byte) 78, (byte) 7};
        byte[] decodedSk = Base58Check.decode(builder.toString());
        byte[] privateKeyBytes = Arrays.copyOfRange(decodedSk, edskPrefix.length, decodedSk.length);

        // Then we create a work array and check if the watermark parameter has been passed.
        byte[] workBytes = ArrayUtils.addAll(bytes);

        if (watermark != null)
        {
            byte[] wmBytes = HEX.decode(watermark);
            workBytes = ArrayUtils.addAll(wmBytes, workBytes);
        }

        // Now we hash the combination of: watermark (if exists) + the bytes passed in parameters.
        // The result will end up in the sig variable.
        byte[] hashedWorkBytes = new byte[32];
        int rc = NaCl.sodium().crypto_generichash(hashedWorkBytes, hashedWorkBytes.length, workBytes, workBytes.length, workBytes, 0);

        int[] lengths = {64};
        byte[] sig = new byte[64];
        int r = NaCl.sodium().crypto_sign_detached(sig, lengths, hashedWorkBytes, hashedWorkBytes.length, privateKeyBytes);

        // To create the edsig, we need to concatenate the edsig prefix with the sig and then encode it.
        // The sbytes will be the concatenation of bytes (in hex) + sig (in hex).
        byte[] edsigPrefix = {9, (byte) 245, (byte) 205, (byte) 134, 18};
        byte[] edsigPrefixedSig = new byte[edsigPrefix.length + sig.length];
        edsigPrefixedSig = ArrayUtils.addAll(edsigPrefix, sig);
        String edsig = Base58Check.encode(edsigPrefixedSig);
        String sbytes = HEX.encode(bytes) + HEX.encode(sig);

        // Now, with all needed values ready, we create and deliver the response.
        JSONObject response = new JSONObject();
        response.put("bytes", HEX.encode(bytes));
        response.put("sig", HEX.encode(sig));
        response.put("edsig", edsig);
        response.put("sbytes", sbytes);

        return response;

    }

    // Tests if a string is a valid JSON.
    private Boolean isJSONObject(String myStr)
    {
        try
        {
            JSONObject testJSON = new JSONObject(myStr);
            testJSON = null;
            return true;
        }
        catch (JSONException e)
        {
            return false;
        }
    }

    // Tests if s string is a valid JSON Array.
    private Boolean isJSONArray(String myStr)
    {
        try
        {
            JSONArray testJSONArray = new JSONArray(myStr);
            testJSONArray = null;
            return true;
        }
        catch (JSONException e)
        {
            return false;
        }
    }


    private byte[] getEncryptionKey(EncKeys keys)
    {
        try
        {
            String base64EncryptedPassword = keys.getEncP();
            String base64EncryptionIv = keys.getEncIv();

            byte[] encryptionIv = Base64.decode(base64EncryptionIv, Base64.DEFAULT);
            byte[] encryptionPassword = Base64.decode(base64EncryptedPassword, Base64.DEFAULT);

            KeyStore keystore = KeyStore.getInstance("AndroidKeyStore");
            keystore.load(null);

            SecretKey secretKey = (SecretKey) keystore.getKey(TZJ_KEY_ALIAS, null);
            Cipher cipher = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/" + KeyProperties.BLOCK_MODE_CBC + "/" + KeyProperties.ENCRYPTION_PADDING_PKCS7);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(encryptionIv));
            byte[] passwordBytes = cipher.doFinal(encryptionPassword);
            String password = new String(passwordBytes, "UTF-8");

            return passwordBytes;

        }
        catch (Exception e)
        {
            return null;
        }

    }

    // Decryption routine.
    private static byte[] decryptBytes(byte[] encrypted, byte[] key)
    {
        try
        {
            SecretKeySpec keySpec = null;
            Cipher cipher = null;
            keySpec = new SecretKeySpec(key, "AES/ECB/PKCS7Padding");
            cipher = Cipher.getInstance("AES/ECB/PKCS7Padding");
            cipher.init(Cipher.DECRYPT_MODE, keySpec);

            return cipher.doFinal(encrypted);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }


}
