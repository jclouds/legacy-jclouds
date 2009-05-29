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
package org.jclouds.aws.s3.jets3t;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

import org.jclouds.aws.s3.commands.options.GetObjectOptions;
import org.jclouds.aws.s3.commands.options.ListBucketOptions;
import org.jclouds.aws.s3.commands.options.PutObjectOptions;
import org.jclouds.aws.s3.domain.CanonicalUser;
import org.jclouds.aws.s3.domain.acl.CannedAccessPolicy;
import org.jclouds.aws.s3.reference.S3Constants;
import org.jclouds.aws.s3.util.S3Utils;
import org.jclouds.aws.util.DateService;
import org.jclouds.http.ContentTypes;
import org.jets3t.service.S3ServiceException;
import org.jets3t.service.acl.AccessControlList;
import org.jets3t.service.model.S3Bucket;
import org.jets3t.service.model.S3Object;
import org.jets3t.service.model.S3Owner;
import org.jets3t.service.utils.RestUtils;
import org.joda.time.DateTime;

import com.google.common.collect.Iterables;

/**
 * Convert between jCloud and JetS3t objects.
 * 
 * @author James Murty
 */
public class Util {

    public static S3Bucket convertBucket(org.jclouds.aws.s3.domain.S3Bucket.Metadata jcBucketMD) {
        S3Bucket jsBucket = new S3Bucket(jcBucketMD.getName());
        if (jcBucketMD.getOwner() != null) {
            jsBucket.setOwner(
                new S3Owner(
                    jcBucketMD.getOwner().getId(), 
                    jcBucketMD.getOwner().getDisplayName()));
        }
        return jsBucket;
    }
    
    public static S3Bucket[] convertBuckets(List<org.jclouds.aws.s3.domain.S3Bucket.Metadata> jcBucketMDs) {
        List<S3Bucket> jsBuckets = new ArrayList<S3Bucket>(jcBucketMDs.size());
        for (org.jclouds.aws.s3.domain.S3Bucket.Metadata jcBucketMD: jcBucketMDs) {
            jsBuckets.add(convertBucket(jcBucketMD));
        }
        return (S3Bucket[]) jsBuckets.toArray(new S3Bucket[jsBuckets.size()]);
    }
    
    public static S3Object[] convertObjectHeads(Set<org.jclouds.aws.s3.domain.S3Object.Metadata> 
          jcObjectMDs) {
       List<S3Object> jsObjects = new ArrayList<S3Object>(jcObjectMDs.size());
       for (org.jclouds.aws.s3.domain.S3Object.Metadata jcObjectMD: jcObjectMDs) {
          jsObjects.add(convertObjectHead(jcObjectMD));
       }
       return (S3Object[]) jsObjects.toArray(new S3Object[jsObjects.size()]);       
    }
    
    public static S3Object convertObjectHead(org.jclouds.aws.s3.domain.S3Object.Metadata jcObjectMD) {
        S3Object jsObject = new S3Object(jcObjectMD.getKey());
        if (jcObjectMD.getOwner() != null) {
            jsObject.setOwner(
                new S3Owner(
            		jcObjectMD.getOwner().getId(), 
            		jcObjectMD.getOwner().getDisplayName()));
        }        
        DateService dateService = new DateService();

        for (Entry<String, Collection<String>> entry : 
              jcObjectMD.getAllHeaders().asMap().entrySet()) 
        { 
           String key = entry.getKey();
           if (key != null) {  // TODO: Why is there a null-keyed item in the map?
              Object value = Iterables.get(entry.getValue(), 0);
              
              // Work around JetS3t bug when adding date items as Strings
              if (S3Object.METADATA_HEADER_LAST_MODIFIED_DATE.equals(key)) {
                 value = dateService.rfc822DateParse(value.toString()).toDate();
              } else if (S3Object.METADATA_HEADER_DATE.equals(key)) {
                 value = dateService.rfc822DateParse(value.toString()).toDate();
              }
              
              if (key.startsWith(S3Constants.USER_METADATA_PREFIX)) {
                 key = key.substring(S3Constants.USER_METADATA_PREFIX.length());
              }
              
              jsObject.addMetadata(key, value);
           }
        }
        return jsObject;
    }

    public static S3Object convertObject(org.jclouds.aws.s3.domain.S3Object jcObject) {
    	S3Object jsObject = convertObjectHead(jcObject.getMetadata());
        if (jcObject.getData() != null) {
    		jsObject.setDataInputStream((InputStream) jcObject.getData());
        }
    	return jsObject;
    }
    
    public static S3Object[] convertObjects(List<org.jclouds.aws.s3.domain.S3Object> jcObjects) {
        List<S3Object> jsObjects = new ArrayList<S3Object>(jcObjects.size());
        for (org.jclouds.aws.s3.domain.S3Object jcObject: jcObjects) {
        	jsObjects.add(convertObject(jcObject));
        }
        return (S3Object[]) jsObjects.toArray(new S3Object[jsObjects.size()]);
    }
    
    public static org.jclouds.aws.s3.domain.S3Object convertObject(S3Object jsObject) 
       throws S3ServiceException 
    {
       org.jclouds.aws.s3.domain.S3Object jcObject = 
          new org.jclouds.aws.s3.domain.S3Object(jsObject.getKey());

       jcObject.getMetadata().setCacheControl(
             (String) jsObject.getMetadata("Cache-Control"));
       jcObject.getMetadata().setContentDisposition(jsObject.getContentDisposition());
       jcObject.getMetadata().setContentEncoding(jsObject.getContentEncoding());
       jcObject.getMetadata().setLastModified(
             new DateTime(jsObject.getLastModifiedDate()));
       jcObject.getMetadata().setSize(jsObject.getContentLength());
       jcObject.getMetadata().setStorageClass(jsObject.getStorageClass());       
       
       if (jsObject.getMd5HashAsHex() != null) {
          jcObject.getMetadata().setMd5(
                S3Utils.fromHexString(jsObject.getMd5HashAsHex()));
       }
       
       if (jsObject.getOwner() != null) {
          jcObject.getMetadata().setOwner(new CanonicalUser(jsObject.getOwner().getId()));
       }
       
       if (jsObject.getDataInputStream() != null) {
          jcObject.setData(jsObject.getDataInputStream());
       } else {
          jcObject.setData("");  // Must explicitly set data for empty jClouds objects.
       }
       
       if (jsObject.getContentType() != null) {       
          jcObject.getMetadata().setContentType(jsObject.getContentType());
       } else {
          // Must always provide a content type for jClouds objects.
          jcObject.getMetadata().setContentType(ContentTypes.BINARY);
          jsObject.setContentType(ContentTypes.BINARY);
       }

       // User metadata
       DateService dateService = new DateService();
       for (Object maybeUserMetadataObj : jsObject.getMetadataMap().entrySet()) 
       {
          String name = ((Entry<String, Object>) maybeUserMetadataObj).getKey();
          Object value = ((Entry<String, Object>) maybeUserMetadataObj).getValue();
          if (!RestUtils.HTTP_HEADER_METADATA_NAMES.contains(name.toLowerCase())) {
             if (value instanceof Date) {
                value = dateService.rfc822DateFormat((Date)value);
             }
             jcObject.getMetadata().getUserMetadata().put(name, (String)value);
          }
       }
       
       return jcObject;
   }

    public static GetObjectOptions convertGetObjectOptions(Calendar ifModifiedSince,
        Calendar ifUnmodifiedSince, String[] ifMatchTags, String[] ifNoneMatchTags) 
    	throws UnsupportedEncodingException 
    {
    	GetObjectOptions options = new GetObjectOptions();
    	if (ifModifiedSince != null) { 
    		options.ifModifiedSince(new DateTime(ifModifiedSince));
    	}
    	if (ifUnmodifiedSince != null) {
    		options.ifUnmodifiedSince(new DateTime(ifUnmodifiedSince));
    	}
    	// TODO: options.ifMd5Matches should accept multiple match tags
    	if (ifMatchTags != null && ifMatchTags.length > 0) {
    		options.ifMd5Matches(ifMatchTags[0].getBytes());
    	}
    	// TODO: options.ifMd5DoesntMatch should accept multiple match tags
    	if (ifNoneMatchTags != null && ifNoneMatchTags.length > 0) {
    		options.ifMd5DoesntMatch(ifNoneMatchTags[0].getBytes());
    	}
    	return options;
    }
    
    public static ListBucketOptions convertListObjectOptions(String prefix, String marker, 
          String delimiter, Long maxKeys) throws UnsupportedEncodingException 
    {
       ListBucketOptions options = new ListBucketOptions();
       if (prefix != null) {
          options.withPrefix(prefix);
       }
       if (marker != null) {
          options.afterMarker(marker);
       }
       if (maxKeys != null) {
          options.maxResults(maxKeys);
       }
       if (delimiter != null) {
          options.delimiter(delimiter);
       }
       return options;
    }
    
    public static PutObjectOptions convertPutObjectOptions(AccessControlList acl) {
       PutObjectOptions options = new PutObjectOptions();
       if (acl == AccessControlList.REST_CANNED_AUTHENTICATED_READ) {
          options.withAcl(CannedAccessPolicy.AUTHENTICATED_READ);          
       } else if (acl == AccessControlList.REST_CANNED_PRIVATE) {
          options.withAcl(CannedAccessPolicy.PRIVATE);          
       } else if (acl == AccessControlList.REST_CANNED_PUBLIC_READ) {
          options.withAcl(CannedAccessPolicy.PUBLIC_READ);          
       } else if (acl == AccessControlList.REST_CANNED_PUBLIC_READ_WRITE) {
          options.withAcl(CannedAccessPolicy.PUBLIC_READ_WRITE);          
       }
       return options;
    }

}
