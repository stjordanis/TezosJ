package milfont.com.tezosj;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import org.json.JSONObject;

import java.math.BigDecimal;

import milfont.com.tezosj_android.domain.Crypto;
import milfont.com.tezosj_android.domain.Rpc;

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
                    try
                    {
                        Boolean result = crypto.checkAddress(myTezosAddress);
                        Log.i("output", "The address " + myTezosAddress + " is " + (result ? "valid" : "invalid"));
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }


                    JSONObject myBalance = new JSONObject();
                    // Gets BALANCE for a given address.
                    try
                    {
                        myBalance = rpc.getBalance(myTezosAddress);
                        Log.i("output", "Your balance is : " + myBalance.get("ok"));
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }

                    // Transfers funds to address.
                    try
                    {
                        JSONObject result = rpc.transfer(myKeys, myTezosAddress, "tz1Wd9SHPYZbjgiDjJSGoE6MSza7HmyrX35a", new BigDecimal("1000"),0);
                        Log.i("output", "Your balance is : " + myBalance.get("ok"));
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }


                }

                // Gets HEAD object from the connected node for a given address.
                //try
                //{
                //    JSONObject result = rpc.getHead();
                //    Log.i("output", "Head : " + result.toString());
                //}
                //catch (Exception e)
                //{
                //    e.printStackTrace();
                //}

            }
        });

        thread.start();


    }

}
