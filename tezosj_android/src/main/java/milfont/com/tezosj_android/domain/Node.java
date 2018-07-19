package milfont.com.tezosj_android.domain;

import milfont.com.tezosj_android.data.TezosGateway;

public class Node
{

    public void setProvider(String provider)
    {
        try
        {
            TezosGateway tzg = new TezosGateway();
            tzg.setProvider(provider);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    public void resetProvider()
    {
        try
        {
            TezosGateway tzg = new TezosGateway();
            tzg.resetProvider();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public String query(String endpoint, String data)
    {
        String response = "";

        try
        {
            TezosGateway tzg = new TezosGateway();
            response = (String) tzg.query(endpoint, data).get("ok");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return response;
    }

}
