package org.diplomska.jpeg.jfif;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import java.util.Arrays;

//~--- classes ----------------------------------------------------------------

/**
 * JfifSegment
 *
 *
 * @version        1.0, 15/11/16
 * @author         Ales Kunst
 */
public abstract class JfifSegment {

   /** Jfif marker */
   byte[] marker;

   /** Content of segment */
   private ByteArrayOutputStream contents;

   /** Field description */
   private short contentSize;

   //~--- constructors --------------------------------------------------------

   /**
    * Constructs ...
    *
    */
   private JfifSegment() {
      contents    = new ByteArrayOutputStream();
      contentSize = -1;
   }

   /**
    * Constructs ...
    *
    *
    * @param marker
    */
   public JfifSegment(byte[] marker) {
      this();
      this.marker = marker;
   }

   //~--- get methods ---------------------------------------------------------

   /**
    * Gets size of the contents
    *
    *
    * @return
    */
   public int getContentSize() {
      int returnContentSize = 0;

      if (contentSize >= 0) {
         returnContentSize = contentSize;
      } else {
         if (contents.size() > 0) {
            byte[] contentSizeBytes = Arrays.copyOf(contents.toByteArray(), 2);

            returnContentSize = ByteBuffer.wrap(contentSizeBytes).order(ByteOrder.BIG_ENDIAN).getShort();
         }
      }

      return returnContentSize;
   }

   /**
    * Get marker.
    *
    *
    * @return
    */
   public final byte[] getMarker() {
      return marker;
   }

   /**
    * Get raw contents of the segment.
    *
    *
    * @return
    */
   public byte[] getPureSegmentContents() {
      int    segmentLength        = getContentSize() - 2;
      byte[] returnSegmentContent = new byte[segmentLength];

      System.arraycopy(getContents().toByteArray(), 2, returnSegmentContent, 0, segmentLength);

      return returnSegmentContent;
   }

   //~--- methods -------------------------------------------------------------

   /**
    * Write byte.
    *
    *
    * @param value
    */
   public void write(byte value) {
      contents.write(value);
   }

   /**
    * Write bytes.
    *
    *
    * @param values
    *
    */
   public void write(byte[] values) {
      try {
         contents.write(values);
      } catch (IOException e) {
         throw new RuntimeException(e);
      }
   }

   /**
    * Write bytes.
    *
    *
    * @param values
    * @param offset
    * @param length
    */
   public void write(byte[] values, int offset, int length) {
      contents.write(values, offset, length);
   }

   //~--- get methods ---------------------------------------------------------

   /**
    * Getter.
    *
    *
    * @return
    */
   protected ByteArrayOutputStream getContents() {
      return contents;
   }
}
