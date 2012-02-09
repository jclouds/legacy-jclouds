/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
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
 */
package org.jclouds.demo.tweetstore.controller;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.nullToEmpty;
import static org.jclouds.demo.paas.RunnableHttpRequest.PLATFORM_REQUEST_ORIGINATOR_HEADER;

import java.io.IOException;
import java.util.Map;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;

import org.jclouds.blobstore.BlobMap;
import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.demo.tweetstore.reference.TweetStoreConstants;
import org.jclouds.logging.Logger;
import org.jclouds.rest.AuthorizationException;

import twitter4j.Status;
import twitter4j.Twitter;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;

/**
 * Grab tweets related to me and store them into blobstores
 * 
 * @author Adrian Cole
 */
@Singleton
public class StoreTweetsController extends HttpServlet {
    
   private static final class StatusToBlob implements Function<Status, Blob> {
      private final BlobMap map;

      private StatusToBlob(BlobMap map) {
         this.map = map;
      }

      public Blob apply(Status from) {
         Blob to = map.blobBuilder().name(from.getId() + "").build();
         to.setPayload(from.getText());
         to.getPayload().getContentMetadata().setContentType(MediaType.TEXT_PLAIN);
         to.getMetadata().getUserMetadata().put(TweetStoreConstants.SENDER_NAME, from.getUser().getScreenName());
         return to;
      }
   }

   /** The serialVersionUID */
   private static final long serialVersionUID = 7215420527854203714L;

   private final Map<String, BlobStoreContext> contexts;
   private final Twitter client;
   private final String container;

   @Resource
   protected Logger logger = Logger.NULL;

   @Inject
   @VisibleForTesting
   public StoreTweetsController(Map<String, BlobStoreContext> contexts,
         @Named(TweetStoreConstants.PROPERTY_TWEETSTORE_CONTAINER) String container, Twitter client) {
      this.container = container;
      this.contexts = contexts;
      this.client = client;
   }

   @VisibleForTesting
   public void addMyTweets(String contextName, Iterable<Status> responseList) {
      BlobStoreContext context = checkNotNull(contexts.get(contextName), "no context for " + contextName + " in "
            + contexts.keySet());
      BlobMap map = context.createBlobMap(container);
      for (Status status : responseList) {
         Blob blob = null;
         try {
            blob = new StatusToBlob(map).apply(status);
            map.put(status.getId() + "", blob);
         } catch (AuthorizationException e) {
            throw e;
         } catch (Exception e) {
            logger.error(e, "Error storing tweet %s (blob[%s]) on map %s/%s", status.getId(), blob, context, container);
         }
      }
   }

   @Override
   protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
       if (nullToEmpty(request.getHeader(PLATFORM_REQUEST_ORIGINATOR_HEADER)).equals("taskqueue-twitter")) {
         try {
            String contextName = checkNotNull(request.getHeader("context"), "missing header context");
            logger.info("retrieving tweets");
            addMyTweets(contextName, client.getMentions());
            logger.debug("done storing tweets");
            response.setContentType(MediaType.TEXT_PLAIN);
            response.getWriter().println("Done!");
         } catch (Exception e) {
            logger.error(e, "Error storing tweets");
            throw new ServletException(e);
         }
      } else {
         response.sendError(401);
      }
   }
}