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
package org.jclouds.epc;

import static org.jclouds.Constants.PROPERTY_ISO3166_CODES;
import static org.jclouds.location.reference.LocationConstants.ENDPOINT;
import static org.jclouds.location.reference.LocationConstants.ISO3166_CODES;
import static org.jclouds.location.reference.LocationConstants.PROPERTY_REGION;
import static org.jclouds.location.reference.LocationConstants.PROPERTY_REGIONS;

import java.net.URI;
import java.util.Properties;

import org.jclouds.providers.ProviderMetadata;
import org.jclouds.providers.internal.BaseProviderMetadata;
import org.jclouds.s3.S3AsyncClient;
import org.jclouds.s3.S3Client;
import org.jclouds.s3.blobstore.S3BlobStoreContext;
import org.jclouds.walrus.WalrusApiMetadata;

/**
 * Implementation of {@link org.jclouds.providers.ProviderMetadata} for Eucalyptus Partner Cloud S3.
 * 
 * @author Adrian Cole
 */
public class EucalyptusPartnerCloudS3ProviderMetadata extends BaseProviderMetadata<S3Client, S3AsyncClient, S3BlobStoreContext<S3Client, S3AsyncClient>, WalrusApiMetadata> {
   
   public static Builder builder() {
      return new Builder();
   }

   @Override
   public Builder toBuilder() {
      return builder().fromProviderMetadata(this);
   }
   
   public EucalyptusPartnerCloudS3ProviderMetadata() {
      super(builder());
   }

   public EucalyptusPartnerCloudS3ProviderMetadata(Builder builder) {
      super(builder);
   }

   protected static Properties defaultProperties() {
      Properties properties = new Properties();
      properties.setProperty(PROPERTY_REGIONS, "Walrus");
      properties.setProperty(PROPERTY_ISO3166_CODES, "US-CA");
      properties.setProperty(PROPERTY_REGION + ".Walrus." + ISO3166_CODES, "US-CA");
      properties.setProperty(PROPERTY_REGION + "." + "Walrus" + "." + ENDPOINT, "http://partnercloud.eucalyptus.com:8773/services/Walrus");
      return properties;
   }
   
   public static class Builder extends BaseProviderMetadata.Builder<S3Client, S3AsyncClient, S3BlobStoreContext<S3Client, S3AsyncClient>, WalrusApiMetadata> {

      protected Builder(){
         id("eucalyptus-partnercloud-s3")
         .name("Eucalyptus Partner Cloud (S3)")
         .apiMetadata(new WalrusApiMetadata())
         .homepage(URI.create("http://www.eucalyptus.com/partners"))
         .console(URI.create("https://partnercloud.eucalyptus.com:8443"))
         .linkedServices("eucalyptus-partnercloud-ec2", "eucalyptus-partnercloud-s3")
         .iso3166Codes("US-CA")
         .endpoint("http://partnercloud.eucalyptus.com:8773/services/Walrus")
         .defaultProperties(EucalyptusPartnerCloudS3ProviderMetadata.defaultProperties());
      }

      @Override
      public EucalyptusPartnerCloudS3ProviderMetadata build() {
         return new EucalyptusPartnerCloudS3ProviderMetadata(this);
      }
      
      @Override
      public Builder fromProviderMetadata(
            ProviderMetadata<S3Client, S3AsyncClient, S3BlobStoreContext<S3Client, S3AsyncClient>, WalrusApiMetadata> in) {
         super.fromProviderMetadata(in);
         return this;
      }
   }
}