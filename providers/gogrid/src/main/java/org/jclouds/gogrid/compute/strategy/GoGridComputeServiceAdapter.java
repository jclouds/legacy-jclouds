/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.gogrid.compute.strategy;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.util.Predicates2.retry;

import java.security.SecureRandom;
import java.util.Set;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.compute.ComputeService;
import org.jclouds.compute.ComputeServiceAdapter;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.reference.ComputeServiceConstants.Timeouts;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.gogrid.GoGridClient;
import org.jclouds.gogrid.compute.suppliers.GoGridHardwareSupplier;
import org.jclouds.gogrid.domain.Ip;
import org.jclouds.gogrid.domain.IpType;
import org.jclouds.gogrid.domain.Option;
import org.jclouds.gogrid.domain.PowerCommand;
import org.jclouds.gogrid.domain.Server;
import org.jclouds.gogrid.domain.ServerImage;
import org.jclouds.gogrid.options.GetIpListOptions;
import org.jclouds.gogrid.predicates.ServerLatestJobCompleted;
import org.jclouds.logging.Logger;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Throwables;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.primitives.Longs;

/**
 * defines the connection between the {@link GoGridClient} implementation and the jclouds
 * {@link ComputeService}
 * 
 */
@Singleton
public class GoGridComputeServiceAdapter implements ComputeServiceAdapter<Server, Hardware, ServerImage, Option> {

   @Resource
   protected Logger logger = Logger.NULL;

   private final GoGridClient client;
   private final Function<Hardware, String> sizeToRam;
   private final Predicate<Server> serverLatestJobCompleted;
   private final Predicate<Server> serverLatestJobCompletedShort;

   @Inject
   protected GoGridComputeServiceAdapter(GoGridClient client, Function<Hardware, String> sizeToRam, Timeouts timeouts) {
      this.client = checkNotNull(client, "client");
      this.sizeToRam = checkNotNull(sizeToRam, "sizeToRam");
      this.serverLatestJobCompleted = retry(new ServerLatestJobCompleted(client.getJobServices()),
            timeouts.nodeRunning * 9l / 10l);
      this.serverLatestJobCompletedShort = retry(new ServerLatestJobCompleted(client.getJobServices()),
            timeouts.nodeRunning * 1l / 10l);
   }

   @Override
   public NodeAndInitialCredentials<Server> createNodeWithGroupEncodedIntoName(String group, String name,
            Template template) {
      Server addedServer = null;
      boolean notStarted = true;
      int numOfRetries = 20;
      GetIpListOptions unassignedIps = new GetIpListOptions().onlyUnassigned().inDatacenter(
               template.getLocation().getId()).onlyWithType(IpType.PUBLIC);
      // lock-free consumption of a shared resource: IP address pool
      while (notStarted) { // TODO: replace with Predicate-based thread
         // collision avoidance for simplicity
         Set<Ip> availableIps = client.getIpServices().getIpList(unassignedIps);
         if (availableIps.isEmpty())
            throw new RuntimeException("No IPs available on this identity.");
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
         addedServer = Iterables.getOnlyElement(client.getServerServices().getServersByName(addedServer.getName()));
      }
      LoginCredentials credentials = LoginCredentials.fromCredentials(client.getServerServices()
               .getServerCredentialsList().get(addedServer.getName()));
      return new NodeAndInitialCredentials<Server>(addedServer, addedServer.getId() + "", credentials);
   }

   private Server addServer(String name, Template template, Ip availableIp) {
      Server addedServer = client.getServerServices().addServer(name,
               checkNotNull(template.getImage().getProviderId()), sizeToRam.apply(template.getHardware()),
               availableIp.getIp());
      return addedServer;
   }

   @Override
   public Iterable<Hardware> listHardwareProfiles() {
      return GoGridHardwareSupplier.H_ALL;

   }

   @Override
   public Iterable<ServerImage> listImages() {
      return client.getImageServices().getImageList();
   }

   @Override
   public Iterable<Server> listNodes() {
      return client.getServerServices().getServerList();
   }

   @Override
   public Iterable<Server> listNodesByIds(final Iterable<String> ids) {
      Set<Long> idsAsLongs = FluentIterable.from(ids)
         .transform(toLong())
         .toSet();
                  
      return client.getServerServices().getServersById(Longs.toArray(idsAsLongs));
   }

   @Override
   public Iterable<Option> listLocations() {
      return client.getServerServices().getDatacenters();
   }

   @Override
   public Server getNode(String id) {
      return Iterables.getOnlyElement(client.getServerServices().getServersById(Long.valueOf(checkNotNull(id, "id"))),
               null);
   }

   @Override
   public ServerImage getImage(String id) {
      return Iterables.getOnlyElement(client.getImageServices().getImagesById(Long.valueOf(checkNotNull(id, "id"))),
               null);
   }
   
   @Override
   public void destroyNode(String id) {
      client.getServerServices().deleteById(Long.valueOf(id));
   }

   @Override
   public void rebootNode(String id) {
      executeCommandOnServer(PowerCommand.RESTART, id);
      Server server = Iterables.getOnlyElement(client.getServerServices().getServersById(Long.valueOf(id)));
      client.getServerServices().power(server.getName(), PowerCommand.START);
      serverLatestJobCompletedShort.apply(server);
   }

   private boolean executeCommandOnServer(PowerCommand command, String id) {
      Server server = Iterables.getOnlyElement(client.getServerServices().getServersById(Long.valueOf(id)));
      client.getServerServices().power(server.getName(), command);
      return serverLatestJobCompleted.apply(server);
   }

   @Override
   public void resumeNode(String id) {
      executeCommandOnServer(PowerCommand.START, id);
   }

   @Override
   public void suspendNode(String id) {
      executeCommandOnServer(PowerCommand.STOP, id);
   }

   private Function<String, Long> toLong() {
      return new Function<String, Long>() {

         @Override
         public Long apply(String id) {
            return Long.valueOf(checkNotNull(id, "id"));
         }
      };
   }
}
