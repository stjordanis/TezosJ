package milfont.com.tezosj_android.domain;

import org.json.JSONObject;

import milfont.com.tezosj_android.data.TezosGateway;


public class Node
{

    public void setProvider(String provider)
    {
        TezosGateway tzg = new TezosGateway();
        tzg.setProvider(provider);
    }

    public void resetProvider()
    {
        TezosGateway tzg = new TezosGateway();
        tzg.resetProvider();
    }

    public JSONObject query(String endpoint, String data)
    {
        TezosGateway tzg = new TezosGateway();
        return tzg.query(endpoint, data);
    }
}
