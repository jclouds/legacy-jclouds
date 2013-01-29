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
import org.jclouds.rds.domain.SecurityGroup;
import org.jclouds.rds.functions.SecurityGroupsToPagedIterable;
import org.jclouds.rds.options.ListSecurityGroupsOptions;
import org.jclouds.rds.xml.DescribeDBSecurityGroupsResultHandler;
import org.jclouds.rds.xml.SecurityGroupHandler;
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
 * @see <a href="http://docs.amazonwebservices.com/AmazonRDS/latest/APIReference" >doc</a>
 * @see SecurityGroupApi
 * @author Adrian Cole
 */
@RequestFilters(FormSigner.class)
@VirtualHost
public interface SecurityGroupAsyncApi {
   /**
    * @see SecurityGroupApi#createWithNameAndDescription
    */
   @Named("CreateDBSecurityGroup")
   @POST
   @Path("/")
   @XMLResponseParser(SecurityGroupHandler.class)
   @FormParams(keys = ACTION, values = "CreateDBSecurityGroup")
   ListenableFuture<SecurityGroup> createWithNameAndDescription(@FormParam("DBSecurityGroupName") String name,
            @FormParam("DBSecurityGroupDescription") String description);

   /**
    * @see SecurityGroupApi#createInVPCWithNameAndDescription
    */
   @Named("CreateDBSecurityGroup")
   @POST
   @Path("/")
   @XMLResponseParser(SecurityGroupHandler.class)
   @FormParams(keys = ACTION, values = "CreateDBSecurityGroup")
   ListenableFuture<SecurityGroup> createInVPCWithNameAndDescription(@FormParam("EC2VpcId") String vpcId,
            @FormParam("DBSecurityGroupName") String name, @FormParam("DBSecurityGroupDescription") String description);

   /**
    * @see SecurityGroupApi#get()
    */
   @Named("DescribeDBSecurityGroups")
   @POST
   @Path("/")
   @XMLResponseParser(SecurityGroupHandler.class)
   @FormParams(keys = "Action", values = "DescribeDBSecurityGroups")
   @Fallback(NullOnNotFoundOr404.class)
   ListenableFuture<SecurityGroup> get(@FormParam("DBSecurityGroupName") String name);

   /**
    * @see SecurityGroupApi#list()
    */
   @Named("DescribeDBSecurityGroups")
   @POST
   @Path("/")
   @XMLResponseParser(DescribeDBSecurityGroupsResultHandler.class)
   @Transform(SecurityGroupsToPagedIterable.class)
   @FormParams(keys = "Action", values = "DescribeDBSecurityGroups")
   ListenableFuture<PagedIterable<SecurityGroup>> list();

   /**
    * @see SecurityGroupApi#list(ListSecurityGroupsOptions)
    */
   @Named("DescribeDBSecurityGroups")
   @POST
   @Path("/")
   @XMLResponseParser(DescribeDBSecurityGroupsResultHandler.class)
   @FormParams(keys = "Action", values = "DescribeDBSecurityGroups")
   ListenableFuture<IterableWithMarker<SecurityGroup>> list(ListSecurityGroupsOptions options);

   /**
    * @see SecurityGroupApi#authorizeIngressToIPRange
    */
   @Named("AuthorizeDBSecurityGroupIngress")
   @POST
   @Path("/")
   @XMLResponseParser(SecurityGroupHandler.class)
   @FormParams(keys = ACTION, values = "AuthorizeDBSecurityGroupIngress")
   ListenableFuture<SecurityGroup> authorizeIngressToIPRange(@FormParam("DBSecurityGroupName") String name,
            @FormParam("CIDRIP") String CIDR);

   /**
    * @see SecurityGroupApi#authorizeIngressToEC2SecurityGroupOfOwner
    */
   @Named("AuthorizeDBSecurityGroupIngress")
   @POST
   @Path("/")
   @XMLResponseParser(SecurityGroupHandler.class)
   @FormParams(keys = ACTION, values = "AuthorizeDBSecurityGroupIngress")
   ListenableFuture<SecurityGroup> authorizeIngressToEC2SecurityGroupOfOwner(
            @FormParam("DBSecurityGroupName") String name,
            @FormParam("EC2SecurityGroupName") String ec2SecurityGroupName,
            @FormParam("EC2SecurityGroupOwnerId") String ec2SecurityGroupOwnerId);

   /**
    * @see SecurityGroupApi#authorizeIngressToVPCSecurityGroup
    */
   @Named("AuthorizeDBSecurityGroupIngress")
   @POST
   @Path("/")
   @XMLResponseParser(SecurityGroupHandler.class)
   @FormParams(keys = ACTION, values = "AuthorizeDBSecurityGroupIngress")
   ListenableFuture<SecurityGroup> authorizeIngressToVPCSecurityGroup(@FormParam("DBSecurityGroupName") String name,
            @FormParam("EC2SecurityGroupId") String vpcSecurityGroupId);


   /**
    * @see SecurityGroupApi#revokeIngressFromIPRange
    */
   @Named("RevokeDBSecurityGroupIngress")
   @POST
   @Path("/")
   @XMLResponseParser(SecurityGroupHandler.class)
   @FormParams(keys = ACTION, values = "RevokeDBSecurityGroupIngress")
   ListenableFuture<SecurityGroup> revokeIngressFromIPRange(@FormParam("DBSecurityGroupName") String name,
            @FormParam("CIDRIP") String CIDR);

   /**
    * @see SecurityGroupApi#revokeIngressFromEC2SecurityGroupOfOwner
    */
   @Named("RevokeDBSecurityGroupIngress")
   @POST
   @Path("/")
   @XMLResponseParser(SecurityGroupHandler.class)
   @FormParams(keys = ACTION, values = "RevokeDBSecurityGroupIngress")
   ListenableFuture<SecurityGroup> revokeIngressFromEC2SecurityGroupOfOwner(
            @FormParam("DBSecurityGroupName") String name,
            @FormParam("EC2SecurityGroupName") String ec2SecurityGroupName,
            @FormParam("EC2SecurityGroupOwnerId") String ec2SecurityGroupOwnerId);

   /**
    * @see SecurityGroupApi#revokeIngressFromVPCSecurityGroup
    */
   @Named("RevokeDBSecurityGroupIngress")
   @POST
   @Path("/")
   @XMLResponseParser(SecurityGroupHandler.class)
   @FormParams(keys = ACTION, values = "RevokeDBSecurityGroupIngress")
   ListenableFuture<SecurityGroup> revokeIngressFromVPCSecurityGroup(@FormParam("DBSecurityGroupName") String name,
            @FormParam("EC2SecurityGroupId") String vpcSecurityGroupId);
   
   /**
    * @see SecurityGroupApi#delete()
    */
   @Named("DeleteDBSecurityGroup")
   @POST
   @Path("/")
   @Fallback(VoidOnNotFoundOr404.class)
   @FormParams(keys = ACTION, values = "DeleteDBSecurityGroup")
   ListenableFuture<Void> delete(@FormParam("DBSecurityGroupName") String name);
}
