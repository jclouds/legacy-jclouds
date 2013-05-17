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
package org.jclouds.openstack.swift.blobstore.strategy.internal;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.inject.ImplementedBy;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.options.PutOptions;
import org.jclouds.openstack.swift.blobstore.functions.BlobToObject;

@ImplementedBy(ParallelMultipartUploadStrategy.class)
public interface AsyncMultipartUploadStrategy {
    ListenableFuture<String> execute(String container, Blob blob, PutOptions options, BlobToObject blob2Object);
}
