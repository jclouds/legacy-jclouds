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
package org.jclouds.trmk.vcloud_0_8.functions;

import java.net.URI;
import java.util.Date;

import javax.inject.Singleton;
import javax.ws.rs.core.HttpHeaders;

import org.jclouds.http.HttpResponse;
import org.jclouds.http.HttpResponseException;
import org.jclouds.trmk.vcloud_0_8.domain.Task;
import org.jclouds.trmk.vcloud_0_8.domain.TaskStatus;
import org.jclouds.trmk.vcloud_0_8.domain.internal.TaskImpl;

import com.google.common.base.Function;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class ParseTaskFromLocationHeader implements Function<HttpResponse, Task> {

   public Task apply(HttpResponse from) {
      String location = from.getFirstHeaderOrNull(HttpHeaders.LOCATION);
      if (location == null)
         location = from.getFirstHeaderOrNull("location");
      if (location != null) {
         return new TaskImpl(URI.create(location), null, TaskStatus.QUEUED, new Date(), null, null, null, null);
      } else {
         throw new HttpResponseException("no uri in headers or content", null, from);
      }

   }
}
