package org.diplomska.jpeg.processor.block.step;

import org.diplomska.jpeg.encoder.JpegImageBlock;
import org.diplomska.jpeg.processor.block.JpegImageBlockProcessorStep;
import org.diplomska.jpeg.util.JpegImageUtils;
import org.diplomska.jpeg.util.RgbColor;

import org.jblas.DoubleMatrix;

//~--- classes ----------------------------------------------------------------

/**
 * Discrete cosine transform processor step.
 *
 *
 * @version        1.0, 15/04/13
 * @author         Ales Kunst
 */
public class DiscreteCosineTransformBlockStep implements JpegImageBlockProcessorStep {

   /** Field description */
   DoubleMatrix fCbChromaMatrix;

   /** Field description */
   DoubleMatrix fCrChromaMatrix;

   /** Field description */
   DoubleMatrix fYLumaMatrix;

   //~--- methods -------------------------------------------------------------

   /**
    * Execute Discrete cosine transform on image block.
    *
    *
    * @param aJpegImageBlock jpeg image block
    */
   @Override
   public void execute(JpegImageBlock aJpegImageBlock) {
      initializeMatrices(aJpegImageBlock);
      calculateDct(aJpegImageBlock);
   }

   /**
    * Method description
    *
    *
    * @param aJpegImageBlock
    */
   private void calculateDct(JpegImageBlock aJpegImageBlock) {
      aJpegImageBlock.setMatrixYLumaForCalculation(JpegImageUtils.calculateDct(fYLumaMatrix));
      aJpegImageBlock.setMatrixCrChromaForCalculation(JpegImageUtils.calculateDct(fCrChromaMatrix));
      aJpegImageBlock.setMatrixCbChromaForCalculation(JpegImageUtils.calculateDct(fCbChromaMatrix));
   }

   /**
    *    Initialize matrices used for DCT.
    *
    *
    *    @param aJpegImageBlock
    */
   private void initializeMatrices(JpegImageBlock aJpegImageBlock) {
      fYLumaMatrix    = new DoubleMatrix(JpegImageUtils.JPEG_IMAGE_BLOCK_WIDTH, JpegImageUtils.JPEG_IMAGE_BLOCK_HEIGHT);
      fCrChromaMatrix = new DoubleMatrix(JpegImageUtils.JPEG_IMAGE_BLOCK_WIDTH, JpegImageUtils.JPEG_IMAGE_BLOCK_HEIGHT);
      fCbChromaMatrix = new DoubleMatrix(JpegImageUtils.JPEG_IMAGE_BLOCK_WIDTH, JpegImageUtils.JPEG_IMAGE_BLOCK_HEIGHT);

      int[][] colorMatrix = aJpegImageBlock.getColorMatrix();

      for (int row = 0; row < JpegImageUtils.JPEG_IMAGE_BLOCK_HEIGHT; row++) {
         for (int column = 0; column < JpegImageUtils.JPEG_IMAGE_BLOCK_WIDTH; column++) {

            // int yLumaColor    = YCrCbColor.getYLuma(yCrCbMatrix[row][column]);
            // int crChromaColor = YCrCbColor.getCrChroma(yCrCbMatrix[row][column]);
            // int cbChromaColor = YCrCbColor.getCbChroma(yCrCbMatrix[row][column]);
//          boolean isYLumaInInterval = (-128 >= yLumaColor) || (yLumaColor <= 127);
//          boolean isCrChromaInInterval = (-128 >= crChromaColor) || (crChromaColor <= 127);
//          boolean isCbChromaInInterval = (-128 >= cbChromaColor) || (cbChromaColor <= 127);
//          
//          if (!(isYLumaInInterval && isCbChromaInInterval && isCrChromaInInterval)) {
//              JpegImageUtils.throwJpegImageEncoderExeption("Outside of interval!!!!");
//          }
//
//          fYLumaMatrix.put(row, column, yLumaColor);
//          fCbChromaMatrix.put(row, column, cbChromaColor);
//          fCrChromaMatrix.put(row, column, crChromaColor);
            double red   = RgbColor.getRed(colorMatrix[row][column]);
            double green = RgbColor.getGreen(colorMatrix[row][column]);
            double blue  = RgbColor.getBlue(colorMatrix[row][column]);

            // Conversion of RGB -> Y luma
            // We must subtract 128 later because we have to convert interval [0..255] to [-128..127]
            // The must be done later for conversion to this interval is because it is encoded to
            // one byte which has range [-128..127]
            double yLuma = (0.299 * red) + (0.5876 * green) + (0.114 * blue);

            // double yLuma = 0.257 * red + 0.504 * green + 0.098 * blue + 16;
            // Conversion of RGB -> Cb Chroma ... interval [-128..127]
            double cbChroma = ((blue - yLuma) / (2 - (2 * 0.114)));

            // double cbChroma = 128 - (0.1687 * red) - (0.3313 * blue) + (0.5 * green);
            // Conversion of RGB -> Cr Chroma ... interval [-128..127]
            double crChroma = ((red - yLuma) / (2 - (2 * 0.299)));

            // double crChroma = 128 + (0.5 * red) - (0.4187 * green) - (0.0813 * blue);
            // double crChroma = ((red - yLuma) / (2 - (2 * 0.299)));
            // Y = 0.257 * r + 0.504 * g + 0.098 * b + 16
            // Cb = -0.148R´ - 0.291G´ + 0.439B´ + 128
            // Cr = 0.439R´ - 0.368G´ - 0.071B´ + 128
            // YCbCr = (220, 119, 132)
            // Conversion of RGB -> Cr Chroma ... interval [-128..127]
            // double crChroma = (0.5 * red) - (0.4187 * green) - (0.0813 * blue) + 128;
            yLuma    = JpegImageUtils.clipValue(yLuma - 128, -128, 127);
            cbChroma = JpegImageUtils.clipValue(cbChroma, -128, 127);
            crChroma = JpegImageUtils.clipValue(crChroma, -128, 127);

            // YCbCr = (220, 119, 132)
            fYLumaMatrix.put(row, column, yLuma);
            fCbChromaMatrix.put(row, column, cbChroma);
            fCrChromaMatrix.put(row, column, crChroma);
         }
      }
   }
}
