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
package org.jclouds.aws.s3.domain;

import static com.google.common.base.Preconditions.checkNotNull;
import org.joda.time.DateTime;

import java.util.SortedSet;
import java.util.TreeSet;

/**
 * A container that provides namespace, access control and aggregation of
 * {@link S3Object}s
 * <p/>
 * <p/>
 * Every object stored in Amazon S3 is contained in a bucket. Buckets partition
 * the namespace of objects stored in Amazon S3 at the top level. Within a
 * bucket, you can use any names for your objects, but bucket names must be
 * unique across all of Amazon S3.
 * <p/>
 * Buckets are similar to Internet domain names. Just as Amazon is the only
 * owner of the domain name Amazon.com, only one person or organization can own
 * a bucket within Amazon S3. Once you create a uniquely named bucket in Amazon
 * S3, you can organize and name the objects within the bucket in any way you
 * like and the bucket will remain yours for as long as you like and as long as
 * you have the Amazon S3 account.
 * <p/>
 * The similarities between buckets and domain names is not a coincidence—there
 * is a direct mapping between Amazon S3 buckets and subdomains of
 * s3.amazonaws.com. Objects stored in Amazon S3 are addressable using the REST
 * API under the domain bucketname.s3.amazonaws.com. For example, if the object
 * homepage.html?is stored in the Amazon S3 bucket mybucket its address would be
 * http://mybucket.s3.amazonaws.com/homepage.html?
 *
 * @author Adrian Cole
 * @see <a
 *      href="http://docs.amazonwebservices.com/AmazonS3/2006-03-01/index.html"
 *      />
 */
public class S3Bucket {
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("S3Bucket");
        sb.append("{metadata=").append(metadata);
        sb.append(", isTruncated=").append(isTruncated);
        sb.append('}');
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof S3Bucket))
            return false;

        S3Bucket s3Bucket = (S3Bucket) o;

        if (isTruncated != s3Bucket.isTruncated)
            return false;
        if (!metadata.equals(s3Bucket.metadata))
            return false;
        if (objects != null ? !objects.equals(s3Bucket.objects)
                : s3Bucket.objects != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = objects != null ? objects.hashCode() : 0;
        result = 31 * result + metadata.hashCode();
        result = 31 * result + (isTruncated ? 1 : 0);
        return result;
    }

    /**
     * System metadata of the S3Bucket
     *
     * @author Adrian Cole
     */
    public static class Metadata {
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder();
            sb.append("Metadata");
            sb.append("{name='").append(name).append('\'');
            sb.append(", creationDate=").append(creationDate);
            sb.append(", canonicalUser=").append(canonicalUser);
            sb.append('}');
            return sb.toString();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (!(o instanceof Metadata))
                return false;

            Metadata metadata = (Metadata) o;
            if (canonicalUser != null ? !canonicalUser
                    .equals(metadata.canonicalUser)
                    : metadata.canonicalUser != null)
                return false;
            if (!name.equals(metadata.name))
                return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = name.hashCode();
            result = 31 * result
                    + (canonicalUser != null ? canonicalUser.hashCode() : 0);
            return result;
        }

        /**
         * Location constraint of the bucket.
         *
         * @author Adrian Cole
         * @see <a href=
         *      "http://docs.amazonwebservices.com/AmazonS3/latest/RESTBucketLocationGET.html"
         *      />
         */
        public static enum LocationConstraint {
            EU
        }

        private final String name;
        private DateTime creationDate;
        private CanonicalUser canonicalUser;

        /**
         * @see #getName()
         */
        public Metadata(String name) {
            this.name = checkNotNull(name, "name");
        }

        /**
         * To comply with Amazon S3 requirements, bucket names must:
         * <p/>
         * Contain lowercase letters, numbers, periods (.), underscores (_), and
         * dashes (-)
         * <p/>
         * Start with a number or letter
         * <p/>
         * Be between 3 and 255 characters long
         * <p/>
         * Not be in an IP address style (e.g., "192.168.5.4")
         */
        public String getName() {
            return name;
        }

        public DateTime getCreationDate() {
            return creationDate;
        }

        public void setCreationDate(DateTime creationDate) {
            this.creationDate = creationDate;
        }

        /**
         * Every bucket and object in Amazon S3 has an owner, the user that
         * created the bucket or object. The owner of a bucket or object cannot
         * be changed. However, if the object is overwritten by another user
         * (deleted and rewritten), the new object will have a new owner.
         */
        public CanonicalUser getOwner() {
            return canonicalUser;
        }

        public void setOwner(CanonicalUser canonicalUser) {
            this.canonicalUser = canonicalUser;
        }

    }

    public static final S3Bucket NOT_FOUND = new S3Bucket("NOT_FOUND");

    private SortedSet<S3Object.Metadata> objects = new TreeSet<S3Object.Metadata>();
    private SortedSet<String> commonPrefixes = new TreeSet<String>();
    private String prefix;
    private String marker;
    private String delimiter;
    private long maxKeys;
    private final Metadata metadata;

    private boolean isTruncated;

    public S3Bucket(String name) {
        this.metadata = new Metadata(name);
    }

    public String getName() {
        return this.metadata.getName();
    }

    public S3Bucket(Metadata metadata) {
        this.metadata = checkNotNull(metadata, "metadata");
    }

    /**
     * @see org.jclouds.aws.s3.S3Connection#listBucket(String)
     */
    public SortedSet<S3Object.Metadata> getContents() {
        return objects;
    }

    public void setContents(SortedSet<S3Object.Metadata> objects) {
        this.objects = objects;
    }

    /**
     * @return true, if the list contains all objects.
     */
    public boolean isTruncated() {
        return isTruncated;
    }

    public void setTruncated(boolean truncated) {
        isTruncated = truncated;
    }

    public Metadata getMetadata() {
        return metadata;
    }

    public void setCommonPrefixes(SortedSet<String> commonPrefixes) {
        this.commonPrefixes = commonPrefixes;
    }

    /**
     * Example:
     * <p/>
     * if the following keys are in the bucket
     * <p/>
     * a/1/a<br/>
     * a/1/b<br/>
     * a/2/a<br/>
     * a/2/b<br/>
     * <p/>
     * and prefix is set to <code>a/</code> and delimiter is set to
     * <code>/</code> then commonprefixes would return 1,2
     *
     * @see org.jclouds.aws.s3.commands.options.ListBucketOptions#getPrefix()
     */
    public SortedSet<String> getCommonPrefixes() {
        return commonPrefixes;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    /**
     * return keys that start with this.
     *
     * @see org.jclouds.aws.s3.commands.options.ListBucketOptions#getPrefix()
     */
    public String getPrefix() {
        return prefix;
    }

    public void setMaxKeys(long maxKeys) {
        this.maxKeys = maxKeys;
    }

    /**
     * @return maximum results of the bucket.
     * @see org.jclouds.aws.s3.commands.options.ListBucketOptions#getMaxKeys()
     */
    public long getMaxKeys() {
        return maxKeys;
    }

    public void setMarker(String marker) {
        this.marker = marker;
    }

    /**
     * when set, bucket contains results whose keys are lexigraphically after
     * marker.
     *
     * @see org.jclouds.aws.s3.commands.options.ListBucketOptions#getMarker()
     */
    public String getMarker() {
        return marker;
    }

    public void setDelimiter(String delimiter) {
        this.delimiter = delimiter;
    }

    /**
     * when set, bucket results will not contain keys that have text following
     * this delimiter.
     * <p/>
     * note that delimiter has no effect on prefix. prefix can contain the
     * delimiter many times, or not at all. delimiter only restricts after the
     * prefix.
     *
     * @see org.jclouds.aws.s3.commands.options.ListBucketOptions#getMarker()
     */
    public String getDelimiter() {
	return delimiter;
    }

}
