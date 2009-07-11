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
package org.jclouds.command;

import java.util.concurrent.Future;

/**
 * 
 * 
 * {@code <E>} type of endpoint that this client will interact with. {@code <Q>} type of request
 * that this endpoint expects. {@code <R>} type of response that this endpoint produces.
 * 
 * @author Adrian Cole
 */
public interface EndpointCommandExecutorService<C extends EndpointCommand<?, ?, R>, R> {

   /**
    * Asks the command to build a request relevant for an endpoint that produces responses of
    * generic type {@code R}. and invokes it on the endpoint, returning a future
    * 
    * @param <T>
    *           type of result the command extracts from the return value
    * @param command
    *           that generates requests
    * @return {@link Future} containing the response from the {@code endpoint}
    */
   Future<R> submit(C command);
}
