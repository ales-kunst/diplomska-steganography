package org.diplomska.jpeg.decoder;

import org.diplomska.jpeg.jfif.JfifSegment;
import org.diplomska.jpeg.jfif.JfifSegmentFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

//~--- classes ----------------------------------------------------------------

/**
 * Class description
 *
 *
 * @version        1.0, 15/11/16
 * @author         Ales Kunst
 */
public class ReadJpegInJfif {

   /** Field description */
   List<JfifSegment> jfifSegments;

   /** Jpeg file contents */
   byte[] jpegContents;

   //~--- constructors --------------------------------------------------------

   /**
    * Constructs ...
    *
    */
   private ReadJpegInJfif() {}

   /**
    * Constructs ...
    *
    *
    * @param inStream
    */
   public ReadJpegInJfif(InputStream inStream) {
      this();
      initialize(inStream);
   }

   //~--- methods -------------------------------------------------------------

   /**
    * Read contents from stream.
    *
    *
    * @param inStream
    *
    * @return Returns byte array of file contents
    *
    * @throws IOException
    */
   protected static byte[] readFromFile(InputStream inStream) throws IOException {
      int                   readBufferSize = 4096;
      int                   bytesRead      = 0;
      byte[]                readBuffer     = new byte[readBufferSize];
      ByteArrayOutputStream outBuffer      = new ByteArrayOutputStream();

      while ((bytesRead = inStream.read(readBuffer, 0, readBufferSize)) != -1) {
         outBuffer.write(readBuffer, 0, bytesRead);
      }

      return outBuffer.toByteArray();
   }

   //~--- get methods ---------------------------------------------------------

   /**
    * Search for Jfif segments.
    *
    *
    * @param marker
    *
    * @return
    */
   public List<JfifSegment> getJfifSegments(byte[] marker) {
      List<JfifSegment> returnJfifSegment = new ArrayList<JfifSegment>();

      for (JfifSegment jfifSegment : jfifSegments) {
         if (Arrays.equals(jfifSegment.getMarker(), marker)) {
            returnJfifSegment.add(jfifSegment);
         }
      }

      return returnJfifSegment;
   }

   /**
    * Getter.
    *
    *
    * @return
    */
   protected byte[] getJpegContents() {
      return jpegContents;
   }

   //~--- methods -------------------------------------------------------------

   /**
    * Method description
    *
    */
   protected void initializeJfifSegments() {
      int index = 0;

      // ByteArrayOutputStream segmentValues = new ByteArrayOutputStream();
      JfifSegment jfifSegment = null;

      while (!isIndexOutOfBounds(index)) {
         byte    value    = jpegContents[index];
         boolean isMarker = (value == (byte) 0xFF);

         if (isMarker) {
            index++;

            byte    nextValue     = jpegContents[index];
            byte[]  marker        = { (byte) 0xFF, nextValue };
            boolean isStuffedByte = (nextValue == (byte) 0x00);
            boolean isEndOfFile   = (nextValue == (byte) 0xD9);

            // If it is new segment
            if (!isStuffedByte && !isEndOfFile) {
               jfifSegment = JfifSegmentFactory.createJfifSegment(marker);
               jfifSegments.add(jfifSegment);
            } else if (isEndOfFile) {
               jfifSegment = JfifSegmentFactory.createJfifSegment(marker);
               jfifSegments.add(jfifSegment);
            } else {

               // In case it is just stuffed byte then add the two bytes to segment
               jfifSegment.write(marker);
            }
         } else {
            jfifSegment.write(value);
         }

         index++;
      }
   }

   /**
    * Initialization.
    *
    *
    * @param inStream
    */
   private void initialize(InputStream inStream) {
      try {
         jfifSegments = new ArrayList<JfifSegment>();
         jpegContents = ReadJpegInJfif.readFromFile(inStream);
         initializeJfifSegments();
      } catch (IOException e) {
         throw new RuntimeException(e);
      }
   }

   //~--- get methods ---------------------------------------------------------

   /**
    * Method description
    *
    *
    * @param index
    *
    * @return
    */
   private boolean isIndexOutOfBounds(int index) {
      return index >= jpegContents.length;
   }
}
