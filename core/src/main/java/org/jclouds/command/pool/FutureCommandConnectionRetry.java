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
package org.jclouds.command.pool;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.Resource;

import org.jclouds.command.FutureCommand;
import org.jclouds.logging.Logger;

/**
 * // TODO: Adrian: Document this!
 * 
 * @author Adrian Cole
 */
public abstract class FutureCommandConnectionRetry<C> {
    protected final BlockingQueue<FutureCommand> commandQueue;
    protected final AtomicInteger errors;
    @Resource
    protected Logger logger = Logger.NULL;

    public FutureCommandConnectionRetry(
	    BlockingQueue<FutureCommand> commandQueue, AtomicInteger errors) {
	this.commandQueue = commandQueue;
	this.errors = errors;
    }

    public abstract void associateHandleWithConnection(
	    FutureCommandConnectionHandle<C> handle, C connection);

    public abstract FutureCommandConnectionHandle<C> getHandleFromConnection(
	    C connection);

    public boolean shutdownConnectionAndRetryOperation(C connection) {
	FutureCommandConnectionHandle<C> handle = getHandleFromConnection(connection);
	if (handle != null) {
	    try {
		logger.info("%1s - shutting down connection", connection);
		handle.shutdownConnection();
		incrementErrorCountAndRetry(handle.getOperation());
		return true;
	    } catch (IOException e) {
		logger.error(e, "%1s - error shutting down connection",
			connection);
	    }
	}
	return false;
    }

    public void incrementErrorCountAndRetry(FutureCommand command) {
	errors.getAndIncrement();
	logger.info("resubmitting command %1s", command);
	commandQueue.add(command);
    }
}
