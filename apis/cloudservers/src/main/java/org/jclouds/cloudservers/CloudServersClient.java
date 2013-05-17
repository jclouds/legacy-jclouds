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
package org.jclouds.cloudservers;

import java.io.Closeable;
import java.util.Set;
import javax.ws.rs.PathParam;

import org.jclouds.cloudservers.domain.Addresses;
import org.jclouds.cloudservers.domain.BackupSchedule;
import org.jclouds.cloudservers.domain.Flavor;
import org.jclouds.cloudservers.domain.Image;
import org.jclouds.cloudservers.domain.Limits;
import org.jclouds.cloudservers.domain.RebootType;
import org.jclouds.cloudservers.domain.Server;
import org.jclouds.cloudservers.domain.SharedIpGroup;
import org.jclouds.cloudservers.options.CreateServerOptions;
import org.jclouds.cloudservers.options.CreateSharedIpGroupOptions;
import org.jclouds.cloudservers.options.ListOptions;
import org.jclouds.cloudservers.options.RebuildServerOptions;

/**
 * Provides access to Cloud Servers via their REST API.
 * <p/>
 * All commands return a Future of the result from Cloud Servers. Any exceptions incurred during
 * processing will be backend in an {@link ExecutionException} as documented in {@link Future#get()}.
 * 
 * @see CloudServersAsyncClient
 * @see <a href="http://docs.rackspacecloud.com/servers/api/cs-devguide-latest.pdf" />
 * @author Adrian Cole
 */
public interface CloudServersClient extends Closeable {
   /**
    * All accounts, by default, have a preconfigured set of thresholds (or limits) to manage
    * capacity and prevent abuse of the system. The system recognizes two kinds of limits: rate
    * limits and absolute limits. Rate limits are thresholds that are reset after a certain amount
    * of time passes. Absolute limits are fixed.
    * 
    * @return limits on the account
    */
   Limits getLimits();

   /**
    * 
    * List all servers (IDs and names only)
    * 
    * This operation provides a list of servers associated with your identity. Servers that have
    * been deleted are not included in this list.
    * <p/>
    * in order to retrieve all details, pass the option {@link ListOptions#withDetails()
    * withDetails()}
    */
   Set<Server> listServers(ListOptions... options);

   /**
    * 
    * This operation returns details of the specified server.
    * 
    * @return null, if the server is not found
    * @see Server
    */
   Server getServer(@PathParam("id") int id);

   /**
    * 
    * This operation deletes a cloud server instance from the system.
    * <p/>
    * Note: When a server is deleted, all images created from that server are also removed.
    * 
    * @return false if the server is not found
    * @see Server
    */
   boolean deleteServer(@PathParam("id") int id);

   /**
    * The reboot function allows for either a soft or hard reboot of a server.
    * <p/>
    * Status Transition:
    * <p/>
    * ACTIVE - REBOOT - ACTIVE (soft reboot)
    * <p/>
    * ACTIVE - HARD_REBOOT - ACTIVE (hard reboot)
    * 
    * @param rebootType
    *           With a soft reboot, the operating system is signaled to restart, which allows for a
    *           graceful shutdown of all processes. A hard reboot is the equivalent of power cycling
    *           the server.
    */
   void rebootServer(int id, RebootType rebootType);

   /**
    * The resize function converts an existing server to a different flavor, in essence, scaling the
    * server up or down. The original server is saved for a period of time to allow rollback if
    * there is a problem. All resizes should be tested and explicitly confirmed, at which time the
    * original server is removed. All resizes are automatically confirmed after 24 hours if they are
    * not confirmed or reverted.
    * <p/>
    * Status Transition:
    * <p/>
    * ACTIVE - QUEUE_RESIZE - PREP_RESIZE - VERIFY_RESIZE
    * <p/>
    * ACTIVE - QUEUE_RESIZE - ACTIVE (on error)
    */
   void resizeServer(int id, int flavorId);

   /**
    * The resize function converts an existing server to a different flavor, in essence, scaling the
    * server up or down. The original server is saved for a period of time to allow rollback if
    * there is a problem. All resizes should be tested and explicitly confirmed, at which time the
    * original server is removed. All resizes are automatically confirmed after 24 hours if they are
    * not confirmed or reverted.
    * <p/>
    * Status Transition:
    * <p/>
    * VERIFY_RESIZE - ACTIVE
    */
   void confirmResizeServer(int id);

   /**
    * The resize function converts an existing server to a different flavor, in essence, scaling the
    * server up or down. The original server is saved for a period of time to allow rollback if
    * there is a problem. All resizes should be tested and explicitly reverted, at which time the
    * original server is removed. All resizes are automatically reverted after 24 hours if they are
    * not reverted or reverted.
    * <p/>
    * Status Transition:
    * <p/>
    * VERIFY_RESIZE - ACTIVE
    */
   void revertResizeServer(int id);

   /**
    * This operation asynchronously provisions a new server. The progress of this operation depends
    * on several factors including location of the requested image, network i/o, host load, and the
    * selected flavor. The progress of the request can be checked by performing a GET on /server/id,
    * which will return a progress attribute (0-100% completion). A password will be randomly
    * generated for you and returned in the response object. For security reasons, it will not be
    * returned in subsequent GET calls against a given server ID.
    * 
    * @param options
    *           - used to specify extra files, metadata, or ip parameters during server creation.
    */
   Server createServer(String name, int imageId, int flavorId, CreateServerOptions... options);

   /**
    * The rebuild function removes all data on the server and replaces it with the specified image.
    * Server ID and IP addresses remain the same.
    * <p/>
    * Status Transition:
    * <p/>
    * ACTIVE - REBUILD - ACTIVE
    * <p/>
    * ACTIVE - REBUILD - ERROR (on error)
    * <p/>
    * 
    * @param options
    *           - imageId is an optional argument. If it is not specified, the server is rebuilt
    *           with the original imageId.
    */
   void rebuildServer(int id, RebuildServerOptions... options);

   /**
    * /** This operation allows you share an IP address to the specified server
    * <p/>
    * This operation shares an IP from an existing server in the specified shared IP group to
    * another specified server in the same group. The operation modifies cloud network restrictions
    * to allow IP traffic for the given IP to/from the server specified.
    * 
    * <p/>
    * Status Transition: ACTIVE - SHARE_IP - ACTIVE (if configureServer is true) ACTIVE -
    * SHARE_IP_NO_CONFIG - ACTIVE
    * 
    * @param configureServer
    *           <p/>
    *           if set to true, the server is configured with the new address, though the address is
    *           not enabled. Note that configuring the server does require a reboot.
    *           <p/>
    *           If set to false, does not bind the IP to the server itself. A heartbeat facility
    *           (e.g. keepalived) can then be used within the servers to perform health checks and
    *           manage IP failover.
    */
   void shareIp(String addressToShare, int serverToTosignBindressTo, int sharedIpGroup, boolean configureServer);

   /**
    * This operation removes a shared IP address from the specified server.
    * <p/>
    * Status Transition: ACTIVE - DELETE_IP - ACTIVE
    * 
    * @param addressToShare
    * @param serverToTosignBindressTo
    * @return
    */
   void unshareIp(String addressToShare, int serverToTosignBindressTo);

   /**
    * This operation allows you to change the administrative password.
    * <p/>
    * Status Transition: ACTIVE - PASSWORD - ACTIVE
    * 
    */
   void changeAdminPass(int id, String adminPass);

   /**
    * This operation allows you to update the name of the server. This operation changes the name of
    * the server in the Cloud Servers system and does not change the server host name itself.
    * <p/>
    * Status Transition: ACTIVE - PASSWORD - ACTIVE
    * 
    */
   void renameServer(int id, String newName);

   /**
    * 
    * List available flavors (IDs and names only)
    * 
    * in order to retrieve all details, pass the option {@link ListOptions#withDetails()
    * withDetails()}
    */
   Set<Flavor> listFlavors(ListOptions... options);

   /**
    * 
    * This operation returns details of the specified flavor.
    * 
    * @return null, if the flavor is not found
    * @see Flavor
    */
   Flavor getFlavor(int id);

   /**
    * 
    * List available images (IDs and names only)
    * 
    * in order to retrieve all details, pass the option {@link ListOptions#withDetails()
    * withDetails()}
    */
   Set<Image> listImages(ListOptions... options);

   /**
    * 
    * This operation returns details of the specified image.
    * 
    * @return null, if the image is not found
    * 
    * @see Image
    */
   Image getImage(int id);

   /**
    * 
    * This operation deletes an image from the system.
    * <p/>
    * Note: Images are immediately removed. Currently, there are no state transitions to track the
    * delete operation.
    * 
    * @return false if the image is not found
    * @see Image
    */
   boolean deleteImage(int id);

   /**
    * 
    * This operation creates a new image for the given server ID. Once complete, a new image will be
    * available that can be used to rebuild or create servers. Specifying the same image name as an
    * existing custom image replaces the image. The image creation status can be queried by
    * performing a GET on /images/id and examining the status and progress attributes.
    * 
    * Status Transition:
    * <p/>
    * QUEUED - PREPARING - SAVING - ACTIVE
    * <p/>
    * QUEUED - PREPARING - SAVING - FAILED (on error)
    * <p/>
    * Note: At present, image creation is an asynchronous operation, so coordinating the creation
    * with data quiescence, etc. is currently not possible.
    * 
    * @throws ResourceNotFoundException
    *            if the server is not found
    * @see Image
    */
   Image createImageFromServer(String imageName, int serverId);

   /**
    * 
    * List shared IP groups (IDs and names only)
    * 
    * in order to retrieve all details, pass the option {@link ListOptions#withDetails()
    * withDetails()}
    */
   Set<SharedIpGroup> listSharedIpGroups(ListOptions... options);

   /**
    * 
    * This operation returns details of the specified shared IP group.
    * 
    * @return null, if the shared ip group is not found
    * 
    * @see SharedIpGroup
    */
   SharedIpGroup getSharedIpGroup(int id);

   /**
    * This operation creates a new shared IP group. Please note, all responses to requests for
    * shared_ip_groups return an array of servers. However, on a create request, the shared IP group
    * can be created empty or can be initially populated with a single server. Use
    * {@link CreateSharedIpGroupOptions} to specify an server.
    */
   SharedIpGroup createSharedIpGroup(String name, CreateSharedIpGroupOptions... options);

   /**
    * This operation deletes the specified shared IP group. This operation will ONLY succeed if 1)
    * there are no active servers in the group (i.e. they have all been terminated) or 2) no servers
    * in the group are actively sharing IPs.
    * 
    * @return false if the shared ip group is not found
    * @see SharedIpGroup
    */
   boolean deleteSharedIpGroup(int id);

   /**
    * List the backup schedule for the specified server
    * 
    * @throws ResourceNotFoundException
    *            , if the server doesn't exist
    */
   BackupSchedule getBackupSchedule(int serverId);

   /**
    * Delete backup schedule for the specified server.
    * <p/>
    * Web Hosting #119571 currently disables the schedule, not deletes it.
    * 
    * @return false if the schedule is not found
    */
   boolean deleteBackupSchedule(int serverId);

   /**
    * Enable/update the backup schedule for the specified server
    * 
    */
   void replaceBackupSchedule(int id, BackupSchedule backupSchedule);

   /**
    * List all server addresses
    * 
    * returns empty set if the server doesn't exist
    */
   Addresses getAddresses(int serverId);

   /**
    * List all public server addresses
    * 
    * returns empty set if the server doesn't exist
    */
   Set<String> listPublicAddresses(int serverId);

   /**
    * List all private server addresses
    * 
    * returns empty set if the server doesn't exist
    */
   Set<String> listPrivateAddresses(int serverId);

}
