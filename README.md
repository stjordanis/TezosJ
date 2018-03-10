# TezosJ
TezosJ is a library that enables Java and Android developers to create apps that communicates with Tezos blockchain.
Operations like creating wallets addresses, getting accounts balance, sending funds, might then be integrated to mobile phone apps
or desktop Java applications.

The main purpose of TezosJ Integration Library is to foster development of applications in Java / Android that interacts
with Tezos ecosystem. This might open Tezos to a whole world of software producers, ready to collaborate with the platform.
TezosJ is to play the role of a layer that will translate default Java method calls to Tezos's network real operations
(create_account, transfer_token, etc.).

# Usage

First of all, add 'tools:replace="android:allowBackup"' to your application tag in AndroidManifest.xml, like this:

   <application tools:replace="android:allowBackup"
           android:allowBackup="true"
           android:icon="@mipmap/ic_launcher" ...
        
Don't forget to add uses-Internet permission:

   <uses-permission android:name="android.permission.INTERNET" />


Then add the lines below to your dependencies on Android project's app build.gradle file:

***compile 'com.squareup.okhttp3:okhttp:3.10.0'***

***compile 'com.milfont.tezos:tezosj_android:0.0.3'***

***compile 'org.bitcoinj:bitcoinj-core:0.14.6'***

***compile 'com.github.joshjdevl.libsodiumjni:libsodium-jni-aar:1.0.8'***



Usage example code:

```
                        Crypto crypto = new Crypto();
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

                            // Gets BALANCE for a given Tezos address.
                            Rpc rpc = new Rpc();
                            JSONObject result = rpc.getBalance(myTezosAddress);
                            Log.i("output", "Your balance is : " + result.get("ok"));

                        }
                        catch (Exception e)
                        {
                        }
```

Remember to put the code above inside a Thread to avoid Network on main thread exception.


# Disclaimer

This software is at Alpha stage. It is currently experimental and is under development.
Many features are not fully implemented yet. This version uses Tezos Alphanet.

# Credits

TezosJ is based on Stephen Andrews' EZTZ Javascript library ( https://github.com/stephenandrews/eztz ).
TezosJ uses Libsodium-JNI from Joshjdevl ( https://github.com/joshjdevl/libsodium-jni ).
TezosJ uses BitcoinJ Java Library ( https://github.com/bitcoinj/bitcoinj ).
SPECIAL THANX TO TEZZIGATOR for providing the code for Tezos Key Generation in Java.

# Latest Changes

v0.0.2 - Added checkAddress feature.  &nbsp;  
v0.0.3 - Added generateKeys feature.


