/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
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
 * ====================================================================
 */
package org.jclouds.demo.tweetstore.controller;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.concurrent.TimeUnit;

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
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

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
         to.setData(from.getText());
         to.getMetadata().getUserMetadata().put(TweetStoreConstants.SENDER_NAME,
                  from.getUser().getScreenName());
         return to;
      }
   }

   /** The serialVersionUID */
   private static final long serialVersionUID = 7215420527854203714L;

   private final Set<BlobMap> maps;
   private final TwitterClient client;

   @Resource
   protected Logger logger = Logger.NULL;

   @Inject
   StoreTweetsController(Map<String, BlobStoreContext<?>> contexts,
            @Named(TweetStoreConstants.PROPERTY_TWEETSTORE_CONTAINER) final String container,
            TwitterClient client) {
      this(Sets.newHashSet(Iterables.transform(contexts.values(),
               new Function<BlobStoreContext<?>, BlobMap>() {
                  public BlobMap apply(BlobStoreContext<?> from) {
                     return from.createBlobMap(container);
                  }
               })), client);
   }

   @VisibleForTesting
   StoreTweetsController(Set<BlobMap> maps, TwitterClient client) {
      this.maps = maps;
      this.client = client;
   }

   @VisibleForTesting
   void addMyTweets(SortedSet<Status> allAboutMe) {
      for (BlobMap map : maps) {
         for (Status status : allAboutMe) {
            map.put(status.getId() + "", new StatusToBlob(map).apply(status));
         }
      }
   }

   @Override
   protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
      if (request.getHeader("X-AppEngine-Cron") != null
               && request.getHeader("X-AppEngine-Cron").equals("true")) {
         try {
            logger.info("retrieving tweets");
            addMyTweets(client.getMyMentions().get(1, TimeUnit.SECONDS));
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