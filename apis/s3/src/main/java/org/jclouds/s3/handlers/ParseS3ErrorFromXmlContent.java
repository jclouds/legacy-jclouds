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
package org.jclouds.s3.handlers;

import static com.google.common.collect.Lists.newArrayList;
import static org.jclouds.s3.reference.S3Constants.PROPERTY_S3_SERVICE_PATH;
import static org.jclouds.s3.reference.S3Constants.PROPERTY_S3_VIRTUAL_HOST_BUCKETS;

import java.net.URI;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.aws.domain.AWSError;
import org.jclouds.aws.handlers.ParseAWSErrorFromXmlContent;
import org.jclouds.aws.util.AWSUtils;
import org.jclouds.blobstore.ContainerNotFoundException;
import org.jclouds.blobstore.KeyNotFoundException;
import org.jclouds.http.HttpCommand;
import org.jclouds.http.HttpResponse;
import org.jclouds.rest.ResourceNotFoundException;
import org.jclouds.s3.S3ApiMetadata;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;

/**
 * @author Adrian Cole
 * 
 */
@Singleton
public class ParseS3ErrorFromXmlContent extends ParseAWSErrorFromXmlContent {

   private final String servicePath;
   private final boolean isVhostStyle;

   @Inject
   public ParseS3ErrorFromXmlContent(AWSUtils utils, @Named(PROPERTY_S3_VIRTUAL_HOST_BUCKETS) boolean isVhostStyle,
            @Named(PROPERTY_S3_SERVICE_PATH) String servicePath) {
      super(utils);
      this.servicePath = servicePath;
      this.isVhostStyle = isVhostStyle;
   }

   protected Exception refineException(HttpCommand command, HttpResponse response, Exception exception, AWSError error,
            String message) {
      switch (response.getStatusCode()) {
         case 404:
            if (!command.getCurrentRequest().getMethod().equals("DELETE")) {
               // If we have a payload/bucket/container that is not all lowercase, vhost-style URLs are not an option
               // and must be automatically converted to their path-based equivalent.  This should only be possible for
               // AWS-S3 since it is the only S3 implementation configured to allow uppercase payload/bucket/container
               // names.
               //
               // http://code.google.com/p/jclouds/issues/detail?id=992
               URI defaultS3Endpoint = URI.create(new S3ApiMetadata().getDefaultEndpoint().get());
               URI requestEndpoint = command.getCurrentRequest().getEndpoint();
               boolean wasPathBasedRequest = requestEndpoint.getHost().contains(defaultS3Endpoint.getHost()) &&
                     requestEndpoint.getHost().equals(defaultS3Endpoint.getHost());

               exception = new ResourceNotFoundException(message, exception);
               if (isVhostStyle && !wasPathBasedRequest) {
                  String container = command.getCurrentRequest().getEndpoint().getHost();
                  String key = command.getCurrentRequest().getEndpoint().getPath();
                  if (key == null || key.equals("/"))
                     exception = new ContainerNotFoundException(container, message);
                  else
                     exception = new KeyNotFoundException(container, key, message);
               } else if (command.getCurrentRequest().getEndpoint().getPath()
                        .indexOf(servicePath.equals("/") ? "/" : servicePath + "/") == 0) {
                  String path = command.getCurrentRequest().getEndpoint().getPath().substring(servicePath.length());
                  List<String> parts = newArrayList(Splitter.on('/').omitEmptyStrings().split(path));
                  if (parts.size() == 1) {
                     exception = new ContainerNotFoundException(parts.get(0), message);
                  } else if (parts.size() > 1) {
                     exception = new KeyNotFoundException(parts.remove(0), Joiner.on('/').join(parts), message);
                  }
               }
            }
            return exception;
         default:
            return super.refineException(command, response, exception, error, message);
      }
   }
}
