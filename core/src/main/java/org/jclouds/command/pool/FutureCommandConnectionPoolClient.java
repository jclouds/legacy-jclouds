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

import com.google.inject.Inject;
import org.jclouds.Logger;
import org.jclouds.Utils;
import org.jclouds.command.FutureCommand;
import org.jclouds.command.FutureCommandClient;
import org.jclouds.lifecycle.BaseLifeCycle;
import org.jclouds.lifecycle.Closer;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * // TODO: Adrian: Document this!
 *
 * @author Adrian Cole
 */
public class FutureCommandConnectionPoolClient<C> extends BaseLifeCycle implements FutureCommandClient {
    @Inject private Closer closer;
    private final FutureCommandConnectionPool<C> futureCommandConnectionPool;
    private final BlockingQueue<FutureCommand> commandQueue;

    @Inject
    public FutureCommandConnectionPoolClient(java.util.logging.Logger logger, ExecutorService executor, FutureCommandConnectionPool<C> futureCommandConnectionPool, BlockingQueue<FutureCommand> commandQueue) {
        super(new Logger(logger), executor, futureCommandConnectionPool);
        this.futureCommandConnectionPool = futureCommandConnectionPool;
        this.commandQueue = commandQueue;
    }


    @Override
    protected boolean shouldDoWork() {
        return super.shouldDoWork() && futureCommandConnectionPool.getStatus().equals(Status.ACTIVE);
    }

    @Override
    protected void doShutdown() {
        if (exception == null && futureCommandConnectionPool.getException() != null)
            exception = futureCommandConnectionPool.getException();
        while (!commandQueue.isEmpty()) {
            FutureCommand command = commandQueue.remove();
            if (command != null) {
                if (exception != null)
                    command.setException(exception);
                else
                    command.cancel(true);
            }
        }
    }

    protected void doWork() throws InterruptedException {
        FutureCommand command = commandQueue.poll(1, TimeUnit.SECONDS);
        if (command != null) {
            try {
                invoke(command);
            } catch (Exception e) {
                Utils.<InterruptedException>rethrowIfRuntimeOrSameType(e);
                logger.error(e, "Error processing command %s", command);
            }
        }
    }


    public <O extends FutureCommand> void submit(O operation) {
        exceptionIfNotActive();
        commandQueue.add(operation);
    }

    protected <O extends FutureCommand> void invoke(O operation) {
        exceptionIfNotActive();
        FutureCommandConnectionHandle<C> connectionHandle = null;
        try {
            connectionHandle = futureCommandConnectionPool.getHandle(operation);
        } catch (InterruptedException e) {
            logger.warn(e, "Interrupted getting a connection for operation %1s; retrying", operation);
            commandQueue.add(operation);
            return;
        } catch (TimeoutException e) {
            logger.warn(e, "Timeout getting a connection for operation %1s; retrying", operation);
            commandQueue.add(operation);
            return;
        }

        if (connectionHandle == null) {
            logger.error("Failed to obtain connection for operation %1s; retrying", operation);
            commandQueue.add(operation);
            return;
        }
        connectionHandle.startConnection();
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("FutureCommandConnectionPoolClient");
        sb.append("{status=").append(status);
        sb.append(", commandQueue=").append((commandQueue != null) ? commandQueue.size() : 0);
        sb.append(", futureCommandConnectionPool=").append(futureCommandConnectionPool);
        sb.append('}');
        return sb.toString();
    }

    public void close(){
        try {
            closer.close();
        } catch (IOException e) {
            e.printStackTrace();  // TODO: Adrian: Customise this generated block
        }
    }
}

