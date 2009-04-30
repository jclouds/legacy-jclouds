/**
 *
 * Copyright (C) 2009 Adrian Cole <adriancole@jclouds.org>
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

import java.io.File;
import java.io.InputStream;
import java.util.Map;
import java.util.Set;

import org.jclouds.aws.s3.domain.S3Bucket;
import org.jclouds.aws.s3.domain.S3Object;

public interface S3ObjectMap extends Map<String, InputStream> {

    InputStream putString(String key, String value);

    InputStream putFile(String key, File value);

    InputStream putBytes(String key, byte[] value);
    
    void putAllStrings(Map<? extends String, ? extends String> map);

    void putAllBytes(Map<? extends String, ? extends byte[]> map);

    void putAllFiles(Map<? extends String, ? extends File> map);

    InputStream put(S3Object object);
    
    void putAll(Set<S3Object> objects);

    S3Bucket getBucket();

}