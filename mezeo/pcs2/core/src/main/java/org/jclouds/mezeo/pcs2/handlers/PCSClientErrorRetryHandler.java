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
package org.jclouds.mezeo.pcs2.handlers;

import javax.annotation.Resource;
import javax.inject.Inject;

import org.jclouds.http.HttpCommand;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.HttpRetryHandler;
import org.jclouds.http.handlers.BackoffLimitedRetryHandler;
import org.jclouds.logging.Logger;

/**
 * Handles Retryable responses with error codes in the 4xx range
 * 
 * @author Adrian Cole
 */
public class PCSClientErrorRetryHandler implements HttpRetryHandler {

   private final BackoffLimitedRetryHandler backoffHandler;

   @Inject
   public PCSClientErrorRetryHandler(BackoffLimitedRetryHandler backoffHandler) {
      this.backoffHandler = backoffHandler;
   }

   @Resource
   protected Logger logger = Logger.NULL;

   public boolean shouldRetryRequest(HttpCommand command, HttpResponse response) {
      if (response.getStatusCode() == 400) {
         return backoffHandler.shouldRetryRequest(command, response);
      }
      return false;
   }

}
