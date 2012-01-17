package org.jclouds.overthere;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.inject.Module;
import org.jclouds.ContextBuilder;
import org.jclouds.aws.ec2.compute.AWSEC2TemplateOptions;
import org.jclouds.aws.ec2.reference.AWSEC2Constants;
import org.jclouds.compute.ComputeService;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.domain.ExecResponse;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.options.RunScriptOptions;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.ec2.EC2ApiMetadata;
import org.jclouds.ec2.compute.domain.PasswordDataAndPrivateKey;
import org.jclouds.ec2.compute.functions.WindowsLoginCredentialsFromEncryptedData;
import org.jclouds.ec2.domain.PasswordData;
import org.jclouds.ec2.services.WindowsClient;
import org.jclouds.encryption.bouncycastle.config.BouncyCastleCryptoModule;
import org.jclouds.logging.Logger;
import org.jclouds.logging.config.ConsoleLoggingModule;
import org.jclouds.overthere.config.OverthereClientModule;
import org.jclouds.predicates.RetryablePredicate;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import javax.annotation.Nullable;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static org.testng.Assert.*;

@Test(groups = "live")
public class OverthereClientLiveTest {

   private static final Logger logger = Logger.CONSOLE;
   private Set<? extends NodeMetadata> nodes;
   private ComputeServiceContext context;
   private ComputeService computeService;
   private WindowsClient windowsClient;

   @BeforeClass
   public void testFixtureSetUp() {
      Properties overrides = new Properties();
      overrides.setProperty(AWSEC2Constants.PROPERTY_EC2_AMI_QUERY, "owner-id=449550055360;state=available;image-type=machine");
      ImmutableSet<Module> modules = ImmutableSet.<Module>of(
         new ConsoleLoggingModule(),
         new BouncyCastleCryptoModule(),
         new OverthereClientModule());
      context = ContextBuilder.newBuilder("aws-ec2")
         .credentials("SECRET", "SECRET")
         .overrides(overrides)
         .modules(modules)
         .build(ComputeServiceContext.class);
      computeService = context.getComputeService();
      windowsClient = context.unwrap(EC2ApiMetadata.CONTEXT_TOKEN).getApi().getWindowsServices();
   }

   @Test
   public void testOverthere() throws Exception {
      Template template = computeService.templateBuilder()
         .imageNameMatches("Windows_Server-2008-R2_SP1-English-64Bit-Base-WinRM-")
         .hardwareId("m1.small")
         .build();
      template.getOptions().as(AWSEC2TemplateOptions.class)
         .inboundPorts(5986);
      logger.info("Starting node with image %s", template.getImage().getId());
      nodes = computeService.createNodesInGroup("overthere", 1, template);
      NodeMetadata node = Iterables.getOnlyElement(nodes);

      // The Administrator password will take some time before it is ready - Amazon says sometimes 15 minutes.
      // So we create a predicate that tests if the password is ready, and wrap it in a retryable predicate.
      Predicate<String> passwordReady = new Predicate<String>() {
         @Override
         public boolean apply(@Nullable String s) {
            if (Strings.isNullOrEmpty(s)) return false;
            PasswordData data = windowsClient.getPasswordDataInRegion(null, s);
            if (data == null) return false;
            return !Strings.isNullOrEmpty(data.getPasswordData());
         }
      };
      RetryablePredicate<String> passwordReadyRetryable = new RetryablePredicate<String>(passwordReady, 600, 10, TimeUnit.SECONDS);
      logger.info("Waiting for the Administrator password. This may take some time.");
      assertTrue(passwordReadyRetryable.apply(node.getProviderId()));
      logger.info("...got it");

      // Now pull together Amazon's encrypted password blob, and the private key that jclouds generated
      PasswordDataAndPrivateKey dataAndKey = new PasswordDataAndPrivateKey(
         windowsClient.getPasswordDataInRegion(null, node.getProviderId()),
         node.getCredentials().getPrivateKey());

      // And apply it to the decryption function
      WindowsLoginCredentialsFromEncryptedData f = context.getUtils().getInjector().getInstance(WindowsLoginCredentialsFromEncryptedData.class);
      LoginCredentials credentials = f.apply(dataAndKey);

      assertEquals(credentials.getUser(), "Administrator");
      assertFalse(Strings.isNullOrEmpty(credentials.getPassword()));
      logger.info("Host / login / password is %s / %s / %s", Iterables.getFirst(node.getPublicAddresses(), "not-public"), credentials.getUser(), credentials.getPassword());

      logger.info("Executing command on node");
      ExecResponse response = computeService.runScriptOnNode(node.getId(), "echo hello world",
         RunScriptOptions.Builder.overrideLoginCredentials(credentials));
      assertEquals(response.getOutput(), "hello world");

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
}
