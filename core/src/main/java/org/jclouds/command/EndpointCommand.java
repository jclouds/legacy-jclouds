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

/**
 * 
 * {@code <E>} type of endpoint that this client will interact with. {@code <Q>} type of request
 * that this endpoint expects. {@code <R>} type of response that this endpoint produces.
 * 
 * @author Adrian Cole
 */
public interface EndpointCommand<E, Q extends Request<E>, R> {

   int incrementFailureCount();

   int getFailureCount();

   /**
    * generates a request object used to invoke this command.
    * 
    * @return
    */
   Q getRequest();

   /**
    * 
    * Used to prevent a command from being re-executed.
    * 
    * Any calls to {@link execute} following this will return the below exception.
    * 
    * @param exception
    */
   void setException(Exception exception);
   
   Exception getException();

}
