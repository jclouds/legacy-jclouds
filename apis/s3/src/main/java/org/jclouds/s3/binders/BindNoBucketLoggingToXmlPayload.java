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
package org.jclouds.s3.binders;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.http.HttpRequest;
import org.jclouds.rest.Binder;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class BindNoBucketLoggingToXmlPayload implements Binder {
   private final BindAsHostPrefixIfConfigured bindAsHostPrefixIfConfigured;

   @Inject
   BindNoBucketLoggingToXmlPayload(BindAsHostPrefixIfConfigured bindAsHostPrefixIfConfigured) {
      this.bindAsHostPrefixIfConfigured = bindAsHostPrefixIfConfigured;
   }

   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Object payload) {
      request = bindAsHostPrefixIfConfigured.bindToRequest(request, payload);
      String stringPayload = "<BucketLoggingStatus xmlns=\"http://s3.amazonaws.com/doc/2006-03-01/\"/>";
      request.setPayload(stringPayload);
      return request;
   }

}
