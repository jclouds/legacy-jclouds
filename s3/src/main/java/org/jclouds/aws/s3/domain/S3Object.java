/**
 *
 * Copyright (C) 2009 Adrian Cole <adrian@jclouds.org>
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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import org.jclouds.aws.s3.commands.options.GetObjectOptions;
import org.jclouds.aws.s3.util.S3Utils;
import org.jclouds.aws.s3.util.S3Utils.Md5InputStreamResult;
import org.jclouds.http.ContentTypes;
import org.joda.time.DateTime;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

/**
 * Amazon S3 is designed to store objects. Objects are stored in
 * {@link S3Bucket buckets} and consist of a {@link S3Object#getValue() value},
 * a {@link S3Object#getKey key}, {@link S3Object.Metadata#getUserMetadata()
 * metadata}, and an access control policy.
 * 
 * @see <a href="http://docs.amazonwebservices.com/AmazonS3/2006-03-01/index.html?UsingObjects.html"
 *      />
 * @author Adrian Cole
 */
public class S3Object {
    public static final S3Object NOT_FOUND = new S3Object(Metadata.NOT_FOUND);

    private Object data;
    private Metadata metadata;
    private long contentLength = -1;
    private String contentRange;

    public S3Object(String key) {
	this(new Metadata(key));
    }

    public S3Object(Metadata metadata) {
	this.metadata = metadata;
    }

    public S3Object(Metadata metadata, Object data) {
	this(metadata);
	setData(data);
    }

    public S3Object(String key, Object data) {
	this(key);
	setData(data);
    }

    /**
     * System and user Metadata for the {@link S3Object}.
     * 
     * @see <a href=
     *      "http://docs.amazonwebservices.com/AmazonS3/2006-03-01/UsingMetadata.html"
     *      />
     * @author Adrian Cole
     * 
     */
    public static class Metadata {
	public static final Metadata NOT_FOUND = new Metadata("NOT_FOUND");

	// parsed during list, head, or get
	private final String key;
	private byte[] md5;
	private volatile long size = -1;

	// only parsed during head or get
	private Multimap<String, String> allHeaders = HashMultimap.create();
	private Multimap<String, String> userMetadata = HashMultimap.create();
	private DateTime lastModified;
	private String dataType = ContentTypes.BINARY;
	private String cacheControl;
	private String dataDisposition;
	private String dataEncoding;

	// only parsed on list
	private CanonicalUser owner = null;
	private String storageClass = null;

	/**
	 * @see #getKey()
	 * @param key
	 */
	public Metadata(String key) {
	    checkNotNull(key, "key");
	    checkArgument(!key.startsWith("/"), "keys cannot start with /");
	    this.key = key;
	}

	@Override
	public String toString() {
	    final StringBuilder sb = new StringBuilder();
	    sb.append("Metadata");
	    sb.append("{key='").append(key).append('\'');
	    sb.append(", lastModified=").append(lastModified);
	    sb.append(", md5=").append(
		    getMd5() == null ? "null" : Arrays.asList(getMd5())
			    .toString());
	    sb.append(", size=").append(size);
	    sb.append(", dataType='").append(dataType).append('\'');
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

	    if (size != metadata.size)
		return false;
	    if (dataType != null ? !dataType.equals(metadata.dataType)
		    : metadata.dataType != null)
		return false;
	    if (!key.equals(metadata.key))
		return false;
	    if (lastModified != null ? !lastModified
		    .equals(metadata.lastModified)
		    : metadata.lastModified != null)
		return false;
	    if (!Arrays.equals(getMd5(), metadata.getMd5()))
		return false;
	    return true;
	}

	@Override
	public int hashCode() {
	    int result = key.hashCode();
	    result = 31 * result
		    + (lastModified != null ? lastModified.hashCode() : 0);
	    result = 31 * result
		    + (getMd5() != null ? Arrays.hashCode(getMd5()) : 0);
	    result = 31 * result + (int) (size ^ (size >>> 32));
	    result = 31 * result + (dataType != null ? dataType.hashCode() : 0);
	    return result;
	}

	/**
	 * The key is the handle that you assign to an object that allows you
	 * retrieve it later. A key is a sequence of Unicode characters whose
	 * UTF-8 encoding is at most 1024 bytes long. Each object in a bucket
	 * must have a unique key.
	 * 
	 * @see <a href=
	 *      "http://docs.amazonwebservices.com/AmazonS3/2006-03-01/UsingKeys.html"
	 *      />
	 */
	public String getKey() {
	    return key;
	}

	public DateTime getLastModified() {
	    return lastModified;
	}

	public void setLastModified(DateTime lastModified) {
	    this.lastModified = lastModified;
	}

	/**
	 * The size of the object, in bytes.
	 * 
	 * @see <a href=
	 *      "http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html?sec14.13."
	 *      />
	 * 
	 * @return
	 */
	public long getSize() {
	    return size;
	}

	public void setSize(long size) {
	    this.size = size;
	}

	/**
	 * A standard MIME type describing the format of the contents. If none
	 * is provided, the default is binary/octet-stream.
	 * 
	 * @see <a href=
	 *      "http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html?sec14.17."
	 *      />
	 * 
	 * @return
	 */
	public String getContentType() {
	    return dataType;
	}

	public void setContentType(String dataType) {
	    this.dataType = dataType;
	}

	public void setMd5(byte[] md5) {
	    this.md5 = md5;
	}

	/**
	 * @return the md5 value stored in the Etag header returned by S3.
	 */
	public byte[] getMd5() {
	    return md5;
	}

	public void setUserMetadata(Multimap<String, String> userMetadata) {
	    this.userMetadata = userMetadata;
	}

	/**
	 * 
	 * Any header starting with <code>x-amz-meta-</code> is considered user
	 * metadata. It will be stored with the object and returned when you
	 * retrieve the object. The total size of the HTTP request, not
	 * including the body, must be less than 8 KB.
	 * 
	 */
	public Multimap<String, String> getUserMetadata() {
	    return userMetadata;
	}

	public void setOwner(CanonicalUser owner) {
	    this.owner = owner;
	}

	/**
	 * Every bucket and object in Amazon S3 has an owner, the user that
	 * created the bucket or object. The owner of a bucket or object cannot
	 * be changed. However, if the object is overwritten by another user
	 * (deleted and rewritten), the new object will have a new owner.
	 */
	public CanonicalUser getOwner() {
	    return owner;
	}

	public void setStorageClass(String storageClass) {
	    this.storageClass = storageClass;
	}

	/**
	 * Currently defaults to 'STANDARD' and not used.
	 */
	public String getStorageClass() {
	    return storageClass;
	}

	public void setCacheControl(String cacheControl) {
	    this.cacheControl = cacheControl;
	}

	/**
	 * Can be used to specify caching behavior along the request/reply
	 * chain.
	 * 
	 * @link http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html?sec14.9.
	 */
	public String getCacheControl() {
	    return cacheControl;
	}

	public void setContentDisposition(String dataDisposition) {
	    this.dataDisposition = dataDisposition;
	}

	/**
	 * Specifies presentational information for the object.
	 * 
	 * @see <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec19.html?sec19.5.1."/>
	 */
	public String getContentDisposition() {
	    return dataDisposition;
	}

	public void setContentEncoding(String dataEncoding) {
	    this.dataEncoding = dataEncoding;
	}

	/**
	 * Specifies what content encodings have been applied to the object and
	 * thus what decoding mechanisms must be applied in order to obtain the
	 * media-type referenced by the Content-Type header field.
	 * 
	 * @see <a href=
	 *      "http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html?sec14.11"
	 *      />
	 */
	public String getContentEncoding() {
	    return dataEncoding;
	}

	public void setAllHeaders(Multimap<String, String> allHeaders) {
	    this.allHeaders = allHeaders;
	}

	/**
	 * 
	 * @return all http response headers associated with this S3Object
	 */
	public Multimap<String, String> getAllHeaders() {
	    return allHeaders;
	}
    }

    /**
     * @see Metadata#getKey()
     */
    public String getKey() {
	return metadata.getKey();
    }

    /**
     * Sets payload for the request or the content from the response. If size
     * isn't set, this will attempt to discover it.
     * 
     * @param data
     *            typically InputStream for downloads, or File, byte [], String,
     *            or InputStream for uploads.
     */
    public void setData(Object data) {
	this.data = checkNotNull(data, "data");
	if (getMetadata().getSize() == -1)
	    this.getMetadata().setSize(S3Utils.calculateSize(data));
    }

    /**
     * generate an MD5 Hash for the current data.
     * 
     * <h2>Note</h2>
     * <p/>
     * If this is an InputStream, it will be converted to a byte array first.
     * 
     * @throws IOException
     *             if there is a problem generating the hash.
     */
    public void generateMd5() throws IOException {
	checkState(data != null, "data");
	if (data instanceof InputStream) {
	    Md5InputStreamResult result = S3Utils
		    .generateMd5Result((InputStream) data);
	    getMetadata().setSize(result.length);
	    getMetadata().setMd5(result.md5);
	    setData(result.data);
	} else {
	    getMetadata().setMd5(S3Utils.md5(data));
	}
    }

    /**
     * 
     * @return InputStream, if downloading, or whatever was set during
     *         {@link #setData(Object)}
     */
    public Object getData() {
	return data;
    }

    public void setMetadata(Metadata metadata) {
	this.metadata = metadata;
    }

    /**
     * 
     * @return System and User metadata relevant to this object.
     */
    public Metadata getMetadata() {
	return metadata;
    }

    @Override
    public String toString() {
	final StringBuilder sb = new StringBuilder();
	sb.append("S3Object");
	sb.append("{metadata=").append(metadata);
	sb.append('}');
	return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
	if (this == o)
	    return true;
	if (!(o instanceof S3Object))
	    return false;

	S3Object s3Object = (S3Object) o;

	if (data != null ? !data.equals(s3Object.data) : s3Object.data != null)
	    return false;
	if (!metadata.equals(s3Object.metadata))
	    return false;

	return true;
    }

    @Override
    public int hashCode() {
	int result = data != null ? data.hashCode() : 0;
	result = 31 * result + metadata.hashCode();
	return result;
    }

    public void setContentLength(long contentLength) {
	this.contentLength = contentLength;
    }

    /**
     * Returns the total size of the downloaded object, or the chunk that's
     * available.
     * <p/>
     * Chunking is only used when
     * {@link org.jclouds.aws.s3.S3Connection#getObject(String, String, org.jclouds.aws.s3.commands.options.GetObjectOptions) }
     * is called with options like tail, range, or startAt.
     * 
     * @see org.jclouds.http.HttpHeaders#CONTENT_LENGTH
     * @see GetObjectOptions
     * @return the length in bytes that can be be obtained from
     *         {@link #getData()}
     * 
     */
    public long getContentLength() {
	return contentLength;
    }

    public void setContentRange(String contentRange) {
	this.contentRange = contentRange;
    }

    /**
     * If this is not-null, {@link #getContentLength() } will the size of chunk
     * of the S3Object available via {@link #getData()}
     * 
     * @see org.jclouds.http.HttpHeaders#CONTENT_RANGE
     * @see GetObjectOptions
     */
    public String getContentRange() {
	return contentRange;
    }

}
