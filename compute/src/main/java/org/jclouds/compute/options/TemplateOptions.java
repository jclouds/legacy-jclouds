package org.jclouds.compute.options;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Contains options supported in the {@code ComputeService#runNode} operation. <h2>
 * Usage</h2> The recommended way to instantiate a TemplateOptions object is to statically import
 * TemplateOptions.* and invoke a static creation method followed by an instance mutator (if needed):
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

   public int[] getInboundPorts() {
      return inboundPorts;
   }

   public byte[] getRunScript() {
      return script;
   }

   /**
    * This script will be executed as the root user upon system startup.
    */
   public TemplateOptions runScript(byte[] script) {
      checkArgument(checkNotNull(script, "script").length <= 16 * 1024,
               "script cannot be larger than 16kb");
      this.script = script;
      return this;
   }

   /**
    * Opens the set of ports to public access.
    */
   public TemplateOptions inboundPorts(int... ports) {
      this.inboundPorts = ports;
      return this;
   }

   public static class Builder {

      /**
       * @see TemplateOptions#inboundPorts
       */
      public static TemplateOptions inboundPorts(int... ports) {
         TemplateOptions options = new TemplateOptions();
         return options.inboundPorts(ports);
      }

      /**
       * @see TemplateOptions#runScript
       */
      public static TemplateOptions runScript(byte[] script) {
         TemplateOptions options = new TemplateOptions();
         return options.runScript(script);
      }

   }
}
