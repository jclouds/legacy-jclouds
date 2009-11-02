package org.jclouds.atmosonline.saas.functions;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.jclouds.atmosonline.saas.domain.BoundedSortedSet;
import org.jclouds.atmosonline.saas.domain.DirectoryEntry;
import org.jclouds.atmosonline.saas.domain.internal.BoundedTreeSet;
import org.jclouds.atmosonline.saas.reference.AtmosStorageHeaders;
import org.jclouds.atmosonline.saas.xml.ListDirectoryResponseHandler;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.http.functions.ParseSax.Factory;

import com.google.common.base.Function;

/**
 * This parses {@link BoundedSortedSet} from HTTP headers and xml content.
 * 
 * @author Adrian Cole
 */
@Singleton
public class ParseDirectoryListFromContentAndHeaders implements
         Function<HttpResponse, BoundedSortedSet<DirectoryEntry>> {

   private final ParseSax.Factory factory;
   private final Provider<ListDirectoryResponseHandler> listHandlerProvider;

   @Inject
   private ParseDirectoryListFromContentAndHeaders(Factory factory,
            Provider<ListDirectoryResponseHandler> orgHandlerProvider) {
      this.factory = factory;
      this.listHandlerProvider = orgHandlerProvider;
   }

   /**
    * parses the http response headers to create a new {@link BoundedSortedSet} object.
    */
   public BoundedSortedSet<DirectoryEntry> apply(HttpResponse from) {
      String token = from.getFirstHeaderOrNull(AtmosStorageHeaders.TOKEN);
      return new BoundedTreeSet<DirectoryEntry>(factory.create(listHandlerProvider.get()).parse(
               from.getContent()), token);
   }
}
