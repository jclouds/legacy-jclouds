/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jclouds.virtualbox.functions;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.filter;
import static org.jclouds.compute.options.RunScriptOptions.Builder.runAsRoot;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.regex.Pattern;

import javax.annotation.Resource;

import org.jclouds.compute.callables.RunScriptOnNode.Factory;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.logging.Logger;
import org.jclouds.scriptbuilder.domain.Statement;
import org.jclouds.scriptbuilder.domain.Statements;
import org.jclouds.virtualbox.domain.BridgedIf;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Splitter;
import com.google.common.base.Throwables;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.google.inject.name.Named;

/**
 * @author Andrea Turli
 */
public class RetrieveActiveBridgedInterfaces implements Function<NodeMetadata, List<BridgedIf>> {

@Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   private final Factory runScriptOnNodeFactory;

   @Inject
   public RetrieveActiveBridgedInterfaces(Factory runScriptOnNodeFactory) {	   
      this.runScriptOnNodeFactory = checkNotNull(runScriptOnNodeFactory, "runScriptOnNodeFactory");
   }

   @Override
   public List<BridgedIf> apply(NodeMetadata host) {
      // Bridged Network
      Statement command = Statements.exec("VBoxManage list bridgedifs");
      String bridgedIfBlocks = runScriptOnNodeFactory.create(host, command, runAsRoot(false).wrapInInitScript(false))
               .init().call().getOutput();

      List<BridgedIf> bridgedInterfaces = retrieveBridgedInterfaceNames(bridgedIfBlocks);
      checkNotNull(bridgedInterfaces);

      // union of bridgedNetwork with inet up and !loopback
      List<BridgedIf> activeNetworkInterfaces = Lists.newArrayList();
      try {
         Enumeration<NetworkInterface> nets = NetworkInterface.getNetworkInterfaces();
         for (NetworkInterface inet : Collections.list(nets)) {
            Iterable<BridgedIf> filteredBridgedInterface = filter(bridgedInterfaces, new IsActiveBridgedInterface(inet));
            Iterables.addAll(activeNetworkInterfaces, filteredBridgedInterface);
         }
      } catch (SocketException e) {
         logger.error(e, "Problem in listing network interfaces.");
         throw Throwables.propagate(e);
      }
      return activeNetworkInterfaces;
   }

   protected static List<BridgedIf> retrieveBridgedInterfaceNames(String bridgedIfBlocks) {
      List<BridgedIf> bridgedInterfaces = Lists.newArrayList();
      // separate the different bridge block
      for (String bridgedIfBlock : Splitter.on(Pattern.compile("(?m)^[ \t]*\r?\n")).split(bridgedIfBlocks)) {
    	  if(!bridgedIfBlock.isEmpty())
    		  bridgedInterfaces.add(new BridgedIfStringToBridgedIf().apply(bridgedIfBlock));
      }
      return bridgedInterfaces;
   }

   private class IsActiveBridgedInterface implements Predicate<BridgedIf> {

      private NetworkInterface networkInterface;

      public IsActiveBridgedInterface(NetworkInterface networkInterface) {
         this.networkInterface = networkInterface;
      }

      @Override
      public boolean apply(BridgedIf bridgedInterface) {
         try {
            return bridgedInterface.getName().startsWith(networkInterface.getDisplayName()) &&
            		bridgedInterface.getStatus().equals("Up") &&
            		networkInterface.isUp() && 
            		!networkInterface.isLoopback();
         } catch (SocketException e) {
            logger.error(e, "Problem in listing network interfaces.");
            throw Throwables.propagate(e);
         }
      }
   }
}
