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
package org.jclouds.http.httpnio.config.internal;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.http.ConnectionReuseStrategy;
import org.apache.http.HttpEntity;
import org.apache.http.impl.DefaultConnectionReuseStrategy;
import org.apache.http.impl.nio.reactor.DefaultConnectingIOReactor;
import org.apache.http.nio.NHttpConnection;
import org.apache.http.nio.entity.BufferingNHttpEntity;
import org.apache.http.nio.protocol.AsyncNHttpClientHandler;
import org.apache.http.nio.protocol.NHttpRequestExecutionHandler;
import org.apache.http.nio.reactor.IOEventDispatch;
import org.apache.http.nio.reactor.IOReactorException;
import org.apache.http.nio.util.ByteBufferAllocator;
import org.apache.http.nio.util.HeapByteBufferAllocator;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpProcessor;
import org.apache.http.protocol.RequestConnControl;
import org.apache.http.protocol.RequestContent;
import org.apache.http.protocol.RequestExpectContinue;
import org.apache.http.protocol.RequestTargetHost;
import org.apache.http.protocol.RequestUserAgent;
import org.jclouds.command.pool.PoolConstants;
import org.jclouds.command.pool.config.FutureCommandConnectionPoolClientModule;
import org.jclouds.http.HttpFutureCommand;
import org.jclouds.http.httpnio.pool.HttpNioFutureCommandConnectionHandle;
import org.jclouds.http.httpnio.pool.HttpNioFutureCommandConnectionPool;
import org.jclouds.http.httpnio.pool.HttpNioFutureCommandExecutionHandler;

import com.google.inject.Inject;
import com.google.inject.Provides;
import com.google.inject.Scopes;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.FactoryProvider;
import com.google.inject.name.Named;

/**
 * // TODO: Adrian: Document this!
 * 
 * @author Adrian Cole
 */
public abstract class BaseHttpNioConnectionPoolClientModule extends
	FutureCommandConnectionPoolClientModule<NHttpConnection> {

    @Provides
    @Singleton
    public AsyncNHttpClientHandler provideAsyncNttpClientHandler(
	    BasicHttpProcessor httpProcessor,
	    NHttpRequestExecutionHandler execHandler,
	    ConnectionReuseStrategy connStrategy,
	    ByteBufferAllocator allocator, HttpParams params) {
	return new AsyncNHttpClientHandler(httpProcessor, execHandler,
		connStrategy, allocator, params);

    }

    @Provides
    @Singleton
    public BasicHttpProcessor provideClientProcessor() {
	BasicHttpProcessor httpproc = new BasicHttpProcessor();
	httpproc.addInterceptor(new RequestContent());
	httpproc.addInterceptor(new RequestTargetHost());
	httpproc.addInterceptor(new RequestConnControl());
	httpproc.addInterceptor(new RequestUserAgent());
	httpproc.addInterceptor(new RequestExpectContinue());
	return httpproc;
    }

    @Provides
    @Singleton
    public HttpParams provideHttpParams() {
	HttpParams params = new BasicHttpParams();
	params.setIntParameter(CoreConnectionPNames.SO_TIMEOUT, 5000)
		.setIntParameter(CoreConnectionPNames.SOCKET_BUFFER_SIZE,
			8 * 1024).setBooleanParameter(
			CoreConnectionPNames.STALE_CONNECTION_CHECK, false)
		.setBooleanParameter(CoreConnectionPNames.TCP_NODELAY, true)
		.setParameter(CoreProtocolPNames.ORIGIN_SERVER, "jclouds/1.0");
	return params;
    }

    protected void configure() {
	super.configure();
	bind(new TypeLiteral<BlockingQueue<HttpFutureCommand<?>>>() {
	}).to(new TypeLiteral<LinkedBlockingQueue<HttpFutureCommand<?>>>() {
	}).in(Scopes.SINGLETON);
	bind(
		HttpNioFutureCommandExecutionHandler.ConsumingNHttpEntityFactory.class)
		.toProvider(
			FactoryProvider
				.newFactory(
					HttpNioFutureCommandExecutionHandler.ConsumingNHttpEntityFactory.class,
					InjectableBufferingNHttpEntity.class))
		.in(Scopes.SINGLETON);
	bind(NHttpRequestExecutionHandler.class).to(
		HttpNioFutureCommandExecutionHandler.class)
		.in(Scopes.SINGLETON);
	bind(ConnectionReuseStrategy.class).to(
		DefaultConnectionReuseStrategy.class).in(Scopes.SINGLETON);
	bind(ByteBufferAllocator.class).to(HeapByteBufferAllocator.class);
	bind(
		HttpNioFutureCommandConnectionPool.FutureCommandConnectionHandleFactory.class)
		.toProvider(
			FactoryProvider
				.newFactory(
					new TypeLiteral<HttpNioFutureCommandConnectionPool.FutureCommandConnectionHandleFactory>() {
					},
					new TypeLiteral<HttpNioFutureCommandConnectionHandle>() {
					}));
    }

    static class InjectableBufferingNHttpEntity extends BufferingNHttpEntity {
	@Inject
	public InjectableBufferingNHttpEntity(@Assisted HttpEntity httpEntity,
		ByteBufferAllocator allocator) {
	    super(httpEntity, allocator);
	}
    }

    @Override
    public BlockingQueue<NHttpConnection> provideAvailablePool(
	    @Named(PoolConstants.PROPERTY_POOL_MAX_CONNECTIONS) int max)
	    throws Exception {
	return new ArrayBlockingQueue<NHttpConnection>(max, true);
    }

    @Provides
    @Singleton
    public abstract IOEventDispatch provideClientEventDispatch(
	    AsyncNHttpClientHandler handler, HttpParams params)
	    throws Exception;

    @Provides
    @Singleton
    public DefaultConnectingIOReactor provideDefaultConnectingIOReactor(
	    @Named(PoolConstants.PROPERTY_POOL_IO_WORKER_THREADS) int ioWorkerThreads,
	    HttpParams params) throws IOReactorException {
	return new DefaultConnectingIOReactor(ioWorkerThreads, params);
    }

}