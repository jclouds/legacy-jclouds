package org.jclouds.compute.options;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Arrays;

/**
 * Contains options supported in the {@code ComputeService#runNode} operation. <h2>
 * Usage</h2> The recommended way to instantiate a TemplateOptions object is to statically import
 * TemplateOptions.* and invoke a static creation method followed by an instance mutator (if
 * needed):
 * <p/>
 * <code>
 * import static org.jclouds.compute.options.TemplateOptions.Builder.*;
 * <p/>
 * ComputeService client = // get connection
 * NodeSet set = client.runNode(name, template.options(inboundPorts(22, 80, 8080, 443)));
 * <code>
 * 
 * @author Adrian Cole
 */
public class TemplateOptions {

   public static final TemplateOptions NONE = new TemplateOptions();

   private int[] inboundPorts = new int[] { 22 };

   private byte[] script;

   private String privateKey;

   private String publicKey;

   private boolean destroyOnError;

   private int port = -1;

   private int seconds = -1;

   public int getPort() {
      return port;
   }

   public int getSeconds() {
      return seconds;
   }

   public int[] getInboundPorts() {
      return inboundPorts;
   }

   public byte[] getRunScript() {
      return script;
   }

   public String getPrivateKey() {
      return privateKey;
   }

   public String getPublicKey() {
      return publicKey;
   }

   public boolean shouldDestroyOnError() {
      return destroyOnError;
   }

   /**
    * When the node is started, wait until the following port is active
    */
   public TemplateOptions blockOnPort(int port, int seconds) {
      checkArgument(port > 0 && port < 65536, "port must be a positive integer < 65535");
      checkArgument(seconds > 0, "seconds must be a positive integer");
      this.port = port;
      this.seconds = seconds;
      return this;
   }

   /**
    * If there is an error applying options after creating the node, destroy it.
    */
   public TemplateOptions destroyOnError() {
      this.destroyOnError = true;
      return this;
   }

   /**
    * This script will be executed as the root user upon system startup. This script gets a
    * prologue, so no #!/bin/bash required, path set up, etc
    */
   public TemplateOptions runScript(byte[] script) {
      checkArgument(checkNotNull(script, "script").length <= 16 * 1024,
               "script cannot be larger than 16kb");
      this.script = script;
      return this;
   }

   /**
    * replaces the rsa ssh key used at login.
    */
   public TemplateOptions installPrivateKey(String privateKey) {
      checkArgument(checkNotNull(privateKey, "privateKey").startsWith(
               "-----BEGIN RSA PRIVATE KEY-----"),
               "key should start with -----BEGIN RSA PRIVATE KEY-----");
      this.privateKey = privateKey;
      return this;
   }

   /**
    * authorized an rsa ssh key.
    */
   public TemplateOptions authorizePublicKey(String publicKey) {
      checkArgument(checkNotNull(publicKey, "publicKey").startsWith("ssh-rsa"),
               "key should start with ssh-rsa");
      this.publicKey = publicKey;
      return this;
   }

   /**
    * Opens the set of ports to public access.
    */
   public TemplateOptions inboundPorts(int... ports) {
      for (int port : ports)
         checkArgument(port > 0 && port < 65536, "port must be a positive integer < 65535");
      this.inboundPorts = ports;
      return this;
   }

   public static class Builder {
      /**
       * @see TemplateOptions#destroyOnError
       */
      public static TemplateOptions destroyOnError() {
         TemplateOptions options = new TemplateOptions();
         return options.destroyOnError();
      }

      /**
       * @see TemplateOptions#inboundPorts
       */
      public static TemplateOptions inboundPorts(int... ports) {
         TemplateOptions options = new TemplateOptions();
         return options.inboundPorts(ports);
      }

      /**
       * @see TemplateOptions#port
       */
      public static TemplateOptions blockOnPort(int port, int seconds) {
         TemplateOptions options = new TemplateOptions();
         return options.blockOnPort(port, seconds);
      }

      /**
       * @see TemplateOptions#runScript
       */
      public static TemplateOptions runScript(byte[] script) {
         TemplateOptions options = new TemplateOptions();
         return options.runScript(script);
      }

      /**
       * @see TemplateOptions#installPrivateKey
       */
      public static TemplateOptions installPrivateKey(String rsaKey) {
         TemplateOptions options = new TemplateOptions();
         return options.installPrivateKey(rsaKey);
      }

      /**
       * @see TemplateOptions#authorizePublicKey
       */
      public static TemplateOptions authorizePublicKey(String rsaKey) {
         TemplateOptions options = new TemplateOptions();
         return options.authorizePublicKey(rsaKey);
      }

   }

   @Override
   public String toString() {
      return "TemplateOptions [inboundPorts=" + Arrays.toString(inboundPorts) + ", privateKey="
               + (privateKey != null) + ", publicKey=" + (publicKey != null) + ", runScript="
               + (script != null) + ", destroyOnError=" + destroyOnError + ", port:seconds=" + port
               + ":" + seconds + "]";
   }
}
