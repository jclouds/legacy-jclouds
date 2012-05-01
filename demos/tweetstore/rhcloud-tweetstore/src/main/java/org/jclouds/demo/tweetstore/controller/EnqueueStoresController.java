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

import static com.google.common.base.Strings.nullToEmpty;

import java.io.IOException;
import java.net.URI;
import java.util.Map;
import java.util.Set;

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
import org.jclouds.demo.paas.reference.PaasConstants;
import org.jclouds.demo.paas.service.taskqueue.TaskQueue;
import org.jclouds.http.HttpRequest;
import org.jclouds.logging.Logger;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableMultimap;

/**
 * Adds tasks to retrieve and store tweets in all registered contexts to an async
 * task queue. 
 * 
 * @author Andrew Phillips
 * @see StoreTweetsController
 */
@Singleton
public class EnqueueStoresController extends HttpServlet {
    /** The serialVersionUID */
    private static final long serialVersionUID = 7215420527854203714L;

    private final Set<String> contextNames;
    private final TaskQueue taskQueue;
    private final String baseUrl;

    @Resource
    protected Logger logger = Logger.NULL;

    @Inject
    public EnqueueStoresController(Map<String, BlobStoreContext> contexts, TaskQueue taskQueue,
            @Named(PaasConstants.PROPERTY_PLATFORM_BASE_URL) String baseUrl) {
        contextNames = contexts.keySet();
        this.taskQueue = taskQueue;
        this.baseUrl = baseUrl;
    }

    @VisibleForTesting
    void enqueueStoreTweetTasks() {
        for (String contextName : contextNames) {
            logger.debug("enqueuing task to store tweets in blobstore '%s'", contextName);
            taskQueue.add(taskQueue.getHttpRequestFactory().create(HttpRequest.builder()
                    .endpoint(URI.create(baseUrl + "/store/do"))
                    .headers(ImmutableMultimap.of("context", contextName))
                    .method("GET").build()));
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!nullToEmpty(request.getHeader("X-Rhcloud-Cron")).equals("true")) {
            response.sendError(401);
        }

        try {
            enqueueStoreTweetTasks();
            response.setContentType(MediaType.TEXT_PLAIN);
            response.getWriter().println("Done!");
        } catch (Exception e) {
            logger.error(e, "Error storing tweets");
            throw new ServletException(e);
        }
    }
}