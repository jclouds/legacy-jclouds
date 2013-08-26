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
package org.jclouds.trmk.ecloud.features;

import static org.jclouds.trmk.ecloud.TerremarkECloudMediaType.TAGSLISTLIST_XML;

import java.net.URI;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;

import org.jclouds.Fallbacks;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.rest.annotations.EndpointParam;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.XMLResponseParser;
import org.jclouds.trmk.ecloud.functions.OrgURIToTagsListEndpoint;
import org.jclouds.trmk.ecloud.xml.TagNameToUsageCountHandler;
import org.jclouds.trmk.vcloud_0_8.filters.SetVCloudTokenCookie;

/**
 * Tag Based Operations
 * <p/>
 * 
 * @author Adrian Cole
 */
@RequestFilters(SetVCloudTokenCookie.class)
public interface TagOperationsApi {

   /**
    * This call returns the list of all tags belonging to the organization.
    * 
    * @return tags
    */
   @GET
   @Consumes(TAGSLISTLIST_XML)
   @XMLResponseParser(TagNameToUsageCountHandler.class)
   @Fallback(Fallbacks.EmptyMapOnNotFoundOr404.class)
   Map<String, Integer> getTagNameToUsageCountInOrg(@Nullable @EndpointParam(parser = OrgURIToTagsListEndpoint.class) URI org);

   /**
    * This call returns the list of all tags by list id.
    * 
    * @return tags
    */
   @GET
   @Consumes(TAGSLISTLIST_XML)
   @XMLResponseParser(TagNameToUsageCountHandler.class)
   @Fallback(Fallbacks.EmptyMapOnNotFoundOr404.class)
   Map<String, Integer> getTagNameToUsageCount(@EndpointParam URI tagList);

}
