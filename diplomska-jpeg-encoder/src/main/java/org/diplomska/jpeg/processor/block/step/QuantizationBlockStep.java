package org.diplomska.jpeg.processor.block.step;

import org.diplomska.jpeg.encoder.JpegImageBlock;
import org.diplomska.jpeg.processor.block.JpegImageBlockProcessorStep;
import org.diplomska.jpeg.util.JpegImageUtils;

import org.jblas.DoubleMatrix;

//~--- classes ----------------------------------------------------------------

/**
 * Class description
 *
 *
 * @version        1.0, 15/04/17
 * @author         Ales Kunst
 */
public class QuantizationBlockStep implements JpegImageBlockProcessorStep {

   /** Luminance quantization matrix for calculation. */
   private DoubleMatrix fLuminanceQuantizationMatrix;

   /** Chrominance matrix for calculation. */
   private DoubleMatrix fChrominanceQuantizationMatrix;

   //~--- constructors --------------------------------------------------------

   /**
    * Constructs ...
    *
    */
   public QuantizationBlockStep() {
      fLuminanceQuantizationMatrix   = new DoubleMatrix(JpegImageUtils.getLuminanceQuantizationMatrix());
      fChrominanceQuantizationMatrix = new DoubleMatrix(JpegImageUtils.getChrominanceQuantizationMatrix());
   }

   //~--- methods -------------------------------------------------------------

   /**
    * Execute this step.
    *
    *
    * @param aJpegImageBlock
    */
   @Override
   public void execute(JpegImageBlock aJpegImageBlock) {
      DoubleMatrix yLumaQuantizedMatrix = aJpegImageBlock.getMatrixYLumaForCalculation()
                                                         .div(fLuminanceQuantizationMatrix);
      DoubleMatrix crChromaQuantizedMatrix = aJpegImageBlock.getMatrixCrChromaForCalculation()
                                                            .div(fChrominanceQuantizationMatrix);
      DoubleMatrix cbChromaQuantizedMatrix = aJpegImageBlock.getMatrixCbChromaForCalculation()
                                                            .div(fChrominanceQuantizationMatrix);
      DoubleMatrix yLumaQuantizedRoundedMatrix = new DoubleMatrix(JpegImageUtils.JPEG_IMAGE_BLOCK_WIDTH,
                                                                  JpegImageUtils.JPEG_IMAGE_BLOCK_HEIGHT);
      DoubleMatrix crChromaQuantizedRoundedMatrix = new DoubleMatrix(JpegImageUtils.JPEG_IMAGE_BLOCK_WIDTH,
                                                                     JpegImageUtils.JPEG_IMAGE_BLOCK_HEIGHT);
      DoubleMatrix cbChromaQuantizedRoundedMatrix = new DoubleMatrix(JpegImageUtils.JPEG_IMAGE_BLOCK_WIDTH,
                                                                     JpegImageUtils.JPEG_IMAGE_BLOCK_HEIGHT);

      for (int x = 0; x < JpegImageUtils.JPEG_IMAGE_BLOCK_WIDTH; x++) {
         for (int y = 0; y < JpegImageUtils.JPEG_IMAGE_BLOCK_HEIGHT; y++) {
            long yLumaRoundedValue    = Math.round(yLumaQuantizedMatrix.get(x, y));
            long crChromaRoundedValue = Math.round(crChromaQuantizedMatrix.get(x, y));
            long cbChromaRoundedValue = Math.round(cbChromaQuantizedMatrix.get(x, y));

            yLumaQuantizedRoundedMatrix.put(x, y, yLumaRoundedValue);
            crChromaQuantizedRoundedMatrix.put(x, y, crChromaRoundedValue);
            cbChromaQuantizedRoundedMatrix.put(x, y, cbChromaRoundedValue);
         }
      }

      aJpegImageBlock.setMatrixYLumaForCalculation(yLumaQuantizedRoundedMatrix);
      aJpegImageBlock.setMatrixCrChromaForCalculation(crChromaQuantizedRoundedMatrix);
      aJpegImageBlock.setMatrixCbChromaForCalculation(cbChromaQuantizedRoundedMatrix);
   }
}
