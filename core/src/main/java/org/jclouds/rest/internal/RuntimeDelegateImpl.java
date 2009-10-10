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
package org.jclouds.rest.internal;

import javax.ws.rs.core.Application;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Variant.VariantListBuilder;
import javax.ws.rs.ext.RuntimeDelegate;

import org.jboss.resteasy.specimpl.UriBuilderImpl;

/**
 * @author Adrian Cole
 * 
 * @see RuntimeDelegate
 */
public class RuntimeDelegateImpl extends RuntimeDelegate {

   /**
    * operation is currently unsupported.
    */
   @Override
   public <T> T createEndpoint(Application application, Class<T> endpointType)
            throws IllegalArgumentException, UnsupportedOperationException {
      throw new UnsupportedOperationException(
               "jclouds does not currently implement the entire jaxrs spec.");
   }

   /**
    * operation is currently unsupported.
    */
   @Override
   public <T> HeaderDelegate<T> createHeaderDelegate(Class<T> type) {
      throw new UnsupportedOperationException(
               "jclouds does not currently implement the entire jaxrs spec.");
   }

   /**
    * operation is currently unsupported.
    */
   @Override
   public ResponseBuilder createResponseBuilder() {
      throw new UnsupportedOperationException(
               "jclouds does not currently implement the entire jaxrs spec.");
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public UriBuilder createUriBuilder() {
      return new UriBuilderImpl();
   }

   /**
    * operation is currently unsupported.
    */
   @Override
   public VariantListBuilder createVariantListBuilder() {
      throw new UnsupportedOperationException(
               "jclouds does not currently implement the entire jaxrs spec.");
   }

}
