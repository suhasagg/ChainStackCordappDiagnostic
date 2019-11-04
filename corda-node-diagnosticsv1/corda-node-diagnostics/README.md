<p align="center">
  <img src="https://www.corda.net/wp-content/uploads/2016/11/fg005_corda_b.png" alt="Corda" width="500">
</p>

# Cordapp Diagnostic 

# Usage

## Running the nodes
``
1)./gradlew deployNodes
2)build/nodes/runnodes
``
## Interacting with the nodes

### Shell
```
 run networkMapSnapshot` to see a list of the other nodes on the network:
    [
      {
      "addresses" : [ "localhost:10002" ],
      "legalIdentitiesAndCerts" : [ "O=Notary, L=London, C=GB" ],
      "platformVersion" : 3,
      "serial" : 1541505484825
    },
      {
      "addresses" : [ "localhost:10005" ],
      "legalIdentitiesAndCerts" : [ "O=PartyA, L=London, C=GB" ],
      "platformVersion" : 3,
      "serial" : 1541505382560
    },
      {
      "addresses" : [ "localhost:10008" ],
      "legalIdentitiesAndCerts" : [ "O=PartyB, L=New York, C=US" ],
      "platformVersion" : 3,
      "serial" : 1541505384742
    }
    ]
```

### Client

`clients/src/main/java/com/nodediagnostic/Client.java` defines a simple command-line client that connects to a node via RPC 
and prints Cordapp info of the nodes.

#### Running the client

##### Via the command line

Run the `runNodeDiagnosticClient` Gradle task. By default, it connects to the node with RPC address `localhost:10006` with 
the username `bluefrog` and the password `test`.

### Webserver

`clients/src/main/java/com/nodediagnostic/webserver/` defines a Spring webserver that connects to a node via RPC and 
allows to fetch Cordapp information over HTTP.

The API endpoints are defined here:

     clients/src/main/java/com/nodediagnostic/webserver/Controller.java

And a static webpage is defined here:

     clients/src/main/resources/static/

#### Running the webserver

##### Via the command line

Run the `runNodeDiagnosticServer` Gradle task. By default, it connects to the node with RPC address `localhost:10006` with 
the username `bluefrog` and the password `test`, and serves the webserver on port `localhost:10050`.

#### Interacting with the webserver

The static webpage is served on:

    http://localhost:10050

While the sole nodediagnostic endpoint is served on:

    http://localhost:10050/getCordAppInfo
    
Returned Cordapp Info:

```
{ 
   "version":"4.3-RC02",
   "revision":"fbc3d543db32269efe9224328f35b40c7f149ab4",
   "platformVersion":5,
   "vendor":"Corda Open Source",
   "cordapps":[ 
      { 
         "type":"CorDapp",
         "name":"cordapp-contracts-states-0.1",
         "shortName":"cordapp-contracts-states-0.1",
         "minimumPlatformVersion":1,
         "targetPlatformVersion":1,
         "version":"Unknown",
         "vendor":"Unknown",
         "licence":"Unknown",
         "jarHash":{ 
            "offset":0,
            "size":32,
            "bytes":"DjbpxiJMrIg9+uqz2j+n7aFszFYs84DtiBV6ENFfPus="
         }
      },
      { 
         "type":"CorDapp",
         "name":"cordapp-0.1",
         "shortName":"cordapp-0.1",
         "minimumPlatformVersion":1,
         "targetPlatformVersion":1,
         "version":"Unknown",
         "vendor":"Unknown",
         "licence":"Unknown",
         "jarHash":{ 
            "offset":0,
            "size":32,
            "bytes":"yGYMTYthSYr+TJjdFkBklhMESN5KB2sxi44qIgb1vBo="
         }
      },
      { 
         "type":"CorDapp",
         "name":"corda-node-diagnostics-0.1",
         "shortName":"CorDapp",
         "minimumPlatformVersion":4,
         "targetPlatformVersion":4,
         "version":"0.1",
         "vendor":"Corda Open Source",
         "licence":"Unknown",
         "jarHash":{ 
            "offset":0,
            "size":32,
            "bytes":"zmiioCLhZFr5rHvWbU6b+4tq3byC5fKfgg7m6ZGh91w="
         }
      }
   ]
}```

#### Syncing Cordapp information in Elasticsearch 

Syncs Node Diagnostics Info to Datastore:Elasticsearch for every Corda Node.
This reduces RPC calls overhead to Corda nodes and also helps to archive NodeDiagnostic Info for every Node.
Snapshot of elasticsearch document.


{
  "_index": "nodediagnostic",
  "_type": "_doc",
  "_id": "D5g7K24BG0qORwgGeAdV",
  "_version": 1,
  "_score": 2,
  "_source": {
    "nodeId": "localhost:10009",
    "timestamp": "2019-11-02 13:59:08.051",
    "version": "4.3-RC02",
    "revision": "fbc3d543db32269efe9224328f35b40c7f149ab4",
    "platformVersion": "5",
    "vendor": "Corda Open Source",
    "cordapps": [
      {
        "type": "CorDapp",
        "name": "cordapp-contracts-states-0.1",
        "shortName": "cordapp-contracts-states-0.1",
        "targetPlatformVersion": "1",
        "minimumPlatformVersion": "1",
        "vendor": "Unknown",
        "version": "Unknown",
        "licence": "Unknown",
        "jarHash": {
          "offset": "0",
          "size": "32",
          "bytes": "DjbpxiJMrIg9+uqz2j+n7aFszFYs84DtiBV6ENFfPus="
        }
      },
      {
        "type": "CorDapp",
        "name": "cordapp-0.1",
        "shortName": "cordapp-0.1",
        "targetPlatformVersion": "1",
        "minimumPlatformVersion": "1",
        "vendor": "Unknown",
        "version": "Unknown",
        "licence": "Unknown",
        "jarHash": {
          "offset": "0",
          "size": "32",
          "bytes": "yGYMTYthSYr+TJjdFkBklhMESN5KB2sxi44qIgb1vBo="
        }
      },
      {
        "type": "CorDapp",
        "name": "corda-node-diagnostics-0.1",
        "shortName": "CorDapp",
        "targetPlatformVersion": "4",
        "minimumPlatformVersion": "4",
        "vendor": "Corda Open Source",
        "version": "0.1",
        "licence": "Unknown",
        "jarHash": {
          "offset": "0",
          "size": "32",
          "bytes": "zmiioCLhZFr5rHvWbU6b+4tq3byC5fKfgg7m6ZGh91w="
        }
      }
    ]
  }
}
```


Reference - 
https://docs.corda.net/head/cordapp-build-systems.html
Information is derived from CordApp manifest. 
These attributes are specified in the JAR manifest of the CorDapp.

```
Cordapp-finance-contract
Manifest-Version: 1.0
Corda-Platform-Version: 5
Automatic-Module-Name: net.corda.contracts
Cordapp-Contract-Version: 1
Sealed: true
Corda-Vendor: Corda Open Source
Min-Platform-Version: 1
Cordapp-Contract-Vendor: R3
Corda-Revision: n/a
Cordapp-Contract-Name: Corda Finance Demo
Target-Platform-Version: 5
Corda-Release-Version: 4.3-RC02
Cordapp-Contract-Licence: Open Source (Apache 2)

Name: net/corda/finance/contracts/asset/OnLedgerAsset$generateExit$1.class
SHA-256-Digest: jy8cMRlXuVyPC/2SaMVHKhcqawGQLh1c79jro6rvJD8=

Cordapp-finance-workflow
Manifest-Version: 1.0
Corda-Platform-Version: 5
Automatic-Module-Name: net.corda.workflows
Sealed: true
Corda-Vendor: Corda Open Source
Cordapp-Workflow-Name: Corda Finance Demo
Min-Platform-Version: 1
Cordapp-Workflow-Vendor: R3
Cordapp-Workflow-Licence: Open Source (Apache 2)
Corda-Revision: fbc3d543db32269efe9224328f35b40c7f149ab4
Cordapp-Workflow-Version: 1
Target-Platform-Version: 5
Corda-Release-Version: 4.3-RC02

Name: net/corda/finance/flows/TwoPartyDealFlow$Secondary.class
SHA-256-Digest: gKx5oQFmPj46XtiL31P5Wu+ntYpaR/c0TgJJVJFZv18=
```


Configuration parameters  -
```
elasticsearchhost=localhost
nodelist=localhost:10006,localhost:10009
rpcusername=bluefrog
rpcpassword=test
elasticsearchsyncperiod=3
elasticsearchindex=nodediagnostic
defaultwebservernode=localhost:10006
```

Main module - 
`
corda-node-diagnostics/clients
`




