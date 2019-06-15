package org.diplomska.jpeg.encoder;

import org.diplomska.jpeg.encoder.JpegImageBlock;
import org.diplomska.jpeg.encoder.JpegImageContext;
import org.diplomska.jpeg.huffman.JpegHuffmanEncoderWithOptimizedTables;
import org.diplomska.jpeg.processor.JpegImageBlockProcessor;
import org.diplomska.jpeg.processor.block.JpegImageBlockProcessorStep;
import org.diplomska.jpeg.processor.block.step.DiscreteCosineTransformBlockStep;
import org.diplomska.jpeg.processor.block.step.QuantizationBlockStep;
import org.diplomska.jpeg.processor.block.step.ZigZagAndRlcBlockStep;
import org.diplomska.jpeg.processor.image.JpegImageProcessorStep;
import org.diplomska.jpeg.processor.image.step.DifferenceCodingOfDcCoefficientsStep;
import org.diplomska.jpeg.processor.image.step.HuffmanCodingStep;
import org.diplomska.jpeg.processor.image.step.WriteJpegInJfif;
import org.diplomska.jpeg.util.JpegImageUtils;
import org.diplomska.jpeg.util.RunLengthCodingStructure;

import org.jblas.DoubleMatrix;

import java.awt.image.BufferedImage;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import java.net.URL;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

//~--- classes ----------------------------------------------------------------

/**
 * Class description
 *
 *
 * @version        1.0, 11.jul. 2015
 * @author         Ales Kunst
 */
public class JpegEncoderTestUtil {

   /** Field description */
   public static final String DCT_STEP = "dct_step";

   /** Field description */
   public static final String DIFFERENCE_CODING_STEP = "difference_coding_step";

   /** Field description */
   public static final String HUFFMAN_CODING_STEP = "huffman_coding_step";

   /** Field description */
   public static final String QUANTIZATION_STEP = "quantization_step";

   /** Field description */
   public static final String ZIG_ZAG_STEP = "zig_zag_step";

   /** Field description */
   public static final String WRITE_JFIF_STEP = "write_jfif_step";

   //~--- methods -------------------------------------------------------------

   /**
    * Add DCT processor into steps map.
    *
    *
    * @param aJpegImageBlockSteps
    *
    * @return
    */
   public static JpegImageBlockProcessorStep addDctJpegImageBlockStepEntry(
           Map<String, JpegImageBlockProcessorStep> aJpegImageBlockSteps) {
      return aJpegImageBlockSteps.put(DCT_STEP, new DiscreteCosineTransformBlockStep());
   }

   /**
    * Method description
    *
    *
    * @param aJpegImageSteps
    *
    * @return
    */
   public static JpegImageProcessorStep addDifferenceCodingOfDctCoefficientStep(
           Map<String, JpegImageProcessorStep> aJpegImageSteps) {
      return aJpegImageSteps.put(DIFFERENCE_CODING_STEP, new DifferenceCodingOfDcCoefficientsStep());
   }

   /**
    * Method description
    *
    *
    * @param aJpegImageSteps
    *
    * @return
    */
   public static JpegImageProcessorStep addHuffmanCodingStep(Map<String, JpegImageProcessorStep> aJpegImageSteps) {
      return aJpegImageSteps.put(HUFFMAN_CODING_STEP,
                                 new HuffmanCodingStep(new JpegHuffmanEncoderWithOptimizedTables()));
   }

   /**
    * Method description
    *
    *
    * @param aJpegImageBlockSteps
    *
    * @return
    */
   public static JpegImageBlockProcessorStep addQuntizationJpegImageBlockStep(
           Map<String, JpegImageBlockProcessorStep> aJpegImageBlockSteps) {
      return aJpegImageBlockSteps.put(QUANTIZATION_STEP, new QuantizationBlockStep());
   }

   /**
    * Add write in jfif format.
    *
    *
    * @param aJpegImageSteps
    *
    * @return
    */
   public static JpegImageProcessorStep addWriteJfifStep(Map<String, JpegImageProcessorStep> aJpegImageSteps) {
      return aJpegImageSteps.put(WRITE_JFIF_STEP, new WriteJpegInJfif(new ByteArrayOutputStream()));
   }

   /**
    * Method description
    *
    *
    * @param aJpegImageBlockSteps
    *
    * @return
    */
   public static JpegImageBlockProcessorStep addZigZagJpegImageBlockStep(
           Map<String, JpegImageBlockProcessorStep> aJpegImageBlockSteps) {
      return aJpegImageBlockSteps.put(ZIG_ZAG_STEP, new ZigZagAndRlcBlockStep());
   }

   /**
    * Calculate DCT according to formula:
    *
    * F(u,v) = 1/4 * C(u) * C(v) * (sum(x: 0 -> 7, sum(y: 0 -> 7, S(u, v, x, y))))
    * where
    * S(u, v, x, y) = f(x, y) * cos((Pi * (2 * x + 1) * u) / 16) * cos((Pi * (2 * y + 1) * v) / 16)
    *
    * @param aColorComponentMatrix
    *
    * @return
    */
   public static DoubleMatrix calculateDct(DoubleMatrix aColorComponentMatrix) {
      DoubleMatrix resultDctMatrix = new DoubleMatrix(aColorComponentMatrix.getRows(),
                                                      aColorComponentMatrix.getColumns());
      int    pixelRow           = 0;
      int    pixelColumn        = 0;
      int    resultRow          = 0;
      int    resultColumn       = 0;
      double invSquareRootOfTwo = 1 / Math.sqrt(2);
      double invFour            = 0.25;    // 1/4

      for (int u = 0; u < JpegImageUtils.JPEG_IMAGE_BLOCK_HEIGHT; u++) {
         resultRow = u;

         for (int v = 0; v < JpegImageUtils.JPEG_IMAGE_BLOCK_WIDTH; v++) {
            resultColumn = v;

            double cu  = (u == 0)
                         ? invSquareRootOfTwo
                         : 1;
            double cv  = (v == 0)
                         ? invSquareRootOfTwo
                         : 1;
            double sum = 0;

            for (int x = 0; x < JpegImageUtils.JPEG_IMAGE_BLOCK_HEIGHT; x++) {
               pixelRow = x;

               for (int y = 0; y < JpegImageUtils.JPEG_IMAGE_BLOCK_WIDTH; y++) {
                  pixelColumn = y;

                  double cos_xu              = Math.cos((Math.PI * ((2 * x) + 1) * u) / 16);
                  double cos_yv              = Math.cos((Math.PI * ((2 * y) + 1) * v) / 16);
                  double colorComponentValue = aColorComponentMatrix.get(pixelRow, pixelColumn);

                  sum += colorComponentValue * cos_xu * cos_yv;
               }    // for y
            }       // for x

            sum = invFour * cu * cv * sum;
            resultDctMatrix.put(resultRow, resultColumn, sum);
         }          // for v
      }             // for u

      return resultDctMatrix;
   }

   /**
    * Method description
    *
    *
    * @param aImageWidth
    * @param aImageHeight
    *
    * @return
    */
   public static JpegImageContext createDummyJpegImageContext(int aImageWidth, int aImageHeight) {
      JpegImageContext resultJpegImageContext = new JpegImageContext(new int[aImageHeight][aImageWidth]);

      return resultJpegImageContext;
   }

   /**
    * Create dummy jpeg image context and override jpeg image blocks.
    *
    *
    * @param aImageWidth
    * @param aImageHeight
    * @param aJpegImageBlocks
    *
    * @return
    */
   public static JpegImageContext createDummyJpegImageContext(int aImageWidth, int aImageHeight,
                                                              List<JpegImageBlock> aJpegImageBlocks) {
      JpegImageContext resultJpegImageContext = createDummyJpegImageContext(aImageWidth, aImageHeight);
      int              index                  = 0;

      for (int jpegImageBlockRow = 0; jpegImageBlockRow < resultJpegImageContext.getJpegImageBlocksHeight();
              jpegImageBlockRow++) {
         for (int jpegImageBlockColumn = 0; jpegImageBlockColumn < resultJpegImageContext.getJpegImageBlocksHeight();
                 jpegImageBlockColumn++) {
            JpegImageBlock jpegImageBlock = aJpegImageBlocks.get(index);

            if (jpegImageBlock != null) {
               resultJpegImageContext = overrideJpegImageBlock(jpegImageBlockRow, jpegImageBlockColumn, jpegImageBlock,
                                                               resultJpegImageContext);
            }

            index++;
         }
      }

      return resultJpegImageContext;
   }

   /**
    * Method description
    *
    *
    * @return
    */
   public static JpegImageContext createTestBlackWhitePngJpegImageContext() {
      URL           testBlackWhitePngUrl   = ClassLoader.getSystemClassLoader().getResource("test_black_white.png");
      BufferedImage testBlackWhitePngImage = null;

      try {
         testBlackWhitePngImage = ImageIO.read(testBlackWhitePngUrl);
      } catch (IOException e) {
         throw new RuntimeException(e);
      }

      return new JpegImageContext(testBlackWhitePngImage);
   }

   /**
    * Method description
    *
    *
    * @return
    */
   public static JpegImageContext createTestPngJpegImageContext() {
      URL           testPngUrl   = ClassLoader.getSystemClassLoader().getResource("test.png");
      BufferedImage testPngImage = null;

      try {
         testPngImage = ImageIO.read(testPngUrl);
      } catch (IOException e) {
         throw new RuntimeException(e);
      }

      return new JpegImageContext(testPngImage);
   }

   //~--- get methods ---------------------------------------------------------

   /**
    * Get bits array length of jpeg image block.
    *
    *
    *
    * @param aJpegImageBlock
    *
    * @return
    */
   public static long getBitArrayLength(JpegImageBlock aJpegImageBlock) {
      long  resultSum           = 0;
      int[] yLumaZigZagArray    = aJpegImageBlock.getZigZagRlcEncodedYLumaArray();
      int[] cbChromaZigZagArray = aJpegImageBlock.getZigZagRlcEncodedCbChromaArray();
      int[] crChromaZigZagArray = aJpegImageBlock.getZigZagRlcEncodedCrChromaArray();

      for (int zigzagArrayElement : yLumaZigZagArray) {
         RunLengthCodingStructure rlcStructure =
            JpegImageUtils.decodeFromEncodedZeroRunLengthCoding(zigzagArrayElement);

         resultSum += 8 + rlcStructure.fNumberOfBits;    // 8 bits + number of bits for coefficient
      }

      for (int zigzagArrayElement : cbChromaZigZagArray) {
         RunLengthCodingStructure rlcStructure =
            JpegImageUtils.decodeFromEncodedZeroRunLengthCoding(zigzagArrayElement);

         resultSum += 8 + rlcStructure.fNumberOfBits;    // 8 bits + number of bits for coefficient
      }

      for (int zigzagArrayElement : crChromaZigZagArray) {
         RunLengthCodingStructure rlcStructure =
            JpegImageUtils.decodeFromEncodedZeroRunLengthCoding(zigzagArrayElement);

         resultSum += 8 + rlcStructure.fNumberOfBits;    // 8 bits + number of bits for coefficient
      }

      return resultSum;
   }

   /**
    * Get length of bits array for huffman encoding.
    *
    *
    * @param aJpegImageContext
    *
    * @return
    */
   public static long getBitArrayLengthForHuffmanEncoding(JpegImageContext aJpegImageContext) {
      long resultSum = 0;

      for (int row = 0; row < aJpegImageContext.getJpegImageBlocksHeight(); row++) {
         for (int column = 0; column < aJpegImageContext.getJpegImageBlocksWidth(); column++) {
            JpegImageBlock jpegImageBlock = aJpegImageContext.getJpegImageBlock(column, row);

            resultSum += getBitArrayLength(jpegImageBlock);
         }
      }

      return resultSum;
   }

   //~--- methods -------------------------------------------------------------

   /**
    * Overrides jpeg image block in jpeg image context.
    *
    *
    * @param aRow
    * @param aColumn
    * @param aJpegImageBlock
    * @param aJpegImageContext
    *
    * @return
    */
   public static JpegImageContext overrideJpegImageBlock(int aRow, int aColumn, JpegImageBlock aJpegImageBlock,
                                                         JpegImageContext aJpegImageContext) {
      aJpegImageContext.putJpegImageBlock(aRow, aColumn, null);
      aJpegImageContext.putJpegImageBlock(aRow, aColumn, aJpegImageBlock);

      return aJpegImageContext;
   }

   /**
    * Run only Dct coding process.
    *
    *
    * @param aJpegImageContext
    *
    * @return
    */
   public static JpegImageBlockProcessor runOnlyDct(JpegImageContext aJpegImageContext) {
      Map<String, JpegImageBlockProcessorStep> jpegImageBlockSteps = new LinkedHashMap<String,
                                                                                       JpegImageBlockProcessorStep>();
      Map<String, JpegImageProcessorStep> jpegImageSteps = new LinkedHashMap<String, JpegImageProcessorStep>();

      addDctJpegImageBlockStepEntry(jpegImageBlockSteps);

      return runSteps(aJpegImageContext, jpegImageBlockSteps, jpegImageSteps);
   }

   /**
    * Run only Difference coding process.
    *
    *
    * @param aJpegImageContext
    *
    * @return
    */
   public static JpegImageBlockProcessor runOnlyDifferenceCodingOfDcCoefficients(JpegImageContext aJpegImageContext) {
      Map<String, JpegImageBlockProcessorStep> jpegImageBlockSteps = new LinkedHashMap<String,
                                                                                       JpegImageBlockProcessorStep>();
      Map<String, JpegImageProcessorStep> jpegImageSteps = new LinkedHashMap<String, JpegImageProcessorStep>();

      addDifferenceCodingOfDctCoefficientStep(jpegImageSteps);

      return runSteps(aJpegImageContext, jpegImageBlockSteps, jpegImageSteps);
   }

   /**
    * Run only Huffman encoding process.
    *
    *
    * @param aJpegImageContext
    *
    * @return
    */
   public static JpegImageBlockProcessor runOnlyHuffmanCoding(JpegImageContext aJpegImageContext) {
      Map<String, JpegImageBlockProcessorStep> jpegImageBlockSteps = new LinkedHashMap<String,
                                                                                       JpegImageBlockProcessorStep>();
      Map<String, JpegImageProcessorStep> jpegImageSteps = new LinkedHashMap<String, JpegImageProcessorStep>();

      addHuffmanCodingStep(jpegImageSteps);

      return runSteps(aJpegImageContext, jpegImageBlockSteps, jpegImageSteps);
   }

   /**
    * Run only Quantization process.
    *
    *
    * @param aJpegImageContext
    *
    * @return
    */
   public static JpegImageBlockProcessor runOnlyQuantization(JpegImageContext aJpegImageContext) {
      Map<String, JpegImageBlockProcessorStep> jpegImageBlockSteps = new LinkedHashMap<String,
                                                                                       JpegImageBlockProcessorStep>();
      Map<String, JpegImageProcessorStep> jpegImageSteps = new LinkedHashMap<String, JpegImageProcessorStep>();

      addQuntizationJpegImageBlockStep(jpegImageBlockSteps);

      return runSteps(aJpegImageContext, jpegImageBlockSteps, jpegImageSteps);
   }

   /**
    * Run only ZigZag process.
    *
    *
    * @param aJpegImageContext
    *
    * @return
    */
   public static JpegImageBlockProcessor runOnlyZigZag(JpegImageContext aJpegImageContext) {
      Map<String, JpegImageBlockProcessorStep> jpegImageBlockSteps = new LinkedHashMap<String,
                                                                                       JpegImageBlockProcessorStep>();
      Map<String, JpegImageProcessorStep> jpegImageSteps = new LinkedHashMap<String, JpegImageProcessorStep>();

      addZigZagJpegImageBlockStep(jpegImageBlockSteps);

      return runSteps(aJpegImageContext, jpegImageBlockSteps, jpegImageSteps);
   }

   /**
    * Run all the steps.
    *
    *
    * @param aJpegImageContext
    * @param aJpegImageBlockSteps
    * @param aJpegImageSteps
    *
    * @return
    */
   public static JpegImageBlockProcessor runSteps(JpegImageContext aJpegImageContext,
                                                  Map<String, JpegImageBlockProcessorStep> aJpegImageBlockSteps,
                                                  Map<String, JpegImageProcessorStep> aJpegImageSteps) {
      JpegImageBlockProcessor returnJpegProcessor = new JpegImageBlockProcessor();

      for (Map.Entry<String, JpegImageBlockProcessorStep> jpegImageBlockStepEntry : aJpegImageBlockSteps.entrySet()) {
         returnJpegProcessor.putJpegImageBlockProcessingStep(jpegImageBlockStepEntry.getKey(),
                                                             jpegImageBlockStepEntry.getValue());
      }

      for (Map.Entry<String, JpegImageProcessorStep> jpegImageStepEntry : aJpegImageSteps.entrySet()) {
         returnJpegProcessor.putJpegImageProcessingStep(jpegImageStepEntry.getKey(), jpegImageStepEntry.getValue());
      }

      for (int row = 0; row < aJpegImageContext.getJpegImageBlocksHeight(); row++) {
         for (int column = 0; column < aJpegImageContext.getJpegImageBlocksWidth(); column++) {
            JpegImageBlock jpegImageBlock = aJpegImageContext.getJpegImageBlock(column, row);

            returnJpegProcessor.processJpegImageBlock(jpegImageBlock);
         }
      }

      returnJpegProcessor.processJpegImage(aJpegImageContext);

      return returnJpegProcessor;
   }

   /**
    * Run process of jpeg encoding until DCT.
    *
    *
    * @param aJpegImageContext
    *
    * @return
    */
   public static JpegImageBlockProcessor runUntilDct(JpegImageContext aJpegImageContext) {
      Map<String, JpegImageBlockProcessorStep> jpegImageBlockSteps = new LinkedHashMap<String,
                                                                                       JpegImageBlockProcessorStep>();
      Map<String, JpegImageProcessorStep> jpegImageSteps = new LinkedHashMap<String, JpegImageProcessorStep>();

      addDctJpegImageBlockStepEntry(jpegImageBlockSteps);

      return runSteps(aJpegImageContext, jpegImageBlockSteps, jpegImageSteps);
   }

   /**
    * Run process of jpeg encoding with Dct, Quantization, ZigZag and Difference coding.
    *
    *
    * @param aJpegImageContext
    *
    * @return
    */
   public static JpegImageBlockProcessor runUntilDifferenceCodingOfDcCoefficients(JpegImageContext aJpegImageContext) {
      Map<String, JpegImageBlockProcessorStep> jpegImageBlockSteps = new LinkedHashMap<String,
                                                                                       JpegImageBlockProcessorStep>();
      Map<String, JpegImageProcessorStep> jpegImageSteps = new LinkedHashMap<String, JpegImageProcessorStep>();

      addDctJpegImageBlockStepEntry(jpegImageBlockSteps);
      addQuntizationJpegImageBlockStep(jpegImageBlockSteps);
      addZigZagJpegImageBlockStep(jpegImageBlockSteps);
      addDifferenceCodingOfDctCoefficientStep(jpegImageSteps);

      return runSteps(aJpegImageContext, jpegImageBlockSteps, jpegImageSteps);
   }

   /**
    * Run process of jpeg encoding with Dct, Quantization, ZigZag, Difference coding and Huffman encoding.
    *
    *
    * @param aJpegImageContext
    *
    * @return
    */
   public static JpegImageBlockProcessor runUntilHuffmanCoding(JpegImageContext aJpegImageContext) {
      Map<String, JpegImageBlockProcessorStep> jpegImageBlockSteps = new LinkedHashMap<String,
                                                                                       JpegImageBlockProcessorStep>();
      Map<String, JpegImageProcessorStep> jpegImageSteps = new LinkedHashMap<String, JpegImageProcessorStep>();

      addDctJpegImageBlockStepEntry(jpegImageBlockSteps);
      addQuntizationJpegImageBlockStep(jpegImageBlockSteps);
      addZigZagJpegImageBlockStep(jpegImageBlockSteps);
      addDifferenceCodingOfDctCoefficientStep(jpegImageSteps);
      addHuffmanCodingStep(jpegImageSteps);

      return runSteps(aJpegImageContext, jpegImageBlockSteps, jpegImageSteps);
   }

   /**
    * Run process of jpeg encoding with Dct and Quantization.
    *
    *
    * @param aJpegImageContext
    *
    * @return
    */
   public static JpegImageBlockProcessor runUntilQuantization(JpegImageContext aJpegImageContext) {
      Map<String, JpegImageBlockProcessorStep> jpegImageBlockSteps = new LinkedHashMap<String,
                                                                                       JpegImageBlockProcessorStep>();
      Map<String, JpegImageProcessorStep> jpegImageSteps = new LinkedHashMap<String, JpegImageProcessorStep>();

      addDctJpegImageBlockStepEntry(jpegImageBlockSteps);
      addQuntizationJpegImageBlockStep(jpegImageBlockSteps);

      return runSteps(aJpegImageContext, jpegImageBlockSteps, jpegImageSteps);
   }

   /**
    * Run process of jpeg encoding with Dct, Quantization, ZigZag, Difference coding, Huffman encoding
    * and write image in Jfif.
    *
    *
    * @param aJpegImageContext
    *
    * @return
    */
   public static JpegImageBlockProcessor runUntilWriteJfif(JpegImageContext aJpegImageContext) {
      Map<String, JpegImageBlockProcessorStep> jpegImageBlockSteps = new LinkedHashMap<String,
                                                                                       JpegImageBlockProcessorStep>();
      Map<String, JpegImageProcessorStep> jpegImageSteps = new LinkedHashMap<String, JpegImageProcessorStep>();

      addDctJpegImageBlockStepEntry(jpegImageBlockSteps);
      addQuntizationJpegImageBlockStep(jpegImageBlockSteps);
      addZigZagJpegImageBlockStep(jpegImageBlockSteps);
      addDifferenceCodingOfDctCoefficientStep(jpegImageSteps);
      addHuffmanCodingStep(jpegImageSteps);
      addWriteJfifStep(jpegImageSteps);

      return runSteps(aJpegImageContext, jpegImageBlockSteps, jpegImageSteps);
   }

   /**
    * Run process of jpeg encoding with Dct, Quantization and ZigZag.
    *
    *
    * @param aJpegImageContext
    *
    * @return
    */
   public static JpegImageBlockProcessor runUntilZigZag(JpegImageContext aJpegImageContext) {
      Map<String, JpegImageBlockProcessorStep> jpegImageBlockSteps = new LinkedHashMap<String,
                                                                                       JpegImageBlockProcessorStep>();
      Map<String, JpegImageProcessorStep> jpegImageSteps = new LinkedHashMap<String, JpegImageProcessorStep>();

      addDctJpegImageBlockStepEntry(jpegImageBlockSteps);
      addQuntizationJpegImageBlockStep(jpegImageBlockSteps);
      addZigZagJpegImageBlockStep(jpegImageBlockSteps);

      return runSteps(aJpegImageContext, jpegImageBlockSteps, jpegImageSteps);
   }
}
