/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.cloudstack.internal;

import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.get;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.jclouds.cloudstack.domain.Account.Type.ADMIN;
import static org.jclouds.cloudstack.domain.Account.Type.DOMAIN_ADMIN;
import static org.jclouds.cloudstack.domain.Account.Type.USER;
import static org.jclouds.reflect.Reflection2.typeToken;
import static org.jclouds.util.Predicates2.retry;
import static org.testng.Assert.assertEquals;

import java.util.NoSuchElementException;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Logger;

import org.jclouds.cloudstack.CloudStackClient;
import org.jclouds.cloudstack.CloudStackContext;
import org.jclouds.cloudstack.CloudStackDomainClient;
import org.jclouds.cloudstack.CloudStackGlobalClient;
import org.jclouds.cloudstack.domain.Account;
import org.jclouds.cloudstack.domain.Template;
import org.jclouds.cloudstack.domain.User;
import org.jclouds.cloudstack.domain.VirtualMachine;
import org.jclouds.cloudstack.features.AccountClient;
import org.jclouds.cloudstack.functions.ReuseOrAssociateNewPublicIPAddress;
import org.jclouds.cloudstack.options.ListTemplatesOptions;
import org.jclouds.cloudstack.predicates.CorrectHypervisorForZone;
import org.jclouds.cloudstack.predicates.JobComplete;
import org.jclouds.cloudstack.predicates.OSCategoryIn;
import org.jclouds.cloudstack.predicates.TemplatePredicates;
import org.jclouds.cloudstack.predicates.UserPredicates;
import org.jclouds.cloudstack.predicates.VirtualMachineDestroyed;
import org.jclouds.cloudstack.predicates.VirtualMachineRunning;
import org.jclouds.cloudstack.strategy.BlockUntilJobCompletesAndReturnResult;
import org.jclouds.compute.ComputeService;
import org.jclouds.compute.domain.ExecResponse;
import org.jclouds.compute.internal.BaseGenericComputeServiceContextLiveTest;
import org.jclouds.predicates.SocketOpen;
import org.jclouds.ssh.SshClient;
import org.jclouds.sshj.config.SshjSshClientModule;
import org.testng.SkipException;
import org.testng.annotations.BeforeGroups;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.net.HostAndPort;
import com.google.common.reflect.TypeToken;
import com.google.inject.Injector;
import com.google.inject.Module;

/**
 * 
 * @author Adrian Cole
 */
public class BaseCloudStackClientLiveTest extends BaseGenericComputeServiceContextLiveTest<CloudStackContext> {
   protected String domainAdminIdentity;
   protected String domainAdminCredential;
   protected String globalAdminIdentity;
   protected String globalAdminCredential;

   public BaseCloudStackClientLiveTest() {
      provider = "cloudstack";
   }
   
   @Override
   protected TypeToken<CloudStackContext> viewType() {
      return typeToken(CloudStackContext.class);
   }
   
   @Override
   protected Properties setupProperties() {
      Properties overrides = super.setupProperties();
      domainAdminIdentity = setIfTestSystemPropertyPresent(overrides, provider + ".domainAdminIdentity");
      domainAdminCredential = setIfTestSystemPropertyPresent(overrides, provider + ".domainAdminCredential");
      globalAdminIdentity = setIfTestSystemPropertyPresent(overrides, provider + ".globalAdminIdentity");
      globalAdminCredential = setIfTestSystemPropertyPresent(overrides,  provider + ".globalAdminCredential");
      return overrides;
   }

   protected Properties setupDomainAdminProperties() {
      if (domainAdminIdentity != null && domainAdminCredential != null) {
         Properties overrides = setupProperties();
         overrides.setProperty(provider + ".identity", domainAdminIdentity);
         overrides.setProperty(provider + ".credential", domainAdminCredential);
         return overrides;
      } else {
         return null;
      }
   }

   protected Properties setupGlobalAdminProperties() {
      if (globalAdminIdentity != null && globalAdminCredential != null) {
         Properties overrides = setupProperties();
         overrides.setProperty(provider + ".identity", globalAdminIdentity);
         overrides.setProperty(provider + ".credential", globalAdminCredential);
         return overrides;
      } else {
         return null;
      }
   }

   public static String defaultTemplateOrPreferredInZone(String defaultTemplate, CloudStackClient client, String zoneId) {
      String templateId = defaultTemplate != null ? defaultTemplate : getTemplateForZone(client, zoneId);
      return templateId;
   }

   public static String getTemplateForZone(CloudStackClient client, String zoneId) {
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
      String templateId = get(templates, 0).getId();
      return templateId;
   }

   protected String prefix = System.getProperty("user.name");

   protected ComputeService computeClient;
   protected CloudStackContext cloudStackContext;
   protected CloudStackClient client;
   protected CloudStackClient adminClient;
   protected User user;

   protected Predicate<HostAndPort> socketTester;
   protected Predicate<String> jobComplete;
   protected Predicate<String> adminJobComplete;
   protected Predicate<VirtualMachine> virtualMachineRunning;
   protected Predicate<VirtualMachine> adminVirtualMachineRunning;
   protected Predicate<VirtualMachine> virtualMachineDestroyed;
   protected Predicate<VirtualMachine> adminVirtualMachineDestroyed;
   protected SshClient.Factory sshFactory;

   protected Injector injector;

   protected ReuseOrAssociateNewPublicIPAddress reuseOrAssociate;

   protected boolean domainAdminEnabled;
   protected CloudStackContext domainAdminComputeContext;
   protected CloudStackDomainClient domainAdminClient;
   protected User domainAdminUser;

   protected boolean globalAdminEnabled;
   protected CloudStackContext globalAdminComputeContext;
   protected CloudStackGlobalClient globalAdminClient;
   protected User globalAdminUser;
   
   protected void checkSSH(HostAndPort socket) {
      socketTester.apply(socket);
      SshClient client = sshFactory.create(socket, loginCredentials);
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

   @BeforeGroups(groups = { "integration", "live" })
   @Override
   public void setupContext() {
      super.setupContext();
      computeClient = view.getComputeService();
      cloudStackContext = CloudStackContext.class.cast(view);
      client = cloudStackContext.getApi();
      user = verifyCurrentUserIsOfType(identity, client.getAccountClient(), USER);

      domainAdminEnabled = setupDomainAdminProperties() != null;
      if (domainAdminEnabled) {
         domainAdminComputeContext = createView(setupDomainAdminProperties(), setupModules());
         domainAdminClient = domainAdminComputeContext.getDomainApi();
         domainAdminUser = verifyCurrentUserIsOfType(domainAdminIdentity, domainAdminClient.getAccountClient(),
               DOMAIN_ADMIN);
         adminClient = domainAdminClient;
      }

      globalAdminEnabled = setupGlobalAdminProperties() != null;
      if (globalAdminEnabled) {
         globalAdminComputeContext = createView(setupGlobalAdminProperties(), setupModules());
         globalAdminClient = globalAdminComputeContext.getGlobalApi();
         globalAdminUser = verifyCurrentUserIsOfType(globalAdminIdentity, globalAdminClient.getAccountClient(), ADMIN);
         adminClient = globalAdminClient;
      }

      injector = cloudStackContext.utils().injector();
      sshFactory = injector.getInstance(SshClient.Factory.class);
      SocketOpen socketOpen = context.utils().injector().getInstance(SocketOpen.class);
      socketTester = retry(socketOpen, 180, 1, 1, SECONDS);
      injector.injectMembers(socketTester);

      jobComplete = retry(new JobComplete(client), 1200, 1, 5, SECONDS);
      injector.injectMembers(jobComplete);
      adminJobComplete = retry(new JobComplete(adminClient), 1200, 1, 5, SECONDS);
      injector.injectMembers(adminJobComplete);
      virtualMachineRunning = retry(new VirtualMachineRunning(client), 600, 5, 5, SECONDS);
      injector.injectMembers(virtualMachineRunning);
      adminVirtualMachineRunning = retry(new VirtualMachineRunning(adminClient), 600, 5, 5, SECONDS);
      injector.injectMembers(adminVirtualMachineRunning);
      virtualMachineDestroyed = retry(new VirtualMachineDestroyed(client), 600, 5, 5, SECONDS);
      injector.injectMembers(virtualMachineDestroyed);
      adminVirtualMachineDestroyed = retry(new VirtualMachineDestroyed(adminClient), 600, 5, 5, SECONDS);
      injector.injectMembers(adminVirtualMachineDestroyed);
      reuseOrAssociate = new ReuseOrAssociateNewPublicIPAddress(client, new BlockUntilJobCompletesAndReturnResult(
            client, jobComplete));
      injector.injectMembers(reuseOrAssociate);
   }

   @Override
   protected Module getSshModule() {
      return new SshjSshClientModule();
   }
   
   private static User verifyCurrentUserIsOfType(String identity, AccountClient accountClient, Account.Type type) {
      Iterable<User> users = Iterables.concat(accountClient.listAccounts());
      Predicate<User> apiKeyMatches = UserPredicates.apiKeyEquals(identity);
      User currentUser;
      try {
         currentUser = Iterables.find(users, apiKeyMatches);
      } catch (NoSuchElementException e) {
         throw new NoSuchElementException(String.format("none of the following users match %s: %s", apiKeyMatches,
               users));
      }

      if (currentUser.getAccountType() != type) {
         Logger.getAnonymousLogger().warning(
               String.format("Expecting an user with type %s. Got: %s", type.toString(), currentUser.toString()));
      }
      return currentUser;
   }

   protected void skipIfNotDomainAdmin() {
      if (!domainAdminEnabled) {
         throw new SkipException("Test cannot run without domain admin identity and credentials");
      }
   }

   protected void skipIfNotGlobalAdmin() {
      if (!globalAdminEnabled) {
         throw new SkipException("Test cannot run without global admin identity and credentials");
      }
   }

}
