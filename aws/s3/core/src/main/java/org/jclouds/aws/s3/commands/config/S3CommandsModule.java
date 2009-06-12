/**
 *
 * Copyright (C) 2009 Global Cloud Specialists, Inc. <info@globalcloudspecialists.com>
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
package org.jclouds.aws.s3.commands.config;

import org.jclouds.aws.s3.commands.BucketExists;
import org.jclouds.aws.s3.commands.DeleteBucket;
import org.jclouds.aws.s3.commands.DeleteObject;
import org.jclouds.aws.s3.commands.GetObject;
import org.jclouds.aws.s3.commands.HeadObject;
import org.jclouds.aws.s3.commands.PutBucket;
import org.jclouds.aws.s3.commands.PutObject;
import org.jclouds.aws.s3.commands.S3CommandFactory;

import com.google.inject.AbstractModule;
import com.google.inject.assistedinject.FactoryProvider;

/**
 * Creates the factories needed to produce S3 commands
 * 
 * @author Adrian Cole
 */
public class S3CommandsModule extends AbstractModule {
   @Override
   protected void configure() {

      bind(S3CommandFactory.DeleteBucketFactory.class).toProvider(
               FactoryProvider.newFactory(S3CommandFactory.DeleteBucketFactory.class,
                        DeleteBucket.class));

      bind(S3CommandFactory.DeleteObjectFactory.class).toProvider(
               FactoryProvider.newFactory(S3CommandFactory.DeleteObjectFactory.class,
                        DeleteObject.class));

      bind(S3CommandFactory.BucketExistsFactory.class).toProvider(
               FactoryProvider.newFactory(S3CommandFactory.BucketExistsFactory.class,
                        BucketExists.class));

      bind(S3CommandFactory.PutBucketFactory.class)
               .toProvider(
                        FactoryProvider.newFactory(S3CommandFactory.PutBucketFactory.class,
                                 PutBucket.class));

      bind(S3CommandFactory.PutObjectFactory.class)
               .toProvider(
                        FactoryProvider.newFactory(S3CommandFactory.PutObjectFactory.class,
                                 PutObject.class));

      bind(S3CommandFactory.GetObjectFactory.class)
               .toProvider(
                        FactoryProvider.newFactory(S3CommandFactory.GetObjectFactory.class,
                                 GetObject.class));

      bind(S3CommandFactory.HeadMetadataFactory.class).toProvider(
               FactoryProvider.newFactory(S3CommandFactory.HeadMetadataFactory.class,
                        HeadObject.class));

   }

}