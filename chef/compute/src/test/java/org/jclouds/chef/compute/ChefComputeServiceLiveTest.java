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
import static com.google.common.collect.Iterables.any;
import static com.google.common.collect.Iterables.concat;
import static com.google.common.collect.Iterables.getLast;
import static org.jclouds.chef.predicates.CookbookVersionPredicates.containsRecipe;
import static org.jclouds.chef.predicates.CookbookVersionPredicates.containsRecipes;
import static org.jclouds.compute.options.TemplateOptions.Builder.runScript;
import static org.testng.Assert.assertEquals;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.List;
import java.util.Properties;

import org.jclouds.chef.ChefContext;
import org.jclouds.chef.ChefContextFactory;
import org.jclouds.chef.domain.CookbookVersion;
import org.jclouds.chef.util.RunListBuilder;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.ComputeServiceContextFactory;
import org.jclouds.compute.RunNodesException;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.predicates.NodePredicates;
import org.jclouds.io.Payload;
import org.jclouds.logging.log4j.config.Log4JLoggingModule;
import org.jclouds.ssh.jsch.config.JschSshClientModule;
import org.jclouds.util.Utils;
import org.testng.annotations.AfterGroups;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;

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
   private Iterable<? extends NodeMetadata> nodes;

   @BeforeGroups(groups = { "live" })
   public void setupAll() throws FileNotFoundException, IOException {
      tag = System.getProperty("jclouds.compute.tag") != null ? System.getProperty("jclouds.compute.tag")
               : "jcloudschef";
      String computeProvider = checkNotNull(System.getProperty("jclouds.compute.provider"), "jclouds.compute.provider");
      String computeEndpoint = System.getProperty("jclouds.compute.endpoint");
      String computeIdentity = checkNotNull(System.getProperty("jclouds.compute.identity"), "jclouds.compute.identity");
      String computeCredential = checkNotNull(System.getProperty("jclouds.compute.credential"),
               "jclouds.compute.credential");
      chefEndpoint = checkNotNull(System.getProperty("jclouds.chef.endpoint"), "jclouds.chef.endpoint");
      String chefIdentity = checkNotNull(System.getProperty("jclouds.chef.identity"), "jclouds.chef.identity");
      String chefCredentialFile = System.getProperty("jclouds.chef.credential.pem");
      if (chefCredentialFile == null || chefCredentialFile.equals(""))
         chefCredentialFile = System.getProperty("user.home") + "/.chef/" + chefIdentity + ".pem";

      Properties props = new Properties();
      props.setProperty(computeProvider + ".identity", computeIdentity);
      props.setProperty(computeProvider + ".credential", computeCredential);
      props.setProperty("chef.endpoint", chefEndpoint);
      props.setProperty("chef.identity", chefIdentity);
      props.setProperty("chef.credential.file", chefCredentialFile);

      if (computeEndpoint != null && !computeEndpoint.trim().equals(""))
         props.setProperty(computeProvider + ".endpoint", computeEndpoint);

      computeContext = new ComputeServiceContextFactory().createContext(computeProvider, ImmutableSet.of(
               new Log4JLoggingModule(), getSshModule()), props);

      chefContext = new ChefContextFactory().createContext(ImmutableSet.<Module> of(new Log4JLoggingModule()), props);
   }

   protected Module getSshModule() {
      return new JschSshClientModule();
   }

   @Test
   public void testCanUpdateRunList() throws IOException {
      String recipe = "apache2";

      Iterable<? extends CookbookVersion> cookbookVersions = chefContext.getChefService().listCookbookVersions();

      if (any(cookbookVersions, containsRecipe(recipe))) {
         List<String> runList = new RunListBuilder().addRecipe(recipe).build();
         chefContext.getChefService().updateRunListForTag(runList, tag);
         assertEquals(chefContext.getChefService().getRunListForTag(tag), runList);
      } else {
         assert false : String.format("recipe %s not in %s", recipe, cookbookVersions);
      }

      // TODO move this to a unit test
      assert any(cookbookVersions, containsRecipe("apache2::mod_proxy"));
      assert any(cookbookVersions, containsRecipes("apache2", "apache2::mod_proxy", "apache2::mod_proxy_http"));
      assert !any(cookbookVersions, containsRecipe("apache2::bar"));
      assert !any(cookbookVersions, containsRecipe("foo::bar"));
   }

   @Test(dependsOnMethods = "testCanUpdateRunList")
   public void testRunNodesWithBootstrap() throws IOException {

      Payload bootstrap = chefContext.getChefService().createClientAndBootstrapScriptForTag(tag);

      try {
         nodes = computeContext.getComputeService().runNodesWithTag(tag, 1, runScript(bootstrap));
      } catch (RunNodesException e) {
         nodes = concat(e.getSuccessfulNodes(), e.getNodeErrors().keySet());
      }

      for (NodeMetadata node : nodes) {
         URI uri = URI.create("http://" + getLast(node.getPublicAddresses()));
         InputStream content = computeContext.utils().http().get(uri);
         String string = Utils.toStringAndClose(content);
         assert string.indexOf("It works!") >= 0 : string;
      }

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
