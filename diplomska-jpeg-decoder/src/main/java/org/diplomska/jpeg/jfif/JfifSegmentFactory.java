package org.diplomska.jpeg.jfif;

import java.util.Arrays;

//~--- classes ----------------------------------------------------------------

/**
 * Class description
 *
 *
 * @version        1.0, 15/11/16
 * @author         Ales Kunst
 */
public class JfifSegmentFactory {

   /** Start of image marker */
   public static final byte[] SOI_MARKER = { (byte) 0xFF, (byte) 0xD8 };

   /** JFIF marker */
   public static final byte[] JFIF_MARKER = { (byte) 0xFF, (byte) 0xE0 };

   /** JFIF identifier */
   private static final byte[] JFIF_IDENTIFIER = { (byte) 0x4A, (byte) 0x46, (byte) 0x49, (byte) 0x46, (byte) 0x00 };

   /** JFIF version is 1.2 */
   private static final byte[] JFIF_VERSION = { (byte) 0x01, (byte) 0x02 };

   /** Define quantum tables marker */
   public static final byte[] DQT_MARKER = { (byte) 0xFF, (byte) 0xDB };

   /** Define Huffman tables marker */
   public static final byte[] DHT_MARKER = { (byte) 0xFF, (byte) 0xC4 };

   /** Start of frame marker */
   public static final byte[] SOF_MARKER = { (byte) 0xFF, (byte) 0xC0 };

   /** Start of scan marker */
   public static final byte[] SOS_MARKER = { (byte) 0xFF, (byte) 0xDA };

   /** End of image marker */
   public static final byte[] EOI_MARKER = { (byte) 0xFF, (byte) 0xD9 };

   //~--- methods -------------------------------------------------------------

   /**
    * Creator method.
    *
    *
    * @param marker
    *
    * @return
    */
   public static JfifSegment createJfifSegment(byte[] marker) {
      JfifSegment returnJfifSegment = null;

      if (Arrays.equals(JFIF_MARKER, marker)) {
         returnJfifSegment = new JfifHeaderSegment(marker);
      } else if (Arrays.equals(DQT_MARKER, marker)) {
         returnJfifSegment = new JfifQuntizationTableSegment(marker);
      } else if (Arrays.equals(SOF_MARKER, marker)) {
         returnJfifSegment = new JfifSofSegment(marker);
      } else if (Arrays.equals(DHT_MARKER, marker)) {
         returnJfifSegment = new JfifHuffmanTableSegment(marker);
      } else if (Arrays.equals(SOS_MARKER, marker)) {
         returnJfifSegment = new JfifSosSegment(marker);
      } else {
         returnJfifSegment = new JfifUnknownSegment(marker);
      }

      return returnJfifSegment;
   }
}
