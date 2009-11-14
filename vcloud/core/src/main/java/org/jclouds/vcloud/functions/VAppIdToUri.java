package org.jclouds.vcloud.functions;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.vcloud.endpoints.internal.VAppRoot;

import com.google.common.base.Function;

/**
 * @author Adrian Cole
 */
@Singleton
public class VAppIdToUri implements Function<Object, String> {
   @Inject
   @VAppRoot
   private String vAppRoot;
   
   @Override
   public String apply(Object from) {
      checkArgument(checkNotNull(from, "from") instanceof Integer,
               "this binder is only valid for Integers!");
      return String.format("%s/%d",vAppRoot,from);
   }

}