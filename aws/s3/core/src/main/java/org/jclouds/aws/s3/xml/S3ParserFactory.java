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
package org.jclouds.aws.s3.xml;

import java.util.List;

import org.jclouds.aws.domain.AWSError;
import org.jclouds.aws.s3.domain.AccessControlList;
import org.jclouds.aws.s3.domain.BucketMetadata;
import org.jclouds.aws.s3.domain.ListBucketResponse;
import org.jclouds.aws.s3.domain.ObjectMetadata;
import org.jclouds.aws.xml.ErrorHandler;
import org.jclouds.http.functions.ParseSax;

import com.google.common.annotations.VisibleForTesting;
import javax.inject.Inject;
import javax.inject.Provider;

/**
 * Creates Parsers needed to interpret S3 Server messages. This class uses guice assisted inject,
 * which mandates the creation of many single-method interfaces. These interfaces are not intended
 * for public api.
 * 
 * @author Adrian Cole
 */
public class S3ParserFactory {

   @Inject
   private GenericParseFactory<List<BucketMetadata>> parseListAllMyBucketsFactory;

   @VisibleForTesting
   public static interface GenericParseFactory<T> {
      ParseSax<T> create(ParseSax.HandlerWithResult<T> handler);
   }

   @Inject
   Provider<ListAllMyBucketsHandler> ListAllMyBucketsHandlerprovider;

   /**
    * @return a parser used to handle {@link org.jclouds.aws.s3.commands.ListOwnedBuckets} responses
    */
   public ParseSax<List<BucketMetadata>> createListBucketsParser() {
      return parseListAllMyBucketsFactory.create(ListAllMyBucketsHandlerprovider.get());
   }

   @Inject
   private GenericParseFactory<ListBucketResponse> parseListBucketFactory;

   @Inject
   Provider<ListBucketHandler> ListBucketHandlerprovider;

   /**
    * @return a parser used to handle {@link org.jclouds.aws.s3.commands.ListBucket} responses
    */
   public ParseSax<ListBucketResponse> createListBucketParser() {
      return parseListBucketFactory.create(ListBucketHandlerprovider.get());
   }

   @Inject
   private GenericParseFactory<ObjectMetadata> parseCopyObjectFactory;

   @Inject
   Provider<CopyObjectHandler> copyObjectHandlerProvider;

   /**
    * @return a parser used to handle {@link org.jclouds.aws.s3.commands.CopyObject} responses
    */
   public ParseSax<ObjectMetadata> createCopyObjectParser() {
      return parseCopyObjectFactory.create(copyObjectHandlerProvider.get());
   }

   @Inject
   private GenericParseFactory<AWSError> parseErrorFactory;

   @Inject
   Provider<ErrorHandler> errorHandlerProvider;

   /**
    * @return a parser used to handle error conditions.
    */
   public ParseSax<AWSError> createErrorParser() {
      return parseErrorFactory.create(errorHandlerProvider.get());
   }

   @Inject
   private GenericParseFactory<AccessControlList> parseAccessControlListFactory;

   @Inject
   Provider<AccessControlListHandler> accessControlListHandlerProvider;

   /**
    * @return a parser used to handle {@link org.jclouds.aws.s3.commands.GetAccessControlList} responses
    */
   public ParseSax<AccessControlList> createAccessControlListParser() {
      return parseAccessControlListFactory.create(accessControlListHandlerProvider.get());
   }

}