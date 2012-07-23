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
package org.jclouds.aws.s3;

import static org.jclouds.Constants.PROPERTY_IO_WORKER_THREADS;
import static org.jclouds.Constants.PROPERTY_MAX_CONNECTIONS_PER_CONTEXT;
import static org.jclouds.Constants.PROPERTY_MAX_CONNECTIONS_PER_HOST;
import static org.jclouds.Constants.PROPERTY_USER_THREADS;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.Future;

import org.jclouds.s3.S3AsyncClient;
import org.jclouds.s3.domain.S3Object;

import com.google.common.base.Throwables;

/**
 * // TODO: Adrian: Document this!
 * 
 * @author Adrian Cole
 */
public abstract class BaseJCloudsPerformanceLiveTest extends BasePerformanceLiveTest {

   protected void printPropertiesOfContext(Properties overrides, String contextName) {
      System.out.printf("%s: loopCount(%s), perContext(%s), perHost(%s), ioWorkers(%s), userThreads(%s)%n", contextName,
            loopCount, overrides.getProperty(PROPERTY_MAX_CONNECTIONS_PER_CONTEXT), overrides
                  .getProperty(PROPERTY_MAX_CONNECTIONS_PER_HOST), overrides.getProperty(PROPERTY_IO_WORKER_THREADS),
            overrides.getProperty(PROPERTY_USER_THREADS));
   }

   @Override
   protected Future<?> putByteArray(String bucket, String key, byte[] data, String contentType) {
      S3Object object = newObject(key);
      object.setPayload(data);
      object.getPayload().getContentMetadata().setContentType(contentType);
      return getApi().putObject(bucket, object);
   }

   public abstract S3AsyncClient getApi();

   @Override
   protected Future<?> putFile(String bucket, String key, File data, String contentType) {
      S3Object object = newObject(key);
      object.setPayload(data);
      object.getPayload().getContentMetadata().setContentType(contentType);
      return getApi().putObject(bucket, object);
   }

   private S3Object newObject(String key) {
      S3Object object = getApi().newS3Object();
      object.getMetadata().setKey(key);
      return object;
   }

   @Override
   protected Future<?> putInputStream(String bucket, String key, InputStream data, String contentType) {
      S3Object object = newObject(key);
      object.setPayload(data);
      try {
         object.getPayload().getContentMetadata().setContentLength(Long.valueOf(data.available()));
      } catch (IOException e) {
         Throwables.propagate(e);
      }
      object.getPayload().getContentMetadata().setContentType(contentType);
      return getApi().putObject(bucket, object);
   }

   @Override
   protected Future<?> putString(String bucket, String key, String data, String contentType) {
      S3Object object = newObject(key);
      object.setPayload(data);
      object.getPayload().getContentMetadata().setContentType(contentType);
      return getApi().putObject(bucket, object);
   }
}
