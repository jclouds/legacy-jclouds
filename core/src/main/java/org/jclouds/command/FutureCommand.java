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
package org.jclouds.command;

import java.util.concurrent.*;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * // TODO: Adrian: Document this!
 * 
 * @author Adrian Cole
 */
public class FutureCommand<Q, R, T> implements Future<T> {

    private final Q request;
    private final ResponseRunnableFuture<R, T> responseRunnableFuture;
    private volatile int failureCount;

    public int incrementFailureCount() {
	return ++failureCount;
    }

    public int getFailureCount() {
	return failureCount;
    }

    public FutureCommand(Q request, ResponseCallable<R, T> responseCallable) {
	this.request = checkNotNull(request, "request");
	this.responseRunnableFuture = new ResponseRunnableFutureTask<R, T>(
		checkNotNull(responseCallable, "responseCallable"));
    }

    public Q getRequest() {
	return request;
    }

    public ResponseRunnableFuture<R, T> getResponseFuture() {
	return responseRunnableFuture;
    }

    public void setException(Exception e) {
	responseRunnableFuture.setException(e);
    }

    public boolean cancel(boolean b) {
	return responseRunnableFuture.cancel(b);
    }

    public boolean isCancelled() {
	return responseRunnableFuture.isCancelled();
    }

    public boolean isDone() {
	return responseRunnableFuture.isDone();
    }

    public T get() throws InterruptedException, ExecutionException {
	return responseRunnableFuture.get();
    }

    public T get(long l, TimeUnit timeUnit) throws InterruptedException,
	    ExecutionException, TimeoutException {
	return responseRunnableFuture.get(l, timeUnit);
    }

    /**
     * // TODO: Adrian: Document this!
     * 
     * @author Adrian Cole
     */
    public static class ResponseRunnableFutureTask<R, T> extends FutureTask<T>
	    implements ResponseRunnableFuture<R, T> {
	private final ResponseCallable<R, T> tCallable;

	public ResponseRunnableFutureTask(ResponseCallable<R, T> tCallable) {
	    super(tCallable);
	    this.tCallable = tCallable;
	}

	@Override
	public String toString() {
	    return "ResponseRunnableFutureTask{" + "tCallable=" + tCallable
		    + '}';
	}

	public R getResponse() {
	    return tCallable.getResponse();
	}

	public void setResponse(R response) {
	    tCallable.setResponse(response);
	}

	/**
	 * opening this to public so that other errors can be associated with
	 * the request, for example i/o errors.
	 * 
	 * @param throwable
	 */
	@Override
	public void setException(Throwable throwable) {
	    super.setException(throwable);
	}

    }

    public interface ResponseRunnableFuture<R, T> extends Response<R>,
	    Runnable, Future<T> {
	public void setException(Throwable throwable);
    }

    public interface ResponseCallable<R, T> extends Response<R>, Callable<T> {
    }

    public interface Response<R> {
	public R getResponse();

	public void setResponse(R response);
    }
}
