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
package org.jclouds;

import java.util.logging.Level;

public class Logger {
    private final java.util.logging.Logger logger;

    public Logger(java.util.logging.Logger logger) {
        this.logger = logger;
    }

    public void trace(String message, Object... args) {
        if (isTraceEnabled())
            logger.finest(String.format(message, args));
    }

    public boolean isTraceEnabled() {
        return logger.isLoggable(Level.FINEST);
    }

    public void debug(String message, Object... args) {
        if (isDebugEnabled())
            logger.fine(String.format(message, args));
    }

    private boolean isDebugEnabled() {
        return logger.isLoggable(Level.FINE);
    }

    public void info(String message, Object... args) {
        if (logger.isLoggable(Level.INFO))
            logger.info(String.format(message, args));
    }

    public void warn(String message, Object... args) {
        if (logger.isLoggable(Level.WARNING))
            logger.log(Level.WARNING, String.format(message, args));
    }

    public void warn(Throwable throwable, String message, Object... args) {
        if (logger.isLoggable(Level.WARNING))
            logger.log(Level.WARNING, String.format(message, args), throwable);
    }

    public void error(String message, Object... args) {
        if (logger.isLoggable(Level.SEVERE))
            logger.log(Level.SEVERE, String.format(message, args));
    }

    public void error(Throwable throwable, String message, Object... args) {
        if (logger.isLoggable(Level.SEVERE))
            logger.log(Level.SEVERE, String.format(message, args), throwable);
    }
}