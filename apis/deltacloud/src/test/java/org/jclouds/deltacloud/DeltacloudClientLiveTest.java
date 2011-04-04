/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */

package org.jclouds.deltacloud;

import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.util.logging.Logger;

import org.jclouds.compute.domain.ExecResponse;
import org.jclouds.deltacloud.domain.Image;
import org.jclouds.deltacloud.domain.Instance;
import org.jclouds.deltacloud.domain.PasswordAuthentication;
import org.jclouds.deltacloud.domain.Transition;
import org.jclouds.deltacloud.domain.TransitionOnAction;
import org.jclouds.deltacloud.options.CreateInstanceOptions;
import org.jclouds.domain.Credentials;
import org.jclouds.http.HttpRequest;
import org.jclouds.net.IPSocket;
import org.jclouds.ssh.SshClient;
import org.jclouds.ssh.jsch.config.JschSshClientModule;
import org.testng.annotations.Test;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.gson.Gson;
import com.google.inject.Guice;

/**
 * Tests behavior of {@code DeltacloudClient}
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", sequential = true, testName = "DeltacloudClientLiveTest")
public class DeltacloudClientLiveTest extends ReadOnlyDeltacloudClientLiveTest {

   protected String prefix = System.getProperty("user.name") + ".test";
   protected Instance instance;
   protected Credentials creds;

   public void testCreateInstance() throws Exception {
      Logger.getAnonymousLogger().info("starting instance");
      instance = client.createInstance(Iterables.find(client.listImages(), new Predicate<Image>() {

         @Override
         public boolean apply(Image input) {
            return input.getDescription().toLowerCase().indexOf("fedora") != -1;
         }

      }).getId(), CreateInstanceOptions.Builder.named(prefix).hardwareProfile("1").realm("us"));
      if (instance.getAuthentication() != null && instance.getAuthentication() instanceof PasswordAuthentication)
         creds = PasswordAuthentication.class.cast(instance.getAuthentication()).getLoginCredentials();
      refreshInstance();
      checkStartedInstance();

      Instance newInfo = client.getInstance(instance.getHref());
      checkInstanceMatchesGet(newInfo);
   }

   protected void checkInstanceMatchesGet(Instance newInfo) {
      assertEquals(newInfo.getHref(), instance.getHref());
   }

   protected void checkStartedInstance() {
      System.out.println(new Gson().toJson(instance));
      assertEquals(instance.getName(), prefix);
      assert stateChanges.get(Instance.State.RUNNING).apply(instance) : instance;
      refreshInstance();
      assertEquals(instance.getState(), Instance.State.RUNNING);
   }

   private Instance refreshInstance() {
      if (instance != null)
         return instance = client.getInstance(instance.getHref());
      return null;
   }

   @Test(dependsOnMethods = "testCreateInstance")
   public void testConnectivity() throws Exception {
      Logger.getAnonymousLogger().info("awaiting ssh");
      assert socketTester.apply(new IPSocket(Iterables.get(instance.getPublicAddresses(), 0), 22)) : instance;
      if (creds != null) {
         Logger.getAnonymousLogger().info("will connect ssh");
         doConnectViaSsh(instance, creds);
      }
   }

   public HttpRequest getAction(Instance.Action action) {
      return instance.getActions().get(action);
   }

   @Test(dependsOnMethods = "testConnectivity")
   public void testLifeCycle() {

      HttpRequest rebootUri = getAction(Instance.Action.REBOOT);
      if (rebootUri != null) {
         client.performAction(rebootUri);
         assert stateChanges.get(Instance.State.RUNNING).apply(instance) : instance;
      }
   }

   @Test(dependsOnMethods = "testLifeCycle")
   public void testDestroyInstance() {
      for (Transition transition : findChainTo(Instance.State.FINISH, refreshInstance().getState(), client
               .getInstanceStates())) {
         if (refreshInstance() == null)
            break;
         if (transition instanceof TransitionOnAction) {
            client.performAction(getAction(TransitionOnAction.class.cast(transition).getAction()));
         }
         Predicate<Instance> stateTester = stateChanges.get(transition.getTo());
         if (stateTester != null)
            assert stateTester.apply(instance) : transition + " : " + instance;
         else
            Logger.getAnonymousLogger().warning(String.format("no state tester for: %s", transition));
      }
      assert refreshInstance() == null;
   }

   protected void doConnectViaSsh(Instance instance, Credentials creds) throws IOException {
      SshClient ssh = Guice.createInjector(new JschSshClientModule()).getInstance(SshClient.Factory.class).create(
               new IPSocket(Iterables.get(instance.getPublicAddresses(), 0), 22), creds);
      try {
         ssh.connect();
         ExecResponse hello = ssh.exec("echo hello");
         assertEquals(hello.getOutput().trim(), "hello");
         System.err.println(ssh.exec("df -k").getOutput());
         System.err.println(ssh.exec("mount").getOutput());
         System.err.println(ssh.exec("uname -a").getOutput());
      } finally {
         if (ssh != null)
            ssh.disconnect();
      }
   }

   @Override
   protected void tearDown() {
      testDestroyInstance();
      super.tearDown();
   }

}
