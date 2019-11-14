package com.nodediagnostic;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import net.corda.core.concurrent.CordaFuture;
import net.corda.core.cordapp.CordappInfo;
import net.corda.core.identity.CordaX500Name;
import net.corda.testing.driver.DriverParameters;
import net.corda.testing.driver.NodeHandle;
import net.corda.testing.driver.NodeParameters;
import net.corda.testing.driver.VerifierType;
import net.corda.testing.node.NotarySpec;
import net.corda.testing.node.TestCordapp;
import net.corda.testing.node.User;
import org.junit.Test;
import net.corda.core.node.NodeDiagnosticInfo;

import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;

import static org.junit.Assert.*;


import static net.corda.testing.driver.Driver.driver;

/**
 * Unit Test for Matching CordaDapp Info
 */
public class NodeDiagnosticTests {

    @Test
    public void nodeDiagnosticData() {

        final User user = new User("bluefrog", "test", ImmutableSet.of("ALL"));
        //Start Corda Nodes for testing purpose
        driver(new DriverParameters()
                .withIsDebug(true)
                .withWaitForAllNodesToFinish(false)
                .withCordappsForAllNodes(Arrays.asList(TestCordapp.findCordapp("net.corda.finance.contracts")))
                .withNotarySpecs(Arrays.asList(new NotarySpec(new CordaX500Name("Notary", "London", "GB"), true, Arrays.asList(user), VerifierType.InMemory, null))), dsl -> {
            CordaFuture<NodeHandle> partyAFuture = dsl.startNode(new NodeParameters()
                    .withProvidedName(new CordaX500Name("ParticipantA", "London", "GB"))
                    .withRpcUsers(ImmutableList.of(user)));
            try {
                matchNodeDiagnosticData(partyAFuture);
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        });

    }

    //Verify Cordapp information returned by nodes

    public static void matchNodeDiagnosticData(CordaFuture<NodeHandle> partyAFuture) throws ExecutionException, InterruptedException {

        NodeDiagnosticInfo nodeData = partyAFuture.get().getRpc().nodeDiagnosticInfo();

        Pattern CORDA_VERSION_REGEX = Pattern.compile("\\d+(\\.\\d+)?(-\\w+)?");
											 /*
											 Matches Corda Version
											 */
        Pattern CORDA_REVISION_REGEX = Pattern.compile("[0-9a-fA-F]+");
        /*
         * Matches Corda Revision
         *
         */
        Pattern CORDA_VENDOR = Pattern.compile("Corda Open Source");
                                         /*
										 Matches Corda Vendor
										 */

        Pattern CORDAPP_SHORT_NAME = Pattern.compile("Corda Finance Demo");
        /*
         * Matches CordApp Shortname
         *
         */
        Pattern CORDAPP_VERSION_REGEX = Pattern.compile("\\d+");
        /*
         * Matches CordApp version
         *
         */
        Pattern CORDAPP_VENDOR = Pattern.compile("R3");
        /*
         * Matches CordApp Vendor
         */
        Pattern CORDAPP_LICENCE = Pattern.compile("Open Source (Apache 2)");

        Pattern CORDAPP_NAME_REGEX = Pattern.compile("corda-finance-contracts");

        assertTrue(CORDA_REVISION_REGEX.matcher(nodeData.getRevision()).find());
        assertEquals(CORDA_VENDOR.toString(), nodeData.getVendor());
        CordappInfo cordappDiagnosticInfo = nodeData.getCordapps().get(0);
        assertEquals(CORDAPP_SHORT_NAME.toString(), cordappDiagnosticInfo.getShortName());
        assertTrue(CORDAPP_NAME_REGEX.matcher(cordappDiagnosticInfo.getName()).find());
        assertTrue(CORDAPP_VERSION_REGEX.matcher(cordappDiagnosticInfo.getVersion()).find());
        assertEquals(CORDAPP_VENDOR.toString(), cordappDiagnosticInfo.getVendor());
        assertEquals(CORDAPP_LICENCE.toString(), cordappDiagnosticInfo.getLicence());
        assertTrue(cordappDiagnosticInfo.getMinimumPlatformVersion() <= nodeData.getPlatformVersion());
        assertTrue(cordappDiagnosticInfo.getTargetPlatformVersion() <= nodeData.getPlatformVersion());
    }


}


