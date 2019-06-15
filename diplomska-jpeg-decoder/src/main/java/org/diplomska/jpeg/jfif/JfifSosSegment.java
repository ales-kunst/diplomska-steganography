package org.diplomska.jpeg.jfif;

import java.io.ByteArrayOutputStream;

//~--- classes ----------------------------------------------------------------

/**
 * Start of scan segment.
 *
 *
 * @version        1.0, 26.nov.2015
 * @author         Ales Kunst
 */
public class JfifSosSegment extends JfifSegment {

   /** Field description */
   private static final int IMAGE_DATA_OFFSET = 12;

   //~--- constructors --------------------------------------------------------

   /**
    * Constructs ...
    *
    *
    * @param marker
    */
   public JfifSosSegment(byte[] marker) {
      super(marker);
   }

   //~--- get methods ---------------------------------------------------------

   /**
    * Method description
    *
    *
    * @return
    */
   public byte[] getImageData() {
      ByteArrayOutputStream returnImageData = new ByteArrayOutputStream();
      byte[]                rawImageData    = getRawImageData();

      for (int index = 0; index < rawImageData.length; index++) {
         byte    value         = rawImageData[index];
         boolean isZeroByte    = (0x00 == value);
         boolean isStuffedByte = false;

         if (index > 0) {
            boolean isPreviousMarkerByte = (rawImageData[index - 1] == (byte) 0xFF);

            isStuffedByte = isZeroByte && isPreviousMarkerByte;
         }

         if (!isStuffedByte) {
            returnImageData.write(value);
         }
      }

      return returnImageData.toByteArray();
   }

   /**
    * Get image data.
    *
    *
    * @return
    */
   public byte[] getRawImageData() {
      byte[] segmentData       = getContents().toByteArray();
      int    lengthOfImageData = segmentData.length - IMAGE_DATA_OFFSET;
      byte[] returnImageData   = new byte[lengthOfImageData];

      System.arraycopy(segmentData, IMAGE_DATA_OFFSET, returnImageData, 0, lengthOfImageData);

      return returnImageData;
   }
}
