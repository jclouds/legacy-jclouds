package org.jclouds.vcloud.terremark.compute;

import static org.jclouds.vcloud.terremark.compute.options.TerremarkVCloudTemplateOptions.Builder.authorizePublicKey;
import static org.jclouds.vcloud.terremark.compute.options.TerremarkVCloudTemplateOptions.Builder.blockOnPort;
import static org.jclouds.vcloud.terremark.compute.options.TerremarkVCloudTemplateOptions.Builder.inboundPorts;
import static org.jclouds.vcloud.terremark.compute.options.TerremarkVCloudTemplateOptions.Builder.installPrivateKey;
import static org.jclouds.vcloud.terremark.compute.options.TerremarkVCloudTemplateOptions.Builder.noKeyPair;
import static org.jclouds.vcloud.terremark.compute.options.TerremarkVCloudTemplateOptions.Builder.sshKeyFingerprint;
import static org.testng.Assert.assertEquals;

import java.io.IOException;

import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.util.Utils;
import org.jclouds.vcloud.terremark.compute.options.TerremarkVCloudTemplateOptions;
import org.testng.annotations.Test;

/**
 * Tests possible uses of TerremarkVCloudTemplateOptions and
 * TerremarkVCloudTemplateOptions.Builder.*
 * 
 * @author Adrian Cole
 */
public class TerremarkVCloudTemplateOptionsTest {

   public void testAs() {
      TemplateOptions options = new TerremarkVCloudTemplateOptions();
      assertEquals(options.as(TerremarkVCloudTemplateOptions.class), options);
   }

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testkeyPairBadFormat() {
      TerremarkVCloudTemplateOptions options = new TerremarkVCloudTemplateOptions();
      options.sshKeyFingerprint("");
   }

   @Test(expectedExceptions = IllegalStateException.class)
   public void testkeyPairAndNoKeyPair() {
      TerremarkVCloudTemplateOptions options = new TerremarkVCloudTemplateOptions();
      options.sshKeyFingerprint("mykeypair");
      options.noKeyPair();
   }

   @Test(expectedExceptions = IllegalStateException.class)
   public void testNoKeyPairAndKeyPair() {
      TerremarkVCloudTemplateOptions options = new TerremarkVCloudTemplateOptions();
      options.noKeyPair();
      options.sshKeyFingerprint("mykeypair");
   }

   @Test
   public void testkeyPair() {
      TerremarkVCloudTemplateOptions options = new TerremarkVCloudTemplateOptions();
      options.sshKeyFingerprint("mykeypair");
      assertEquals(options.getSshKeyFingerprint(), "mykeypair");
   }

   @Test
   public void testNullkeyPair() {
      TerremarkVCloudTemplateOptions options = new TerremarkVCloudTemplateOptions();
      assertEquals(options.getSshKeyFingerprint(), null);
   }

   @Test
   public void testkeyPairStatic() {
      TerremarkVCloudTemplateOptions options = sshKeyFingerprint("mykeypair");
      assertEquals(options.getSshKeyFingerprint(), "mykeypair");
   }

   @Test(expectedExceptions = NullPointerException.class)
   public void testkeyPairNPE() {
      sshKeyFingerprint(null);
   }

   @Test
   public void testnoKeyPair() {
      TerremarkVCloudTemplateOptions options = new TerremarkVCloudTemplateOptions();
      options.noKeyPair();
      assertEquals(options.getSshKeyFingerprint(), null);
      assert !options.shouldAutomaticallyCreateKeyPair();
   }

   @Test
   public void testFalsenoKeyPair() {
      TerremarkVCloudTemplateOptions options = new TerremarkVCloudTemplateOptions();
      assertEquals(options.getSshKeyFingerprint(), null);
      assert options.shouldAutomaticallyCreateKeyPair();
   }

   @Test
   public void testnoKeyPairStatic() {
      TerremarkVCloudTemplateOptions options = noKeyPair();
      assertEquals(options.getSshKeyFingerprint(), null);
      assert !options.shouldAutomaticallyCreateKeyPair();
   }

   // superclass tests
   @SuppressWarnings("deprecation")
   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testinstallPrivateKeyBadFormat() {
      TerremarkVCloudTemplateOptions options = new TerremarkVCloudTemplateOptions();
      options.installPrivateKey("whompy");
   }

   @SuppressWarnings("deprecation")
   @Test
   public void testinstallPrivateKey() throws IOException {
      TerremarkVCloudTemplateOptions options = new TerremarkVCloudTemplateOptions();
      options.installPrivateKey("-----BEGIN RSA PRIVATE KEY-----");
      assertEquals(Utils.toStringAndClose(options.getPrivateKey().getInput()), "-----BEGIN RSA PRIVATE KEY-----");
   }

   @Test
   public void testNullinstallPrivateKey() {
      TerremarkVCloudTemplateOptions options = new TerremarkVCloudTemplateOptions();
      assertEquals(options.getPrivateKey(), null);
   }

   @Test
   public void testinstallPrivateKeyStatic() throws IOException {
      TerremarkVCloudTemplateOptions options = installPrivateKey("-----BEGIN RSA PRIVATE KEY-----");
      assertEquals(Utils.toStringAndClose(options.getPrivateKey().getInput()), "-----BEGIN RSA PRIVATE KEY-----");
   }

   @Test(expectedExceptions = NullPointerException.class)
   public void testinstallPrivateKeyNPE() {
      installPrivateKey(null);
   }

   @SuppressWarnings("deprecation")
   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testauthorizePublicKeyBadFormat() {
      TerremarkVCloudTemplateOptions options = new TerremarkVCloudTemplateOptions();
      options.authorizePublicKey("whompy");
   }

   @SuppressWarnings("deprecation")
   @Test
   public void testauthorizePublicKey() throws IOException {
      TerremarkVCloudTemplateOptions options = new TerremarkVCloudTemplateOptions();
      options.authorizePublicKey("ssh-rsa");
      assertEquals(Utils.toStringAndClose(options.getPublicKey().getInput()), "ssh-rsa");
   }

   @Test
   public void testNullauthorizePublicKey() {
      TerremarkVCloudTemplateOptions options = new TerremarkVCloudTemplateOptions();
      assertEquals(options.getPublicKey(), null);
   }

   @Test
   public void testauthorizePublicKeyStatic() throws IOException {
      TerremarkVCloudTemplateOptions options = authorizePublicKey("ssh-rsa");
      assertEquals(Utils.toStringAndClose(options.getPublicKey().getInput()), "ssh-rsa");
   }

   @Test(expectedExceptions = NullPointerException.class)
   public void testauthorizePublicKeyNPE() {
      authorizePublicKey(null);
   }

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testblockOnPortBadFormat() {
      TerremarkVCloudTemplateOptions options = new TerremarkVCloudTemplateOptions();
      options.blockOnPort(-1, -1);
   }

   @Test
   public void testblockOnPort() {
      TerremarkVCloudTemplateOptions options = new TerremarkVCloudTemplateOptions();
      options.blockOnPort(22, 30);
      assertEquals(options.getPort(), 22);
      assertEquals(options.getSeconds(), 30);

   }

   @Test
   public void testNullblockOnPort() {
      TerremarkVCloudTemplateOptions options = new TerremarkVCloudTemplateOptions();
      assertEquals(options.getPort(), -1);
      assertEquals(options.getSeconds(), -1);
   }

   @Test
   public void testblockOnPortStatic() {
      TerremarkVCloudTemplateOptions options = blockOnPort(22, 30);
      assertEquals(options.getPort(), 22);
      assertEquals(options.getSeconds(), 30);
   }

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testinboundPortsBadFormat() {
      TerremarkVCloudTemplateOptions options = new TerremarkVCloudTemplateOptions();
      options.inboundPorts(-1, -1);
   }

   @Test
   public void testinboundPorts() {
      TerremarkVCloudTemplateOptions options = new TerremarkVCloudTemplateOptions();
      options.inboundPorts(22, 30);
      assertEquals(options.getInboundPorts()[0], 22);
      assertEquals(options.getInboundPorts()[1], 30);

   }

   @Test
   public void testDefaultOpen22() {
      TerremarkVCloudTemplateOptions options = new TerremarkVCloudTemplateOptions();
      assertEquals(options.getInboundPorts()[0], 22);
   }

   @Test
   public void testinboundPortsStatic() {
      TerremarkVCloudTemplateOptions options = inboundPorts(22, 30);
      assertEquals(options.getInboundPorts()[0], 22);
      assertEquals(options.getInboundPorts()[1], 30);
   }
}
