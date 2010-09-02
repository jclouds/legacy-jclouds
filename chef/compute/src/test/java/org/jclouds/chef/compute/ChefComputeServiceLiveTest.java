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

package org.jclouds.chef.compute;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Sets.newHashSet;
import static org.jclouds.io.Payloads.newStringPayload;
import static org.jclouds.scriptbuilder.domain.Statements.createFile;
import static org.jclouds.scriptbuilder.domain.Statements.exec;
import static org.jclouds.scriptbuilder.domain.Statements.newStatementList;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.jclouds.chef.ChefContext;
import org.jclouds.chef.ChefContextFactory;
import org.jclouds.compute.BaseComputeServiceLiveTest;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.ComputeServiceContextFactory;
import org.jclouds.compute.RunNodesException;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.compute.predicates.NodePredicates;
import org.jclouds.crypto.Pems;
import org.jclouds.json.Json;
import org.jclouds.logging.log4j.config.Log4JLoggingModule;
import org.jclouds.scriptbuilder.domain.OsFamily;
import org.jclouds.scriptbuilder.domain.Statement;
import org.jclouds.ssh.jsch.config.JschSshClientModule;
import org.jclouds.util.Utils;
import org.testng.annotations.AfterGroups;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import com.google.common.base.Charsets;
import com.google.common.base.Splitter;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.io.Files;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", testName = "chef.ChefComputeServiceLiveTest")
public class ChefComputeServiceLiveTest {

   private ComputeServiceContext computeContext;
   private ChefContext chefContext;
   private String tag;
   private String clientName;
   private String chefEndpoint;
   private Map<String, String> keyPair;
   private Iterable<? extends NodeMetadata> nodes;

   @BeforeGroups(groups = { "live" })
   public void setupCompute() throws FileNotFoundException, IOException {
      tag = System.getProperty("jclouds.compute.tag") != null ? System.getProperty("jclouds.compute.tag")
               : "jcloudschef";
      String computeProvider = checkNotNull(System.getProperty("jclouds.compute.provider"), "jclouds.compute.provider");
      String computeEndpoint = System.getProperty("jclouds.compute.endpoint");
      Properties props = new Properties();
      if (computeEndpoint != null && !computeEndpoint.trim().equals(""))
         props.setProperty(computeProvider + ".endpoint", computeEndpoint);
      String computeIdentity = checkNotNull(System.getProperty("jclouds.compute.identity"), "jclouds.compute.identity");
      String computeCredential = checkNotNull(System.getProperty("jclouds.compute.credential"),
               "jclouds.compute.credential");
      computeContext = new ComputeServiceContextFactory().createContext(computeProvider, computeIdentity,
               computeCredential, ImmutableSet.of(new Log4JLoggingModule(), getSshModule()), props);
      keyPair = BaseComputeServiceLiveTest.setupKeyPair();
   }

   @BeforeGroups(groups = { "live" })
   public void setupChef() throws IOException {
      chefEndpoint = checkNotNull(System.getProperty("jclouds.chef.endpoint"), "jclouds.chef.endpoint");
      String chefIdentity = checkNotNull(System.getProperty("jclouds.chef.identity"), "jclouds.chef.identity");
      String chefCredentialFile = System.getProperty("jclouds.chef.credential.pem");
      if (chefCredentialFile == null || chefCredentialFile.equals(""))
         chefCredentialFile = System.getProperty("user.home") + "/.chef/" + chefIdentity + ".pem";
      Properties props = new Properties();
      props.setProperty("chef.endpoint", chefEndpoint);
      chefContext = new ChefContextFactory().createContext(chefIdentity, Files.toString(new File(chefCredentialFile),
               Charsets.UTF_8), ImmutableSet.<Module> of(new Log4JLoggingModule()), props);
   }

   public static class InstallChefGems implements Statement {

      public String render(OsFamily family) {
         try {
            return Utils.toStringAndClose(InstallChefGems.class.getClassLoader().getResourceAsStream(
                     "install-chef-gems.sh"));
         } catch (IOException e) {
            Throwables.propagate(e);
            return null;
         }
      }

      @Override
      public Iterable<String> functionDependecies(OsFamily family) {
         return Collections.emptyList();
      }

   }

   protected Module getSshModule() {
      return new JschSshClientModule();
   }

   @Test
   public void test() throws IOException, InterruptedException {
      clientName = findNextClientName(chefContext, tag + "-validator-%d");

      System.out.println("created new client: " + clientName);

      List<String> runList = ImmutableList.of("recipe[apache2]");
      Json json = computeContext.utils().json();

      String clientKey = Pems.pem(chefContext.getApi().createClient(clientName).getPrivateKey());

      Statement installChefGems = new InstallChefGems();
      String chefConfigDir = "{root}etc{fs}chef";
      Statement createChefConfigDir = exec("{md} " + chefConfigDir);
      Statement createClientRb = createFile(chefConfigDir + "{fs}client.rb", ImmutableList.of("require 'rubygems'",
               "require 'ohai'", "o = Ohai::System.new", "o.all_plugins", String.format(
                        "node_name \"%s-\" + o[:ipaddress]", tag), "log_level :info", "log_location STDOUT", String
                        .format("validation_client_name \"%s\"", clientName), String.format("chef_server_url \"%s\"",
                        chefEndpoint)));

      Statement createValidationPem = createFile(chefConfigDir + "{fs}validation.pem", Splitter.on('\n').split(
               clientKey));
      String chefBootFile = chefConfigDir + "{fs}first-boot.json";

      Statement createFirstBoot = createFile(chefBootFile, Collections.singleton(json.toJson(ImmutableMap
               .<String, List<String>> of("run_list", runList), new TypeLiteral<Map<String, List<String>>>() {
      }.getType())));

      Statement runChef = exec("chef-client -j " + chefBootFile);

      Statement bootstrapAndRunChef = newStatementList(installChefGems, createChefConfigDir, createClientRb,
               createValidationPem, createFirstBoot, runChef);

      String runScript = bootstrapAndRunChef.render(OsFamily.UNIX);
      System.out.println(runScript);

      TemplateOptions options = computeContext.getComputeService().templateOptions().//
               installPrivateKey(newStringPayload(keyPair.get("private"))).//
               authorizePublicKey(newStringPayload(keyPair.get("public"))).//
               runScript(newStringPayload(runScript));

      try {
         nodes = computeContext.getComputeService().runNodesWithTag(tag, 1, options);
      } catch (RunNodesException e) {
         nodes = Iterables.concat(e.getSuccessfulNodes(), e.getNodeErrors().keySet());
      }

      for (NodeMetadata node : nodes) {
         URI uri = URI.create("http://" + Iterables.getLast(node.getPublicAddresses()));
         InputStream content = computeContext.utils().http().get(uri);
         String string = Utils.toStringAndClose(content);
         assert string.indexOf("It works!") >= 0 : string;
      }

   }

   private String findNextClientName(ChefContext context, String pattern) {
      Set<String> nodes = context.getApi().listClients();
      String nodeName;
      Set<String> names = newHashSet(nodes);
      int index = 0;
      while (true) {
         nodeName = String.format(pattern, index++);
         if (!names.contains(nodeName))
            break;
      }
      return nodeName;
   }

   @AfterGroups(groups = { "live" })
   public void teardownCompute() {
      if (computeContext != null) {
         computeContext.getComputeService().destroyNodesMatching(NodePredicates.withTag(tag));
         computeContext.close();
      }
   }

   @AfterGroups(groups = { "live" })
   public void teardownChef() {
      if (chefContext != null) {
         chefContext.getChefService().cleanupStaleNodesAndClients(tag + "-", 1);
         if (clientName != null && chefContext.getApi().clientExists(clientName))
            chefContext.getApi().deleteClient(clientName);
         chefContext.close();
      }
   }
}
