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

package org.jclouds.rimuhosting.miro;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.jclouds.concurrent.Timeout;
import org.jclouds.rimuhosting.miro.binder.CreateServerOptions;
import org.jclouds.rimuhosting.miro.domain.Image;
import org.jclouds.rimuhosting.miro.domain.MetaData;
import org.jclouds.rimuhosting.miro.domain.NewServerResponse;
import org.jclouds.rimuhosting.miro.domain.PricingPlan;
import org.jclouds.rimuhosting.miro.domain.Server;
import org.jclouds.rimuhosting.miro.domain.ServerInfo;

/**
 * Provides synchronous access to RimuHosting.
 * <p/>
 * 
 * @author Ivan Meredith
 * @see RimuHostingAsyncClient
 * @see <a href="TODO: insert URL of client documentation" />
 */
@Timeout(duration = 40, timeUnit = TimeUnit.MINUTES)
public interface RimuHostingClient {

   /**
    * This operation returns a list of images that can be used for server
    * creation. c
    * 
    * @see Image
    */
   Set<? extends Image> getImageList();

   /**
    * Returns a list of servers that belong to this identity.
    * 
    * @return An empty set if there are no servers.
    * @see Server
    */
   Set<? extends Server> getServerList();

   /**
    * Returns a list of pricing plans that can be used for server creation.
    * 
    * @see PricingPlan
    */
   Set<? extends PricingPlan> getPricingPlanList();

   /**
    * This operation creates a node based on its name, imageId and planId.
    * 
    * A password can be specified with the option
    * {@link CreateServerOptions#withPassword(String) | withPassword()}
    * 
    * Key-Value @{link {@link MetaData | metadata} can be included with the
    * option {@link CreateServerOptions#withMetaData(List) | withMetaData()}
    * 
    * @see CreateServerOptions
    * 
    *      TODO: add more CreateServerOptions
    */
   NewServerResponse createServer(String name, String imageId, String planId, CreateServerOptions... options);

   /**
    * Gets a server based on its id.
    * 
    * @return null if server id is invalid.
    * @see Server
    */
   Server getServer(Long id);

   /**
    * Restarts a server.
    * 
    * @return State of the server.
    */
   ServerInfo restartServer(Long id);

   /**
    * Destroys a server. This an async operation.
    * 
    * @return A list of messages that have something to do with the shutdown.
    *         Can ignore safely.
    */
   List<String> destroyServer(Long id);
}
