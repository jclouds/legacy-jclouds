/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */

package org.jclouds.vcloud.functions;

import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;
import java.util.Map;
import java.util.NoSuchElementException;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.vcloud.domain.NamedResource;
import org.jclouds.vcloud.domain.Organization;
import org.jclouds.vcloud.endpoints.Org;
import org.jclouds.vcloud.endpoints.TasksList;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.collect.Iterables;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class OrgNameAndTasksListNameToEndpoint implements Function<Object, URI> {
   private final Supplier<Map<String, ? extends Organization>> orgMap;
   private final String defaultOrg;
   private final URI defaultUri;

   @Inject
   public OrgNameAndTasksListNameToEndpoint(Supplier<Map<String, ? extends Organization>> orgMap,
         @Org String defaultOrg, @TasksList URI defaultUri) {
      this.orgMap = orgMap;
      this.defaultOrg = defaultOrg;
      this.defaultUri = defaultUri;
   }

   @SuppressWarnings("unchecked")
   public URI apply(Object from) {
      Iterable<Object> orgTasksList = (Iterable<Object>) checkNotNull(from, "args");
      Object org = Iterables.get(orgTasksList, 0);
      Object tasksList = Iterables.get(orgTasksList, 1);
      if (org == null && tasksList == null)
         return defaultUri;
      else if (org == null)
         org = defaultOrg;

      try {
         Map<String, NamedResource> tasksLists = checkNotNull(orgMap.get().get(org)).getTasksLists();
         return tasksList == null ? Iterables.getLast(tasksLists.values()).getLocation() : tasksLists.get(tasksList)
               .getLocation();
      } catch (NullPointerException e) {
         throw new NoSuchElementException(org + "/" + tasksList + " not found in " + orgMap.get());
      }
   }

}