package milfont.com.tezosj_android.domain;

import org.json.JSONObject;
import milfont.com.tezosj_android.data.TezosGateway;

public class Crypto
{

    public JSONObject generateKeys(String mnemonic, String passphrase)
    {
        TezosGateway tzg = new TezosGateway();
        JSONObject jsonResponse = tzg.generateKeys(mnemonic, passphrase);

        return jsonResponse;
    }

    public JSONObject generateKeysNoSeed()
    {
        TezosGateway tzg = new TezosGateway();
        JSONObject jsonResponse = tzg.generateKeysNoSeed();

        return jsonResponse;
    }

    public JSONObject generateKeysSalted(String mnemonic, String passphrase)
    {
        TezosGateway tzg = new TezosGateway();
        JSONObject jsonResponse = tzg.generateKeysSalted(mnemonic, passphrase);

        return jsonResponse;
    }

    public JSONObject generateKeysFromSeedMulti(String mnemonic, String passphrase, Integer n)
    {
        TezosGateway tzg = new TezosGateway();
        JSONObject jsonResponse = tzg.generateKeysFromSeedMulti(mnemonic, passphrase, n);

        return jsonResponse;
    }

    public JSONObject sign(Byte[] bytes, String sk)
    {
        TezosGateway tzg = new TezosGateway();
        JSONObject jsonResponse = tzg.sign(bytes, sk);

        return jsonResponse;
    }

    public Boolean verify(Byte[] bytes, String signature, String pk)
    {
        TezosGateway tzg = new TezosGateway();
        Boolean response = tzg.verify(bytes, signature, pk);

        return response;
    }

    public String generateMnemonic()
    {
        TezosGateway tzg = new TezosGateway();
        String response = tzg.generateMnemonic();

        return response;
    }

    public Boolean checkAddress(String address)
    {
        TezosGateway tzg = new TezosGateway();
        Boolean response = tzg.checkAddress(address);

        return response;
    }

}
