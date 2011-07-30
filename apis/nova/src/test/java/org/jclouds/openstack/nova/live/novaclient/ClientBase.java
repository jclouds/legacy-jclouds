/**
 *
 * Copyright (C) 2011 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.openstack.nova.live.novaclient;

import static org.jclouds.openstack.nova.live.PropertyHelper.setupKeyPair;
import static org.jclouds.openstack.nova.live.PropertyHelper.setupOverrides;
import static org.jclouds.openstack.nova.live.PropertyHelper.setupProperties;
import static org.jclouds.openstack.nova.options.CreateServerOptions.Builder.withFile;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.jclouds.logging.slf4j.config.SLF4JLoggingModule;
import org.jclouds.net.IPSocket;
import org.jclouds.openstack.nova.NovaClient;
import org.jclouds.openstack.nova.domain.Image;
import org.jclouds.openstack.nova.domain.Server;
import org.jclouds.predicates.RetryablePredicate;
import org.jclouds.predicates.SocketOpen;
import org.jclouds.rest.RestContextFactory;
import org.jclouds.ssh.SshClient;
import org.jclouds.sshj.config.SshjSshClientModule;
import org.testng.annotations.BeforeTest;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Injector;
import com.google.inject.Module;

/**
 * @author Victor Galkin
 */
public class ClientBase {
   protected int testImageId;
   protected NovaClient client;
   protected SshClient.Factory sshFactory;
   @SuppressWarnings("unused")
   private Predicate<IPSocket> socketTester;
   protected String provider = "nova";
   protected String serverPrefix = System.getProperty("user.name") + ".cs";
   protected Map<String, String> keyPair;
   Map<String, String> metadata = ImmutableMap.of("jclouds", "rackspace");

   @BeforeTest
   public void before() throws IOException {
      Properties properties = setupOverrides(setupProperties(this.getClass()));

      Injector injector = new RestContextFactory().createContextBuilder(provider,
            ImmutableSet.<Module>of(new SLF4JLoggingModule(), new SshjSshClientModule()), properties)
            .buildInjector();

      client = injector.getInstance(NovaClient.class);

      sshFactory = injector.getInstance(SshClient.Factory.class);
      SocketOpen socketOpen = injector.getInstance(SocketOpen.class);
      socketTester = new RetryablePredicate<IPSocket>(socketOpen, 120, 1, TimeUnit.SECONDS);
      injector.injectMembers(socketOpen); // add logger

      keyPair = setupKeyPair(properties);

      testImageId = Integer.valueOf(properties.getProperty("test.nova.image.id"));
   }

   protected Server getDefaultServerImmediately() {
      String defaultName = serverPrefix + "default";
      for (Server server : client.listServers()) {
         if (server.getName().equals(defaultName))
            return server;
      }
      return createDefaultServer(defaultName);
   }

   protected Server createDefaultServer(String serverName) {
      String imageRef = client.getImage(testImageId).getURI().toASCIIString();
      String flavorRef = client.getFlavor(1).getURI().toASCIIString();

      return client.createServer(serverName, imageRef, flavorRef, withFile("/etc/jclouds.txt",
            "rackspace".getBytes()).withMetadata(metadata));
   }

   protected Image getDefaultImageImmediately(Server server) {
      String defaultName = "hoofie";
      for (Image image : client.listImages()) {
         if (image.getName() != null)
            if (image.getName().equals(defaultName))
               return image;
      }
      return createDefaultImage("hoofie", server);
   }

   private Image createDefaultImage(String name, Server server) {
      return client.createImageFromServer("hoofie", server.getId());
   }

   protected void waitServerDeleted(int serverId) throws InterruptedException {
      while (null != client.getServer(serverId)) {
         System.out.println("Await deleted server" + serverId);
         Thread.sleep(1000);
      }
   }
}
