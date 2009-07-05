/**
 *
 * Copyright (C) 2009 Global Cloud Specialists, Inc. <info@globalcloudspecialists.com>
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
package org.jclouds.aws.s3;

import org.jclouds.cloud.CloudContext;

/**
 * Represents an authenticated context to S3.
 * 
 * <h2>Note</h2> Please issue {@link #close()} when you are finished with this context in order to
 * release resources.
 * 
 * 
 * @see S3Connection
 * @see S3InputStreamMap
 * @see S3ObjectMap
 * @author Adrian Cole
 * 
 */
public interface S3Context extends CloudContext<S3Connection> {

   /**
    * Creates a <code>Map<String,InputStream></code> view of the specified bucket.
    * 
    * @param bucket
    */
   S3InputStreamMap createInputStreamMap(String bucket);

   /**
    * Creates a <code>Map<String,S3Object></code> view of the specified bucket.
    * 
    * @param bucket
    */
   S3ObjectMap createS3ObjectMap(String bucket);

}