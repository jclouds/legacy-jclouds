package org.jclouds.slicehost.predicates;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.annotation.Resource;
import javax.inject.Singleton;

import org.jclouds.logging.Logger;
import org.jclouds.slicehost.SlicehostClient;
import org.jclouds.slicehost.domain.Slice;

import com.google.common.base.Predicate;
import com.google.inject.Inject;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class SliceTerminated implements Predicate<Slice> {

   private final SlicehostClient client;

   @Resource
   protected Logger logger = Logger.NULL;

   @Inject
   public SliceTerminated(SlicehostClient client) {
      this.client = client;
   }

   public boolean apply(Slice slice) {
      logger.trace("looking for state on slice %s", checkNotNull(slice, "slice"));
      slice = refresh(slice);
      if (slice == null)
         return true;
      logger.trace("%s: looking for slice state %s: currently: %s", slice.getId(), Slice.Status.TERMINATED, slice
            .getStatus());
      return slice.getStatus() == Slice.Status.TERMINATED;
   }

   private Slice refresh(Slice slice) {
      return client.getSlice(slice.getId());
   }
}
