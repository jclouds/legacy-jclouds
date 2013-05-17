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
package org.jclouds.trmk.vcloud_0_8.binders;

import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;
import java.util.Map;
import java.util.NoSuchElementException;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.http.HttpRequest;
import org.jclouds.rest.MapBinder;
import org.jclouds.trmk.vcloud_0_8.domain.Org;
import org.jclouds.trmk.vcloud_0_8.domain.ReferenceType;
import org.jclouds.trmk.vcloud_0_8.endpoints.TasksList;

import com.google.common.base.Supplier;
import com.google.common.collect.Iterables;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class OrgNameAndTasksListNameToEndpoint implements MapBinder {
   private final Supplier<Map<String, ? extends Org>> orgMap;
   private final Supplier<ReferenceType> defaultOrg;
   private final Supplier<ReferenceType> defaultTasksList;

   @Inject
   public OrgNameAndTasksListNameToEndpoint(Supplier<Map<String, ? extends Org>> orgMap,
         @org.jclouds.trmk.vcloud_0_8.endpoints.Org Supplier<ReferenceType> defaultOrg, @TasksList Supplier<ReferenceType> defaultTasksList) {
      this.orgMap = orgMap;
      this.defaultOrg = defaultOrg;
      this.defaultTasksList = defaultTasksList;
   }

   @SuppressWarnings("unchecked")
   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Map<String, Object> postParams) {
      Object org = postParams.get("orgName");
      Object tasksList = postParams.get("tasksListName");
      if (org == null && tasksList == null)
         return (R) request.toBuilder().endpoint(defaultTasksList.get().getHref()).build();
      else if (org == null)
         org = defaultOrg.get().getName();

      try {
         Map<String, ReferenceType> tasksLists = checkNotNull(orgMap.get().get(org)).getTasksLists();
         URI endpoint = tasksList == null ? Iterables.getLast(tasksLists.values()).getHref() : tasksLists.get(tasksList).getHref();
         return (R) request.toBuilder().endpoint(endpoint).build();
      } catch (NullPointerException e) {
         throw new NoSuchElementException(org + "/" + tasksList + " not found in " + orgMap.get());
      }
   }

   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Object input) {
      throw new IllegalStateException(getClass() + " needs parameters");
   }
}
