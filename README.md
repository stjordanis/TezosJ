# TezosJ
TezosJ is a library that enables Java and Android developers to create apps that communicates with Tezos blockchain.
Operations like creating wallets addresses, getting accounts balance, sending funds, might then be integrated to mobile phone apps
or desktop Java applications.

The main purpose of TezosJ Integration Library is to foster development of applications in Java / Android that interacts
with Tezos ecosystem. This might open Tezos to a whole world of software producers, ready to collaborate with the platform.
TezosJ is to play the role of a layer that will translate default Java method calls to Tezos's network real operations
(create_account, transfer_token, etc.).

# Usage

Add the line below to your dependencies on Android project's app build.gradle file:

   ***
   compile 'com.squareup.okhttp3:okhttp:3.10.0'
   compile 'com.milfont.tezos:tezosj_android:0.0.1'
   ***

Usage example code:

```
// Gets BALANCE for a given Tezos address.
Rpc rpc = new Rpc();
JSONObject result = rpc.getBalance("tz1ZmsfxQrzHk8kjuYJp765LMg1ZpXbsqbPf");
Log.i("output", "Your balance is : " + result.get("ok"));
```

# Disclaimer

This software is at Alpha stage. It is currently experimental and is under development.
Many features are not fully implemented yet. This version uses Tezos Alphanet.

# Credits

TezosJ is based on Stephen Andrews' EZTZ Javascript library.


