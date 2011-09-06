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
package org.jclouds.scality.rs2;

import static org.jclouds.blobstore.attr.BlobScopes.CONTAINER;

import org.jclouds.blobstore.attr.BlobScope;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.SkipEncoding;
import org.jclouds.s3.S3AsyncClient;
import org.jclouds.s3.filters.RequestAuthorizeSignature;

/**
 * 
 * @author Adrian Cole
 */
@SkipEncoding('/')
@RequestFilters(RequestAuthorizeSignature.class)
@BlobScope(CONTAINER)
public interface ScalityRS2AsyncClient extends S3AsyncClient {
   public static final String VERSION = "2006-03-01";

}
