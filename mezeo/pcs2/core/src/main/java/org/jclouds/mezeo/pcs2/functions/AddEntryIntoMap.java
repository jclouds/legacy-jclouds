package org.jclouds.mezeo.pcs2.functions;

import static com.google.common.base.Preconditions.checkState;

import java.lang.reflect.Constructor;
import java.util.Map;

import javax.inject.Inject;

import org.jclouds.http.HttpResponse;
import org.jclouds.http.functions.ReturnStringIf200;
import org.jclouds.rest.InvocationContext;
import org.jclouds.rest.internal.GeneratedHttpRequest;

import com.google.common.base.Function;

/**
 * 
 * @author Adrian Cole
 */
public class AddEntryIntoMap implements Function<HttpResponse, Void>, InvocationContext {
   ReturnStringIf200 returnIf200;
   private GeneratedHttpRequest<?> request;

   @Inject
   private AddEntryIntoMap(ReturnStringIf200 returnIf200) {
      this.returnIf200 = returnIf200;
   }

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
      checkState(request.getArgs() != null, "args should be initialized at this point");
      Map<String, String> map = null;
      String key = null;
      for (Object arg : request.getArgs()) {
         if (arg instanceof Map)
            map = (Map<String, String>) arg;
         else if (arg instanceof String)
            key = arg.toString();
      }
      checkState(map != null, "No Map found in args, improper method declarations");
      checkState(key != null, "No String found in args, improper method declarations");

      map.put(key, returnIf200.apply(from).trim());
      return v;
   }

   public void setContext(GeneratedHttpRequest<?> request) {
      this.request = request;
   }

}