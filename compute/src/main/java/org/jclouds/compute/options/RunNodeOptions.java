package org.jclouds.compute.options;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Contains options supported in the {@code ComputeService#runNode} operation. <h2>
 * Usage</h2> The recommended way to instantiate a RunOptions object is to statically import
 * RunOptions.* and invoke a static creation method followed by an instance mutator (if needed):
 * <p/>
 * <code>
 * import static org.jclouds.compute.options.RunOptions.Builder.*
 * <p/>
 * ComputeService client = // get connection
 * CreateNodeResponse client.runNode(name, template, openPorts(22, 80, 8080, 443));
 * <code>
 * 
 * @author Adrian Cole
 */
public class RunNodeOptions {

   public static final RunNodeOptions NONE = new RunNodeOptions();

   private int[] openPorts = new int[0];

   private byte[] script;

   public int[] getOpenPorts() {
      return openPorts;
   }
   
   public byte[] getRunScript() {
      return script;
   }


   /**
    * This script will be executed as the root user upon system startup.
    */
   public RunNodeOptions runScript(byte[] script) {
      checkArgument(checkNotNull(script, "script").length <= 16 * 1024,
               "script cannot be larger than 16kb");
      this.script = script;
      return this;
   }

   /**
    * Opens the set of ports to public access.
    */
   public RunNodeOptions openPorts(int... ports) {
      this.openPorts = ports;
      return this;
   }

   public static class Builder {

      /**
       * @see RunNodeOptions#openPorts
       */
      public static RunNodeOptions openPorts(int... ports) {
         RunNodeOptions options = new RunNodeOptions();
         return options.openPorts(ports);
      }

      /**
       * @see RunNodeOptions#runScript
       */
      public static RunNodeOptions runScript(byte[] script) {
         RunNodeOptions options = new RunNodeOptions();
         return options.runScript(script);
      }

   }
}
