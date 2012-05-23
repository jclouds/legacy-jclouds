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

import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.demo.tweetstore.reference.TweetStoreConstants;
import org.jclouds.logging.Logger;

import com.google.common.annotations.VisibleForTesting;

/**
 * Grab tweets related to me and store them into blobstores
 * 
 * @author Adrian Cole
 */
@Singleton
public class ClearTweetsController extends HttpServlet {
   /** The serialVersionUID */
   private static final long serialVersionUID = 7215420527854203714L;

   private final Map<String, BlobStoreContext> contexts;
   private final String container;

   @Resource
   protected Logger logger = Logger.NULL;

   @Inject
   @VisibleForTesting
   public ClearTweetsController(Map<String, BlobStoreContext> contexts,
         @Named(TweetStoreConstants.PROPERTY_TWEETSTORE_CONTAINER) String container) {
      this.container = container;
      this.contexts = contexts;
   }

   @VisibleForTesting
   public void clearContainer(String contextName) {
        BlobStoreContext context = checkNotNull(contexts.get(contextName),
                "no context for %s in %s", contextName, contexts.keySet());
        try {
            context.getBlobStore().clearContainer(container);
        } catch (Exception e) {
            logger.error(e, "Error clearing tweets in %s/%s", container, context);
        }
   }

   @Override
   protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
       if (nullToEmpty(request.getHeader("X-Originator")).equals("admin")) {
         try {
            String contextName = checkNotNull(request.getHeader("context"), "missing header context");
            logger.info("clearing tweets in %s/%s", container, contextName);
            clearContainer(contextName);
            logger.debug("done clearing tweets");
            response.setContentType(MediaType.TEXT_PLAIN);
            response.getWriter().println("Done!");
         } catch (Exception e) {
            logger.error(e, "Error clearing tweets");
            throw new ServletException(e);
         }
      } else {
         response.sendError(401);
      }
   }
}