package org.jclouds.fujitsu.fgcp.compute.config;

import java.util.Properties;

import org.jclouds.Constants;
import org.jclouds.compute.domain.ExecResponse;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.internal.BaseComputeServiceLiveTest;
import org.jclouds.rest.AuthorizationException;
import org.jclouds.sshj.config.SshjSshClientModule;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;

public abstract class FGCPBaseComputeServiceLiveTest extends
      BaseComputeServiceLiveTest {

   @Override
   protected Properties setupProperties() {
      Properties overrides = super.setupProperties();
   
      String proxy = System.getenv("http_proxy");
      if (proxy != null) {
   
         String[] parts = proxy.split("http://|:|@");
   
         overrides.setProperty(Constants.PROPERTY_PROXY_HOST, parts[parts.length - 2]);
         overrides.setProperty(Constants.PROPERTY_PROXY_PORT, parts[parts.length - 1]);
   
         if (parts.length >= 4) {
            overrides.setProperty(Constants.PROPERTY_PROXY_USER, parts[parts.length - 4]);
            overrides.setProperty(Constants.PROPERTY_PROXY_PASSWORD, parts[parts.length - 3]);
         }
      }
   
      // enables peer verification using the CAs bundled with the JRE (or
      // value of javax.net.ssl.trustStore if set)
      overrides.setProperty(Constants.PROPERTY_TRUST_ALL_CERTS, "false");
   
      return overrides;
   }

   @Override
   protected Module getSshModule() {
      return new SshjSshClientModule();
   }

   @Override
   @Test(enabled = false, expectedExceptions = AuthorizationException.class)
   public void testCorrectAuthException() throws Exception {
      // http://code.google.com/p/jclouds/issues/detail?id=1060
   }

   // fgcp does not support metadata
   @Override
   protected void checkUserMetadataInNodeEquals(NodeMetadata node, ImmutableMap<String, String> userMetadata) {
      assert node.getUserMetadata().isEmpty() : String.format(
            "node userMetadata not empty: %s %s", node,
            node.getUserMetadata());
    }

   // node name can't be retrieved through the API and is therefore null
   protected void checkResponseEqualsHostname(ExecResponse execResponse,
         NodeMetadata node) {
      assert node.getHostname() == null : node + " with hostname: "
            + node.getHostname();
   }

   // tags are not (yet) supported for fgcp
   protected void checkTagsInNodeEquals(NodeMetadata node,
         ImmutableSet<String> tags) {
      assert node.getTags().isEmpty() : String.format(
            "node tags found %s (%s) in node %s", node.getTags(), tags, node);
   }

   /*
    * public void testCreateAndRunAService() throws Exception {
    * super.testCreateAndRunAService(); }
    */

   // this test requires network access to the VM it creates:
   // before running it, start an SSL/VPN connection to the last updated vsys'
   // DMZ.
   // may also need to configure SNAT and FW rules to allow the VM to
   // communicate out (53 for DNS, 80 for yum).
   public void testAScriptExecutionAfterBootWithBasicTemplate()
         throws Exception {
      super.testAScriptExecutionAfterBootWithBasicTemplate();
   }

   @Override
   @Test(enabled = false)
   public void testOptionToNotBlock() throws Exception {
      // start call returns before node reaches running state, but
      // test may be failing due to the system being in a 're-configuring'
      // state while destroying nodes of a previous test.
      // http://code.google.com/p/jclouds/issues/detail?id=1066
         /*
   org.jclouds.compute.RunNodesException: error running 1 node group(fgcp-aublock) location(UZXC0GRT-IZKDVGIL5-N-SECURE1) image(IMG_3c9820_71OW9NZC268) size(islanda-cbrm_140) options({inboundPorts=[], blockUntilRunning=false})
   Execution failures:
   
   1) ExecutionException on fgcp-aublock-787:
   java.util.concurrent.ExecutionException: java.lang.IllegalStateException: The status of Instance[UZXC0GRT-IZKDVGIL5] is [RECONFIG_ING].
      at com.google.common.util.concurrent.AbstractFuture$Sync.getValue(AbstractFuture.java:289)
      at com.google.common.util.concurrent.AbstractFuture$Sync.get(AbstractFuture.java:276)
      at com.google.common.util.concurrent.AbstractFuture.get(AbstractFuture.java:111)
      at org.jclouds.concurrent.FutureIterables$1.run(FutureIterables.java:134)
      at java.util.concurrent.ThreadPoolExecutor$Worker.runTask(Unknown Source)
      at java.util.concurrent.ThreadPoolExecutor$Worker.run(Unknown Source)
      at java.lang.Thread.run(Unknown Source)
   Caused by: java.lang.IllegalStateException: The status of Instance[UZXC0GRT-IZKDVGIL5] is [RECONFIG_ING].
      at org.jclouds.fujitsu.fgcp.xml.FGCPJAXBParser.fromXML(FGCPJAXBParser.java:75)
      at org.jclouds.http.functions.ParseXMLWithJAXB.apply(ParseXMLWithJAXB.java:91)
      at org.jclouds.http.functions.ParseXMLWithJAXB.apply(ParseXMLWithJAXB.java:86)
      at org.jclouds.http.functions.ParseXMLWithJAXB.apply(ParseXMLWithJAXB.java:73)
      at org.jclouds.http.functions.ParseXMLWithJAXB.apply(ParseXMLWithJAXB.java:54)
      at com.google.common.base.Functions$FunctionComposition.apply(Functions.java:209)
      at com.google.common.util.concurrent.Futures$3.apply(Futures.java:380)
      at com.google.common.util.concurrent.Futures$ChainingListenableFuture.run(Futures.java:522)
      at java.util.concurrent.ThreadPoolExecutor$Worker.runTask(Unknown Source)
      at java.util.concurrent.ThreadPoolExecutor$Worker.run(Unknown Source)
      at java.lang.Thread.run(Unknown Source)
      at org.jclouds.concurrent.config.DescribingExecutorService.submit(DescribingExecutorService.java:89)
      at org.jclouds.compute.strategy.impl.CreateNodesWithGroupEncodedIntoNameThenAddToSet.createNodeInGroupWithNameAndTemplate(CreateNodesWithGroupEncodedIntoNameThenAddToSet.java:170)
      at org.jclouds.compute.strategy.impl.CreateNodesWithGroupEncodedIntoNameThenAddToSet.execute(CreateNodesWithGroupEncodedIntoNameThenAddToSet.java:125)
      at org.jclouds.compute.internal.BaseComputeService.createNodesInGroup(BaseComputeService.java:213)
      at org.jclouds.compute.internal.BaseComputeService.createNodesInGroup(BaseComputeService.java:229)
      at org.jclouds.compute.internal.BaseComputeServiceLiveTest.testOptionToNotBlock(BaseComputeServiceLiveTest.java:803)
          */
      }

/*   @Override
   @Test(enabled = false)
   public void testCreateTwoNodesWithRunScript() {
   }

   @Override
   @Test(enabled = false)
   public void testCreateAnotherNodeWithANewContextToEnsureSharedMemIsntRequired() {
   }

   @Override
   @Test(enabled = false)
   public void testGet() {
   }
*/
//   @Override
//   @Test(enabled = false)
//   public void testConcurrentUseOfComputeServiceToCreateNodes() throws Exception {
      // http://code.google.com/p/jclouds/issues/detail?id=1066
      /*
      1) ExecutionException on twin0-f6a:
         java.util.concurrent.ExecutionException: org.jclouds.http.HttpResponseException: Error parsing input
         {statusCode=200, message=OK, headers={Date=[Sun, 26 Aug 2012 01:22:50 GMT], Transfer-Encoding=[chunked], Set-Cookie=[JSESSIONID=8A07404DF0405E46B3A748C3763B0D9F; Path=/ovisspxy; Secure], Connection=[close]}, payload=[content=true, contentMetadata=[contentDisposition=null, contentEncoding=null, contentLanguage=null, contentLength=null, contentMD5=null, contentType=text/xml;charset=UTF-8, expires=null], written=false]}
            at com.google.common.util.concurrent.AbstractFuture$Sync.getValue(AbstractFuture.java:289)
            at com.google.common.util.concurrent.AbstractFuture$Sync.get(AbstractFuture.java:276)
            at com.google.common.util.concurrent.AbstractFuture.get(AbstractFuture.java:111)
            at org.jclouds.concurrent.FutureIterables$1.run(FutureIterables.java:134)
            at java.util.concurrent.ThreadPoolExecutor$Worker.runTask(Unknown Source)
            at java.util.concurrent.ThreadPoolExecutor$Worker.run(Unknown Source)
            at java.lang.Thread.run(Unknown Source)
         Caused by: org.jclouds.http.HttpResponseException: Error parsing input
         {statusCode=200, message=OK, headers={Date=[Sun, 26 Aug 2012 01:22:50 GMT], Transfer-Encoding=[chunked], Set-Cookie=[JSESSIONID=8A07404DF0405E46B3A748C3763B0D9F; Path=/ovisspxy; Secure], Connection=[close]}, payload=[content=true, contentMetadata=[contentDisposition=null, contentEncoding=null, contentLanguage=null, contentLength=null, contentMD5=null, contentType=text/xml;charset=UTF-8, expires=null], written=false]}
            at org.jclouds.http.functions.ParseXMLWithJAXB.apply(ParseXMLWithJAXB.java:78)
            at org.jclouds.http.functions.ParseXMLWithJAXB.apply(ParseXMLWithJAXB.java:1)
            at com.google.common.base.Functions$FunctionComposition.apply(Functions.java:209)
            at com.google.common.util.concurrent.Futures$3.apply(Futures.java:380)
            at com.google.common.util.concurrent.Futures$ChainingListenableFuture.run(Futures.java:522)
            at java.util.concurrent.ThreadPoolExecutor$Worker.runTask(Unknown Source)
            at java.util.concurrent.ThreadPoolExecutor$Worker.run(Unknown Source)
            at java.lang.Thread.run(Unknown Source)
            at org.jclouds.concurrent.config.DescribingExecutorService.submit(DescribingExecutorService.java:89)
            at org.jclouds.compute.strategy.impl.CreateNodesWithGroupEncodedIntoNameThenAddToSet.createNodeInGroupWithNameAndTemplate(CreateNodesWithGroupEncodedIntoNameThenAddToSet.java:170)
            at org.jclouds.compute.strategy.impl.CreateNodesWithGroupEncodedIntoNameThenAddToSet.execute(CreateNodesWithGroupEncodedIntoNameThenAddToSet.java:125)
            at org.jclouds.compute.internal.BaseComputeService.createNodesInGroup(BaseComputeService.java:213)
            at org.jclouds.compute.internal.BaseComputeService.createNodesInGroup(BaseComputeService.java:229)
            at org.jclouds.compute.internal.BaseComputeServiceLiveTest$1.call(BaseComputeServiceLiveTest.java:442)
            at org.jclouds.compute.internal.BaseComputeServiceLiveTest$1.call(BaseComputeServiceLiveTest.java:1)
            ... 3 more
         Caused by: org.jclouds.http.HttpException: The status of Instance[UZXC0GRT-9Q988189J] is [RECONFIG_ING].
            at org.jclouds.fujitsu.fgcp.xml.FGCPJAXBParser.fromXML(FGCPJAXBParser.java:81)
            at org.jclouds.http.functions.ParseXMLWithJAXB.apply(ParseXMLWithJAXB.java:91)
*/      
//   }
}
