package com.nodediagnostic.webserver;

import com.util.GlobalConfiguration;
import net.corda.core.messaging.CordaRPCOps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import net.corda.client.rpc.CordaRPCClient;
import net.corda.core.node.NodeInfo;
import net.corda.core.node.NodeDiagnosticInfo;
import net.corda.core.utilities.NetworkHostAndPort;

import java.util.List;

import static net.corda.core.utilities.NetworkHostAndPort.parse;

/**
 * API endpoints defined here.
 */
@RestController
@RequestMapping("/") // The paths for HTTP requests are relative to this base path.
public class Controller {
    private final CordaRPCOps proxy;
    private final static Logger logger = LoggerFactory.getLogger(Controller.class);

    public Controller(NodeRPCConnection rpc) {
        this.proxy = rpc.proxy;
    }

//API for fetching CordAppInfo

    @GetMapping(value = "/getCordAppInfo", produces = "application/json")
    private NodeDiagnosticInfo getCordAppInfoData() {
        final NetworkHostAndPort nodeAddress = parse(GlobalConfiguration.get("defaultwebservernode"));
        final String rpcUsername = "bluefrog";
        final String rpcPassword = "test";
        final CordaRPCClient client = new CordaRPCClient(nodeAddress);
        final CordaRPCOps proxy = client.start(rpcUsername, rpcPassword).getProxy();

        // Interact with the node.
        // For example, here we print the nodes on the network.
        final List<NodeInfo> nodes = proxy.networkMapSnapshot();
        final NodeDiagnosticInfo nodesInfo = proxy.nodeDiagnosticInfo();
        return nodesInfo;
    }
}