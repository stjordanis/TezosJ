package milfont.com.tezosj;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import org.json.JSONObject;

import java.math.BigDecimal;

import milfont.com.tezosj_android.domain.Crypto;
import milfont.com.tezosj_android.domain.Rpc;

import static milfont.com.tezosj_android.helper.Constants.TEZOS_SYMBOL;

public class MainActivity extends AppCompatActivity
{
    final Rpc rpc = new Rpc();
    final Crypto crypto = new Crypto();
    String myTezosAddress = "";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        testMethods();
    }

    private void testMethods()
    {
        Thread thread = new Thread(new Runnable()
        {
            @Override
            public void run()
            {

                String words = crypto.generateMnemonic();
                JSONObject myKeys = crypto.generateKeys(words, "test");

                try
                {
                    Log.i("output", "mnemonic   : " + myKeys.get("mnemonic"));
                    Log.i("output", "passphrase : " + myKeys.get("passphrase"));
                    Log.i("output", "sk         : " + myKeys.get("sk"));
                    Log.i("output", "pk         : " + myKeys.get("pk"));
                    Log.i("output", "pkh        : " + myKeys.get("pkh"));

                    myTezosAddress = myKeys.get("pkh").toString();

                }
                catch (Exception e)
                {
                }

                if (myTezosAddress.length() > 0)
                {
                    // Checks if ADDRESS is valid.
                    Boolean result = crypto.checkAddress(myTezosAddress);
                    Log.i("output", "\n\nThe address " + myTezosAddress + " is " + (result ? "valid" : "invalid"));

                    // Gets BALANCE for a given address.
                    String myBalance = rpc.getBalance(myTezosAddress);
                    Log.i("output", "Your balance is : " + new BigDecimal(myBalance).divide(BigDecimal.valueOf(1000000)) + TEZOS_SYMBOL +"\n\n");

                }

                // Gets HEAD object from the connected node for a given address.
                //String result = rpc.getHead();
                //Log.i("output", "Head : " + result.toString());
            }
        });

        thread.start();
    }
}
