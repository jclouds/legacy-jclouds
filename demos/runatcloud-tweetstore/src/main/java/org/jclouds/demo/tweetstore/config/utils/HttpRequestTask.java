package org.jclouds.demo.tweetstore.config.utils;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.inject.name.Names.bindProperties;
import static java.lang.String.format;

import java.util.Properties;

import javax.ws.rs.core.UriBuilder;

import org.jclouds.PropertiesBuilder;
import org.jclouds.concurrent.config.ExecutorServiceModule;
import org.jclouds.http.HttpCommand;
import org.jclouds.http.HttpCommandExecutorService;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.config.JavaUrlHttpCommandExecutorServiceModule;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.sun.jersey.api.uri.UriBuilderImpl;

public class HttpRequestTask implements Runnable {
    public static Factory factory(Properties props) {
        return factory(props, format("%s@%d", Factory.class.getName(), System.currentTimeMillis()));
    }

    public static Factory factory(Properties props, String originator) {
        return new Factory(props, originator);
    }
    
    public static class Factory {
        private static final String HTTP_REQUEST_ORIGINATOR_HEADER = "X-RUN@cloud-Originator";
        
        protected final HttpCommandExecutorService httpClient;
        protected final String originator;
        
        private Factory(final Properties props, String originator) {
            this.originator = originator;
            httpClient = Guice.createInjector(new ExecutorServiceModule(),
                new JavaUrlHttpCommandExecutorServiceModule(),
                new AbstractModule() {
                    @Override
                    protected void configure() {
                        // URL connection defaults
                        Properties toBind = new PropertiesBuilder().build();
                        toBind.putAll(checkNotNull(props, "properties"));
                        toBind.putAll(System.getProperties());
                        bindProperties(binder(), toBind);
                        bind(UriBuilder.class).to(UriBuilderImpl.class);
                    }
                }).getInstance(HttpCommandExecutorService.class);
        }
        
        public HttpRequestTask create(HttpRequest request) {
            HttpRequest requestWithSubmitter = request.toBuilder().headers(
                    copyOfWithEntry(request.getHeaders(), 
                            HTTP_REQUEST_ORIGINATOR_HEADER, originator)).build();
            return new HttpRequestTask(httpClient, requestWithSubmitter);
        }
        
        private static <K, V> Multimap<K, V> copyOfWithEntry(
                Multimap<? extends K, ? extends V> multimap, K k1, V v1) {
            return ImmutableMultimap.<K, V>builder().putAll(multimap).put(k1, v1).build();
        }
    }
    
    private final HttpCommandExecutorService httpClient;
    private final HttpRequest request;
    
    private HttpRequestTask(HttpCommandExecutorService httpClient, HttpRequest request) {
        this.httpClient = httpClient;
        this.request = request;
    }
    
    @Override
    public void run() {
        httpClient.submit(new ImmutableHttpCommand(request));
    }
    
    private class ImmutableHttpCommand implements HttpCommand {
        private final HttpRequest request;

        public ImmutableHttpCommand(HttpRequest request) {
            this.request = request;
        }
        
        @Override
        public void setException(Exception exception) {
        }

        @Override
        public void setCurrentRequest(HttpRequest request) {
        }

        @Override
        public boolean isReplayable() {
            return false;
        }

        @Override
        public int incrementRedirectCount() {
            return 0;
        }

        @Override
        public int incrementFailureCount() {
            return 0;
        }

        @Override
        public int getRedirectCount() {
            return 0;
        }

        @Override
        public int getFailureCount() {
            return 0;
        }

        @Override
        public Exception getException() {
            return null;
        }

        @Override
        public HttpRequest getCurrentRequest() {
            return request;
        }
    }
}
