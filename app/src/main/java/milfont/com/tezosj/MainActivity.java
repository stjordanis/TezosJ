package milfont.com.tezosj;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import org.json.JSONObject;

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

                JSONObject jsonObject = crypto.generateKeys(words, "test");

                try
                {
                    Log.i("output", "mnemonic   : " + jsonObject.get("mnemonic"));
                    Log.i("output", "passphrase : " + jsonObject.get("passphrase"));
                    Log.i("output", "sk         : " + jsonObject.get("sk"));
                    Log.i("output", "pk         : " + jsonObject.get("pk"));
                    Log.i("output", "pkh        : " + jsonObject.get("pkh"));

                    myTezosAddress = jsonObject.get("pkh").toString();

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


                    // Gets BALANCE for a given address.
                    try
                    {
                        JSONObject result = rpc.getBalance(myTezosAddress);
                        Log.i("output", "Your balance is : " + result.get("ok"));
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
