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
package org.jclouds.cloudstack.features;

import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.jclouds.Fallbacks.EmptySetOnNotFoundOr404;
import org.jclouds.cloudstack.domain.Cluster;
import org.jclouds.cloudstack.domain.Host;
import org.jclouds.cloudstack.filters.AuthenticationFilter;
import org.jclouds.cloudstack.options.AddClusterOptions;
import org.jclouds.cloudstack.options.AddHostOptions;
import org.jclouds.cloudstack.options.AddSecondaryStorageOptions;
import org.jclouds.cloudstack.options.DeleteHostOptions;
import org.jclouds.cloudstack.options.ListClustersOptions;
import org.jclouds.cloudstack.options.ListHostsOptions;
import org.jclouds.cloudstack.options.UpdateClusterOptions;
import org.jclouds.cloudstack.options.UpdateHostOptions;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.QueryParams;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.SelectJson;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * Provides asynchronous access to cloudstack via their REST API.
 * <p/>
 *
 * @see GlobalHostClient
 * @see <a href="http://download.cloud.com/releases/2.2.0/api_2.2.12/TOC_Global_Admin.html" />
 * @author Andrei Savu
 */
@RequestFilters(AuthenticationFilter.class)
@QueryParams(keys = "response", values = "json")
public interface GlobalHostAsyncClient {

   /**
    * @see GlobalHostClient#listHosts
    */
   @GET
   @QueryParams(keys = { "command", "listAll" }, values = { "listHosts", "true" })
   @SelectJson("host")
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(EmptySetOnNotFoundOr404.class)
   ListenableFuture<Set<Host>> listHosts(ListHostsOptions... options);

   /**
    * Adds a new host.
    *
    * @param zoneId the Zone ID for the host
    * @param url the host URL
    * @param hypervisor hypervisor type of the host
    * @param username the username for the host
    * @param password the password for the host
    * @param options optional arguments
    * @return the new host.
    */
   @GET
   @QueryParams(keys = "command", values = "addHost")
   @SelectJson("host")
   @Consumes(MediaType.APPLICATION_JSON)
   ListenableFuture<Host> addHost(@QueryParam("zoneid") String zoneId, @QueryParam("url") String url, @QueryParam("hypervisor") String hypervisor, @QueryParam("username") String username, @QueryParam("password") String password, AddHostOptions... options);

   /**
    * Updates a host.
    *
    * @param hostId the ID of the host to update
    * @param options optional arguments
    * @return the modified host.
    */
   @GET
   @QueryParams(keys = "command", values = "updateHost")
   @SelectJson("host")
   @Consumes(MediaType.APPLICATION_JSON)
   ListenableFuture<Host> updateHost(@QueryParam("id") String hostId, UpdateHostOptions... options);

   /**
    * Update password of a host on management server.
    *
    * @param hostId the host ID
    * @param username the username for the host
    * @param password the password for the host
    */
   @GET
   @QueryParams(keys = "command", values = "updateHostPassword")
   @Consumes(MediaType.APPLICATION_JSON)
   ListenableFuture<Void> updateHostPassword(@QueryParam("hostid") String hostId, @QueryParam("username") String username, @QueryParam("password") String password);

   /**
    * Deletes a host.
    *
    * @param hostId the host ID
    * @param options optional arguments
    */
   @GET
   @QueryParams(keys = "command", values = "deleteHost")
   @Consumes(MediaType.APPLICATION_JSON)
   ListenableFuture<Void> deleteHost(@QueryParam("id") String hostId, DeleteHostOptions... options);

   /**
    * Prepares a host for maintenance.
    *
    * @param hostId the host ID
    * @return a job reference number for tracking this asynchronous job.
    */
   @GET
   @QueryParams(keys = "command", values = "prepareHostForMaintenance")
   @SelectJson("jobid")
   @Consumes(MediaType.APPLICATION_JSON)
   ListenableFuture<String> prepareHostForMaintenance(@QueryParam("id") String hostId);

   /**
    * Cancels host maintenance.
    *
    * @param hostId the host ID
    * @return a job reference number for tracking this asynchronous job.
    */
   @GET
   @QueryParams(keys = "command", values = "cancelHostMaintenance")
   @SelectJson("jobid")
   @Consumes(MediaType.APPLICATION_JSON)
   ListenableFuture<String> cancelHostMaintenance(@QueryParam("id") String hostId);

   /**
    * Reconnects a host.
    *
    * @param hostId
    * @return a job reference number for tracking this asynchronous job.
    */
   @GET
   @QueryParams(keys = "command", values = "reconnectHost")
   @SelectJson("jobid")
   @Consumes(MediaType.APPLICATION_JSON)
   ListenableFuture<String> reconnectHost(@QueryParam("id") String hostId);

   /**
    * Adds secondary storage.
    *
    * @param url the URL for the secondary storage
    * @param options optional arguments
    * @return the host of the storage.
    */
   @GET
   @QueryParams(keys = "command", values = "addSecondaryStorage")
   @SelectJson("host")
   @Consumes(MediaType.APPLICATION_JSON)
   ListenableFuture<Host> addSecondaryStorage(@QueryParam("url") String url, AddSecondaryStorageOptions... options);

   /**
    * @see GlobalHostClient#listClusters
    */
   @GET
   @QueryParams(keys = { "command", "listAll" }, values = { "listClusters", "true" })
   @SelectJson("cluster")
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(EmptySetOnNotFoundOr404.class)
   ListenableFuture<Set<Cluster>> listClusters(ListClustersOptions... options);

   /**
    * Adds a new cluster.
    *
    * @param zoneId the Zone ID for the cluster
    * @param clusterName the cluster name
    * @param clusterType type of the cluster
    * @param hypervisor hypervisor type of the cluster
    * @param options optional arguments
    * @return the new cluster.
    */
   @GET
   @QueryParams(keys = "command", values = "addCluster")
   @SelectJson("cluster")
   @Consumes(MediaType.APPLICATION_JSON)
   ListenableFuture<Cluster> addCluster(@QueryParam("zoneid") String zoneId, @QueryParam("clustername") String clusterName, @QueryParam("clustertype") Host.ClusterType clusterType, @QueryParam("hypervisor") String hypervisor, AddClusterOptions... options);

   /**
    * Updates an existing cluster.
    *
    * @param clusterId the ID of the cluster
    * @param options optional arguments
    * @return the modified cluster
    */
   @GET
   @QueryParams(keys = "command", values = "updateCluster")
   @SelectJson("cluster")
   @Consumes(MediaType.APPLICATION_JSON)
   ListenableFuture<Cluster> updateCluster(@QueryParam("id") String clusterId, UpdateClusterOptions... options);

   /**
    * Update password of a cluster on management server.
    *
    * @param clusterId the cluster ID
    * @param username the username for the cluster
    * @param password the password for the cluster
    */
   @GET
   @QueryParams(keys = "command", values = "updateHostPassword")
   @SelectJson("cluster")
   @Consumes(MediaType.APPLICATION_JSON)
   ListenableFuture<Void> updateClusterPassword(@QueryParam("clusterid") String clusterId, @QueryParam("username") String username, @QueryParam("password") String password);

   /**
    * Deletes a cluster.
    *
    * @param clusterId the cluster ID
    */
   @GET
   @QueryParams(keys = "command", values = "deleteCluster")
   @Consumes(MediaType.APPLICATION_JSON)
   ListenableFuture<Void> deleteCluster(@QueryParam("id") String clusterId);

}
