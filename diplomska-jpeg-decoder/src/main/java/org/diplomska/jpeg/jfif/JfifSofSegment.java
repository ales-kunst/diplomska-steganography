package org.diplomska.jpeg.jfif;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

//~--- classes ----------------------------------------------------------------

/**
 * Class description
 *
 *
 * @version        1.0, 15/11/18
 * @author         Ales Kunst
 */
public class JfifSofSegment extends JfifSegment {

   /** Field description */
   public static int OFFSET_IMAGE_HEIGHT = 1;

   /** Field description */
   public static int OFFSET_IMAGE_WIDTH = 3;

   //~--- constructors --------------------------------------------------------

   /**
    * Constructs ...
    *
    *
    * @param marker
    */
   public JfifSofSegment(byte[] marker) {
      super(marker);
   }

   //~--- get methods ---------------------------------------------------------

   /**
    * Get image height.
    *
    *
    * @return
    */
   public int getImageHeight() {
      byte[] heightBytes = new byte[2];

      System.arraycopy(getPureSegmentContents(), OFFSET_IMAGE_HEIGHT, heightBytes, 0, heightBytes.length);

      return ByteBuffer.wrap(heightBytes).order(ByteOrder.BIG_ENDIAN).getShort();
   }

   /**
    * Method description
    *
    *
    * @return
    */
   public int getImageWidth() {
      byte[] widthBytes = new byte[2];

      System.arraycopy(getPureSegmentContents(), OFFSET_IMAGE_WIDTH, widthBytes, 0, widthBytes.length);

      return ByteBuffer.wrap(widthBytes).order(ByteOrder.BIG_ENDIAN).getShort();
   }
}
