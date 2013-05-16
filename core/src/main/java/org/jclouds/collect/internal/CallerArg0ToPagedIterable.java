package org.jclouds.collect.internal;

import org.jclouds.collect.IterableWithMarker;
import org.jclouds.collect.PagedIterable;
import org.jclouds.collect.PagedIterables;
import org.jclouds.http.HttpRequest;
import org.jclouds.rest.InvocationContext;
import org.jclouds.rest.internal.GeneratedHttpRequest;

import com.google.common.annotations.Beta;
import com.google.common.base.Function;
import com.google.common.base.Optional;

/**
 * @author Adrian Cole
 * @deprecated Arg0ToPagedIterable.FromCaller
 */
@Beta
@Deprecated
public abstract class CallerArg0ToPagedIterable<T, I extends CallerArg0ToPagedIterable<T, I>> implements
      Function<IterableWithMarker<T>, PagedIterable<T>>, InvocationContext<I> {

   private GeneratedHttpRequest request;

   @Override
   public PagedIterable<T> apply(IterableWithMarker<T> input) {
      if (input.nextMarker() == null)
         return PagedIterables.of(input);

      Optional<String> arg0Option = Optional.absent();
      if (request.getCaller().get().getArgs().size() > 0) {
         Object arg0 = request.getCaller().get().getArgs().get(0);
         if (arg0 != null)
            arg0Option = Optional.of(arg0.toString());
      }
      final String arg0 = arg0Option.orNull();
      return PagedIterables.advance(input, markerToNextForCallingArg0(arg0));
   }

   protected abstract Function<Object, IterableWithMarker<T>> markerToNextForCallingArg0(String arg0);

   @SuppressWarnings("unchecked")
   @Override
   public I setContext(HttpRequest request) {
      this.request = GeneratedHttpRequest.class.cast(request);
      return (I) this;
   }

}
