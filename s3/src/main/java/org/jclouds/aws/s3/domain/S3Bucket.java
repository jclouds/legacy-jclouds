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
import org.jclouds.aws.s3.domain.S3Object;

import java.util.HashSet;
import java.util.Set;

/**
 * // TODO: Adrian: Document this!
 *
 * @author Adrian Cole
 */
public class S3Bucket {
    public static final S3Bucket NOT_FOUND = new S3Bucket();

    private String name;
    private DateTime creationDate;
    private S3Owner canonicalUser;
    private Set<S3Object> objects = new HashSet<S3Object>();
    boolean isComplete;
    boolean hasData;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name.toLowerCase();
    }

    public DateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(DateTime creationDate) {
        this.creationDate = creationDate;
    }

    public S3Owner getCanonicalUser() {
        return canonicalUser;
    }

    public void setCanonicalUser(S3Owner canonicalUser) {
        this.canonicalUser = canonicalUser;
    }

    public Set<S3Object> getContents() {
        return objects;
    }

    public void setContents(Set<S3Object> objects) {
        this.objects = objects;
    }


    public boolean isComplete() {
        return isComplete;
    }

    public void setComplete(boolean complete) {
        isComplete = complete;
    }

    public boolean isHasData() {
        return hasData;
    }

    public void setHasData(boolean hasData) {
        this.hasData = hasData;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof S3Bucket)) return false;

        S3Bucket s3Bucket = (S3Bucket) o;

        if (hasData != s3Bucket.hasData) return false;
        if (isComplete != s3Bucket.isComplete) return false;
        if (canonicalUser != null ? !canonicalUser.equals(s3Bucket.canonicalUser) : s3Bucket.canonicalUser != null)
            return false;
        if (objects != null ? !objects.equals(s3Bucket.objects) : s3Bucket.objects != null) return false;
        if (creationDate != null ? !creationDate.equals(s3Bucket.creationDate) : s3Bucket.creationDate != null)
            return false;
        if (!name.equals(s3Bucket.name)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + (creationDate != null ? creationDate.hashCode() : 0);
        result = 31 * result + (canonicalUser != null ? canonicalUser.hashCode() : 0);
        result = 31 * result + (objects != null ? objects.hashCode() : 0);
        result = 31 * result + (isComplete ? 1 : 0);
        result = 31 * result + (hasData ? 1 : 0);
        return result;
    }
}
