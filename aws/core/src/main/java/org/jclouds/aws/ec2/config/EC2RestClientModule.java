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
package org.jclouds.aws.ec2.config;

import java.net.URI;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.aws.ec2.EC2;
import org.jclouds.aws.ec2.domain.Region;
import org.jclouds.aws.ec2.domain.RunningInstance;
import org.jclouds.aws.ec2.filters.FormSigner;
import org.jclouds.aws.ec2.predicates.InstanceStateRunning;
import org.jclouds.aws.ec2.reference.EC2Constants;
import org.jclouds.aws.ec2.services.AMIAsyncClient;
import org.jclouds.aws.ec2.services.AMIClient;
import org.jclouds.aws.ec2.services.AvailabilityZoneAndRegionAsyncClient;
import org.jclouds.aws.ec2.services.AvailabilityZoneAndRegionClient;
import org.jclouds.aws.ec2.services.ElasticBlockStoreAsyncClient;
import org.jclouds.aws.ec2.services.ElasticBlockStoreClient;
import org.jclouds.aws.ec2.services.ElasticIPAddressAsyncClient;
import org.jclouds.aws.ec2.services.ElasticIPAddressClient;
import org.jclouds.aws.ec2.services.InstanceAsyncClient;
import org.jclouds.aws.ec2.services.InstanceClient;
import org.jclouds.aws.ec2.services.KeyPairAsyncClient;
import org.jclouds.aws.ec2.services.KeyPairClient;
import org.jclouds.aws.ec2.services.MonitoringAsyncClient;
import org.jclouds.aws.ec2.services.MonitoringClient;
import org.jclouds.aws.ec2.services.SecurityGroupAsyncClient;
import org.jclouds.aws.ec2.services.SecurityGroupClient;
import org.jclouds.aws.handlers.AWSClientErrorRetryHandler;
import org.jclouds.aws.handlers.AWSRedirectionRetryHandler;
import org.jclouds.aws.handlers.ParseAWSErrorFromXmlContent;
import org.jclouds.aws.util.RequestSigner;
import org.jclouds.concurrent.internal.SyncProxy;
import org.jclouds.date.DateService;
import org.jclouds.date.TimeStamp;
import org.jclouds.http.HttpErrorHandler;
import org.jclouds.http.HttpRetryHandler;
import org.jclouds.http.RequiresHttp;
import org.jclouds.http.annotation.ClientError;
import org.jclouds.http.annotation.Redirection;
import org.jclouds.http.annotation.ServerError;
import org.jclouds.predicates.RetryablePredicate;
import org.jclouds.rest.ConfiguresRestClient;
import org.jclouds.rest.RestClientFactory;

import com.google.common.base.Predicate;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;

/**
 * Configures the EC2 connection.
 * 
 * @author Adrian Cole
 */
@RequiresHttp
@ConfiguresRestClient
public class EC2RestClientModule extends AbstractModule {
   @Provides
   @Singleton
   protected Predicate<RunningInstance> instanceStateRunning(InstanceStateRunning stateRunning) {
      return new RetryablePredicate<RunningInstance>(stateRunning, 600, 3, TimeUnit.SECONDS);
   }

   @Override
   protected void configure() {
      bindErrorHandlers();
      bindRetryHandlers();
   }

   @Provides
   @Singleton
   Map<Region, URI> provideRegions(AvailabilityZoneAndRegionClient client) {
      return client.describeRegions();
   }

   @Provides
   @TimeStamp
   protected String provideTimeStamp(final DateService dateService,
            @Named(EC2Constants.PROPERTY_EC2_EXPIREINTERVAL) final int expiration) {
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
   protected AMIAsyncClient provideAMIAsyncClient(RestClientFactory factory) {
      return factory.create(AMIAsyncClient.class);
   }

   @Provides
   @Singleton
   public AMIClient provideAMIClient(AMIAsyncClient client) throws IllegalArgumentException,
            SecurityException, NoSuchMethodException {
      return SyncProxy.create(AMIClient.class, client);
   }

   @Provides
   @Singleton
   protected ElasticIPAddressAsyncClient provideElasticIPAddressAsyncClient(
            RestClientFactory factory) {
      return factory.create(ElasticIPAddressAsyncClient.class);
   }

   @Provides
   @Singleton
   public ElasticIPAddressClient provideElasticIPAddressClient(ElasticIPAddressAsyncClient client)
            throws IllegalArgumentException, SecurityException, NoSuchMethodException {
      return SyncProxy.create(ElasticIPAddressClient.class, client);
   }

   @Provides
   @Singleton
   protected InstanceAsyncClient provideInstanceAsyncClient(RestClientFactory factory) {
      return factory.create(InstanceAsyncClient.class);
   }

   @Provides
   @Singleton
   public InstanceClient provideInstanceClient(InstanceAsyncClient client)
            throws IllegalArgumentException, SecurityException, NoSuchMethodException {
      return SyncProxy.create(InstanceClient.class, client);
   }

   @Provides
   @Singleton
   protected KeyPairAsyncClient provideKeyPairAsyncClient(RestClientFactory factory) {
      return factory.create(KeyPairAsyncClient.class);
   }

   @Provides
   @Singleton
   public KeyPairClient provideKeyPairClient(KeyPairAsyncClient client)
            throws IllegalArgumentException, SecurityException, NoSuchMethodException {
      return SyncProxy.create(KeyPairClient.class, client);
   }

   @Provides
   @Singleton
   protected SecurityGroupAsyncClient provideSecurityGroupAsyncClient(RestClientFactory factory) {
      return factory.create(SecurityGroupAsyncClient.class);
   }

   @Provides
   @Singleton
   public SecurityGroupClient provideSecurityGroupClient(SecurityGroupAsyncClient client)
            throws IllegalArgumentException, SecurityException, NoSuchMethodException {
      return SyncProxy.create(SecurityGroupClient.class, client);
   }

   @Provides
   @Singleton
   protected MonitoringAsyncClient provideMonitoringAsyncClient(RestClientFactory factory) {
      return factory.create(MonitoringAsyncClient.class);
   }

   @Provides
   @Singleton
   public MonitoringClient provideMonitoringClient(MonitoringAsyncClient client)
            throws IllegalArgumentException, SecurityException, NoSuchMethodException {
      return SyncProxy.create(MonitoringClient.class, client);
   }

   @Provides
   @Singleton
   protected AvailabilityZoneAndRegionAsyncClient provideAvailabilityZoneAndRegionAsyncClient(
            RestClientFactory factory) {
      return factory.create(AvailabilityZoneAndRegionAsyncClient.class);
   }

   @Provides
   @Singleton
   public AvailabilityZoneAndRegionClient provideAvailabilityZoneAndRegionClient(
            AvailabilityZoneAndRegionAsyncClient client) throws IllegalArgumentException,
            SecurityException, NoSuchMethodException {
      return SyncProxy.create(AvailabilityZoneAndRegionClient.class, client);
   }

   @Provides
   @Singleton
   protected ElasticBlockStoreAsyncClient provideElasticBlockStoreAsyncClient(
            RestClientFactory factory) {
      return factory.create(ElasticBlockStoreAsyncClient.class);
   }

   @Provides
   @Singleton
   public ElasticBlockStoreClient provideElasticBlockStoreClient(ElasticBlockStoreAsyncClient client)
            throws IllegalArgumentException, SecurityException, NoSuchMethodException {
      return SyncProxy.create(ElasticBlockStoreClient.class, client);
   }

   @Provides
   @Singleton
   @EC2
   protected URI provideURI(@Named(EC2Constants.PROPERTY_EC2_ENDPOINT) String endpoint) {
      return URI.create(endpoint);
   }

   protected void bindErrorHandlers() {
      bind(HttpErrorHandler.class).annotatedWith(Redirection.class).to(
               ParseAWSErrorFromXmlContent.class);
      bind(HttpErrorHandler.class).annotatedWith(ClientError.class).to(
               ParseAWSErrorFromXmlContent.class);
      bind(HttpErrorHandler.class).annotatedWith(ServerError.class).to(
               ParseAWSErrorFromXmlContent.class);
   }

   protected void bindRetryHandlers() {
      bind(HttpRetryHandler.class).annotatedWith(Redirection.class).to(
               AWSRedirectionRetryHandler.class);
      bind(HttpRetryHandler.class).annotatedWith(ClientError.class).to(
               AWSClientErrorRetryHandler.class);
   }
}