package org.jclouds.virtualbox.functions;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.compute.options.RunScriptOptions.Builder.runAsRoot;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.regex.Pattern;

import javax.annotation.Resource;

import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.logging.Logger;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Splitter;
import com.google.common.base.Throwables;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.google.inject.name.Named;

public class RetrieveActiveBridgedInterfaces implements
      Function<String, List<String>> {

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   private ComputeServiceContext context;

   @Inject
   public RetrieveActiveBridgedInterfaces(ComputeServiceContext context) {
      super();
      this.context = context;
   }

   @Override
   public List<String> apply(String hostId) {
      // Bridged Network
      String command = "vboxmanage list bridgedifs";
      String bridgedIfBlocks = context
            .getComputeService()
            .runScriptOnNode(hostId, command,
                  runAsRoot(false).wrapInInitScript(false)).getOutput();

      List<String> bridgedInterfaces = retrieveBridgedInterfaceNames(bridgedIfBlocks);
      checkNotNull(bridgedInterfaces);

      // union of bridgedNetwork with inet up and !loopback
      List<String> activeNetworkInterfaceNames = Lists.newArrayList();
      Enumeration<NetworkInterface> nets;
      try {
         Iterable<String> filterdBridgedInterface = null;
         nets = NetworkInterface.getNetworkInterfaces();
         for (NetworkInterface inet : Collections.list(nets)) {
            filterdBridgedInterface = Iterables.filter(bridgedInterfaces,
                  new IsActiveBridgedInterface(inet));
            for (String filterInetName : filterdBridgedInterface) {
               activeNetworkInterfaceNames.add(filterInetName);
            }
         }
      } catch (SocketException e) {
         logger.error(e, "Problem in listing network interfaces.");
         propagate(e);
      }
      return activeNetworkInterfaceNames;
   }

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
            for (String string : Splitter.on("Name:").split(
                  bridgedInterfaceName)) {
               if (!string.isEmpty())
                  bridgedInterfaceNames.add(string.trim());
            }
         }
      }
      return bridgedInterfaceNames;
   }

   protected <T> T propagate(Exception e) {
      Throwables.propagate(e);
      assert false;
      return null;
   }
   
   private class IsActiveBridgedInterface implements Predicate<String> {

      private NetworkInterface networkInterface;

      public IsActiveBridgedInterface(NetworkInterface networkInterface) {

         super();
         this.networkInterface = networkInterface;
      }

      @Override
      public boolean apply(String bridgedInterfaceName) {
         try {
            return (bridgedInterfaceName.startsWith(networkInterface
                  .getDisplayName()) && networkInterface.isUp() && !networkInterface
                     .isLoopback());
         } catch (SocketException e) {
            logger.error(e, "Problem in listing network interfaces.");
            propagate(e);
         }
         return false;
      }
   };
}