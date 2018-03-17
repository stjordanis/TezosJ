package milfont.com.tezosj_android.domain;

import org.json.JSONObject;

import java.math.BigDecimal;

import milfont.com.tezosj_android.data.TezosGateway;


public class Rpc
{

    public JSONObject getHead() throws Exception
    {
        TezosGateway tzg = new TezosGateway();
        JSONObject response = tzg.getHead();

        return response;
    }

    public JSONObject sendOperation(JSONObject operation, JSONObject keys, Integer fee)
    {
        TezosGateway tzg = new TezosGateway();
        return tzg.sendOperation(operation, keys, fee);
    }


    public JSONObject getBalance(String address) throws Exception
    {
        TezosGateway tzg = new TezosGateway();
        JSONObject response = tzg.getBalance(address);

        return response;
    }

    public JSONObject transfer(JSONObject keys, String from, String to, BigDecimal amount, Integer fee) throws Exception
    {
        TezosGateway tzg = new TezosGateway();
        JSONObject response = tzg.transfer(keys, from, to, amount, fee);

        return response;
    }

}
