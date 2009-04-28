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
package org.jclouds.http.commands.callables.xml;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import org.apache.commons.io.IOUtils;
import org.jclouds.Logger;
import org.jclouds.Utils;
import org.jclouds.http.HttpException;
import org.jclouds.http.HttpFutureCommand;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import java.io.InputStream;

/**
 * // TODO: Adrian: Document this!
 *
 * @author Adrian Cole
 */
public class ParseSax<T> extends HttpFutureCommand.ResponseCallable<T> {

    private XMLReader parser;
    private HandlerWithResult<T> handler;
    private boolean suckFirst = false;

    @Inject
    public ParseSax(java.util.logging.Logger logger, XMLReader parser, @Assisted HandlerWithResult<T> handler) {
        super(new Logger(logger));
        this.parser = parser;
        this.handler = handler;
    }

    public T call() throws HttpException {
        InputStream input = null;
        try {
            input = getResponse().getContent();
            if (input != null) {
                return parse(input);
            } else {
                throw new HttpException("No input to parse");
            }
        } catch (Exception e) {
            Utils.<HttpException>rethrowIfRuntimeOrSameType(e);
            throw new HttpException("Error parsing input for " + getResponse(), e);
        }
    }

    public T parse(InputStream xml) throws HttpException {
        parseAndCloseStream(xml, handler);
        return handler.getResult();
    }

    private void parseAndCloseStream(InputStream xml, ContentHandler handler) throws HttpException {
        parser.setContentHandler(handler);
        String response = null;
        try {
            if (suckFirst) {
                response = IOUtils.toString(xml);
                logger.trace("received content %n%s", response);
                IOUtils.closeQuietly(xml);
                xml = IOUtils.toInputStream(response);
            }
            parser.parse(new InputSource(xml));
        } catch (Exception e) {
            StringBuilder message = new StringBuilder();
            message.append("Error parsing input for ").append(handler);
            if (response != null) {
                message.append("\n").append(response);
            }
            logger.error(e, message.toString());
            Utils.<HttpException>rethrowIfRuntimeOrSameType(e);
            throw new HttpException(message.toString(), e);
        } finally {
            IOUtils.closeQuietly(xml);
        }
    }

    /**
     * // TODO: Adrian: Document this!
     *
     * @author Adrian Cole
     */
    public abstract static class HandlerWithResult<T> extends DefaultHandler {
        public abstract T getResult();
    }
}
