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
package org.jclouds.aws.ec2.services;

import static org.jclouds.aws.ec2.reference.EC2Parameters.ACTION;
import static org.jclouds.aws.ec2.reference.EC2Parameters.VERSION;

import java.util.SortedSet;
import java.util.concurrent.Future;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import org.jclouds.aws.ec2.EC2;
import org.jclouds.aws.ec2.binders.BindKeyNameToIndexedFormParams;
import org.jclouds.aws.ec2.domain.KeyPair;
import org.jclouds.aws.ec2.filters.FormSigner;
import org.jclouds.aws.ec2.xml.DescribeKeyPairsResponseHandler;
import org.jclouds.aws.ec2.xml.KeyPairResponseHandler;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.Endpoint;
import org.jclouds.rest.annotations.FormParams;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.VirtualHost;
import org.jclouds.rest.annotations.XMLResponseParser;

/**
 * Provides access to EC2 via their REST API.
 * <p/>
 * 
 * @author Adrian Cole
 */
@Endpoint(EC2.class)
@RequestFilters(FormSigner.class)
@FormParams(keys = VERSION, values = "2009-11-30")
@VirtualHost
public interface KeyPairAsyncClient {

   /**
    * @see BaseEC2Client#createKeyPair
    */
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "CreateKeyPair")
   @XMLResponseParser(KeyPairResponseHandler.class)
   Future<KeyPair> createKeyPair(@FormParam("KeyName") String keyName);

   /**
    * @see BaseEC2Client#describeKeyPairs
    */
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "DescribeKeyPairs")
   @XMLResponseParser(DescribeKeyPairsResponseHandler.class)
   Future<? extends SortedSet<KeyPair>> describeKeyPairs(
            @BinderParam(BindKeyNameToIndexedFormParams.class) String... keyPairNames);

   /**
    * @see BaseEC2Client#deleteKeyPair
    */
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "DeleteKeyPair")
   Future<Void> deleteKeyPair(@FormParam("KeyName") String keyName);

}
