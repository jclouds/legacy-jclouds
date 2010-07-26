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
 * 
 * @author Adrian Cole
 */
@Singleton
public class SliceActive implements Predicate<Slice> {

   private final SlicehostClient client;

   @Resource
   protected Logger logger = Logger.NULL;

   @Inject
   public SliceActive(SlicehostClient client) {
      this.client = client;
   }

   public boolean apply(Slice slice) {
      logger.trace("looking for state on slice %s", checkNotNull(slice, "slice"));
      slice = refresh(slice);
      if (slice == null)
         return false;
      logger.trace("%s: looking for slice state %s: currently: %s", slice.getId(), Slice.Status.ACTIVE, slice
            .getStatus());
      return slice.getStatus() == Slice.Status.ACTIVE;
   }

   private Slice refresh(Slice slice) {
      return client.getSlice(slice.getId());
   }
}
