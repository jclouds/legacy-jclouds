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
package org.jclouds.ec2.features;

import static org.jclouds.aws.reference.FormParameters.ACTION;

import java.util.Set;

import javax.inject.Named;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import org.jclouds.Fallbacks.EmptySetOnNotFoundOr404;
import org.jclouds.aws.filters.FormSigner;
import org.jclouds.ec2.binders.BindKeyNamesToIndexedFormParams;
import org.jclouds.ec2.domain.KeyPair;
import org.jclouds.ec2.xml.DescribeKeyPairsResponseHandler;
import org.jclouds.ec2.xml.KeyPairResponseHandler;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.location.functions.RegionToEndpointOrProviderIfNull;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.EndpointParam;
import org.jclouds.rest.annotations.Fallback;
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
@RequestFilters(FormSigner.class)
@VirtualHost
public interface KeyPairApi {

   /**
    * Creates a new 2048-bit RSA key pair with the specified name. The public key is stored by
    * Amazon EC2 and the private key is displayed on the console. The private key is returned as an
    * unencrypted PEM encoded PKCS#8 private key. If a key with the specified name already exists,
    * Amazon EC2 returns an error.
    * 
    * @param region
    *           Key pairs (to connect to instances) are Region-specific.
    * @param keyName
    *           A unique name for the key pair.
    * 
    * @see #runInstances
    * @see #describeKeyPairs
    * @see #deleteKeyPair
    * 
    * @see <a href=
    *      "http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/ApiReference-query-CreateKeyPair.html"
    *      />
    */
   @Named("CreateKeyPair")
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "CreateKeyPair")
   @XMLResponseParser(KeyPairResponseHandler.class)
   KeyPair createKeyPairInRegion(
            @EndpointParam(parser = RegionToEndpointOrProviderIfNull.class) @Nullable String region,
            @FormParam("KeyName") String keyName);

   /**
    * Returns information about key pairs available to you. If you specify key pairs, information
    * about those key pairs is returned. Otherwise, information for all registered key pairs is
    * returned.
    * 
    * @param region
    *           Key pairs (to connect to instances) are Region-specific.
    * @param keyPairNames
    *           Key pairs to describe.
    * 
    * @see #runInstances
    * @see #describeAvailabilityZones
    * @see <a href="http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/ApiReference-query-DescribeKeyPairs.html"
    *      />
    */
   @Named("DescribeKeyPairs")
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "DescribeKeyPairs")
   @XMLResponseParser(DescribeKeyPairsResponseHandler.class)
   @Fallback(EmptySetOnNotFoundOr404.class)
   Set<KeyPair> describeKeyPairsInRegion(
            @EndpointParam(parser = RegionToEndpointOrProviderIfNull.class) @Nullable String region,
            @BinderParam(BindKeyNamesToIndexedFormParams.class) String... keyPairNames);

   /**
    * Deletes the specified key pair, by removing the public key from Amazon EC2. You must own the
    * key pair
    * 
    * @param region
    *           Key pairs (to connect to instances) are Region-specific.
    * @param keyName
    *           Name of the key pair to delete
    * 
    * @see #describeKeyPairs
    * @see #createKeyPair
    * 
    * @see <a href=
    *      "http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/ApiReference-query-DeleteKeyPair.html"
    *      />
    */
   @Named("DeleteKeyPair")
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "DeleteKeyPair")
   void deleteKeyPairInRegion(
            @EndpointParam(parser = RegionToEndpointOrProviderIfNull.class) @Nullable String region,
            @FormParam("KeyName") String keyName);

}
