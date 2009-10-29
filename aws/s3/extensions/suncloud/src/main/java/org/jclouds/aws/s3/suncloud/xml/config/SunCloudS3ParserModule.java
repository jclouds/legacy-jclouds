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
package org.jclouds.aws.s3.suncloud.xml.config;

import org.jclouds.aws.domain.AWSError;
import org.jclouds.aws.s3.suncloud.xml.SunCloudS3ErrorHandler;
import org.jclouds.aws.s3.xml.S3ParserFactory;
import org.jclouds.aws.s3.xml.config.S3ParserModule;
import org.jclouds.http.functions.ParseSax;

import com.google.inject.TypeLiteral;
import com.google.inject.assistedinject.FactoryProvider;

/**
 * Creates the factories needed to interpret S3 responses from Sun Cloud Storage Object Service
 * 
 * @author Adrian Cole
 */
public class SunCloudS3ParserModule extends S3ParserModule {
   protected void bindErrorHandler() {
      TypeLiteral<S3ParserFactory.GenericParseFactory<AWSError>> errorTypeLiteral = new TypeLiteral<S3ParserFactory.GenericParseFactory<AWSError>>() {
      };
      bind(new TypeLiteral<ParseSax.HandlerWithResult<AWSError>>() {
      }).to(SunCloudS3ErrorHandler.class);
      bind(errorTypeLiteral).toProvider(
               FactoryProvider.newFactory(errorTypeLiteral, new TypeLiteral<ParseSax<AWSError>>() {
               }));
   }
}