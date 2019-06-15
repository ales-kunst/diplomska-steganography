package org.diplomska.jpeg.steps;

/**
 * Jpeg decoder step interface
 *
 *
 * @version        1.0, 15/11/27
 * @author         Ales Kunst
 */
public interface JpegDecoderStep {

   /**
    * Execute method.
    *
    *
    * @param jpegDecoderContext
    */
   void execute(JpegDecoderContext jpegDecoderContext);

   //~--- get methods ---------------------------------------------------------

   /**
    * Get step name.
    *
    *
    * @return
    */
   String getName();
}
