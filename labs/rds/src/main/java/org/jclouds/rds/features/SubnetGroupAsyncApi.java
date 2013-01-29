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
package org.jclouds.rds.features;

import static org.jclouds.aws.reference.FormParameters.ACTION;

import javax.inject.Named;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.Fallbacks.VoidOnNotFoundOr404;
import org.jclouds.aws.filters.FormSigner;
import org.jclouds.collect.IterableWithMarker;
import org.jclouds.collect.PagedIterable;
import org.jclouds.rds.domain.SubnetGroup;
import org.jclouds.rds.functions.SubnetGroupsToPagedIterable;
import org.jclouds.rds.options.ListSubnetGroupsOptions;
import org.jclouds.rds.xml.DescribeDBSubnetGroupsResultHandler;
import org.jclouds.rds.xml.SubnetGroupHandler;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.FormParams;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.Transform;
import org.jclouds.rest.annotations.VirtualHost;
import org.jclouds.rest.annotations.XMLResponseParser;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * Provides access to Amazon RDS via the Query API
 * <p/>
 * 
 * @see <a href="http://docs.amazonwebservices.com/AmazonRDS/latest/APIReference"
 *      >doc</a>
 * @see SubnetGroupApi
 * @author Adrian Cole
 */
@RequestFilters(FormSigner.class)
@VirtualHost
public interface SubnetGroupAsyncApi {
 
   /**
    * @see SubnetGroupApi#get()
    */
   @Named("DescribeDBSubnetGroups")
   @POST
   @Path("/")
   @XMLResponseParser(SubnetGroupHandler.class)
   @FormParams(keys = "Action", values = "DescribeDBSubnetGroups")
   @Fallback(NullOnNotFoundOr404.class)
   ListenableFuture<SubnetGroup> get(@FormParam("DBSubnetGroupName") String name);

   /**
    * @see SubnetGroupApi#list()
    */
   @Named("DescribeDBSubnetGroups")
   @POST
   @Path("/")
   @XMLResponseParser(DescribeDBSubnetGroupsResultHandler.class)
   @Transform(SubnetGroupsToPagedIterable.class)
   @FormParams(keys = "Action", values = "DescribeDBSubnetGroups")
   ListenableFuture<PagedIterable<SubnetGroup>> list();

   /**
    * @see SubnetGroupApi#list(ListSubnetGroupsOptions)
    */
   @Named("DescribeDBSubnetGroups")
   @POST
   @Path("/")
   @XMLResponseParser(DescribeDBSubnetGroupsResultHandler.class)
   @FormParams(keys = "Action", values = "DescribeDBSubnetGroups")
   ListenableFuture<IterableWithMarker<SubnetGroup>> list(ListSubnetGroupsOptions options);

   /**
    * @see SubnetGroupApi#delete()
    */
   @Named("DeleteDBSubnetGroup")
   @POST
   @Path("/")
   @Fallback(VoidOnNotFoundOr404.class)
   @FormParams(keys = ACTION, values = "DeleteDBSubnetGroup")
   ListenableFuture<Void> delete(@FormParam("DBSubnetGroupName") String name);
}
