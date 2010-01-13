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
package org.jclouds.rimuhosting.miro.compute;

import org.jclouds.compute.domain.NodeIdentity;
import org.jclouds.rimuhosting.miro.RimuHostingClient;
import org.jclouds.rimuhosting.miro.domain.Server;

public class RimuHostingNodeIdentity implements NodeIdentity {
   org.jclouds.rimuhosting.miro.domain.Server rhServer;

   RimuHostingClient rhClient;

   public RimuHostingNodeIdentity(Server rhServer,
            RimuHostingClient rhClient) {
      this.rhServer = rhServer;
      this.rhClient = rhClient;
   }

   public String getId() {
      return rhServer.toString();
   }

   public Boolean destroy() {
      rhClient.destroyServer(rhServer.getId());
      return Boolean.TRUE;
   }

   @Override
   public String getName() {
      return rhServer.getName();
   }

   @Override
   public int compareTo(NodeIdentity o) {
      return (this == o) ? 0 : getId().compareTo(o.getId());
   }
}
