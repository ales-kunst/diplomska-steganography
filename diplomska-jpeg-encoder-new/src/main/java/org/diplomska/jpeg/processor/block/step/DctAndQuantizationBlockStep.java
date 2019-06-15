package org.diplomska.jpeg.processor.block.step;

import org.diplomska.jpeg.encoder.JpegImageBlock;
import org.diplomska.jpeg.processor.block.JpegImageBlockProcessorStep;
import org.diplomska.util.structs.RgbColor;

import org.jblas.DoubleMatrix;

//~--- classes ----------------------------------------------------------------

/**
 * Class description
 *
 *
 * @version        Enter version here..., 16/04/13
 * @author         Enter your name here...
 */
public class DctAndQuantizationBlockStep implements JpegImageBlockProcessorStep {

   /** 2D discrete cosine transform multiplication matrix */
   private DoubleMatrix DCT_MULTIPLICATION_MATRIX = new DoubleMatrix(JPEG_IMAGE_BLOCK_HEIGHT, JPEG_IMAGE_BLOCK_WIDTH);

   /** Transposed discrete cosine transform multiplication matrix */
   private DoubleMatrix dctMultiplicationMatrixTransposed;

   /** Field description */
   DoubleMatrix cbChromaMatrix;

   /** Field description */
   DoubleMatrix fCrChromaMatrix;

   /** Field description */
   DoubleMatrix fYLumaMatrix;

   /** Field description */
   DoubleMatrix fYLumaDctMatrix;

   /** Field description */
   DoubleMatrix fCbChromaDctMatrix;

   /** Field description */
   DoubleMatrix fCrChromaDctMatrix;

   //~--- constructors --------------------------------------------------------

   /**
    * Constructs ...
    *
    */
   public DctAndQuantizationBlockStep() {
      initializeCosineMatrixForDct();
   }

   //~--- methods -------------------------------------------------------------

   @Override
   public void execute(JpegImageBlock aJpegImageBlock) {
      calculateDct(aJpegImageBlock);
      quantize(aJpegImageBlock);
   }

   /**
    * Method description
    *
    *
    * @param aJpegImageBlock
    */
   private void calculateDct(JpegImageBlock aJpegImageBlock) {
      initializeMatrices(aJpegImageBlock);
      fYLumaDctMatrix    = calculateDct(fYLumaMatrix);
      fCbChromaDctMatrix = calculateDct(cbChromaMatrix);
      fCrChromaDctMatrix = calculateDct(fCrChromaMatrix);
   }

   /**
    *          Calculate Discrete Cosine Transform.
    *
    *
    *          @param aMatrix color values of one color component (YCrCb)
    *
    *          @return returns calculated DCT matrix.
    */
   private DoubleMatrix calculateDct(DoubleMatrix aMatrix) {
      DoubleMatrix resultDctMatrix;

      resultDctMatrix = DCT_MULTIPLICATION_MATRIX.mmul(aMatrix);
      resultDctMatrix = resultDctMatrix.mmul(dctMultiplicationMatrixTransposed);

      return resultDctMatrix;
   }

   /**
    * Method description
    *
    *
    * @param aValue
    * @param aIntervalStart
    * @param aIntervalEnd
    *
    * @return
    */
   private double clipValue(double aValue, double aIntervalStart, double aIntervalEnd) {
      double resultClippedValue = aValue;

      if (resultClippedValue < aIntervalStart) {

         // clip to interval start
         resultClippedValue = aIntervalStart;
      } else if (resultClippedValue > aIntervalEnd) {

         // clip to interval end
         resultClippedValue = aIntervalEnd;
      }

      return resultClippedValue;
   }

   /**
    * Initialize DCT multiplication matrix.
    *
    */
   private void initializeCosineMatrixForDct() {
      int k = 0;

      for (int row_i = 0; row_i < JPEG_IMAGE_BLOCK_HEIGHT; row_i++) {
         for (int column_j = 0; column_j < JPEG_IMAGE_BLOCK_WIDTH; column_j++) {
            double dctEntry = Math.sqrt(2d) / 2;

            if (k != 0) {
               int piMultiplier = row_i + (k * 2 * column_j);

               dctEntry = Math.cos((piMultiplier * Math.PI) / 16d);
            }

            DCT_MULTIPLICATION_MATRIX.put(row_i, column_j, dctEntry * 0.5d);
         }

         k++;
      }

      dctMultiplicationMatrixTransposed = DCT_MULTIPLICATION_MATRIX.transpose();
   }

   /**
    *    Initialize matrices used for DCT.
    *
    *
    *    @param aJpegImageBlock
    */
   private void initializeMatrices(JpegImageBlock aJpegImageBlock) {
      fYLumaMatrix    = new DoubleMatrix(JPEG_IMAGE_BLOCK_WIDTH, JPEG_IMAGE_BLOCK_HEIGHT);
      fCrChromaMatrix = new DoubleMatrix(JPEG_IMAGE_BLOCK_WIDTH, JPEG_IMAGE_BLOCK_HEIGHT);
      cbChromaMatrix  = new DoubleMatrix(JPEG_IMAGE_BLOCK_WIDTH, JPEG_IMAGE_BLOCK_HEIGHT);

      for (int row = 0; row < JPEG_IMAGE_BLOCK_HEIGHT; row++) {
         for (int column = 0; column < JPEG_IMAGE_BLOCK_WIDTH; column++) {
            int    color = aJpegImageBlock.getImageValue(row, column);
            double red   = RgbColor.getRed(color);
            double green = RgbColor.getGreen(color);
            double blue  = RgbColor.getBlue(color);

            // Conversion
            double yLuma    = (0.299 * red) + (0.5876 * green) + (0.114 * blue);
            double cbChroma = ((blue - yLuma) / (2 - (2 * 0.114)));
            double crChroma = ((red - yLuma) / (2 - (2 * 0.299)));

            yLuma    = clipValue(yLuma - 128, -128, 127);
            cbChroma = clipValue(cbChroma, -128, 127);
            crChroma = clipValue(crChroma, -128, 127);

            // YCbCr = (220, 119, 132)
            fYLumaMatrix.put(row, column, yLuma);
            cbChromaMatrix.put(row, column, cbChroma);
            fCrChromaMatrix.put(row, column, crChroma);
         }
      }
   }

   /**
    * Method description
    *
    *
    * @param aJpegImageBlock
    */
   private void quantize(JpegImageBlock aJpegImageBlock) {
      DoubleMatrix yLumaQuantMatrix    = fYLumaDctMatrix.div(LUMINANCE_QUANTIZATION_MATRIX);
      DoubleMatrix cbChromaQuantMatrix = fCbChromaDctMatrix.div(CHROMINANCE_QUANTIZATION_MATRIX);
      DoubleMatrix crChromaQuantMatrix = fCrChromaDctMatrix.div(CHROMINANCE_QUANTIZATION_MATRIX);

      aJpegImageBlock.setQuantizationMatrices(yLumaQuantMatrix, cbChromaQuantMatrix, crChromaQuantMatrix);
   }
}
