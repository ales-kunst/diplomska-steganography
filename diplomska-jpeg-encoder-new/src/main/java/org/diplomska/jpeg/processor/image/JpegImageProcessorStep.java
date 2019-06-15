package org.diplomska.jpeg.processor.image;

import org.diplomska.jpeg.encoder.JpegImageContext;
import org.diplomska.util.structs.JpegImageConstants;

//~--- interfaces -------------------------------------------------------------

/**
 * Interface description
 *
 *
 * @version        1.0, 15/04/30
 * @author         Ales Kunst
 */
public interface JpegImageProcessorStep extends JpegImageConstants {

   /**
    * Method which processes the whole jpeg image blocks
    *
    *
    *
    * @param aJpegImageContext
    */
   void execute(JpegImageContext aJpegImageContext);
}
