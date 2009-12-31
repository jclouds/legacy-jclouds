/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.demo.tweetstore.controller;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.util.Map;
import java.util.SortedSet;

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
import org.jclouds.twitter.TwitterClient;
import org.jclouds.twitter.domain.Status;

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
         Blob to = map.newBlob();
         to.getMetadata().setContentType(MediaType.TEXT_PLAIN);
         to.getMetadata().setName(from.getId() + "");
         to.setPayload(from.getText());
         to.getMetadata().getUserMetadata().put(TweetStoreConstants.SENDER_NAME,
                  from.getUser().getScreenName());
         return to;
      }
   }

   /** The serialVersionUID */
   private static final long serialVersionUID = 7215420527854203714L;

   private final Map<String, BlobStoreContext<?, ?>> contexts;
   private final TwitterClient client;
   private final String container;

   @Resource
   protected Logger logger = Logger.NULL;

   @Inject
   @VisibleForTesting
   StoreTweetsController(Map<String, BlobStoreContext<?, ?>> contexts,
            @Named(TweetStoreConstants.PROPERTY_TWEETSTORE_CONTAINER) final String container,
            TwitterClient client) {
      this.container = container;
      this.contexts = contexts;
      this.client = client;
   }

   @VisibleForTesting
   void addMyTweets(String contextName, SortedSet<Status> allAboutMe) {
      BlobStoreContext<?, ?> context = checkNotNull(contexts.get(contextName), "no context for "
               + contextName + " in " + contexts.keySet());
      BlobMap map = context.createBlobMap(container);
      for (Status status : allAboutMe) {
         try {
            map.put(status.getId() + "", new StatusToBlob(map).apply(status));
         } catch (Exception e) {
            logger.error(e, "Error storing tweet %s on map %s/%s", status.getId(), context,
                     container);
         }
      }
   }

   @Override
   protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
      if (request.getHeader("X-AppEngine-QueueName") != null
               && request.getHeader("X-AppEngine-QueueName").equals("twitter")) {
         try {
            String contextName = checkNotNull(request.getHeader("context"),
                     "missing header context");
            logger.info("retrieving tweets");
            addMyTweets(contextName, client.getMyMentions());
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