package milfont.com.tezosj_android.domain;

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

    public String query(String endpoint, String data)
    {
        TezosGateway tzg = new TezosGateway();
        String response = tzg.query(endpoint, data);

        return response;
    }
}
