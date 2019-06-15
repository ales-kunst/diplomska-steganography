package org.diplomska.jpeg.encoder;

import org.diplomska.jpeg.processor.JpegImageBlockProcessor;
import org.diplomska.util.JpegImageEncoderUtils;

import org.perf4j.StopWatch;
import org.perf4j.slf4j.Slf4JStopWatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//~--- classes ----------------------------------------------------------------

/**
 * Jpeg encoder.
 *
 *
 * @version        1.0, 15/04/02
 * @author         Ales Kunst
 */
public class JpegEncoder {

   /** LOGGER */
   private static final Logger LOGGER = LoggerFactory.getLogger(JpegEncoder.class);

   //~--- fields --------------------------------------------------------------

   /** Jpeg image processor */
   private JpegImageBlockProcessor fJpegImageBlockProcessor;

   /** Jpeg image context */
   private JpegImageContext fJpegImageContext;

   //~--- constructors --------------------------------------------------------

   /**
    * Default constructor.
    *
    *
    *
    *
    * @param aJpegImageContext
    * @param aJpegImageBlockProcessor
    */
   public JpegEncoder(JpegImageContext aJpegImageContext, JpegImageBlockProcessor aJpegImageBlockProcessor) {
      fJpegImageContext        = aJpegImageContext;
      fJpegImageBlockProcessor = aJpegImageBlockProcessor;
   }

   //~--- methods -------------------------------------------------------------

   /**
    *    Method description
    *
    *
    *    @return
    */
   public boolean encode() {
      StopWatch STOPWATCH = new Slf4JStopWatch("JPEGEncode", JpegImageEncoderUtils.TIMING_LOGGER);

      for (int row = 0; row < fJpegImageContext.getJpegImageBlocksHeight(); row++) {
         STOPWATCH.start("JPEGEncode");

         for (int column = 0; column < fJpegImageContext.getJpegImageBlocksWidth(); column++) {
            JpegImageBlock jpegImageBlock = fJpegImageContext.getJpegImageBlock(column, row);

            fJpegImageBlockProcessor.processJpegImageBlock(jpegImageBlock);
         }

         STOPWATCH.lap("JPEGEncode");
      }

      fJpegImageBlockProcessor.processJpegImage(fJpegImageContext);
      STOPWATCH.stop("JPEGEncode");

      return true;
   }
}
