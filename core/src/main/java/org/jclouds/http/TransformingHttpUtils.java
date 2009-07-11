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
package org.jclouds.http;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.SynchronousQueue;

import com.google.common.base.Function;

public class TransformingHttpUtils {
   
   public static <T> HttpCommandRendezvous<T> submitHttpCommand(HttpCommand command,
            final Function<HttpResponse, T> responseTransformer, ExecutorService executorService) {
      final SynchronousQueue<?> channel = new SynchronousQueue<Object>();

      // should block and immediately parse the response on exit.
      Future<T> future = executorService.submit(new Callable<T>() {
         public T call() throws Exception {
            Object o = channel.take();
            if (o instanceof Exception)
               throw (Exception) o;
            return responseTransformer.apply((HttpResponse) o);
         }
      });

      HttpCommandRendezvous<T> rendezvous = new HttpCommandRendezvous<T>(command, channel, future);
      return rendezvous;
   }

}
