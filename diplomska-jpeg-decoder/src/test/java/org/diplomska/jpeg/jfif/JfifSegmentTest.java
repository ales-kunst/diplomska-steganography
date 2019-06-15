package org.diplomska.jpeg.jfif;

import java.io.InputStream;

//~--- classes ----------------------------------------------------------------

/**
 * Class description
 *
 *
 * @version        Enter version here..., 16/04/12
 * @author         Enter your name here...
 */
public abstract class JfifSegmentTest {

   /**
    * Method description
    *
    *
    * @param filename
    *
    * @return
    */
   protected InputStream getResourceStream(String filename) {
      return ClassLoader.getSystemClassLoader().getResourceAsStream(filename);
   }
}
