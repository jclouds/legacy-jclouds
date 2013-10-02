/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.http;

import java.util.concurrent.Future;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * Capable of invoking http commands.
 * 
 * @author Adrian Cole
 */
public interface HttpCommandExecutorService { 

   /**
    * Returns a potentially deferred {@code HttpResponse} from a server responding to the
    * {@code command}. The output {@code ListenableFuture} need not be
    * {@linkplain Future#isDone done}, making {@code HttpCommandExecutorService}
    * suitable for asynchronous derivations.
    * 
    */
   ListenableFuture<HttpResponse> submit(HttpCommand command);
   
   /**
    * Returns a {@code HttpResponse} from the server which responded to the
    * {@code command}.
    */
   HttpResponse invoke(HttpCommand command);

}
