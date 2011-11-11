package org.jclouds.virtualbox.functions;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.compute.options.RunScriptOptions.Builder.runAsRoot;

import java.util.List;
import java.util.regex.Pattern;

import org.jclouds.compute.ComputeServiceContext;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.inject.Inject;

public class RetrieveActiveBridgedInterface implements Function<String, String> {

   private ComputeServiceContext context;

   @Inject
   public RetrieveActiveBridgedInterface(ComputeServiceContext context) {
      super();
      this.context = context;
   }

   @Override
   public String apply(String hostId) {
      // Bridged Network
      String command = "vboxmanage list bridgedifs";
      String bridgedIfBlocks = context
            .getComputeService()
            .runScriptOnNode(hostId, command,
                  runAsRoot(false).wrapInInitScript(false)).getOutput();

      List<String> bridgedInterfaces = retrieveBridgedInterfaceNames(bridgedIfBlocks);
      checkNotNull(bridgedInterfaces);
      
      // ifconfig
      command = "ifconfig";
      String ifconfigBlocks = context
            .getComputeService()
            .runScriptOnNode(hostId, command,
                  runAsRoot(false).wrapInInitScript(false)).getOutput();
      
      //| grep status: active 
      List<String> interfaceNames = retrieveActiveNetworkInterfaces(ifconfigBlocks);

      for (String bi : bridgedInterfaces) {
         for (String ifs : interfaceNames) {
            if(bi.startsWith(ifs))
               return bi;
         }
      }

         return null;
   }

   /**
    * 
    * @return hostInterface
    */
   protected static List<String> retrieveBridgedInterfaceNames(
         String bridgedIfBlocks) {
      List<String> bridgedInterfaceNames = Lists.newArrayList();
      // separate the different bridge block
      for (String bridgedIfBlock : Splitter.on(
            Pattern.compile("(?m)^[ \t]*\r?\n")).split(bridgedIfBlocks)) {

         Iterable<String> bridgedIfName = Iterables.filter(Splitter.on("\n")
               .split(bridgedIfBlock), new Predicate<String>() {
            @Override
            public boolean apply(String arg0) {
               return arg0.startsWith("Name:");
            }
         });
         for (String bridgedInterfaceName : bridgedIfName) {
            for (String string : Splitter.on("Name:").split(bridgedInterfaceName)) {
               if(!string.equals(""))
                  bridgedInterfaceNames.add(string.trim());   
            }
            
         }
      }
      return bridgedInterfaceNames;
   }

   /**
    * 
    * @return hostInterface
    */
   protected static List<String> retrieveActiveNetworkInterfaces(
         String ifconfigBlocks) {
      List<String> activeNICnames = Lists.newArrayList();

      ifconfigBlocks = ifconfigBlocks.replace("\r\n\t", " ");

         Iterable<String> interfaceName = Iterables.filter(Splitter.on("\n").split(ifconfigBlocks), new Predicate<String>() {
            @Override
            public boolean apply(String arg0) {
               return arg0.contains("status: active");
            }
         });
         for (String bridgedInterfaceName : interfaceName) {
            activeNICnames.add(Splitter.on(":").split(bridgedInterfaceName).iterator().next());
         }
      
      return activeNICnames;
   }
   
}
