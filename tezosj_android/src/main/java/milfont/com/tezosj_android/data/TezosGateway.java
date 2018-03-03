package milfont.com.tezosj_android.data;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class TezosGateway
{

    final String DEFAULT_PROVIDER = "https://tezrpc.me/api";
    OkHttpClient client = new OkHttpClient();
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    public static final MediaType textPlainMT = MediaType.parse("text/plain; charset=utf-8");


    // Crypto methods.

    public JSONObject generateKeys(String mnemonic, String passphrase)
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

    public JSONObject sign(Byte[] bytes, String sk)
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

    public String generateMnemonic()
    {
        // TODO : Implement this feature.

        return "";
    }

    public Boolean checkAddress(String address)
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

    public String query(String endpoint, String data)
    {
        String result = "";

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
            Response response = client.newCall(request).execute();
            result = response.body().string();
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
        String result = query("/blocks/head", null);
        JSONObject jobj = new JSONObject(result);

        return jobj;
    }


    public JSONObject sendOperation(JSONObject operation, String[] keys, Integer fee)
    {
        JSONObject response = null;

        // TODO : Implement this feature.
        // sendOperation(operation, keys, fee);

        return response;
    }


    public JSONObject getBalance(String address) throws Exception
    {
        String result = query("/blocks/prevalidation/proto/context/contracts/" + address + "/balance", null);
        JSONObject jobj = new JSONObject(result);

        return jobj;
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