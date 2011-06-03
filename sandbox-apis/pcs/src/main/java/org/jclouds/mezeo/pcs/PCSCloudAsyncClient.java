/**
 *
 * Copyright (C) 2011 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.mezeo.pcs;

import java.net.URI;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

import org.jclouds.http.filters.BasicAuthentication;
import org.jclouds.mezeo.pcs.endpoints.Contacts;
import org.jclouds.mezeo.pcs.endpoints.Metacontainers;
import org.jclouds.mezeo.pcs.endpoints.Projects;
import org.jclouds.mezeo.pcs.endpoints.Recyclebin;
import org.jclouds.mezeo.pcs.endpoints.RootContainer;
import org.jclouds.mezeo.pcs.endpoints.Shares;
import org.jclouds.mezeo.pcs.endpoints.Tags;
import org.jclouds.mezeo.pcs.xml.CloudXlinkHandler;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.XMLResponseParser;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * Provides URIs to PCS services via their REST API.
 * <p/>
 * 
 * @author Adrian Cole
 */
@RequestFilters(BasicAuthentication.class)
public interface PCSCloudAsyncClient {

   public interface Response {
      @RootContainer
      URI getRootContainerUrl();

      @Contacts
      URI getContactsUrl();

      @Shares
      URI getSharesUrl();

      @Projects
      URI getProjectsUrl();

      @Metacontainers
      URI getMetacontainersUrl();

      @Recyclebin
      URI getRecyclebinUrl();

      @Tags
      URI getTagsUrl();
   }

   @GET
   @XMLResponseParser(CloudXlinkHandler.class)
   @Path("/v{jclouds.api-version}")
   ListenableFuture<Response> authenticate();
}
