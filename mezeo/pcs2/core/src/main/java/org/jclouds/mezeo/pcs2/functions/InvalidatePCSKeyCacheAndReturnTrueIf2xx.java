package org.jclouds.mezeo.pcs2.functions;

import java.util.concurrent.ConcurrentMap;

import javax.inject.Inject;

import org.apache.commons.io.IOUtils;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.mezeo.pcs2.domain.FileMetadata;
import org.jclouds.rest.RestContext;

import com.google.common.base.Function;

/**
 * invalidates cache and returns true when the http response code is in the range 200-299.
 * 
 * @author Adrian Cole
 */
public class InvalidatePCSKeyCacheAndReturnTrueIf2xx implements Function<HttpResponse, Boolean>,
         RestContext {
   private final ConcurrentMap<Key, String> cache;
   private final ConcurrentMap<Key, FileMetadata> mdCache;
   private HttpRequest request;
   private Object[] args;

   @Inject
   public InvalidatePCSKeyCacheAndReturnTrueIf2xx(ConcurrentMap<Key, String> cache,
            ConcurrentMap<Key, FileMetadata> mdCache) {
      this.cache = cache;
      this.mdCache = mdCache;
   }

   public Boolean apply(HttpResponse from) {
      IOUtils.closeQuietly(from.getContent());
      int code = from.getStatusCode();
      if (code >= 300 || code < 200) {
         throw new IllegalStateException("incorrect code for this operation: " + from);
      }
      Key key = new Key(getArgs()[0].toString(), getArgs()[1].toString());
      cache.remove(key);
      mdCache.remove(key);
      return true;
   }

   public Object[] getArgs() {
      return args;
   }

   public HttpRequest getRequest() {
      return request;
   }

   public void setContext(HttpRequest request, Object[] args) {
      this.args = args;
      this.request = request;
   }

}