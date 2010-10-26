package org.jclouds.rimuhosting.miro.compute.config;

import java.util.Set;

import org.jclouds.compute.config.BindComputeSuppliersByClass;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.Image;
import org.jclouds.domain.Location;
import org.jclouds.rimuhosting.miro.compute.suppliers.RimuHostingDefaultLocationSupplier;
import org.jclouds.rimuhosting.miro.compute.suppliers.RimuHostingHardwareSupplier;
import org.jclouds.rimuhosting.miro.compute.suppliers.RimuHostingImageSupplier;
import org.jclouds.rimuhosting.miro.compute.suppliers.RimuHostingLocationSupplier;

import com.google.common.base.Supplier;

public class RimuHostingBindComputeSuppliersByClass extends BindComputeSuppliersByClass {

   @Override
   protected Class<? extends Supplier<Set<? extends Hardware>>> defineHardwareSupplier() {
      return RimuHostingHardwareSupplier.class;
   }

   @Override
   protected Class<? extends Supplier<Set<? extends Image>>> defineImageSupplier() {
      return RimuHostingImageSupplier.class;
   }

   @Override
   protected Class<? extends Supplier<Set<? extends Location>>> defineLocationSupplier() {
      return RimuHostingLocationSupplier.class;
   }

   @Override
   protected Class<? extends Supplier<Location>> defineDefaultLocationSupplier() {
      return RimuHostingDefaultLocationSupplier.class;
   }
}