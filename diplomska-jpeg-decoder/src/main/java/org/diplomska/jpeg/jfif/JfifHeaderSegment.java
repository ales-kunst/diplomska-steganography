package org.diplomska.jpeg.jfif;

/**
 * Jfif header segment (SOI).
 *
 *
 * @version        1.0, 15/11/16
 * @author         Ales Kunst
 */
public class JfifHeaderSegment extends JfifSegment {

   /**
    * Constructs ...
    *
    *
    * @param marker
    */
   public JfifHeaderSegment(byte[] marker) {
      super(marker);
   }
}
