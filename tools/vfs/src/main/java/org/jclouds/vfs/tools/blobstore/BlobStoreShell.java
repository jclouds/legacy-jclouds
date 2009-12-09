/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
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
 * ====================================================================
 */
package org.jclouds.vfs.tools.blobstore;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.commons.vfs.FileContent;
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystemException;
import org.apache.commons.vfs.FileSystemManager;
import org.apache.commons.vfs.FileType;
import org.apache.commons.vfs.FileUtil;
import org.apache.commons.vfs.Selectors;
import org.apache.commons.vfs.VFS;
import org.apache.commons.vfs.cache.SoftRefFilesCache;
import org.apache.commons.vfs.impl.DefaultFileSystemManager;
import org.jclouds.http.HttpUtils;
import org.jclouds.vfs.provider.blobstore.BlobStoreFileProvider;

import com.google.common.collect.Lists;

/**
 * A simple command-line shell for performing file operations. Adapted from original file:
 * org.apache.commons.vfs.example.Shell written by <a href="mailto:adammurdoch@apache.org">Adam
 * Murdoch</a> and Gary D. Gregory.
 * 
 * @author Adrian Cole
 * @author <a href="mailto:adammurdoch@apache.org">Adam Murdoch</a>
 * @author Gary D. Gregory
 */
public class BlobStoreShell {
   public static String INVALID_SYNTAX = "Invalid parameters. Syntax is: blobstore://account:key@service/container";

   private final DefaultFileSystemManager remoteMgr;
   private FileObject remoteCwd;

   private final FileSystemManager mgr;
   private FileObject cwd;

   private final BufferedReader reader;

   public static void main(String... args) {

      if (args.length != 1)
         throw new IllegalArgumentException(INVALID_SYNTAX);
      URI location;
      try {
         location = HttpUtils.createUri(args[0]);
         checkArgument(location.getScheme().equals("blobstore"), "wrong scheme");
      } catch (IllegalArgumentException e) {
         throw new IllegalArgumentException(String.format("%s%n%s", e.getMessage(), INVALID_SYNTAX));
      }
      try {
         (new BlobStoreShell(args[0])).go();
      } catch (Exception e) {
         e.printStackTrace();
         System.exit(1);
      }
      System.exit(0);
   }

   private BlobStoreShell(String uri) throws FileSystemException {
      remoteMgr = new DefaultFileSystemManager();
      remoteMgr.setFilesCache(new SoftRefFilesCache());
      remoteMgr.addProvider("blobstore", new BlobStoreFileProvider());
      remoteMgr.init();
      remoteCwd = remoteMgr.resolveFile(checkNotNull(uri, "uri"));
      mgr = VFS.getManager();
      cwd = mgr.resolveFile(System.getProperty("user.dir"));
      reader = new BufferedReader(new InputStreamReader(System.in));
   }

   private void go() throws Exception {
      System.out.println("BlobStore Shell [ 1.0 ]");
      while (true) {
         final String[] cmd = nextCommand();
         if (cmd == null) {
            return;
         }
         if (cmd.length == 0) {
            continue;
         }
         final String cmdName = cmd[0];
         if (cmdName.equalsIgnoreCase("exit") || cmdName.equalsIgnoreCase("quit")) {
            return;
         }
         try {
            handleCommand(cmd);
         } catch (final Exception e) {
            System.err.println("Command failed:");
            e.printStackTrace(System.err);
         }
      }
   }

   /**
    * Handles a command.
    */
   private void handleCommand(final String[] cmd) throws Exception {
      final String cmdName = cmd[0];
      if (cmdName.equalsIgnoreCase("cat")) {
         cat(cmd);
      } else if (cmdName.equalsIgnoreCase("cd")) {
         cd(cmd);
      } else if (cmdName.equalsIgnoreCase("lcd")) {
         lcd(cmd);
      } else if (cmdName.equalsIgnoreCase("cp")) {
         cp(cmd);
      } else if (cmdName.equalsIgnoreCase("put")) {
         put(cmd);
      } else if (cmdName.equalsIgnoreCase("get")) {
         get(cmd);
      } else if (cmdName.equalsIgnoreCase("help")) {
         help();
      } else if (cmdName.equalsIgnoreCase("ls")) {
         ls(cmd);
      } else if (cmdName.equalsIgnoreCase("lls")) {
         lls(cmd);
      } else if (cmdName.equalsIgnoreCase("pwd")) {
         pwd();
      } else if (cmdName.equalsIgnoreCase("lpwd")) {
         lpwd();
      } else if (cmdName.equalsIgnoreCase("rm")) {
         rm(cmd);
      } else if (cmdName.equalsIgnoreCase("touch")) {
         touch(cmd);
      } else {
         System.err.println("Unknown command \"" + cmdName + "\".");
      }
   }

   /**
    * Does a 'help' command.
    */
   private void help() {
      System.out.println("Commands:");
      System.out
               .println("cat <file>         Displays the contents of a file on the remote machine.");
      System.out.println("cd [folder]        Changes current folder on the remote machine.");
      System.out.println("lcd [folder]       Changes current folder on the local machine.");
      System.out.println("cp <src> <dest>    Copies a file or folder on the remote machine.");
      System.out
               .println("put <src> <dest>   Copies a file or folder on the local machine to remote one.");
      System.out
               .println("get <src> <dest>   Copies a file or folder on the remote machine to local one.");
      System.out.println("help               Shows this message on the remote machine.");
      System.out
               .println("ls [-R] [path]     Lists contents of a file or folder on the remote machine.");
      System.out
               .println("lls [-R] [path]    Lists contents of a file or folder on the local machine.");
      System.out.println("pwd                Displays current folder on the remote machine.");
      System.out.println("lpwd               Displays current folder on the local machine.");
      System.out.println("rm <path>          Deletes a file or folder on the remote machine.");
      System.out
               .println("touch <path>       Sets the last-modified time of a file on the remote machine.");
      System.out.println("exit       Exits this program.");
      System.out.println("quit       Exits this program.");
   }

   /**
    * Does an 'rm' command.
    */
   private void rm(final String[] cmd) throws Exception {
      if (cmd.length < 2) {
         throw new Exception("USAGE: rm <path>");
      }

      final FileObject file = remoteMgr.resolveFile(remoteCwd, cmd[1]);
      file.delete(Selectors.SELECT_SELF);
   }

   /**
    * Does a 'cp' command.
    */
   private void cp(final String[] cmd) throws Exception {
      if (cmd.length < 3) {
         throw new Exception("USAGE: cp <src> <dest>");
      }

      FileObject src = remoteMgr.resolveFile(remoteCwd, cmd[1]);
      FileObject dest = remoteMgr.resolveFile(remoteCwd, cmd[2]);
      if (dest.exists() && dest.getType() == FileType.FOLDER) {
         dest = dest.resolveFile(src.getName().getBaseName());
      }

      dest.copyFrom(src, Selectors.SELECT_ALL);
   }

   /**
    * Does a 'get' command.
    */
   private void get(final String[] cmd) throws Exception {
      if (cmd.length < 3) {
         throw new Exception("USAGE: get <src> <dest>");
      }

      FileObject src = remoteMgr.resolveFile(remoteCwd, cmd[1]);
      FileObject dest = mgr.resolveFile(cwd, cmd[2]);
      if (dest.exists() && dest.getType() == FileType.FOLDER) {
         dest = dest.resolveFile(src.getName().getBaseName());
      }

      dest.copyFrom(src, Selectors.SELECT_ALL);
   }

   /**
    * Does a 'put' command.
    */
   private void put(final String[] cmd) throws Exception {
      if (cmd.length < 3) {
         throw new Exception("USAGE: put <src> <dest>");
      }

      FileObject src = mgr.resolveFile(cwd, cmd[2]);
      FileObject dest = remoteMgr.resolveFile(remoteCwd, cmd[1]);
      if (dest.exists() && dest.getType() == FileType.FOLDER) {
         dest = dest.resolveFile(src.getName().getBaseName());
      }

      dest.copyFrom(src, Selectors.SELECT_ALL);
   }

   /**
    * Does a 'cat' command.
    */
   private void cat(final String[] cmd) throws Exception {
      if (cmd.length < 2) {
         throw new Exception("USAGE: cat <path>");
      }

      // Locate the file
      final FileObject file = remoteMgr.resolveFile(remoteCwd, cmd[1]);

      // Dump the contents to System.out
      FileUtil.writeContent(file, System.out);
      System.out.println();
   }

   /**
    * Does a 'pwd' command.
    */
   private void pwd() {
      System.out.println("Current remote folder is " + remoteCwd.getName().getFriendlyURI());
   }

   /**
    * Does a 'lpwd' command.
    */
   private void lpwd() {
      System.out.println("Current local folder is " + cwd.getName().getFriendlyURI());
   }

   /**
    * Does a 'cd' command. If the target directory does not exist, a message is printed to
    * <code>System.err</code>.
    */
   private void cd(final String[] cmd) throws Exception {
      final String path;
      if (cmd.length > 1) {
         path = cmd[1];
      } else {
         path = System.getProperty("user.home");
      }

      // Locate and validate the folder
      FileObject tmp = remoteMgr.resolveFile(remoteCwd, path);
      if (tmp.exists()) {
         remoteCwd = tmp;
      } else {
         System.out.println("Folder does not exist: " + tmp.getName().getFriendlyURI());
      }
      System.out.println("Current remote folder is " + remoteCwd.getName().getFriendlyURI());
   }

   /**
    * Does a 'lcd' command. If the target directory does not exist, a message is printed to
    * <code>System.err</code>.
    */
   private void lcd(final String[] cmd) throws Exception {
      final String path;
      if (cmd.length > 1) {
         path = cmd[1];
      } else {
         path = System.getProperty("user.home");
      }

      // Locate and validate the folder
      FileObject tmp = mgr.resolveFile(cwd, path);
      if (tmp.exists()) {
         cwd = tmp;
      } else {
         System.out.println("Folder does not exist: " + tmp.getName().getFriendlyURI());
      }
      System.out.println("Current local folder is " + cwd.getName().getFriendlyURI());
   }

   /**
    * Does an 'ls' command.
    */
   private void ls(final String[] cmd) throws FileSystemException {
      ls(remoteMgr, remoteCwd, cmd);
   }

   /**
    * Does an 'lls' command.
    */
   private void lls(final String[] cmd) throws FileSystemException {
      ls(mgr, cwd, cmd);
   }

   private void ls(FileSystemManager mg, FileObject wd, final String[] cmd)
            throws FileSystemException {
      int pos = 1;
      final boolean recursive;
      if (cmd.length > pos && cmd[pos].equals("-R")) {
         recursive = true;
         pos++;
      } else {
         recursive = false;
      }

      final FileObject file;
      if (cmd.length > pos) {
         file = mg.resolveFile(wd, cmd[pos]);
      } else {
         file = wd;
      }

      if (file.getType() == FileType.FOLDER) {
         // List the contents
         System.out.println("Contents of " + file.getName().getFriendlyURI());
         listChildren(file, recursive, "");
      } else {
         // Stat the file
         System.out.println(file.getName());
         final FileContent content = file.getContent();
         System.out.println("Size: " + content.getSize() + " bytes.");
         final DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.MEDIUM,
                  DateFormat.MEDIUM);
         final String lastMod = dateFormat.format(new Date(content.getLastModifiedTime()));
         System.out.println("Last modified: " + lastMod);
      }
   }

   /**
    * Does a 'touch' command.
    */
   private void touch(final String[] cmd) throws Exception {
      if (cmd.length < 2) {
         throw new Exception("USAGE: touch <path>");
      }
      final FileObject file = remoteMgr.resolveFile(remoteCwd, cmd[1]);
      if (!file.exists()) {
         file.createFile();
      }
      file.getContent().setLastModifiedTime(System.currentTimeMillis());
   }

   /**
    * Lists the children of a folder.
    */
   private void listChildren(final FileObject dir, final boolean recursive, final String prefix)
            throws FileSystemException {
      final FileObject[] children = dir.getChildren();
      for (int i = 0; i < children.length; i++) {
         final FileObject child = children[i];
         System.out.print(prefix);
         System.out.print(child.getName().getBaseName());
         if (child.getType() == FileType.FOLDER) {
            System.out.println("/");
            if (recursive) {
               listChildren(child, recursive, prefix + "    ");
            }
         } else {
            System.out.println();
         }
      }
   }

   /**
    * Returns the next command, split into tokens.
    */
   private String[] nextCommand() throws IOException {
      System.out.print("> ");
      final String line = reader.readLine();
      if (line == null) {
         return null;
      }
      final List<String> cmd = Lists.newArrayList();
      final StringTokenizer tokens = new StringTokenizer(line);
      while (tokens.hasMoreTokens()) {
         cmd.add(tokens.nextToken());
      }
      return (String[]) cmd.toArray(new String[cmd.size()]);
   }
}
