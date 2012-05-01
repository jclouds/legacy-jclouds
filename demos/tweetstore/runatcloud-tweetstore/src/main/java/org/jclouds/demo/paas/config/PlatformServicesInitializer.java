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
package org.jclouds.demo.paas.config;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.concurrent.TimeUnit.SECONDS;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.jclouds.concurrent.config.ExecutorServiceModule;
import org.jclouds.demo.paas.PlatformServices;
import org.jclouds.demo.paas.service.scheduler.Scheduler;
import org.jclouds.demo.paas.service.taskqueue.TaskQueue;
import org.jclouds.http.HttpCommandExecutorService;
import org.jclouds.http.config.JavaUrlHttpCommandExecutorServiceModule;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.inject.Guice;

/**
 * @author Andrew Phillips
 */
public class PlatformServicesInitializer implements ServletContextListener {
    public static final String PLATFORM_SERVICES_ATTRIBUTE_NAME = PlatformServices.class.getName();

    // keep in sync with cloudbees-web.xml
    protected static final String HOST_PARAMETER = "application.host";

    @Override
    public void contextInitialized(ServletContextEvent contextEvent) {
        ServletContext context = contextEvent.getServletContext();
        context.setAttribute(PLATFORM_SERVICES_ATTRIBUTE_NAME, createServices(context));
    }

    protected static PlatformServices createServices(ServletContext context) {
        HttpCommandExecutorService httpClient = createHttpClient(context);
        return new PlatformServices(getBaseUrl(context), new Scheduler(httpClient), 
                createTaskQueues(httpClient));
    }

    protected static HttpCommandExecutorService createHttpClient(
            final ServletContext context) {
        return Guice.createInjector(new ExecutorServiceModule(),
                new JavaUrlHttpCommandExecutorServiceModule(),
                new HttpClientModule(context))
                .getInstance(HttpCommandExecutorService.class);
    }

    protected static String getBaseUrl(ServletContext context) {
        return "http://" + checkNotNull(context.getInitParameter(HOST_PARAMETER), HOST_PARAMETER)
               + context.getContextPath();
    }

    // TODO: make the number and names of queues configurable
    protected static ImmutableMap<String, TaskQueue> createTaskQueues(HttpCommandExecutorService httpClient) {
        Builder<String, TaskQueue> taskQueues = ImmutableMap.builder();
        taskQueues.put("twitter", TaskQueue.builder(httpClient)
                                  .name("twitter").period(SECONDS.toMillis(30))
                                  .build());
        return taskQueues.build();
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        ServletContext context = servletContextEvent.getServletContext();
        context.removeAttribute(PLATFORM_SERVICES_ATTRIBUTE_NAME);
    }
}
