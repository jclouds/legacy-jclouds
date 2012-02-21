/**
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
package org.jclouds.demo.tweetstore.integration.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class Zips {
    private static final int DEFAULT_BUFFER_SIZE = 1024 * 4;
    
    public static File zipFile(String fileToZip, String zipFile,
            boolean excludeToplevelFolder) throws IOException {
        ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(zipFile));
        try {
            File srcFile = new File(fileToZip);
            if (excludeToplevelFolder && srcFile.isDirectory()) {
                for (String fileName : srcFile.list()) {
                    addToZip("", fileToZip + "/" + fileName, zipOut);
                }
            } else {
                addToZip("", fileToZip, zipOut);
            }
            zipOut.flush();
        } finally {
            zipOut.close();
        }
        return new File(zipFile);
    }

    private static void addToZip(String path, String srcFile,
            ZipOutputStream zipOut) throws IOException {
        File file = new File(srcFile);
        String filePath = ("".equals(path) ? file.getName() 
                                           : path + "/" + file.getName());
        if (file.isDirectory()) {
            for (String fileName : file.list()) {
                addToZip(filePath, srcFile + "/" + fileName, zipOut);
            }
        } else {
            zipOut.putNextEntry(new ZipEntry(filePath));
            FileInputStream in = new FileInputStream(srcFile);
            try {
                byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
                int len;
                while ((len = in.read(buffer)) != -1) {
                    zipOut.write(buffer, 0, len);
                }
            } finally {
                in.close();
            }
        }
    }
}