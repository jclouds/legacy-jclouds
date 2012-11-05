package org.jclouds.overthere;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.util.Properties;
import java.util.Set;

import org.jclouds.Constants;
import org.jclouds.ContextBuilder;
import org.jclouds.aws.ec2.compute.AWSEC2TemplateOptions;
import org.jclouds.compute.ComputeService;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.domain.ExecResponse;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.domain.TemplateBuilderSpec;
import org.jclouds.compute.options.RunScriptOptions;
import org.jclouds.compute.predicates.NodePredicates;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.encryption.bouncycastle.config.BouncyCastleCryptoModule;
import org.jclouds.logging.Logger;
import org.jclouds.logging.config.ConsoleLoggingModule;
import org.jclouds.overthere.config.OverthereRunScriptClientModule;
import org.testng.SkipException;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.base.Predicates;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.inject.Module;

@Test(groups = "live")
public class OverthereClientLiveTest {

   private static final Logger logger = Logger.CONSOLE;
   
   protected String computeProvider;
   protected String computeIdentity;
   protected String computeCredential;
   protected String computeEndpoint;
   protected String computeApiversion;
   protected String computeBuildversion;
   private TemplateBuilderSpec windowsTemplate;

   private Set<? extends NodeMetadata> nodes;
   private ComputeServiceContext context;
   private ComputeService computeService;

   @BeforeClass
   public void testFixtureSetUp() {
      Properties overrides = setupProperties();

      ImmutableSet<Module> modules = ImmutableSet.<Module>of(
         new ConsoleLoggingModule(),
         new BouncyCastleCryptoModule(),
         new OverthereRunScriptClientModule());
      
      ContextBuilder builder = ContextBuilder.newBuilder(computeProvider)
               .credentials(computeIdentity, computeCredential)
               .overrides(overrides)
               .modules(modules);
      if (computeApiversion != null)
         builder.apiVersion(computeApiversion);
      if (computeBuildversion != null)
         builder.buildVersion(computeBuildversion);
      
      context = builder.build(ComputeServiceContext.class);
      
      computeService = context.getComputeService();
   }

   // Following pattern in BaseLoadBalancerServiceLiveTest...
   protected Properties setupProperties() {
      // Standard options, copy+pasted from BaseContextLiveTest...
      Properties overrides = new Properties();
      overrides.setProperty(Constants.PROPERTY_TRUST_ALL_CERTS, "true");
      overrides.setProperty(Constants.PROPERTY_RELAX_HOSTNAME, "true");
      computeProvider = setIfTestSystemPropertyPresent(overrides, ".provider");
      computeIdentity = setIfTestSystemPropertyPresent(overrides, ".identity");
      computeCredential = setIfTestSystemPropertyPresent(overrides, ".credential");
      computeEndpoint = setIfTestSystemPropertyPresent(overrides, ".endpoint");
      computeApiversion = setIfTestSystemPropertyPresent(overrides, ".api-version");
      computeBuildversion = setIfTestSystemPropertyPresent(overrides, ".build-version");
      
      // The Windows image; follows pattern in EC2ComputeServiceLiveTest for ebsTemplate
      // e.g. hardwareId=m1.small,imageId=us-east-1/ami-0cb76d65
      String windowsSpec = setIfTestSystemPropertyPresent(overrides, ".windows-template");
      if (windowsSpec != null) {
         windowsTemplate = TemplateBuilderSpec.parse(windowsSpec);
      }
      
      return overrides;
   }

   // Following pattern in BaseContextLiveTest...
   protected String setIfTestSystemPropertyPresent(Properties overrides, String key) {
      if (System.getProperties().containsKey("test.compute" + key)) {
         String val = System.getProperty("test.compute" + key);
         overrides.setProperty(key, val);
         return val;
      }
      return null;
   }

   @AfterMethod
   public void tearDown() {
      if (nodes != null && !nodes.isEmpty()) {
         logger.info("Destroying nodes");
         computeService.destroyNodesMatching(Predicates.in(nodes));
         nodes = null;
      }
   }

   @AfterClass
   public void testFixtureTearDown() {
      context.close();
   }

   @Test
   public void testOverthere() throws Exception {
      if (windowsTemplate == null) {
         throw new SkipException("Test cannot run without the parameter test." + computeProvider + ".windows-template; " + 
                  "this property should be in the format defined in TemplateBuilderSpec");
      }

      Template template = computeService.templateBuilder().from(windowsTemplate).build();
      template.getOptions().as(AWSEC2TemplateOptions.class)
               .inboundPorts(5986);
      
      logger.info("Starting node with image %s", template.getImage().getId());
      nodes = computeService.createNodesInGroup("overthere", 1, template);
      NodeMetadata node = Iterables.getOnlyElement(nodes);

      LoginCredentials credentials = node.getCredentials();
      
      assertEquals(credentials.getUser(), "Administrator");
      assertFalse(Strings.isNullOrEmpty(credentials.getPassword()));
      logger.info("Location:Id / Host / login / password is %s:%s / %s / %s / %s", node.getLocation().getId(), node.getId(), Iterables.getFirst(node.getPublicAddresses(), "not-public"), credentials.getUser(), credentials.getPassword());

      assertCommandsExecute(node, credentials);
   }

   /**
    * This test is extremely useful during dev, to speed up testing by using an existing aws-ec2 VM.
    * However, it can't be automated because we don't have a VM constantly running.
    * 
    * Modify the nodeId + password to match your existing node.
    * To generate a node, you could run testOverthere() with the tearDown disabled.
    */
   @Test(enabled=false)
   public void testOverthereOnPreexistingMachine() throws Exception {
      // Find the existing (hard-coded) node
      String nodeId = "us-east-1/i-ab33a1d0";
      String password = "jgXhJmfS@wK";
      String user = "Administrator";
      LoginCredentials credentials = LoginCredentials.builder().user(user).password(password).build();
      Set<? extends NodeMetadata> existingNodes = computeService.listNodesDetailsMatching(NodePredicates.withIds(nodeId));
      NodeMetadata node = Iterables.getOnlyElement(existingNodes);

      logger.info("Location:Id / Host / login / password is %s:%s / %s / %s / %s", node.getLocation().getId(), node.getId(), Iterables.getFirst(node.getPublicAddresses(), "not-public"), credentials.getUser(), credentials.getPassword());

      assertCommandsExecute(node, credentials);
   }
   
   private void assertCommandsExecute(NodeMetadata node, LoginCredentials credentials) {
      logger.info("Executing no-arg command");
      ExecResponse response = computeService.runScriptOnNode(node.getId(), "dir",
         RunScriptOptions.Builder.overrideLoginCredentials(credentials).wrapInInitScript(false));
      assertTrue(response.getOutput().contains("Directory of"), "response="+response);
      
      // FIXME Fails with:
      //      '"echo hello world "' is not recognized as an internal or external command
      //      See the FIXME in OverthereRunner.call, about "individual arguments"
//      logger.info("Executing command with args");
//      ExecResponse response2 = computeService.runScriptOnNode(node.getId(), "echo hello world",
//         RunScriptOptions.Builder.overrideLoginCredentials(credentials).wrapInInitScript(false));
//      assertEquals(response2.getOutput(), "hello world", "response="+response2);
   }
}
