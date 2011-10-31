/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
//
//package org.jclouds.cloudstack;
//
//import static org.testng.Assert.assertEquals;
//
//import java.io.IOException;
//import java.util.logging.Logger;
//
//import org.jclouds.compute.domain.ExecResponse;
//import org.jclouds.cloudstack.domain.Instance;
//import org.jclouds.cloudstack.domain.InstanceAction;
//import org.jclouds.cloudstack.domain.InstanceState;
//import org.jclouds.cloudstack.features.ReadOnlyCloudStackClientLiveTest;
//import org.jclouds.cloudstack.options.CreateInstanceOptions;
//import org.jclouds.domain.Credentials;
//import org.jclouds.http.HttpRequest;
//import org.jclouds.net.IPSocket;
//import org.jclouds.ssh.SshClient;
//import org.jclouds.ssh.jsch.config.JschSshClientModule;
//import org.testng.annotations.AfterGroups;
//import org.testng.annotations.Test;
//
//import com.google.common.collect.Iterables;
//import com.google.gson.Gson;
//import com.google.inject.Guice;
//
///**
// * Tests behavior of {@code CloudStackClient}
// * 
// * @author Adrian Cole
// */
//@Test(groups = "live", singleThreaded = true, testName = "CloudStackClientLiveTest")
//public class CloudStackClientLiveTest extends ReadOnlyCloudStackClientLiveTest {
//
//   protected String prefix = System.getProperty("user.name") + ".test";
//   protected Instance instance;
//
//   public void testCreateInstance() throws Exception {
//      Logger.getAnonymousLogger().info("starting instance");
//      instance = client.createInstance(Iterables.get(client.listImages(), 0).getId(),
//            CreateInstanceOptions.Builder.named(prefix));
//      instance = client.getInstance(instance.getHref());
//      checkStartedInstance();
//
//      Instance newInfo = client.getInstance(instance.getHref());
//      checkInstanceMatchesGet(newInfo);
//
//   }
//
//   protected void checkInstanceMatchesGet(Instance newInfo) {
//      assertEquals(newInfo.getHref(), instance.getHref());
//   }
//
//   protected void checkStartedInstance() {
//      System.out.println(new Gson().toJson(instance));
//      assertEquals(instance.getName(), prefix);
//      assertEquals(instance.getState(), InstanceState.RUNNING);
//   }
//
//   @Test(dependsOnMethods = "testCreateInstance")
//   public void testConnectivity() throws Exception {
//      Logger.getAnonymousLogger().info("awaiting ssh");
//      // TODO
//      // assert socketTester.apply(new IPSocket(Iterables.get(instance.getPublicAddresses(), 0),
//      // 22)) : instance;
//      // doConnectViaSsh(instance, getSshCredentials(instance));
//   }
//
//   private Credentials getSshCredentials(Instance instance2) {
//      // TODO
//      return null;
//   }
//
//   public HttpRequest refreshInstanceAndGetAction(InstanceAction action) {
//      return client.getInstance(instance.getHref()).getActions().get(action);
//   }
//
//   @Test(dependsOnMethods = "testConnectivity")
//   public void testLifeCycle() throws Exception {
//      client.performAction(refreshInstanceAndGetAction(InstanceAction.STOP));
//      assertEquals(client.getInstance(instance.getHref()).getState(), InstanceState.STOPPED);
//
//      client.performAction(refreshInstanceAndGetAction(InstanceAction.START));
//      assertEquals(client.getInstance(instance.getHref()).getState(), InstanceState.RUNNING);
//
//      client.performAction(refreshInstanceAndGetAction(InstanceAction.REBOOT));
//      assertEquals(client.getInstance(instance.getHref()).getState(), InstanceState.RUNNING);
//
//   }
//
//   @Test(dependsOnMethods = "testLifeCycle")
//   public void testDestroyInstance() throws Exception {
//      try {
//         client.performAction(refreshInstanceAndGetAction(InstanceAction.STOP));
//         assertEquals(client.getInstance(instance.getHref()).getState(), InstanceState.STOPPED);
//      } catch (IllegalArgumentException e) {
//      }
//      client.performAction(refreshInstanceAndGetAction(InstanceAction.DESTROY));
//      assertEquals(client.getInstance(instance.getHref()), null);
//   }
//
//   protected void doConnectViaSsh(Instance instance, Credentials creds) throws IOException {
//      SshClient ssh = Guice.createInjector(new JschSshClientModule()).getInstance(SshClient.Factory.class)
//            .create(new IPSocket(Iterables.get(instance.getPublicAddresses(), 0), 22), creds);
//      try {
//         ssh.connect();
//         ExecResponse hello = ssh.exec("echo hello");
//         assertEquals(hello.getOutput().trim(), "hello");
//         System.err.println(ssh.exec("df -k").getOutput());
//         System.err.println(ssh.exec("mount").getOutput());
//         System.err.println(ssh.exec("uname -a").getOutput());
//      } finally {
//         if (ssh != null)
//            ssh.disconnect();
//      }
//   }
//
//   @AfterGroups(groups = "live")
//   @Override
//   protected void tearDown() {
//      try {
//         testDestroyInstance();
//      } catch (Exception e) {
//         // no need to check null or anything as we swallow all
//      }
//      super.tearDown();
//   }
//
// }
