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
package org.jclouds.azureblob.domain;

import org.jclouds.io.PayloadEnclosing;
import org.jclouds.javax.annotation.Nullable;

import com.google.common.collect.Multimap;


/**
 * Amazon S3 is designed to store objects. Objects are stored in buckets and consist of a
 * {@link ObjectPropertiesBlob#getInput() value}, a {@link ObjectProperties#getKey key},
 * {@link ObjectProperties#getUserProperties() metadata}, and an access control policy.
 *
 * @author Adrian Cole
 * @see <a href="http://docs.amazonwebservices.com/AmazonS3/2006-03-01/index.html?UsingObjects.html"
 *      />
 */
public interface AzureBlob extends PayloadEnclosing, Comparable<AzureBlob> {


   public interface Factory {
      AzureBlob create(@Nullable MutableBlobProperties properties);
   }

   /**
    * @return System and User metadata relevant to this object.
    */
   MutableBlobProperties getProperties();

   Multimap<String, String> getAllHeaders();

   void setAllHeaders(Multimap<String, String> allHeaders);
}
