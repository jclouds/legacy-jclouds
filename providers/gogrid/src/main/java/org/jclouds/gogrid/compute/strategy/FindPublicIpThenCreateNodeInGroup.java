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

package org.jclouds.gogrid.compute.strategy;

import static com.google.common.base.Preconditions.checkNotNull;

import java.security.SecureRandom;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.reference.ComputeServiceConstants.Timeouts;
import org.jclouds.compute.strategy.CreateNodeWithGroupEncodedIntoName;
import org.jclouds.gogrid.GoGridClient;
import org.jclouds.gogrid.domain.Ip;
import org.jclouds.gogrid.domain.IpType;
import org.jclouds.gogrid.domain.PowerCommand;
import org.jclouds.gogrid.domain.Server;
import org.jclouds.gogrid.options.GetIpListOptions;
import org.jclouds.gogrid.predicates.ServerLatestJobCompleted;
import org.jclouds.predicates.RetryablePredicate;

import com.google.common.base.Function;
import com.google.common.base.Throwables;
import com.google.common.collect.Iterables;

/**
 * @author Oleksiy Yarmula
 */
@Singleton
public class FindPublicIpThenCreateNodeInGroup implements CreateNodeWithGroupEncodedIntoName {
   private final GoGridClient client;
   private final Function<Hardware, String> sizeToRam;
   private final Function<Server, NodeMetadata> serverToNodeMetadata;
   private RetryablePredicate<Server> serverLatestJobCompleted;
   private RetryablePredicate<Server> serverLatestJobCompletedShort;

   @Inject
   protected FindPublicIpThenCreateNodeInGroup(GoGridClient client,
            Function<Server, NodeMetadata> serverToNodeMetadata, Function<Hardware, String> sizeToRam,
            Timeouts timeouts) {
      this.client = client;
      this.serverToNodeMetadata = serverToNodeMetadata;
      this.sizeToRam = sizeToRam;
      this.serverLatestJobCompleted = new RetryablePredicate<Server>(new ServerLatestJobCompleted(
               client.getJobServices()), timeouts.nodeRunning * 9l / 10l);
      this.serverLatestJobCompletedShort = new RetryablePredicate<Server>(
               new ServerLatestJobCompleted(client.getJobServices()),
               timeouts.nodeRunning * 1l / 10l);
   }

   @Override
   public NodeMetadata createNodeWithGroupEncodedIntoName(String group, String name, Template template) {
      Server addedServer = null;
      boolean notStarted = true;
      int numOfRetries = 20;
      // lock-free consumption of a shared resource: IP address pool
      while (notStarted) { // TODO: replace with Predicate-based thread
         // collision avoidance for
         // simplicity
         Set<Ip> availableIps = client.getIpServices().getIpList(
                  new GetIpListOptions().onlyUnassigned().onlyWithType(IpType.PUBLIC).inDatacenter(
                           template.getLocation().getId()));
         if (availableIps.size() == 0)
            throw new RuntimeException("No public IPs available on this identity.");
         int ipIndex = new SecureRandom().nextInt(availableIps.size());
         Ip availableIp = Iterables.get(availableIps, ipIndex);
         try {
            addedServer = addServer(name, template, availableIp);
            notStarted = false;
         } catch (Exception e) {
            if (--numOfRetries == 0)
               Throwables.propagate(e);
            notStarted = true;
         }
      }
      if (template.getOptions().shouldBlockUntilRunning()) {
         serverLatestJobCompleted.apply(addedServer);
         client.getServerServices().power(addedServer.getName(), PowerCommand.START);
         serverLatestJobCompletedShort.apply(addedServer);
         addedServer = Iterables.getOnlyElement(client.getServerServices().getServersByName(
                  addedServer.getName()));
      }
      return serverToNodeMetadata.apply(addedServer);
   }

   private Server addServer(String name, Template template, Ip availableIp) {
      Server addedServer;
      addedServer = client.getServerServices().addServer(name,
               checkNotNull(template.getImage().getProviderId()),
               sizeToRam.apply(template.getHardware()), availableIp.getIp());
      return addedServer;
   }
}