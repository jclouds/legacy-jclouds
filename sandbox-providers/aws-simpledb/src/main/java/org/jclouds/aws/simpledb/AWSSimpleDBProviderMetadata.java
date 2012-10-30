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
package org.jclouds.aws.simpledb;

import java.net.URI;
import java.util.Set;

import org.jclouds.providers.BaseProviderMetadata;
import org.jclouds.providers.ProviderMetadata;

import com.google.common.collect.ImmutableSet;

/**
 * Implementation of @ link org.jclouds.types.ProviderMetadata} for Amazon's SimpleDB provider.
 * 
 * @author Adrian Cole
 */
public class AWSSimpleDBProviderMetadata extends BaseProviderMetadata {

   /**
    * {@inheritDoc}
    */
   @Override
   public String getId() {
      return "aws-simpledb";
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String getType() {
      return ProviderMetadata.TABLE_TYPE;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String getName() {
      return "Amazon SimpleDB";
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String getIdentityName() {
      return "Access Key ID";
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String getCredentialName() {
      return "Secret Access Key";
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public URI getHomepage() {
      return URI.create("http://aws.amazon.com/simpledb");
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public URI getConsole() {
      return null;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public URI getApiDocumentation() {
      return URI.create("http://docs.amazonwebservices.com/AmazonSimpleDB/latest/DeveloperGuide");
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Set<String> getLinkedServices() {
      return ImmutableSet.of("aws-s3", "aws-ec2", "aws-elb", "aws-simpledb");
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Set<String> getIso3166Codes() {
      return ImmutableSet.of("US-VA", "US-CA", "US-OR", "BR-SP", "IE", "SG", "JP-13");
   }
}
