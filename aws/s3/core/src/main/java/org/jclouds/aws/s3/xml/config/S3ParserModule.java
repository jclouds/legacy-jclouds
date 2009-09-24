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
package org.jclouds.aws.s3.xml.config;

import java.util.List;

import org.jclouds.aws.domain.AWSError;
import org.jclouds.aws.s3.domain.AccessControlList;
import org.jclouds.aws.s3.domain.BucketMetadata;
import org.jclouds.aws.s3.domain.ListBucketResponse;
import org.jclouds.aws.s3.domain.ObjectMetadata;
import org.jclouds.aws.s3.xml.AccessControlListHandler;
import org.jclouds.aws.s3.xml.CopyObjectHandler;
import org.jclouds.aws.s3.xml.ListAllMyBucketsHandler;
import org.jclouds.aws.s3.xml.ListBucketHandler;
import org.jclouds.aws.s3.xml.S3ParserFactory;
import org.jclouds.aws.xml.ErrorHandler;
import org.jclouds.command.ConfiguresResponseTransformer;
import org.jclouds.http.functions.ParseSax;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.google.inject.assistedinject.FactoryProvider;

/**
 * Creates the factories needed to interpret S3 responses
 * 
 * @author Adrian Cole
 */
@ConfiguresResponseTransformer
public class S3ParserModule extends AbstractModule {
   protected final TypeLiteral<S3ParserFactory.GenericParseFactory<AWSError>> errorTypeLiteral = new TypeLiteral<S3ParserFactory.GenericParseFactory<AWSError>>() {
   };
   protected final TypeLiteral<S3ParserFactory.GenericParseFactory<List<BucketMetadata>>> listBucketsTypeLiteral = new TypeLiteral<S3ParserFactory.GenericParseFactory<List<BucketMetadata>>>() {
   };
   protected final TypeLiteral<S3ParserFactory.GenericParseFactory<ListBucketResponse>> bucketTypeLiteral = new TypeLiteral<S3ParserFactory.GenericParseFactory<ListBucketResponse>>() {
   };
   protected final TypeLiteral<S3ParserFactory.GenericParseFactory<ObjectMetadata>> objectMetadataTypeLiteral = new TypeLiteral<S3ParserFactory.GenericParseFactory<ObjectMetadata>>() {
   };
   protected final TypeLiteral<S3ParserFactory.GenericParseFactory<AccessControlList>> accessControlListTypeLiteral = new TypeLiteral<S3ParserFactory.GenericParseFactory<AccessControlList>>() {
   };

   @Override
   protected void configure() {
      bindErrorHandler();
      bindCallablesThatReturnParseResults();
      bindParserImplementationsToReturnTypes();
   }

   protected void bindErrorHandler() {
      bind(new TypeLiteral<ParseSax.HandlerWithResult<AWSError>>() {
      }).to(ErrorHandler.class);
   }

   private void bindParserImplementationsToReturnTypes() {
      bind(new TypeLiteral<ParseSax.HandlerWithResult<List<BucketMetadata>>>() {
      }).to(ListAllMyBucketsHandler.class);
      bind(new TypeLiteral<ParseSax.HandlerWithResult<ListBucketResponse>>() {
      }).to(ListBucketHandler.class);
      bind(new TypeLiteral<ParseSax.HandlerWithResult<ObjectMetadata>>() {
      }).to(CopyObjectHandler.class);
      bind(new TypeLiteral<ParseSax.HandlerWithResult<AccessControlList>>() {
      }).to(AccessControlListHandler.class);
   }

   private void bindCallablesThatReturnParseResults() {
      bind(listBucketsTypeLiteral).toProvider(
               FactoryProvider.newFactory(listBucketsTypeLiteral,
                        new TypeLiteral<ParseSax<List<BucketMetadata>>>() {
                        }));
      bind(bucketTypeLiteral).toProvider(
               FactoryProvider.newFactory(bucketTypeLiteral,
                        new TypeLiteral<ParseSax<ListBucketResponse>>() {
               }));
      bind(objectMetadataTypeLiteral).toProvider(
               FactoryProvider.newFactory(objectMetadataTypeLiteral,
                        new TypeLiteral<ParseSax<ObjectMetadata>>() {
                        }));
      bind(accessControlListTypeLiteral).toProvider(
               FactoryProvider.newFactory(accessControlListTypeLiteral,
                        new TypeLiteral<ParseSax<AccessControlList>>() {
                        }));
      bind(errorTypeLiteral).toProvider(
               FactoryProvider.newFactory(errorTypeLiteral, new TypeLiteral<ParseSax<AWSError>>() {
               }));
   }

}