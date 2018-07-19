package milfont.com.tezosj_android.domain;

import org.json.JSONObject;

import java.math.BigDecimal;

import milfont.com.tezosj_android.data.TezosGateway;

public class Rpc
{

    public String getHead()
    {
        String response = "";

        try
        {
            TezosGateway tzg = new TezosGateway();
            response = (String) tzg.getHead().get("ok");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return response;
    }

    public String sendOperation(JSONObject operation, JSONObject keys, Integer fee)
    {
        String response = "";

        try
        {
            // TODO : Implement sendOperation.
            // TezosGateway tzg = new TezosGateway();
            //response = (String) tzg.sendOperation(operation, keys, fee).get("ok");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return response;
    }

    public String getBalance(String address)
    {
        String response = "";

        try
        {
            TezosGateway tzg = new TezosGateway();
            response = (String) tzg.getBalance(address).get("ok");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return response.replaceAll("[^\\d.]", "");
    }

    public String transfer(JSONObject keys, String from, String to, BigDecimal amount, Integer fee)
    {
        String response = "";

        try
        {
            // TezosGateway tzg = new TezosGateway();
            // TODO: Implement Transfer.
            //response = (String) tzg.transfer(keys, from, to, amount, fee).get("ok");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return response;
    }

}
