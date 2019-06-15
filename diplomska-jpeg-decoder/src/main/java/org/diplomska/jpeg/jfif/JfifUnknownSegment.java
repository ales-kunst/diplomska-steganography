package org.diplomska.jpeg.jfif;

/**
 * Unknown segment class.
 *
 *
 * @version        1.0, 15/11/18
 * @author         Ales Kunst
 */
public class JfifUnknownSegment extends JfifSegment {

   /**
    * Constructs ...
    *
    *
    * @param marker
    */
   public JfifUnknownSegment(byte[] marker) {
      super(marker);
   }

   //~--- get methods ---------------------------------------------------------

   /**
    * Getter.
    *
    *
    * @return
    */
   @Override
   public byte[] getPureSegmentContents() {
      return null;
   }
}
