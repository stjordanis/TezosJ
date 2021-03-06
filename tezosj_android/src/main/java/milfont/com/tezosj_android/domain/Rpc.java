package milfont.com.tezosj_android.domain;

import org.json.JSONObject;
import java.math.BigDecimal;

import milfont.com.tezosj_android.data.TezosGateway;
import milfont.com.tezosj_android.model.EncKeys;


public class Rpc
{

    private TezosGateway tezosGateway = null;


    public Rpc()
    {
        this.tezosGateway = new TezosGateway();
    }


    public String getHead()
    {
        JSONObject result = new JSONObject();
        String response = "";

        try
        {
            response = (String) tezosGateway.getHead().get("result");
            result.put("result", response);

        }
        catch (Exception e)
        {
            e.printStackTrace();
            try
            {
                result.put("result", "An error occured when trying to do getHead operation. See stacktrace for more info.");
            }
            catch (Exception f)
            {
                f.printStackTrace();
            }
        }

        return response;
    }

    public JSONObject getBalance(String address)
    {
        JSONObject result = new JSONObject();
        String response = "";

        try
        {
            response = (String) tezosGateway.getBalance(address).get("result");
            result.put("result", response);

        }
        catch (Exception e)
        {
            e.printStackTrace();
            try
            {
                result.put("result", e.toString());
            }
            catch (Exception f)
            {
                f.printStackTrace();
            }
        }

        return result;

    }

    public JSONObject transfer(String from, String to, BigDecimal amount, String fee, String gasLimit, String storageLimit, EncKeys encKeys)
    {
        JSONObject result = new JSONObject();

        try
        {
            result = (JSONObject) tezosGateway.sendTransaction(from, to, amount, fee, gasLimit, storageLimit, encKeys);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new java.lang.RuntimeException("An error occured while trying to do perform an operation. See stacktrace for more info.");
        }

        return result;

    }

}
