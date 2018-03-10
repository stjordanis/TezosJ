package milfont.com.tezosj_android.domain;

import org.json.JSONArray;
import org.json.JSONObject;

import milfont.com.tezosj_android.data.TezosGateway;


public class Contract
{

    public void originate(String address)
    {
        TezosGateway tzg = new TezosGateway();
        tzg.originate(address);
    }

    public JSONObject storage(String contractAddress)
    {
        TezosGateway tzg = new TezosGateway();
        JSONObject response = tzg.storage(contractAddress);

        return response;
    }

    public String load(String contractAddress)
    {
        TezosGateway tzg = new TezosGateway();
        String response = tzg.load(contractAddress);

        return response;
    }

    public JSONArray watch(String contractAddress, Integer interval)
    {
        TezosGateway tzg = new TezosGateway();
        JSONArray response = tzg.watch(contractAddress, interval);

        return response;

    }

    public JSONObject send(String contractAddress, String[] keys, Integer amount, String parameter, Integer fee)
    {
        TezosGateway tzg = new TezosGateway();
        JSONObject response = tzg.send(contractAddress, keys, amount, parameter, fee);

        return response;
    }

}
