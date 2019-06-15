package org.diplomska.jpeg.processor.block;

import org.diplomska.jpeg.encoder.JpegImageBlock;
import org.diplomska.util.structs.JpegImageConstants;

//~--- interfaces -------------------------------------------------------------

/**
 * Interface for Jpeg processing step.
 *
 *
 * @version        1.0, 15/04/03
 * @author         Ales Kunst
 */
public interface JpegImageBlockProcessorStep extends JpegImageConstants {

   /**
    * Execute step.
    *
    *
    * @param aJpegImageBlock
    *
    */
   void execute(JpegImageBlock aJpegImageBlock);
}
