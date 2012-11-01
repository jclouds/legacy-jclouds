/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.jclouds.abiquo.domain.util;

import static com.google.common.collect.Iterables.filter;

import java.util.List;

import org.jclouds.abiquo.predicates.LinkPredicates;

import com.abiquo.model.rest.RESTLink;
import com.abiquo.model.transport.SingleResourceTransportDto;
import com.google.common.collect.Lists;

/**
 * Utility method to work with {@link RESTLink} objects.
 * 
 * @author Ignasi Barrera
 */
public class LinkUtils {
   /**
    * Get the link that points to the current resource.
    * 
    * @param dto
    *           The target dto.
    * @return The link to the current resource.
    */
   public static RESTLink getSelfLink(final SingleResourceTransportDto dto) {
      RESTLink link = dto.searchLink("edit");
      return link == null ? dto.searchLink("self") : link;
   }

   /**
    * Filter the given link list and return only the links that point to a NIC.
    * 
    * @param links
    *           The list with the links to filter.
    * @return A list with all links that point to a NIC.
    */
   public static List<RESTLink> filterNicLinks(final List<RESTLink> links) {
      return Lists.newLinkedList(filter(links, LinkPredicates.isNic()));
   }
}
