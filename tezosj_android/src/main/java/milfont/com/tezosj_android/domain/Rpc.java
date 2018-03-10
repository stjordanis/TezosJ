package milfont.com.tezosj_android.domain;

import org.json.JSONObject;

import milfont.com.tezosj_android.data.TezosGateway;


public class Rpc
{

    public JSONObject getHead() throws Exception
    {
        TezosGateway tzg = new TezosGateway();
        JSONObject response = tzg.getHead();

        return response;
    }


    public JSONObject sendOperation(JSONObject operation, String[] keys, Integer fee)
    {
        TezosGateway tzg = new TezosGateway();
        JSONObject response = tzg.sendOperation(operation, keys, fee);

        return response;
    }


    public JSONObject getBalance(String address) throws Exception
    {
        TezosGateway tzg = new TezosGateway();
        JSONObject response = tzg.getBalance(address);

        return response;
    }


}
