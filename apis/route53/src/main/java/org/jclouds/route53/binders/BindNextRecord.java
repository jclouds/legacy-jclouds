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
package org.jclouds.route53.binders;

import javax.inject.Singleton;

import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpRequest.Builder;
import org.jclouds.rest.Binder;
import org.jclouds.route53.domain.ResourceRecordSetIterable.NextRecord;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class BindNextRecord implements Binder {
   @SuppressWarnings("unchecked")
   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Object payload) {
      NextRecord from = NextRecord.class.cast(payload);
      Builder<?> builder = request.toBuilder();
      builder.addQueryParam("name", from.getName());
      if (from.getType().isPresent())
         builder.addQueryParam("type", from.getType().get());
      if (from.getIdentifier().isPresent())
         builder.addQueryParam("identifier", from.getIdentifier().get());
      return (R) builder.build();
   }
}
