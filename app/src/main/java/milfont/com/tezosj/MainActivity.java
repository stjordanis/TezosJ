package milfont.com.tezosj;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import org.json.JSONObject;
import milfont.com.tezosj_android.domain.Rpc;

public class MainActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Rpc rpc = new Rpc();

        Thread thread = new Thread(new Runnable()
        {
            @Override
            public void run()
            {

                // Gets BALANCE for a given address.
                try
                {
                    JSONObject result = rpc.getBalance("tz1ZmsfxQrzHk8kjuYJp765LMg1ZpXbsqbPf");
                    Log.i("output", "Your balance is : " + result.get("ok"));
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }


                // Gets HEAD object from the connected node for a given address.
                try
                {
                    JSONObject result = rpc.getHead();
                    Log.i("output", "Head : " + result.toString());
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }


            }
        });

        thread.start();


    }

}
