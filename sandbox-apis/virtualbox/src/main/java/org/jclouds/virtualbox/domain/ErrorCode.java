/*
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.jclouds.virtualbox.domain;

import org.virtualbox_4_1.VBoxException;
import org.virtualbox_4_1.jaxws.RuntimeFaultMsg;

import java.util.HashMap;
import java.util.Map;

/**
 * Maps the error codes in the Virtual Box Java API into enum values.
 * <p/>
 * <p/>
 * To get the error code from a VBoxException, use:
 * <pre>
 * try {
 *    ...
 *    ...
 * }
 * catch (VBoxException vboxException) {
 *    RuntimeFaultMsg fault = (RuntimeFaultMsg) vboxException.getWrapped();
 *    int faultCode = fault.getFaultInfo().getResultCode();
 *    ErrorCode errorCode = ErrorCode.valueOf(faultCode);
 * }
 * </pre>
 *
 * @author Mattias Holmqvist
 */
public enum ErrorCode {

   E_OBJECT_NOT_FOUND(2159738881L),
   E_INVALID_VM_STATE(2159738882L),
   E_VM_ERROR(2159738883L),
   E_FILE_ERROR(2159738884L),
   E_IPRT_ERROR(2159738885L),
   E_PDM_ERROR(2159738886L),
   E_INVALID_OBJECT_STATE(2159738887L),
   E_HOST_ERROR(2159738888L),
   E_NOT_SUPPORTED(2159738889L),
   E_XML_ERROR(2159738890L),
   E_INVALID_SESSION_STATE(2159738891L),
   E_OBJECT_IN_USE(2159738892L),
   E_ACCESSDENIED(2147942405L),
   E_POINTER(2147500035L),
   E_FAIL(2147500037L),
   E_NOTIMPL(2147500033L),
   E_OUTOFMEMORY(2147942414L),
   E_INVALIDARG(2147942487L),
   E_UNEXPECTED(2147549183L),
   E_UNKNOWN_ERROR_CODE(-1L),
   E_ERROR_CODE_UNAVAILABLE(-2L);

   private long code;

   ErrorCode(long code) {
      this.code = code;
   }

   private static Map<Long, ErrorCode> table = new HashMap<Long, ErrorCode>();

   static {
      for (ErrorCode errorCode : ErrorCode.values()) {
         table.put(errorCode.code, errorCode);
      }
   }

   /**
    * Returns an ErrorCode from the fault code given by the VirtualBox API.
    *
    * @param vboxException the exception to get the error code from.
    * @return an ErrorCode representing the given fault code.
    */
   public static ErrorCode valueOf(VBoxException vboxException) {
      final Throwable wrapped = vboxException.getWrapped();
      if (wrapped instanceof RuntimeFaultMsg) {
         final RuntimeFaultMsg faultCode = (RuntimeFaultMsg) wrapped;
         final int resultCode = faultCode.getFaultInfo().getResultCode();
         final ErrorCode errorCode = table.get(unsignedIntToLong(resultCode));
         if (errorCode != null) {
            return errorCode;
         }
         return E_UNKNOWN_ERROR_CODE;
      }
      return E_ERROR_CODE_UNAVAILABLE;
   }

   private static long unsignedIntToLong(int faultCode) {
      return faultCode & 0xffffffffL;
   }

}
