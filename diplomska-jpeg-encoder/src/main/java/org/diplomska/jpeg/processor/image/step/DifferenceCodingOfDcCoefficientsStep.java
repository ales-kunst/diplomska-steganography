package org.diplomska.jpeg.processor.image.step;

import org.diplomska.jpeg.encoder.JpegImageBlock;
import org.diplomska.jpeg.encoder.JpegImageContext;
import org.diplomska.jpeg.processor.image.JpegImageProcessorStep;
import org.diplomska.jpeg.util.JpegImageUtils;
import org.diplomska.jpeg.util.RunLengthCodingStructure;

//~--- classes ----------------------------------------------------------------

/**
 * Class description
 *
 *
 * @version        1.0, 15/06/10
 * @author         Ales Kunst
 */
public class DifferenceCodingOfDcCoefficientsStep implements JpegImageProcessorStep {

   /**
    * Method description
    *
    *
    * @param aJpegImageContext
    */
   @Override
   public void execute(JpegImageContext aJpegImageContext) {
      int rows                          = aJpegImageContext.getJpegImageBlocks().length;
      int columns                       = aJpegImageContext.getJpegImageBlocks()[0].length;
      int previousDcYLumaCoefficient    = 0;
      int previousDcCbChromaCoefficient = 0;
      int previousDcCrChromaCoefficient = 0;

      for (int row = 0; row < rows; row++) {
         for (int column = 0; column < columns; column++) {
            JpegImageBlock currentJpegImageBlock = aJpegImageContext.getJpegImageBlocks()[row][column];

            previousDcYLumaCoefficient = calculateDcCoefficientForYLuma(currentJpegImageBlock,
                                                                        previousDcYLumaCoefficient);
            previousDcCbChromaCoefficient = calculateDcCoefficientForCbChroma(currentJpegImageBlock,
                                                                              previousDcCbChromaCoefficient);
            previousDcCrChromaCoefficient = calculateDcCoefficientForCrChroma(currentJpegImageBlock,
                                                                              previousDcCrChromaCoefficient);
         }
      }
   }

   /**
    *    Calculate difference of two Dc values
    *
    *
    *    @param aCurrentDcComponent
    *    @param aPreviousDcCoefficient
    *
    *    @return
    */
   protected int calculateDcCoefficientDifference(int aCurrentDcComponent, int aPreviousDcCoefficient) {
      RunLengthCodingStructure rlcStructure;
      int                      differenceDcCoefficient = 0;
      int                      returnNewDcComponent    = 0;

      rlcStructure            = JpegImageUtils.decodeFromEncodedZeroRunLengthCoding(aCurrentDcComponent);
      differenceDcCoefficient = rlcStructure.getCoefficientNumber() - aPreviousDcCoefficient;
      returnNewDcComponent    = JpegImageUtils.encodeToZeroRunLengthCoding(rlcStructure.fNumberOfPreceedingZeroes,
                                                                           differenceDcCoefficient);

      return returnNewDcComponent;
   }

   /**
    * Calculate difference for Cb component.
    *
    *
    * @param aJpegImageBlock
    * @param aPreviousDcCbChromaCoefficient
    *
    * @return
    */
   protected int calculateDcCoefficientForCbChroma(JpegImageBlock aJpegImageBlock, int aPreviousDcCbChromaCoefficient) {
      RunLengthCodingStructure rlcStructure;
      int                      currentDcComponent    = 0;
      int                      newEncodedDcComponent = 0;

      currentDcComponent    = aJpegImageBlock.getZigZagRlcEncodedCbChromaArray()[0];
      rlcStructure          = JpegImageUtils.decodeFromEncodedZeroRunLengthCoding(currentDcComponent);
      newEncodedDcComponent = calculateDcCoefficientDifference(currentDcComponent, aPreviousDcCbChromaCoefficient);
      aJpegImageBlock.setDcCoefficientInZigZagRlcCbChromaArray(newEncodedDcComponent);

      return rlcStructure.getCoefficientNumber();
   }

   /**
    * Calculate difference for Cr component.
    *
    *
    * @param aJpegImageBlock
    * @param aPreviousDcCrChromaCoefficient
    *
    * @return
    */
   protected int calculateDcCoefficientForCrChroma(JpegImageBlock aJpegImageBlock, int aPreviousDcCrChromaCoefficient) {
      RunLengthCodingStructure rlcStructure;
      int                      currentDcComponent    = 0;
      int                      newEncodedDcComponent = 0;

      currentDcComponent    = aJpegImageBlock.getZigZagRlcEncodedCrChromaArray()[0];
      rlcStructure          = JpegImageUtils.decodeFromEncodedZeroRunLengthCoding(currentDcComponent);
      newEncodedDcComponent = calculateDcCoefficientDifference(currentDcComponent, aPreviousDcCrChromaCoefficient);
      aJpegImageBlock.setDcCoefficientInZigZagRlcCrChromaArray(newEncodedDcComponent);

      return rlcStructure.getCoefficientNumber();
   }

   /**
    * Calculate difference for Y component.
    *
    *
    * @param aJpegImageBlock
    * @param aPreviousDcYLumaCoefficient
    *
    * @return
    */
   protected int calculateDcCoefficientForYLuma(JpegImageBlock aJpegImageBlock, int aPreviousDcYLumaCoefficient) {
      RunLengthCodingStructure rlcStructure;
      int                      currentDcComponent    = 0;
      int                      newEncodedDcComponent = 0;

      currentDcComponent    = aJpegImageBlock.getZigZagRlcEncodedYLumaArray()[0];
      rlcStructure          = JpegImageUtils.decodeFromEncodedZeroRunLengthCoding(currentDcComponent);
      newEncodedDcComponent = calculateDcCoefficientDifference(currentDcComponent, aPreviousDcYLumaCoefficient);
      aJpegImageBlock.setDcCoefficientInZigZagRlcYLumaArray(newEncodedDcComponent);

      return rlcStructure.getCoefficientNumber();
   }
}
