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
package org.jclouds.aws.s3.xml;

import static com.google.common.base.Preconditions.checkNotNull;

import org.jclouds.aws.s3.domain.CanonicalUser;
import org.jclouds.aws.s3.domain.S3Bucket;
import org.jclouds.aws.s3.domain.S3Object;
import org.jclouds.aws.s3.util.DateService;
import org.jclouds.aws.s3.util.S3Utils;
import org.jclouds.http.commands.callables.xml.ParseSax;
import org.xml.sax.Attributes;

import com.google.inject.Inject;

/**
 * Parses the following XML document:
 * <p/>
 * ListBucketResult xmlns="http://s3.amazonaws.com/doc/2006-03-01"
 * 
 * @see <a href="http://docs.amazonwebservices.com/AmazonS3/2006-03-01/index.html?RESTBucketGET.html"
 *      />
 * @author Adrian Cole
 */
public class ListBucketHandler extends ParseSax.HandlerWithResult<S3Bucket> {
    private S3Bucket s3Bucket;
    private S3Object.Metadata currentObjectMetadata;
    private CanonicalUser currentOwner;
    private StringBuilder currentText = new StringBuilder();

    private final DateService dateParser;

    @Inject
    public ListBucketHandler(DateService dateParser) {
	this.dateParser = dateParser;
    }

    public S3Bucket getResult() {
	return s3Bucket;
    }

    public void setBucketName(String bucketName) {
	this.s3Bucket = new S3Bucket(checkNotNull(bucketName, "bucketName"));
    }

    private boolean inCommonPrefixes;

    public void startElement(String uri, String name, String qName,
	    Attributes attrs) {
	if (qName.equals("CommonPrefixes")) {
	    inCommonPrefixes = true;
	}
    }

    public void endElement(String uri, String name, String qName) {
	if (qName.equals("ID")) {
	    currentOwner = new CanonicalUser(currentText.toString());
	} else if (qName.equals("DisplayName")) {
	    currentOwner.setDisplayName(currentText.toString());
	} else if (qName.equals("Key")) { // content stuff
	    currentObjectMetadata = new S3Object.Metadata(currentText
		    .toString());
	} else if (qName.equals("LastModified")) {
	    currentObjectMetadata.setLastModified(dateParser
		    .dateTimeFromXMLFormat(currentText.toString()));
	} else if (qName.equals("ETag")) {
	    currentObjectMetadata.setMd5(S3Utils.fromHexString(currentText
		    .toString().replaceAll("\"", "")));
	} else if (qName.equals("Size")) {
	    currentObjectMetadata.setSize(Long
		    .parseLong(currentText.toString()));
	} else if (qName.equals("Owner")) {
	    currentObjectMetadata.setOwner(currentOwner);
	} else if (qName.equals("StorageClass")) {
	    currentObjectMetadata.setStorageClass(currentText.toString());
	} else if (qName.equals("Contents")) {
	    s3Bucket.getContents().add(currentObjectMetadata);
	} else if (qName.equals("Name")) {// bucket stuff last, as least likely
	} else if (qName.equals("Prefix")) {
	    String prefix = currentText.toString().trim();
	    if (inCommonPrefixes)
		s3Bucket.getCommonPrefixes().add(prefix);
	    else
		s3Bucket.setPrefix(prefix);
	} else if (qName.equals("Delimiter")) {
	    if (!currentText.toString().equals(""))
		s3Bucket.setDelimiter(currentText.toString().trim());
	} else if (qName.equals("Marker")) {
	    if (!currentText.toString().equals(""))
		s3Bucket.setMarker(currentText.toString());
	} else if (qName.equals("MaxKeys")) {
	    s3Bucket.setMaxKeys(Long.parseLong(currentText.toString()));
	} else if (qName.equals("IsTruncated")) {
	    boolean isTruncated = Boolean.parseBoolean(currentText.toString());
	    s3Bucket.setComplete(!isTruncated);
	}
	currentText = new StringBuilder();
    }

    public void characters(char ch[], int start, int length) {
	currentText.append(ch, start, length);
    }
}
