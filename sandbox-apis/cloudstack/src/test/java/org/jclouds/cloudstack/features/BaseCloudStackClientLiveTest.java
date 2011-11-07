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
package org.jclouds.cloudstack.features;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.testng.Assert.assertEquals;

import java.util.NoSuchElementException;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.jclouds.Constants;
import org.jclouds.cloudstack.CloudStackAsyncClient;
import org.jclouds.cloudstack.CloudStackClient;
import org.jclouds.cloudstack.domain.Account;
import org.jclouds.cloudstack.domain.User;
import org.jclouds.cloudstack.domain.VirtualMachine;
import org.jclouds.cloudstack.functions.ReuseOrAssociateNewPublicIPAddress;
import org.jclouds.cloudstack.predicates.JobComplete;
import org.jclouds.cloudstack.predicates.UserPredicates;
import org.jclouds.cloudstack.predicates.VirtualMachineDestroyed;
import org.jclouds.cloudstack.predicates.VirtualMachineRunning;
import org.jclouds.compute.domain.ExecResponse;
import org.jclouds.domain.Credentials;
import org.jclouds.logging.log4j.config.Log4JLoggingModule;
import org.jclouds.net.IPSocket;
import org.jclouds.predicates.InetSocketAddressConnect;
import org.jclouds.predicates.RetryablePredicate;
import org.jclouds.rest.RestContext;
import org.jclouds.rest.RestContextFactory;
import org.jclouds.ssh.SshClient;
import org.jclouds.sshj.config.SshjSshClientModule;
import org.testng.annotations.AfterGroups;
import org.testng.annotations.BeforeGroups;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;

/**
 * 
 * @author Adrian Cole
 */
public class BaseCloudStackClientLiveTest {
   protected String prefix = System.getProperty("user.name");

   protected CloudStackClient client;
   protected RestContext<CloudStackClient, CloudStackAsyncClient> context;
   protected String provider = "cloudstack";
   protected String identity;
   protected String credential;
   protected String endpoint;
   protected String apiversion;
   protected Predicate<IPSocket> socketTester;
   protected RetryablePredicate<Long> jobComplete;
   protected RetryablePredicate<VirtualMachine> virtualMachineRunning;
   protected RetryablePredicate<VirtualMachine> virtualMachineDestroyed;
   protected SshClient.Factory sshFactory;
   protected String password = "password";

   protected Injector injector;

   protected ReuseOrAssociateNewPublicIPAddress reuseOrAssociate;

   protected void setupCredentials() {
      identity = checkNotNull(System.getProperty("test." + provider + ".identity"), "test." + provider
            + ".identity must be set.  ex. apiKey");
      credential = checkNotNull(System.getProperty("test." + provider + ".credential"), "test." + provider
            + ".credential must be set.  ex. secretKey");
      endpoint = checkNotNull(System.getProperty("test." + provider + ".endpoint"), "test." + provider
            + ".endpoint must be set.  ex. http://localhost:8080/client/api");
      apiversion = System.getProperty("test." + provider + ".apiversion");
   }

   protected void checkSSH(IPSocket socket) {
      socketTester.apply(socket);
      SshClient client = sshFactory.create(socket, new Credentials("root", password));
      try {
         client.connect();
         ExecResponse exec = client.exec("echo hello");
         System.out.println(exec);
         assertEquals(exec.getOutput().trim(), "hello");
      } finally {
         if (client != null)
            client.disconnect();
      }
   }

   protected Properties setupProperties() {
      Properties overrides = new Properties();
      overrides.setProperty(Constants.PROPERTY_TRUST_ALL_CERTS, "true");
      overrides.setProperty(Constants.PROPERTY_RELAX_HOSTNAME, "true");
      overrides.setProperty(provider + ".identity", identity);
      overrides.setProperty(provider + ".credential", credential);
      overrides.setProperty(provider + ".endpoint", endpoint);
      if (apiversion != null)
         overrides.setProperty(provider + ".apiversion", apiversion);
      return overrides;
   }

   @BeforeGroups(groups = "live")
   public void setupClient() {
      setupCredentials();
      Properties overrides = setupProperties();
      context = new RestContextFactory().createContext(provider, ImmutableSet.<Module> of(new Log4JLoggingModule()),
            overrides);

      client = context.getApi();
      // check access
      Iterable<User> users = Iterables.concat(client.getAccountClient().listAccounts());
      User currentUser; 
      Predicate<User> apiKeyMatches = UserPredicates.apiKeyEquals(identity);
      try {
         currentUser = Iterables.find(users, apiKeyMatches);
      } catch (NoSuchElementException e) {
         throw new NoSuchElementException(String.format("none of the following users match %s: %s", apiKeyMatches,
               users));
      }

      if (currentUser.getAccountType() != Account.Type.USER)
         throw new IllegalArgumentException(String.format(
               "invalid account type: %s, please specify an apiKey of a USER, for example: %s",
               currentUser.getAccountType(), Iterables.filter(users, UserPredicates.isUserAccount())));

      injector = Guice.createInjector(new SshjSshClientModule(), new Log4JLoggingModule());
      sshFactory = injector.getInstance(SshClient.Factory.class);
      socketTester = new RetryablePredicate<IPSocket>(new InetSocketAddressConnect(), 180, 1, 1, TimeUnit.SECONDS);
      injector.injectMembers(socketTester);
      jobComplete = new RetryablePredicate<Long>(new JobComplete(client), 1200, 1, 5, TimeUnit.SECONDS);
      injector.injectMembers(jobComplete);
      virtualMachineRunning = new RetryablePredicate<VirtualMachine>(new VirtualMachineRunning(client), 600, 5, 5,
            TimeUnit.SECONDS);
      injector.injectMembers(virtualMachineRunning);
      virtualMachineDestroyed = new RetryablePredicate<VirtualMachine>(new VirtualMachineDestroyed(client), 600, 5, 5,
            TimeUnit.SECONDS);
      injector.injectMembers(virtualMachineDestroyed);
      reuseOrAssociate = new ReuseOrAssociateNewPublicIPAddress(client, jobComplete);
      injector.injectMembers(reuseOrAssociate);
   }

   @AfterGroups(groups = "live")
   protected void tearDown() {
      if (context != null)
         context.close();
   }

}