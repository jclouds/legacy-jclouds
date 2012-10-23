package org.jclouds.vsphere.utils;

import com.vmware.vim25.mo.VirtualMachine;

public class VirtualMachines {

   public static boolean isTemplate(VirtualMachine vm) {
      return vm.getConfig().isTemplate();
   }
   
}
