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
package org.jclouds.openstack.nova.v1_1.features;

import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.jclouds.concurrent.Timeout;
import org.jclouds.openstack.domain.Resource;
import org.jclouds.openstack.nova.v1_1.NovaClient;
import org.jclouds.openstack.nova.v1_1.domain.RebootType;
import org.jclouds.openstack.nova.v1_1.domain.Server;
import org.jclouds.openstack.nova.v1_1.options.CreateServerOptions;
import org.jclouds.openstack.nova.v1_1.options.RebuildServerOptions;
import org.jclouds.rest.annotations.ExceptionParser;
import org.jclouds.rest.annotations.MapBinder;
import org.jclouds.rest.annotations.Payload;
import org.jclouds.rest.annotations.PayloadParam;
import org.jclouds.rest.annotations.Unwrap;
import org.jclouds.rest.functions.ReturnFalseOnNotFoundOr404;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * Provides synchronous access to Server.
 * <p/>
 * 
 * @see ServerAsyncClient
 * @see <a href="http://docs.openstack.org/api/openstack-compute/1.1/content/Servers-d1e2073.html"
 *      />
 * @author Adrian Cole
 */
@Timeout(duration = 30, timeUnit = TimeUnit.SECONDS)
public interface ServerClient {

   /**
    * List all servers (IDs, names, links)
    * 
    * @return all servers (IDs, names, links)
    */
   Set<Resource> listServers();

   /**
    * List all servers (all details)
    * 
    * @return all servers (all details)
    */
   Set<Server> listServersInDetail();

   /**
    * List details of the specified server
    * 
    * @param id
    *           id of the server
    * @return server or null if not found
    */
   Server getServer(String id);

   /**
    * @see NovaClient#createServer
    */
   Server createServer(String name, String imageRef,
                       String flavorRef, CreateServerOptions... options);

   /**
    * @see NovaClient#deleteServer
    */
   Boolean deleteServer(String id);

   /**
    * @see NovaClient#rebootServer
    */
   void rebootServer(String id, RebootType rebootType);

   /**
    * @see NovaClient#resizeServer
    */
   void resizeServer(String id, String flavorId);

   /**
    * @see NovaClient#confirmResizeServer
    */
   void confirmResizeServer(String id);

   /**
    * @see NovaClient#revertResizeServer
    */
   void revertResizeServer(String id);

   /**
    * @see NovaClient#rebuildServer
    */
   void rebuildServer(String id, RebuildServerOptions... options);

   /**
    * @see NovaClient#changeAdminPass
    */
   void changeAdminPass(String id, String adminPass);

   /**
    * @see NovaClient#renameServer
    */
   void renameServer(String id, String newName);

}
