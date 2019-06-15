package org.diplomska.jpeg.processor;

import org.diplomska.jpeg.encoder.JpegImageBlock;
import org.diplomska.jpeg.encoder.JpegImageContext;
import org.diplomska.jpeg.processor.block.JpegImageBlockProcessorStep;
import org.diplomska.jpeg.processor.image.JpegImageProcessorStep;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.Map;

//~--- classes ----------------------------------------------------------------

/**
 * Class description
 *
 *
 * @version        1.0, 15/04/03
 * @author         Ales Kunst
 */
public class JpegImageBlockProcessor {

   /** LOGGER */
   private static final Logger LOGGER = LoggerFactory.getLogger(JpegImageBlockProcessor.class);

   //~--- fields --------------------------------------------------------------

   /** Field description */
   Map<String, JpegImageBlockProcessorStep> fJpegImageBlockProcessingSteps;

   /** Field description */
   Map<String, JpegImageProcessorStep> fJpegImageProcessingSteps;

   //~--- constructors --------------------------------------------------------

   /**
    * Default constructor.
    *
    */
   public JpegImageBlockProcessor() {
      fJpegImageBlockProcessingSteps = new LinkedHashMap<String, JpegImageBlockProcessorStep>();
      fJpegImageProcessingSteps      = new LinkedHashMap<String, JpegImageProcessorStep>();
   }

   /**
    * Default constructor.
    *
    *
    * @param aJpegImageBlockProcessingSteps
    * @param aJpegImageProcessingSteps
    */
   public JpegImageBlockProcessor(Map<String, JpegImageBlockProcessorStep> aJpegImageBlockProcessingSteps,
                                  Map<String, JpegImageProcessorStep> aJpegImageProcessingSteps) {
      this();
      fJpegImageBlockProcessingSteps.putAll(aJpegImageBlockProcessingSteps);
      fJpegImageProcessingSteps.putAll(aJpegImageProcessingSteps);
   }

   //~--- get methods ---------------------------------------------------------

   /**
    * Method description
    *
    *
    * @param stepName
    *
    * @return
    */
   public JpegImageBlockProcessorStep getJpegImageBlockProcessorStep(String stepName) {
      return fJpegImageBlockProcessingSteps.get(stepName);
   }

   /**
    * Method description
    *
    *
    * @param stepName
    *
    * @return
    */
   public JpegImageProcessorStep getJpegImageProcessorStep(String stepName) {
      return fJpegImageProcessingSteps.get(stepName);
   }

   //~--- methods -------------------------------------------------------------

   /**
    * Proces one Jpeg image.
    *
    *
    *
    *
    * @param aJpegImageContext
    */
   public void processJpegImage(JpegImageContext aJpegImageContext) {
      for (Map.Entry<String, JpegImageProcessorStep> entry : fJpegImageProcessingSteps.entrySet()) {
         LOGGER.debug("Executing Jpeg image block step [{}]", entry.getKey());
         entry.getValue().execute(aJpegImageContext);
      }
   }

   /**
    * Proces one Jpeg image block.
    *
    *
    * @param aJpegImageBlock
    */
   public void processJpegImageBlock(JpegImageBlock aJpegImageBlock) {
      for (Map.Entry<String, JpegImageBlockProcessorStep> entry : fJpegImageBlockProcessingSteps.entrySet()) {
         LOGGER.debug("Executing Jpeg image step [{}]", entry.getKey());
         entry.getValue().execute(aJpegImageBlock);
      }
   }

   /**
    * Put jpeg image block processing step into the processing list.
    *
    *
    * @param aName
    * @param aImageProcessorStep
    *
    * @return
    */
   public JpegImageBlockProcessorStep putJpegImageBlockProcessingStep(String aName,
                                                                      JpegImageBlockProcessorStep aImageProcessorStep) {
      return fJpegImageBlockProcessingSteps.put(aName, aImageProcessorStep);
   }

   /**
    * Put image processing step into processing list
    *
    *
    * @param aName
    * @param aJpegImageProcessorStep
    *
    * @return
    */
   public JpegImageProcessorStep putJpegImageProcessingStep(String aName,
                                                            JpegImageProcessorStep aJpegImageProcessorStep) {
      return fJpegImageProcessingSteps.put(aName, aJpegImageProcessorStep);
   }
}
