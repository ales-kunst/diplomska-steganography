package org.diplomska.jpeg.processor.image;

import org.diplomska.jpeg.encoder.JpegImageContext;

//~--- interfaces -------------------------------------------------------------

/**
 * Interface description
 *
 *
 * @version        1.0, 15/04/30
 * @author         Ales Kunst
 */
public interface JpegImageProcessorStep {

   /**
    * Method which processes the whole jpeg image blocks
    *
    *
    *
    * @param aJpegImageContext
    */
   void execute(JpegImageContext aJpegImageContext);
}
