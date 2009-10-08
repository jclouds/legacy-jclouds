package org.jclouds.mezeo.pcs2.functions;

import static com.google.common.base.Preconditions.checkState;

import java.lang.reflect.Constructor;

import javax.inject.Inject;

import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.functions.ReturnStringIf200;
import org.jclouds.rest.RestContext;

import com.google.common.base.Function;
import com.google.common.collect.Multimap;

/**
 * 
 * @author Adrian Cole
 */
public class AddEntryIntoMultiMap implements Function<HttpResponse, Void>, RestContext {
   ReturnStringIf200 returnIf200;

   @Inject
   private AddEntryIntoMultiMap(ReturnStringIf200 returnIf200) {
      this.returnIf200 = returnIf200;
   }

   private Object[] args;
   private HttpRequest request;

   static final Void v;
   static {
      Constructor<Void> cv;
      try {
         cv = Void.class.getDeclaredConstructor();
         cv.setAccessible(true);
         v = cv.newInstance();
      } catch (Exception e) {
         throw new Error("Error setting up class", e);
      }
   }

   @SuppressWarnings("unchecked")
   public Void apply(HttpResponse from)

   {
      checkState(args != null, "args should be initialized at this point");
      Multimap<String, String> map = null;
      String key = null;
      for (Object arg : args) {
         if (arg instanceof Multimap)
            map = (Multimap<String, String>) arg;
         else if (arg instanceof String)
            key = arg.toString();
      }
      checkState(map != null, "No Multimap found in args, improper method declarations");
      checkState(key != null, "No String found in args, improper method declarations");

      map.put(key, returnIf200.apply(from).trim());
      return v;
   }

   public Object[] getArgs() {
      return args;
   }

   public HttpRequest getRequest() {
      return request;
   }

   public void setContext(HttpRequest request, Object[] args) {
      this.request = request;
      this.args = args;
   }

}