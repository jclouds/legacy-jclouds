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
package org.jclouds.aws.s3.handlers;

import javax.annotation.Resource;

import org.jclouds.aws.domain.AWSError;
import org.jclouds.aws.s3.util.S3Utils;
import org.jclouds.aws.s3.xml.S3ParserFactory;
import org.jclouds.http.HttpException;
import org.jclouds.http.HttpFutureCommand;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.HttpRetryHandler;
import org.jclouds.logging.Logger;

import com.google.inject.Inject;
import com.google.inject.name.Named;

/**
 * Handles Retryable responses with error codes in the 3xx range
 * 
 * @author Adrian Cole
 */
public class AWSClientErrorRetryHandler implements HttpRetryHandler {
   private final S3ParserFactory parserFactory;

   private final int retryCountLimit;

   @Resource
   protected Logger logger = Logger.NULL;

   @Inject
   public AWSClientErrorRetryHandler(S3ParserFactory parserFactory,
            @Named("jclouds.http.max-retries") int retryCountLimit) {
      this.retryCountLimit = retryCountLimit;
      this.parserFactory = parserFactory;
   }

   public boolean shouldRetryRequest(HttpFutureCommand<?> command, HttpResponse response) {
      if (command.getFailureCount() > retryCountLimit)
         return false;
      if (response.getStatusCode() == 400) {
         byte[] content = S3Utils.closeConnectionButKeepContentStream(response);
         command.incrementRedirectCount();
         try {
            AWSError error = S3Utils.parseAWSErrorFromContent(parserFactory, command, response,
                     new String(content));
            if ("RequestTimeout".equals(error.getCode())) {
               return true;
            }
         } catch (HttpException e) {
            logger.warn(e, "error parsing response: %s", new String(content));
         }
      }
      return false;
   }
}
