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
package org.jclouds.logging.log4j;

import static org.apache.log4j.Level.ERROR;
import static org.apache.log4j.Level.WARN;

import org.jclouds.logging.BaseLogger;
import org.jclouds.logging.Logger;

/**
 * {@link org.apache.log4j.Logger} implementation of {@link Logger}.
 * 
 * @author Adrian Cole
 * 
 */
public class Log4JLogger extends BaseLogger {
    private final org.apache.log4j.Logger logger;
    private final String category;

    public static class Log4JLoggerFactory implements LoggerFactory {
	public Logger getLogger(String category) {
	    return new Log4JLogger(category, org.apache.log4j.Logger
		    .getLogger(category));
	}
    }

    public Log4JLogger(String category, org.apache.log4j.Logger logger) {
	this.category = category;
	this.logger = logger;
    }

    @Override
    protected void logTrace(String message) {
	logger.trace(message);
    }

    public boolean isTraceEnabled() {
	return logger.isTraceEnabled();
    }

    @Override
    protected void logDebug(String message) {
	logger.debug(message);
    }

    public boolean isDebugEnabled() {
	return logger.isDebugEnabled();
    }

    @Override
    protected void logInfo(String message) {
	logger.info(message);
    }

    public boolean isInfoEnabled() {
	return logger.isInfoEnabled();
    }

    @Override
    protected void logWarn(String message) {
	logger.warn(message);
    }

    @Override
    protected void logWarn(String message, Throwable e) {
	logger.warn(message, e);
    }

    public boolean isWarnEnabled() {
	return logger.isEnabledFor(WARN);
    }

    @Override
    protected void logError(String message) {
	logger.error(message);
    }

    @Override
    protected void logError(String message, Throwable e) {
	logger.error(message, e);
    }

    public boolean isErrorEnabled() {
	return logger.isEnabledFor(ERROR);
    }

    public String getCategory() {
	return category;
    }
}
