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

import static com.google.common.base.Strings.emptyToNull;
import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.get;
import static org.testng.Assert.assertEquals;

import java.util.NoSuchElementException;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import org.jclouds.cloudstack.CloudStackAsyncClient;
import org.jclouds.cloudstack.CloudStackClient;
import org.jclouds.cloudstack.domain.Account;
import org.jclouds.cloudstack.domain.Template;
import org.jclouds.cloudstack.domain.User;
import org.jclouds.cloudstack.domain.VirtualMachine;
import org.jclouds.cloudstack.functions.ReuseOrAssociateNewPublicIPAddress;
import org.jclouds.cloudstack.options.ListTemplatesOptions;
import org.jclouds.cloudstack.predicates.CorrectHypervisorForZone;
import org.jclouds.cloudstack.predicates.JobComplete;
import org.jclouds.cloudstack.predicates.OSCategoryIn;
import org.jclouds.cloudstack.predicates.TemplatePredicates;
import org.jclouds.cloudstack.predicates.UserPredicates;
import org.jclouds.cloudstack.predicates.VirtualMachineDestroyed;
import org.jclouds.cloudstack.predicates.VirtualMachineRunning;
import org.jclouds.compute.BaseVersionedServiceLiveTest;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.ComputeServiceContextFactory;
import org.jclouds.compute.domain.ExecResponse;
import org.jclouds.domain.Credentials;
import org.jclouds.logging.log4j.config.Log4JLoggingModule;
import org.jclouds.net.IPSocket;
import org.jclouds.predicates.InetSocketAddressConnect;
import org.jclouds.predicates.RetryablePredicate;
import org.jclouds.rest.RestContext;
import org.jclouds.ssh.SshClient;
import org.jclouds.sshj.config.SshjSshClientModule;
import org.testng.annotations.AfterGroups;
import org.testng.annotations.BeforeGroups;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;

/**
 * 
 * @author Adrian Cole
 */
public class BaseCloudStackClientLiveTest extends BaseVersionedServiceLiveTest {
   protected String domainAdminIdentity;
   protected String domainAdminCredential;

   public BaseCloudStackClientLiveTest() {
      provider = "cloudstack";
   }

   @Override
   protected void setupCredentials() {
      super.setupCredentials();
      domainAdminIdentity = emptyToNull(System.getProperty("test." + provider + ".domainAdminIdentity"));
      domainAdminCredential = emptyToNull(System.getProperty("test." + provider + ".domainAdminCredential"));
   }

   protected Properties setupAdminProperties() {
      if (domainAdminIdentity != null && domainAdminCredential != null) {
         Properties overrides = setupProperties();
         overrides.setProperty(provider + ".identity", domainAdminIdentity);
         overrides.setProperty(provider + ".credential", domainAdminCredential);
         return overrides;
      } else {
         return null;
      }
   }

   public static long defaultTemplateOrPreferredInZone(Long defaultTemplate, CloudStackClient client, long zoneId) {
      long templateId = defaultTemplate != null ? defaultTemplate : getTemplateForZone(client, zoneId);
      return templateId;
   }

   public static long getTemplateForZone(CloudStackClient client, long zoneId) {
      // TODO enum, as this is way too easy to mess up.
      Set<String> acceptableCategories = ImmutableSet.of("Ubuntu", "CentOS");

      final Predicate<Template> hypervisorPredicate = new CorrectHypervisorForZone(client).apply(zoneId);
      final Predicate<Template> osTypePredicate = new OSCategoryIn(client).apply(acceptableCategories);

      @SuppressWarnings("unchecked")
      Predicate<Template> templatePredicate = Predicates.<Template> and(TemplatePredicates.isReady(),
            hypervisorPredicate, osTypePredicate);
      Iterable<Template> templates = filter(
            client.getTemplateClient().listTemplates(ListTemplatesOptions.Builder.zoneId(zoneId)), templatePredicate);
      if (Iterables.any(templates, TemplatePredicates.isPasswordEnabled())) {
         templates = filter(templates, TemplatePredicates.isPasswordEnabled());
      }
      if (Iterables.size(templates) == 0) {
         throw new NoSuchElementException(templatePredicate.toString());
      }
      long templateId = get(templates, 0).getId();
      return templateId;
   }

   protected String prefix = System.getProperty("user.name");

   protected ComputeServiceContext computeContext;
   protected RestContext<CloudStackClient, CloudStackAsyncClient> context;
   protected CloudStackClient client;
   protected CloudStackClient adminClient;
   protected User user;

   protected Predicate<IPSocket> socketTester;
   protected RetryablePredicate<Long> jobComplete;
   protected RetryablePredicate<Long> adminJobComplete;
   protected RetryablePredicate<VirtualMachine> virtualMachineRunning;
   protected RetryablePredicate<VirtualMachine> virtualMachineDestroyed;
   protected SshClient.Factory sshFactory;
   protected String password = "password";

   protected Injector injector;

   protected ReuseOrAssociateNewPublicIPAddress reuseOrAssociate;

   protected boolean domainAdminEnabled;
   protected ComputeServiceContext domainAdminComputeContext;
   protected RestContext<CloudStackClient, CloudStackAsyncClient> domainAdminContext;
   protected CloudStackClient domainAdminClient;
   protected User domainAdminUser;

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

   @BeforeGroups(groups = "live")
   public void setupClient() {
      setupCredentials();

      computeContext = new ComputeServiceContextFactory().createContext(provider,
            ImmutableSet.<Module> of(new Log4JLoggingModule(), new SshjSshClientModule()), setupProperties());
      context = computeContext.getProviderSpecificContext();
      client = context.getApi();
      user = verifyCurrentUserIsOfType(context, Account.Type.USER);

      domainAdminEnabled = setupAdminProperties() != null;

      if (domainAdminEnabled) {
         domainAdminComputeContext = new ComputeServiceContextFactory().createContext(provider,
               ImmutableSet.<Module> of(new Log4JLoggingModule(), new SshjSshClientModule()), setupAdminProperties());
         domainAdminContext = domainAdminComputeContext.getProviderSpecificContext();
         domainAdminClient = domainAdminContext.getApi();
         domainAdminUser = verifyCurrentUserIsOfType(domainAdminContext, Account.Type.DOMAIN_ADMIN);
         adminClient = domainAdminContext.getApi();
      }

      injector = Guice.createInjector(new SshjSshClientModule(), new Log4JLoggingModule());
      sshFactory = injector.getInstance(SshClient.Factory.class);
      socketTester = new RetryablePredicate<IPSocket>(new InetSocketAddressConnect(), 180, 1, 1, TimeUnit.SECONDS);
      injector.injectMembers(socketTester);
      jobComplete = new RetryablePredicate<Long>(new JobComplete(client), 1200, 1, 5, TimeUnit.SECONDS);
      injector.injectMembers(jobComplete);
      adminJobComplete = new RetryablePredicate<Long>(new JobComplete(adminClient), 1200, 1, 5, TimeUnit.SECONDS);
      injector.injectMembers(adminJobComplete);
      virtualMachineRunning = new RetryablePredicate<VirtualMachine>(new VirtualMachineRunning(client), 600, 5, 5,
            TimeUnit.SECONDS);
      injector.injectMembers(virtualMachineRunning);
      virtualMachineDestroyed = new RetryablePredicate<VirtualMachine>(new VirtualMachineDestroyed(client), 600, 5, 5,
            TimeUnit.SECONDS);
      injector.injectMembers(virtualMachineDestroyed);
      reuseOrAssociate = new ReuseOrAssociateNewPublicIPAddress(client, jobComplete);
      injector.injectMembers(reuseOrAssociate);
   }

   protected static User verifyCurrentUserIsOfType(RestContext<CloudStackClient, CloudStackAsyncClient> context,
         Account.Type type) {
      Iterable<User> users = Iterables.concat(context.getApi().getAccountClient().listAccounts());
      Predicate<User> apiKeyMatches = UserPredicates.apiKeyEquals(context.getIdentity());
      User currentUser;
      try {
         currentUser = Iterables.find(users, apiKeyMatches);
      } catch (NoSuchElementException e) {
         throw new NoSuchElementException(String.format("none of the following users match %s: %s", apiKeyMatches,
               users));
      }

      if (currentUser.getAccountType() != type) {
         Logger.getAnonymousLogger().warning("You are using an administrative account for testing");
         /* throw new IllegalArgumentException(String.format(
               "invalid account type: %s, please specify an apiKey of %s, for example: %s",
               currentUser.getAccountType(), type, Iterables.filter(users, UserPredicates.accountTypeEquals(type))));
          */
      }
      return currentUser;
   }

   @AfterGroups(groups = "live")
   protected void tearDown() {
      if (context != null)
         context.close();
   }

}