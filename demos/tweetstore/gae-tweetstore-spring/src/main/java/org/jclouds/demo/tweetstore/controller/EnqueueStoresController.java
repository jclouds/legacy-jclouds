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

import static com.google.appengine.api.taskqueue.TaskOptions.Builder.withUrl;
import static com.google.appengine.repackaged.com.google.common.base.Strings.nullToEmpty;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;

import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.logging.Logger;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.TaskOptions.Method;
import com.google.common.annotations.VisibleForTesting;

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
    private final Queue taskQueue;

    @Resource
    protected Logger logger = Logger.NULL;

    @Inject
    public EnqueueStoresController(Map<String, BlobStoreContext<?, ?>> contexts,
            Queue taskQueue) {
        contextNames = contexts.keySet();
        this.taskQueue = taskQueue;
    }

    @VisibleForTesting
    void enqueueStoreTweetTasks() {
        for (String contextName : contextNames) {
            logger.debug("enqueuing task to store tweets in blobstore '%s'", contextName);
            taskQueue.add(withUrl("/store/do").header("context", contextName).method(Method.GET));
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!nullToEmpty(request.getHeader("X-AppEngine-Cron")).equals("true")) {
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