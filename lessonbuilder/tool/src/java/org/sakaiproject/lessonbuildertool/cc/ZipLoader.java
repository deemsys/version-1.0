package org.sakaiproject.lessonbuildertool.cc;

/***********
 * This code is based on a reference implementation done for the IMS Consortium.
 * The copyright notice for that implementation is included below. 
 * All modifications are covered by the following copyright notice.
 *
 * Copyright (c) 2011 Rutgers, the State University of New Jersey
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");                                                                
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.opensource.org/licenses/ECL-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**********************************************************************************
 * $URL: http://ims-dev.googlecode.com/svn/trunk/cc/IMS_CCParser_v1p0/src/main/java/org/imsglobal/cc/ZipLoader.java $
 * $Id: ZipLoader.java 227 2011-01-08 18:26:55Z drchuck $
 **********************************************************************************
 *
 * Copyright (c) 2010 IMS GLobal Learning Consortium
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License. 
 *
 **********************************************************************************/

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.PrintWriter;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * 
 * This is a simple example of a cartridge loader. This class needs to provide access to files within a cartridge. The 
 * parser DOES NOT CARE HOW. In this case, we assume that a zip file is given to us, which we unzip into the specified 
 * directory or the system temp directory. We don't clean up after ourselves. 
 * 
 * Implementers will need to provide a cartridge loader of their own, which can return files from cartridges to the parser,
 * as requested. The parser uses paths and filenames from the manifest. If a file cannot be found, then an exception should
 * be thrown.
 * 
 * Another example of a loader can be found in the test code (TestLoader).
 * 
 * @author Phil Nicholls
 * @version 1.0
 *
 */

public class ZipLoader implements CartridgeLoader {

  private File root;
  private String rootPath;
  private File cc;
  private boolean unzipped;
  private final int BUFFER=4096;
  
  private
  ZipLoader(File the_cc, File dir) throws IOException {
    root=dir;
    cc=the_cc;
    unzipped=false;
    rootPath = root.getCanonicalPath();
  }
  
  private void
  unzip() throws FileNotFoundException, IOException {
    if (!unzipped) {
      BufferedOutputStream dest=null;
      FileInputStream fis = new FileInputStream(cc);
      ZipInputStream zis = new ZipInputStream(new BufferedInputStream(fis));
      ZipEntry entry;
      while ((entry = zis.getNextEntry())!=null) {
        File target=new File(root, entry.getName());
	// not sure if you can put things like .. into a zip file, but be careful
	if (!target.getCanonicalPath().startsWith(rootPath))
	    throw new FileNotFoundException(target.getCanonicalPath());
        if (entry.isDirectory()) {
          target.mkdirs();
        } else {
          if (target.getParentFile().exists()==false) {
            target.getParentFile().mkdirs();
          }
          int count;
          byte data[] = new byte[BUFFER];
          FileOutputStream fos = new FileOutputStream(target);
          dest=new BufferedOutputStream(fos, BUFFER);
          while ((count = zis.read(data,0,BUFFER))!=-1) {
            dest.write(data,0,count);
          }
          dest.flush();
          dest.close();
	  // System.out.println("wrote file " + target);
        }
      }
      zis.close();
      fis.close();
      unzipped=true;
    }
  }
  
  /* (non-Javadoc)
   * @see com.psydev.ims.cc.CCParser.CartridgeUtils#getFile(java.lang.String)
   */
  public InputStream
  getFile(String the_target) throws FileNotFoundException, IOException {
    unzip();
    // System.out.println("getfile " + root + "::"  + the_target + "::" + (new File(root, the_target)).getCanonicalPath());
    File f = new File(root, the_target);
    // check for people using .. or other tricks
    if (!f.getCanonicalPath().startsWith(rootPath))
	throw new FileNotFoundException(f.getCanonicalPath());
    return new FileInputStream(new File(root, the_target));
  }
    
  public static CartridgeLoader
    getUtilities(File the_cc, String unzip_dir) throws FileNotFoundException, IOException {
    File unzip=new File(unzip_dir,the_cc.getName());
    if (!unzip.exists()) {
      unzip.mkdir();
    }
    return new ZipLoader(the_cc, unzip);
  }
  
  public static CartridgeLoader
    getUtilities(File the_cc) throws FileNotFoundException, IOException {
    return getUtilities(the_cc, System.getProperty("java.io.tmpdir"));
  }
  
}
