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
package org.jclouds.aws.s3.commands;

import static com.google.common.base.Preconditions.checkNotNull;

import org.jclouds.http.HttpFutureCommand;

/**
 * Conditionally adds the amazon host header to requests.
 * 
 * @author Adrian Cole
 * 
 * @param <T>
 */
public class S3FutureCommand<T> extends HttpFutureCommand<T> {

   public S3FutureCommand(String method, String uri, ResponseCallable<T> responseCallable,
            String bucketName) {
      super(method, String.format("/%1$s%2$s", checkNotNull(bucketName, "bucketName"),
               checkNotNull(uri, "uri")), responseCallable);
   }

   public S3FutureCommand(String method, String uri, ResponseCallable<T> responseCallable) {
      super(method, uri, responseCallable);
   }

}