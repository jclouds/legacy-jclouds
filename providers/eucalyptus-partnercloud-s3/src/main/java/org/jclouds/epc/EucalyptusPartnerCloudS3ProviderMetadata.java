/**
 *
 * Copyright (C) 2011 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.epc;

import com.google.common.collect.ImmutableSet;

import java.net.URI;
import java.util.Set;

import org.jclouds.providers.BaseProviderMetadata;

/**
 * Implementation of {@ link org.jclouds.types.ProviderMetadata} for Eucalpytus'
 * Partner Cloud S3 provider.
 * 
 * @author Jeremy Whitlock <jwhitlock@apache.org>
 */
public class EucalyptusPartnerCloudS3ProviderMetadata extends BaseProviderMetadata {

   /**
    * {@inheritDoc}
    */
   @Override
   public String getId() {
      return "eucalyptus-partnercloud-s3";
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String getType() {
      return BLOBSTORE_TYPE;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String getName() {
      return "Eucalyptus Partner Cloud (S3)";
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String getIdentityName() {
      return "Username";
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String getCredentialName() {
      return "Password";
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public URI getHomepage() {
      return URI.create("http://www.eucalyptus.com/partners");
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public URI getConsole() {
      return URI.create("https://partnercloud.eucalyptus.com:8443");
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public URI getApiDocumentation() {
      return URI.create("http://open.eucalyptus.com/wiki/IntroducingEucalyptus_v2.0");
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Set<String> getIso3166Codes() {
      return ImmutableSet.of("US-CA");
   }

}
