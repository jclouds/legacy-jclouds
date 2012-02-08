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
package org.jclouds.demo.paas.service.scheduler;

import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;

import javax.servlet.ServletContext;

import org.jclouds.demo.paas.PlatformServices;
import org.jclouds.demo.paas.RunnableHttpRequest;
import org.jclouds.http.HttpRequest;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerException;

/**
 * @author Andrew Phillips
 */
public class HttpRequestJob implements Job {
    protected static final String URL_ATTRIBUTE_NAME = "url";

    // keep in sync with "quartz:scheduler-context-servlet-context-key" param in web.xml
    protected static final String SERVLET_CONTEXT_KEY = "servlet-context";

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        PlatformServices platform = JobContexts.getPlatform(context);
        RunnableHttpRequest request = platform.getScheduler().getHttpRequestFactory().create(
                HttpRequest.builder()
                .endpoint(JobContexts.getTargetUrl(platform.getBaseUrl(), context))
                .method("GET").build());
        request.run();
    }

    private static class JobContexts {
        private static URI getTargetUrl(String baseUrl, JobExecutionContext context) {
            return URI.create(baseUrl + (String) checkNotNull(
                    context.getMergedJobDataMap().get(URL_ATTRIBUTE_NAME), URL_ATTRIBUTE_NAME));
        }
        
        private static PlatformServices getPlatform(JobExecutionContext jobContext) throws JobExecutionException {
            try {
                return PlatformServices.get((ServletContext) checkNotNull(
                        jobContext.getScheduler().getContext().get(SERVLET_CONTEXT_KEY), SERVLET_CONTEXT_KEY));
            } catch (SchedulerException exception) {
                throw new JobExecutionException("Unable to get platform services from the job execution context", exception);
            }
        }
    }
}
