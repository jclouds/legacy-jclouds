/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.aws.ec2.config;

import java.net.URI;
import java.util.Date;
import java.util.Map;

import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.aws.domain.Region;
import org.jclouds.aws.ec2.EC2;
import org.jclouds.aws.ec2.EC2AsyncClient;
import org.jclouds.aws.ec2.EC2Client;
import org.jclouds.aws.ec2.ELB;
import org.jclouds.aws.ec2.domain.AvailabilityZoneInfo;
import org.jclouds.aws.ec2.reference.EC2Constants;
import org.jclouds.aws.ec2.services.AMIAsyncClient;
import org.jclouds.aws.ec2.services.AMIClient;
import org.jclouds.aws.ec2.services.AvailabilityZoneAndRegionAsyncClient;
import org.jclouds.aws.ec2.services.AvailabilityZoneAndRegionClient;
import org.jclouds.aws.ec2.services.ElasticBlockStoreAsyncClient;
import org.jclouds.aws.ec2.services.ElasticBlockStoreClient;
import org.jclouds.aws.ec2.services.ElasticIPAddressAsyncClient;
import org.jclouds.aws.ec2.services.ElasticIPAddressClient;
import org.jclouds.aws.ec2.services.ElasticLoadBalancerAsyncClient;
import org.jclouds.aws.ec2.services.ElasticLoadBalancerClient;
import org.jclouds.aws.ec2.services.InstanceAsyncClient;
import org.jclouds.aws.ec2.services.InstanceClient;
import org.jclouds.aws.ec2.services.KeyPairAsyncClient;
import org.jclouds.aws.ec2.services.KeyPairClient;
import org.jclouds.aws.ec2.services.MonitoringAsyncClient;
import org.jclouds.aws.ec2.services.MonitoringClient;
import org.jclouds.aws.ec2.services.SecurityGroupAsyncClient;
import org.jclouds.aws.ec2.services.SecurityGroupClient;
import org.jclouds.aws.filters.FormSigner;
import org.jclouds.aws.handlers.AWSClientErrorRetryHandler;
import org.jclouds.aws.handlers.AWSRedirectionRetryHandler;
import org.jclouds.aws.handlers.ParseAWSErrorFromXmlContent;
import org.jclouds.date.DateService;
import org.jclouds.date.TimeStamp;
import org.jclouds.http.HttpErrorHandler;
import org.jclouds.http.HttpRetryHandler;
import org.jclouds.http.RequiresHttp;
import org.jclouds.http.annotation.ClientError;
import org.jclouds.http.annotation.Redirection;
import org.jclouds.http.annotation.ServerError;
import org.jclouds.rest.ConfiguresRestClient;
import org.jclouds.rest.RequestSigner;
import org.jclouds.rest.config.RestClientModule;

import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.inject.Provides;

/**
 * Configures the EC2 connection.
 * 
 * @author Adrian Cole
 */
@RequiresHttp
@ConfiguresRestClient
public class EC2RestClientModule extends
      RestClientModule<EC2Client, EC2AsyncClient> {
   public static final Map<Class<?>, Class<?>> DELEGATE_MAP = ImmutableMap
         .<Class<?>, Class<?>> builder()//
         .put(AMIClient.class, AMIAsyncClient.class)//
         .put(ElasticIPAddressClient.class, ElasticIPAddressAsyncClient.class)//
         .put(InstanceClient.class, InstanceAsyncClient.class)//
         .put(KeyPairClient.class, KeyPairAsyncClient.class)//
         .put(SecurityGroupClient.class, SecurityGroupAsyncClient.class)//
         .put(MonitoringClient.class, MonitoringAsyncClient.class)//
         .put(AvailabilityZoneAndRegionClient.class,
               AvailabilityZoneAndRegionAsyncClient.class)//
         .put(ElasticBlockStoreClient.class, ElasticBlockStoreAsyncClient.class)//
         .put(ElasticLoadBalancerClient.class,
               ElasticLoadBalancerAsyncClient.class)//
         .build();

   public EC2RestClientModule() {
      super(EC2Client.class, EC2AsyncClient.class, DELEGATE_MAP);
   }

   @Provides
   @Singleton
   @ELB
   protected URI provideELBURI(
         @Named(EC2Constants.PROPERTY_ELB_ENDPOINT) String endpoint) {
      return URI.create(endpoint);
   }

   @Provides
   @Singleton
   @ELB
   Map<String, URI> provideELBRegions() {
      return ImmutableMap
            .<String, URI> of(
                  Region.US_EAST_1,
                  URI
                        .create("https://elasticloadbalancing.us-east-1.amazonaws.com"),
                  Region.US_WEST_1,
                  URI
                        .create("https://elasticloadbalancing.us-west-1.amazonaws.com"),
                  Region.EU_WEST_1,
                  URI
                        .create("https://elasticloadbalancing.eu-west-1.amazonaws.com"),
                  Region.AP_SOUTHEAST_1,
                  URI
                        .create("https://elasticloadbalancing.ap-southeast-1.amazonaws.com"));
   }

   @Provides
   @Singleton
   @EC2
   String provideCurrentRegion(@EC2 Map<String, URI> regionMap,
         @EC2 URI currentUri) {
      ImmutableBiMap<URI, String> map = ImmutableBiMap.<String, URI> builder()
            .putAll(regionMap).build().inverse();
      String region = map.get(currentUri);
      assert region != null : currentUri + " not in " + map;
      return region;
   }

   private RuntimeException regionException = null;

   @Provides
   @Singleton
   @EC2
   Map<String, URI> provideRegions(EC2Client client) {
      // http://code.google.com/p/google-guice/issues/detail?id=483
      // guice doesn't remember when singleton providers throw exceptions.
      // in this case, if describeRegions fails, it is called again for
      // each provider method that depends on it. To short-circuit this,
      // we remember the last exception trusting that guice is single-threaded
      if (regionException != null)
         throw regionException;
      try {
         return client.getAvailabilityZoneAndRegionServices().describeRegions();
      } catch (RuntimeException e) {
         this.regionException = e;
         throw e;
      }
   }

   @Provides
   @Singleton
   Map<String, String> provideAvailabilityZoneToRegions(EC2Client client,
         @EC2 Map<String, URI> regions) {
      Map<String, String> map = Maps.newHashMap();
      for (String region : regions.keySet()) {
         for (AvailabilityZoneInfo zoneInfo : client
               .getAvailabilityZoneAndRegionServices()
               .describeAvailabilityZonesInRegion(region)) {
            map.put(zoneInfo.getZone(), region);
         }
      }
      return map;
   }

   @Provides
   @TimeStamp
   protected String provideTimeStamp(final DateService dateService,
         @Named(EC2Constants.PROPERTY_AWS_EXPIREINTERVAL) final int expiration) {
      return dateService.iso8601DateFormat(new Date(System.currentTimeMillis()
            + (expiration * 1000)));
   }

   @Provides
   @Singleton
   RequestSigner provideRequestSigner(FormSigner in) {
      return in;
   }

   @Provides
   @Singleton
   @EC2
   protected URI provideURI(
         @Named(EC2Constants.PROPERTY_EC2_ENDPOINT) String endpoint) {
      return URI.create(endpoint);
   }

   @Override
   protected void bindErrorHandlers() {
      bind(HttpErrorHandler.class).annotatedWith(Redirection.class).to(
            ParseAWSErrorFromXmlContent.class);
      bind(HttpErrorHandler.class).annotatedWith(ClientError.class).to(
            ParseAWSErrorFromXmlContent.class);
      bind(HttpErrorHandler.class).annotatedWith(ServerError.class).to(
            ParseAWSErrorFromXmlContent.class);
   }

   @Override
   protected void bindRetryHandlers() {
      bind(HttpRetryHandler.class).annotatedWith(Redirection.class).to(
            AWSRedirectionRetryHandler.class);
      bind(HttpRetryHandler.class).annotatedWith(ClientError.class).to(
            AWSClientErrorRetryHandler.class);
   }
}