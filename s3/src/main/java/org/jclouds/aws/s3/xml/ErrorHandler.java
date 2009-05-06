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

import org.jclouds.aws.s3.domain.S3Error;
import org.jclouds.http.commands.callables.xml.ParseSax;

/**
 * Parses the error from the Amazon S3 REST API.
 * 
 * @author Adrian Cole
 */
public class ErrorHandler extends ParseSax.HandlerWithResult<S3Error> {

    private S3Error error = new S3Error();
    private StringBuilder currentText = new StringBuilder();

    public S3Error getResult() {
	return error;
    }

    public void endElement(String uri, String name, String qName) {

	if (qName.equals("Code")) {
	    error.setCode(currentText.toString());
	} else if (qName.equals("Message")) {
	    error.setMessage(currentText.toString());
	} else if (qName.equals("Resource")) {
	    error.setResource(currentText.toString());
	} else if (qName.equals("RequestId")) {
	    error.setRequestId(currentText.toString());
	} else if (qName.equals("HostId")) {
	    error.setHostId(currentText.toString());
	} else if (qName.equals("Header")) {
	    error.setHeader(currentText.toString());
	} else if (qName.equals("SignatureProvided")) {
	    error.setSignatureProvided(currentText.toString());
	} else if (qName.equals("StringToSign")) {
	    error.setStringToSign(currentText.toString());
	} else if (qName.equals("StringToSignBytes")) {
	    error.setStringToSignBytes(currentText.toString());
	}
	currentText = new StringBuilder();
    }

    public void characters(char ch[], int start, int length) {
	currentText.append(ch, start, length);
    }
}
