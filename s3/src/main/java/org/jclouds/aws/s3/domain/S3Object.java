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
package org.jclouds.aws.s3.domain;

import org.joda.time.DateTime;
import org.jclouds.aws.s3.domain.S3Owner;

/**
 * // TODO: Adrian: Document this!
 *
 * @author Adrian Cole
 */
public class S3Object {
    public static final S3Object NOT_FOUND = new S3Object();

    private String key;
    private DateTime lastModified;
    private String eTag;
    private long size;
    private S3Owner owner;
    private String contentType;
    private String storageClass = "STANDARD"; //there is currently no other type.
    private String contentMD5;
    private String server;
    private Object content;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public DateTime getLastModified() {
        return lastModified;
    }

    public void setLastModified(DateTime lastModified) {
        this.lastModified = lastModified;
    }

    public String getETag() {
        return eTag;
    }

    public void setETag(String eTag) {
        this.eTag = eTag;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public S3Owner getOwner() {
        return owner;
    }

    public void setOwner(S3Owner owner) {
        this.owner = owner;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getStorageClass() {
        return storageClass;
    }

    public void setStorageClass(String storageClass) {
        this.storageClass = storageClass;
    }

    public String getContentMD5() {
        return contentMD5;
    }

    public void setContentMD5(String contentMD5) {
        this.contentMD5 = contentMD5;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public Object getContent() {
        return content;
    }

    public void setContent(Object content) {
        this.content = content;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof S3Object)) return false;

        S3Object s3Object = (S3Object) o;

        if (size != s3Object.size) return false;
        if (contentMD5 != null ? !contentMD5.equals(s3Object.contentMD5) : s3Object.contentMD5 != null) return false;
        if (contentType != null ? !contentType.equals(s3Object.contentType) : s3Object.contentType != null)
            return false;
        if (eTag != null ? !eTag.equals(s3Object.eTag) : s3Object.eTag != null) return false;
        if (!key.equals(s3Object.key)) return false;
        if (lastModified != null ? !lastModified.equals(s3Object.lastModified) : s3Object.lastModified != null)
            return false;
        if (owner != null ? !owner.equals(s3Object.owner) : s3Object.owner != null) return false;
        if (server != null ? !server.equals(s3Object.server) : s3Object.server != null) return false;
        if (storageClass != null ? !storageClass.equals(s3Object.storageClass) : s3Object.storageClass != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = key.hashCode();
        result = 31 * result + (lastModified != null ? lastModified.hashCode() : 0);
        result = 31 * result + (eTag != null ? eTag.hashCode() : 0);
        result = 31 * result + (int) (size ^ (size >>> 32));
        result = 31 * result + (owner != null ? owner.hashCode() : 0);
        result = 31 * result + (contentType != null ? contentType.hashCode() : 0);
        result = 31 * result + (storageClass != null ? storageClass.hashCode() : 0);
        result = 31 * result + (contentMD5 != null ? contentMD5.hashCode() : 0);
        result = 31 * result + (server != null ? server.hashCode() : 0);
        return result;
    }
}

