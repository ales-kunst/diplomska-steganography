package org.diplomska.jpeg.processor.block;

import org.diplomska.jpeg.encoder.JpegImageBlock;

//~--- interfaces -------------------------------------------------------------

/**
 * Interface for Jpeg processing step.
 *
 *
 * @version        1.0, 15/04/03
 * @author         Ales Kunst
 */
public interface JpegImageBlockProcessorStep {

   /**
    * Execute step.
    *
    *
    * @param aJpegImageBlock
    *
    */
   void execute(JpegImageBlock aJpegImageBlock);
}
