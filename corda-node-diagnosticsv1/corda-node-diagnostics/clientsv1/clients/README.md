<p align="center">
  <img src="https://www.corda.net/wp-content/uploads/2016/11/fg005_corda_b.png" alt="Corda" width="500">
</p>

# Cordapp Diagnostic 

This code presents a microservice to publish node diagnostic Info.
Node Diagnostic Info obtained after parsing jar manifest using RPC endpoint is published to Amazon SQS.
Different Application/Client Threads maintain a long poll connection with SQS. Node Diagnostic Info Messages are evicted from the queue on poll.
Code also presents a grpc server/client along with Rest API for fetching node Diagnostic Info and publishing Node Diagnostic Info obtained.
Node Diagnostic Data is also stored in distributed key value store - Dynamo DB.
Please node this code is in development mode and has few bugs.


 
