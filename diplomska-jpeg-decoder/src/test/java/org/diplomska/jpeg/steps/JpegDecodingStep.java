package org.diplomska.jpeg.steps;

import org.diplomska.jpeg.decoder.JpegDecoder;

import java.io.IOException;
import java.io.InputStream;

import java.util.List;

//~--- classes ----------------------------------------------------------------

/**
 * Abstract test class.
 *
 *
 * @version        1.0, 15/11/27
 * @author         Ales Kunst
 */
public abstract class JpegDecodingStep {

   /**
    * Execute jpeg decoder.
    *
    *
    * @param fileName
    * @param steps
    *
    * @return
    */
   protected JpegDecoderContext executeDecoding(String fileName, List<JpegDecoderStep> steps) {
      InputStream        inStream                 = null;
      JpegDecoderContext returnJpegDecoderContext = null;

      try {
         try {
            inStream                 = getResourceStream(fileName);
            returnJpegDecoderContext = executeDecoding(inStream, steps);
         } finally {
            inStream.close();
         }
      } catch (IOException ioe) {
         throw new RuntimeException(ioe);
      }

      return returnJpegDecoderContext;
   }

   /**
    * Method description
    *
    *
    * @param inStream
    * @param steps
    *
    * @return
    */
   protected JpegDecoderContext executeDecoding(InputStream inStream, List<JpegDecoderStep> steps) {
      JpegDecoder        jpegDecoder        = new JpegDecoder(steps);
      JpegDecoderContext jpegDecoderContext = null;

      try {
         try {
            jpegDecoder.decode(inStream);
            jpegDecoderContext = jpegDecoder.getJpegDecoderContext();
         } finally {
            inStream.close();
         }
      } catch (IOException ioe) {
         throw new RuntimeException(ioe);
      }

      return jpegDecoderContext;
   }

   //~--- get methods ---------------------------------------------------------

   /**
    * Get file.
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
