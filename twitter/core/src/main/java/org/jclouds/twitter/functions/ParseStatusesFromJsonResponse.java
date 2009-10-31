package org.jclouds.twitter.functions;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.SortedSet;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.http.functions.ParseJson;
import org.jclouds.twitter.domain.Status;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * This parses {@link Status} from a gson string.
 * 
 * @author Adrian Cole
 */
@Singleton
public class ParseStatusesFromJsonResponse extends ParseJson<SortedSet<Status>> {

   @Inject
   public ParseStatusesFromJsonResponse(Gson gson) {
      super(gson);
   }

   public SortedSet<Status> apply(InputStream stream) {
      Type setType = new TypeToken<SortedSet<Status>>() {
      }.getType();
      try {
         return gson.fromJson(new InputStreamReader(stream, "UTF-8"), setType);
      } catch (UnsupportedEncodingException e) {
         throw new RuntimeException("jclouds requires UTF-8 encoding", e);
      }
   }
}