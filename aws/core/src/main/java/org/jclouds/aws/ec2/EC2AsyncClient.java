/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
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
 * ====================================================================
 */
package org.jclouds.aws.ec2;

import static org.jclouds.aws.ec2.reference.EC2Parameters.ACTION;
import static org.jclouds.aws.ec2.reference.EC2Parameters.VERSION;

import java.net.InetAddress;
import java.util.SortedSet;
import java.util.concurrent.Future;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import org.jclouds.aws.ec2.binders.BindGroupNameToIndexedFormParams;
import org.jclouds.aws.ec2.binders.BindInetAddressesToIndexedFormParams;
import org.jclouds.aws.ec2.binders.BindInstanceIdsToIndexedFormParams;
import org.jclouds.aws.ec2.binders.BindKeyNameToIndexedFormParams;
import org.jclouds.aws.ec2.binders.BindUserIdGroupPairToSourceSecurityGroupFormParams;
import org.jclouds.aws.ec2.domain.Image;
import org.jclouds.aws.ec2.domain.ImageAttribute;
import org.jclouds.aws.ec2.domain.IpProtocol;
import org.jclouds.aws.ec2.domain.KeyPair;
import org.jclouds.aws.ec2.domain.PublicIpInstanceIdPair;
import org.jclouds.aws.ec2.domain.Reservation;
import org.jclouds.aws.ec2.domain.SecurityGroup;
import org.jclouds.aws.ec2.domain.TerminatedInstance;
import org.jclouds.aws.ec2.domain.UserIdGroupPair;
import org.jclouds.aws.ec2.filters.FormSigner;
import org.jclouds.aws.ec2.functions.ReturnVoidOnGroupNotFound;
import org.jclouds.aws.ec2.options.DescribeImagesOptions;
import org.jclouds.aws.ec2.options.RunInstancesOptions;
import org.jclouds.aws.ec2.xml.AllocateAddressResponseHandler;
import org.jclouds.aws.ec2.xml.DescribeAddressesResponseHandler;
import org.jclouds.aws.ec2.xml.DescribeImagesResponseHandler;
import org.jclouds.aws.ec2.xml.DescribeInstancesResponseHandler;
import org.jclouds.aws.ec2.xml.DescribeKeyPairsResponseHandler;
import org.jclouds.aws.ec2.xml.DescribeSecurityGroupsResponseHandler;
import org.jclouds.aws.ec2.xml.KeyPairResponseHandler;
import org.jclouds.aws.ec2.xml.RunInstancesResponseHandler;
import org.jclouds.aws.ec2.xml.TerminateInstancesResponseHandler;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.Endpoint;
import org.jclouds.rest.annotations.ExceptionParser;
import org.jclouds.rest.annotations.FormParams;
import org.jclouds.rest.annotations.ParamParser;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.VirtualHost;
import org.jclouds.rest.annotations.XMLResponseParser;
import org.jclouds.rest.functions.InetAddressToHostAddress;

/**
 * Provides access to EC2 via their REST API.
 * <p/>
 * 
 * @author Adrian Cole
 */
@Endpoint(EC2.class)
@RequestFilters(FormSigner.class)
@FormParams(keys = VERSION, values = "2009-08-15")
@VirtualHost
public interface EC2AsyncClient {

   /**
    * @see EC2Client#describeImages
    */
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "DescribeImages")
   @XMLResponseParser(DescribeImagesResponseHandler.class)
   Future<? extends SortedSet<Image>> describeImages(DescribeImagesOptions... options);

   /**
    * Returns information about an attribute of an AMI. Only one attribute can be specified per
    * call.
    * 
    * @param imageId
    *           The ID of the AMI for which an attribute will be described
    * @param attribute
    *           the attribute to describe
    * @see #describeImages
    * @see #modifyImageAttribute
    * @see #resetImageAttribute
    * @see <a href="http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/ApiReference-query-DescribeImageAttribute.html"
    *      />
    * @see DescribeImagesOptions
    */
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "DescribeImageAttribute")
   Future<String> describeImageAttribute(@FormParam("ImageId") String imageId,
            @FormParam("Attribute") ImageAttribute attribute);

   /**
    * Acquires an elastic IP address for use with your account.
    * 
    * @see #describeAddresses
    * @see #releaseAddress
    * @see #associateAddress
    * @see #disassociateAddress
    * @see <a href="http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/ApiReference-query-AllocateAddress.html"
    */
   @POST
   @Path("/")
   @XMLResponseParser(AllocateAddressResponseHandler.class)
   @FormParams(keys = ACTION, values = "AllocateAddress")
   Future<InetAddress> allocateAddress();

   /**
    * @see EC2Client#associateAddress
    */
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "AssociateAddress")
   Future<Void> associateAddress(
            @FormParam("PublicIp") @ParamParser(InetAddressToHostAddress.class) InetAddress publicIp,
            @FormParam("InstanceId") String instanceId);

   /**
    * @see EC2Client#disassociateAddress
    */
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "DisassociateAddress")
   Future<Void> disassociateAddress(
            @FormParam("PublicIp") @ParamParser(InetAddressToHostAddress.class) InetAddress publicIp);

   /**
    * @see EC2Client#releaseAddress
    */
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "ReleaseAddress")
   Future<Void> releaseAddress(
            @FormParam("PublicIp") @ParamParser(InetAddressToHostAddress.class) InetAddress publicIp);

   /**
    * @see EC2Client#describeAddresses
    */
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "DescribeAddresses")
   @XMLResponseParser(DescribeAddressesResponseHandler.class)
   Future<? extends SortedSet<PublicIpInstanceIdPair>> describeAddresses(
            @BinderParam(BindInetAddressesToIndexedFormParams.class) InetAddress... publicIps);

   /**
    * @see EC2Client#describeInstances
    */
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "DescribeInstances")
   @XMLResponseParser(DescribeInstancesResponseHandler.class)
   Future<? extends SortedSet<Reservation>> describeInstances(
            @BinderParam(BindInstanceIdsToIndexedFormParams.class) String... instanceIds);

   /**
    * @see EC2Client#runInstances
    */
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "RunInstances")
   @XMLResponseParser(RunInstancesResponseHandler.class)
   Future<Reservation> runInstances(@FormParam("ImageId") String imageId,
            @FormParam("MinCount") int minCount, @FormParam("MaxCount") int maxCount,
            RunInstancesOptions... options);

   /**
    * @see EC2Client#terminateInstances
    */
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "TerminateInstances")
   @XMLResponseParser(TerminateInstancesResponseHandler.class)
   Future<? extends SortedSet<TerminatedInstance>> terminateInstances(
            @FormParam("InstanceId.0") String instanceId,
            @BinderParam(BindInstanceIdsToIndexedFormParams.class) String... instanceIds);

   /**
    * @see EC2Client#createKeyPair
    */
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "CreateKeyPair")
   @XMLResponseParser(KeyPairResponseHandler.class)
   Future<KeyPair> createKeyPair(@FormParam("KeyName") String keyName);

   /**
    * @see EC2Client#describeKeyPairs
    */
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "DescribeKeyPairs")
   @XMLResponseParser(DescribeKeyPairsResponseHandler.class)
   Future<? extends SortedSet<KeyPair>> describeKeyPairs(
            @BinderParam(BindKeyNameToIndexedFormParams.class) String... keyPairNames);

   /**
    * @see EC2Client#deleteKeyPair
    */
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "DeleteKeyPair")
   Future<Void> deleteKeyPair(@FormParam("KeyName") String keyName);

   /**
    * @see EC2Client#createSecurityGroup
    */
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "CreateSecurityGroup")
   Future<Void> createSecurityGroup(@FormParam("GroupName") String name,
            @FormParam("GroupDescription") String description);

   /**
    * @see EC2Client#deleteSecurityGroup
    */
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "DeleteSecurityGroup")
   @ExceptionParser(ReturnVoidOnGroupNotFound.class)
   Future<Void> deleteSecurityGroup(@FormParam("GroupName") String name);

   /**
    * @see EC2Client#describeSecurityGroups
    */
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "DescribeSecurityGroups")
   @XMLResponseParser(DescribeSecurityGroupsResponseHandler.class)
   Future<? extends SortedSet<SecurityGroup>> describeSecurityGroups(
            @BinderParam(BindGroupNameToIndexedFormParams.class) String... securityGroupNames);

   /**
    * @see EC2Client#authorizeSecurityGroupIngress(String,UserIdGroupPair)
    */
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "AuthorizeSecurityGroupIngress")
   Future<Void> authorizeSecurityGroupIngress(
            @FormParam("GroupName") String groupName,
            @BinderParam(BindUserIdGroupPairToSourceSecurityGroupFormParams.class) UserIdGroupPair sourceSecurityGroup);

   /**
    * @see EC2Client#authorizeSecurityGroupIngress(String,IpProtocol,int,int,String)
    */
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "AuthorizeSecurityGroupIngress")
   Future<Void> authorizeSecurityGroupIngress(@FormParam("GroupName") String groupName,
            @FormParam("IpProtocol") IpProtocol ipProtocol, @FormParam("FromPort") int fromPort,
            @FormParam("ToPort") int toPort, @FormParam("CidrIp") String cidrIp);

   /**
    * @see EC2Client#revokeSecurityGroupIngress(String,UserIdGroupPair)
    */
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "RevokeSecurityGroupIngress")
   Future<Void> revokeSecurityGroupIngress(
            @FormParam("GroupName") String groupName,
            @BinderParam(BindUserIdGroupPairToSourceSecurityGroupFormParams.class) UserIdGroupPair sourceSecurityGroup);

   /**
    * @see EC2Client#revokeSecurityGroupIngress(String,IpProtocol,int,int,String)
    */
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "RevokeSecurityGroupIngress")
   Future<Void> revokeSecurityGroupIngress(@FormParam("GroupName") String groupName,
            @FormParam("IpProtocol") IpProtocol ipProtocol, @FormParam("FromPort") int fromPort,
            @FormParam("ToPort") int toPort, @FormParam("CidrIp") String cidrIp);
}
