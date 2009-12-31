/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.rimuhosting.miro;

import com.google.common.base.Predicate;
import org.jclouds.logging.Logger;
import org.jclouds.rimuhosting.miro.domain.Server;
import org.jclouds.rimuhosting.miro.domain.NewServerResponse;
import org.jclouds.ssh.SshClient.Factory;

import javax.annotation.Resource;
import javax.inject.Inject;
import java.net.InetSocketAddress;

/**
 * @author Ivan Meredith
 */
public class RimuHostingComputeClient {
   @Resource
   protected Logger logger = Logger.NULL;

   private final Predicate<InetSocketAddress> socketTester;
   private final RimuHostingClient rhClient;

   @Inject
   public RimuHostingComputeClient(RimuHostingClient rhClient, Factory sshFactory,
            Predicate<InetSocketAddress> socketTester) {
      this.rhClient = rhClient;
      this.sshFactory = sshFactory;
      this.socketTester = socketTester;
   }

   private final Factory sshFactory;


   public Long start(String name, String planId, String imageId) {
      logger.debug(">> instantiating RimuHosting VPS name(%s) plan(%s) image(%s)", name, planId, imageId);
      NewServerResponse serverRespone = rhClient.createServer(name, imageId, planId);
      logger.debug(">> VPS id(%d) started and running.", serverRespone.getServer().getId());
      return serverRespone.getServer().getId();
   }



   public void reboot(Long id) {
      Server server = rhClient.getServer(id);
      logger.debug(">> rebooting VPS(%d)", server.getId());
      rhClient.restartServer(id);
      logger.debug("<< on VPS(%d)", server.getId());
   }

   public void destroy(Long id) {
      Server server = rhClient.getServer(id);
      logger.debug(">> destroy VPS(%d)", server.getId());
      rhClient.destroyServer(id);
      logger.debug(">> destroyed VPS");
   }
}