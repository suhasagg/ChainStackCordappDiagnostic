package com.elasticsearchsync;

import com.util.GlobalConfiguration;
import net.corda.client.rpc.CordaRPCClient;
import net.corda.client.rpc.CordaRPCConnection;
import net.corda.core.messaging.CordaRPCOps;
import net.corda.core.node.NodeDiagnosticInfo;
import net.corda.core.utilities.NetworkHostAndPort;
import net.corda.client.rpc.GracefulReconnect;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.*;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentType;

import java.net.*;
import java.io.*;
import java.util.Date;
import java.util.List;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;

import com.model.NodeDiagnosticData;

import static net.corda.core.utilities.NetworkHostAndPort.parse;

/**
 * Syncs Node Diagnostics Info to Datastore:Elasticsearch
 */
public class ElasticsearchSync {

    public static TransportClient instance;

    public static TransportClient getInstance() throws UnknownHostException {
        if (instance == null) {
            instance = new PreBuiltTransportClient(Settings.EMPTY)
                    .addTransportAddress(new TransportAddress(InetAddress.getByName(GlobalConfiguration.get("elasticsearchhost")), 9300));
            return instance;
        }
        return instance;
    }

    /*
     Schedular service inserts NodeDiagnostic Info in ES periodically. Implemented using ScheduledExecutorService At Fixed Rate
     at a fixed period for every Corda Node - period can be configured via configuration
    */

    public static void main(String[] args) {
        ScheduledExecutorService ses = Executors.newScheduledThreadPool(1);
        Runnable task1 = () -> {
            List<String> list = new ArrayList<>();
            for (String host : GlobalConfiguration.getList("nodelist")) {
                System.out.println(host);
                Collections.addAll(list, host);
            }
            for (String nodeData : list) {
                NetworkHostAndPort nodeAddress = parse(nodeData);
                String rpcUsername = GlobalConfiguration.get("rpcusername");
                String rpcPassword = GlobalConfiguration.get("rpcpassword");
                CordaRPCClient client = new CordaRPCClient(nodeAddress);
                GracefulReconnect gracefulReconnect = new GracefulReconnect();
                CordaRPCConnection connection = client.start(rpcUsername, rpcPassword, gracefulReconnect);
                CordaRPCOps proxy = connection.getProxy();
                NodeDiagnosticInfo nodesInfo = proxy.nodeDiagnosticInfo();
                try {
                    save(nodesInfo, nodeAddress.toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        ses.scheduleAtFixedRate(task1, 5, GlobalConfiguration.getInt("elasticsearchsyncperiod"), TimeUnit.SECONDS);
    }

    /*
    Saves NodeDiagnosticInfo in Elasticsearch periodically - period can be configured via configuration
    1)This helps in reducing RPC calls overhead to Nodes, This layer reduced load even if
    2)Cordapp information for every node is stored in Elasticsearch with NodeId and timestamp
    3)NodeDiagnostic Info polled is archived in elasticsearch with timestamp
    a)This helps in tracking Cordapp history on every node
    b)Can be used for generating alerts after scanning patterns in NodeDiagnostic information
    */

    public static void save(NodeDiagnosticInfo nodesInfo, String nodeId) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(nodesInfo);
        NodeDiagnosticData info = NodeDiagnosticData.FromJson(json);
        info.setNodeId(nodeId);
        Date date = new Date();
        long time = date.getTime();
        Timestamp ts = new Timestamp(time);
        info.setTimestamp(ts.toString());
        byte[] data = mapper.writeValueAsBytes(info);
        System.setProperty("es.set.netty.runtime.available.processors", "false");
        TransportClient client = getInstance();
        IndexResponse response = client.prepareIndex(GlobalConfiguration.get("elasticsearchindex"), "_doc")
                .setSource(data, XContentType.JSON)
                .get();
    }

}