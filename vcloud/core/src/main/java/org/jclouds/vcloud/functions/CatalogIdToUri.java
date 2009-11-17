package org.jclouds.vcloud.functions;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.vcloud.endpoints.internal.CatalogItemRoot;

import com.google.common.base.Function;

/**
 * @author Adrian Cole
 */
@Singleton
public class CatalogIdToUri implements Function<Object, String> {
   @Inject
   @CatalogItemRoot
   private String catalogItemRoot;

   public String apply(Object from) {
      checkArgument(checkNotNull(from, "from") instanceof String,
               "this binder is only valid for String!");
      return String.format("%s/%s", catalogItemRoot, from);
   }

}