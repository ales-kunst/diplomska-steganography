package org.diplomska.jpeg.steps;

import org.diplomska.jpeg.steps.huffman.HuffmanDecodingStep;
import org.diplomska.jpeg.steps.struct.JpegBlock;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

import java.util.ArrayList;
import java.util.List;

//~--- classes ----------------------------------------------------------------

/**
 * Class description
 *
 *
 * @version        1.0, 15/11/30
 * @author         Ales Kunst
 */
public class BufferedImageExportDecoderStepTest extends JpegDecodingStep {

   /**
    * Method description
    *
    *
    * @throws Exception
    */
   @Before
   public void setUp() throws Exception {}

   //~--- methods -------------------------------------------------------------

   /**
    * Method description
    *
    *
    * @throws Exception
    */
   @After
   public void tearDown() throws Exception {}

   /**
    * Method description
    *
    */
   @Test
   public void testExecute() {
      checkSimpleImage();
      checkComplexImage();
      checkAlpeSmallImage();
      checkAlpeNormalImage();
      checkAlpsWithStrangeEnd();
      checkGodsEyeImage();
   }

   /**
    * Method description
    *
    */
   @SuppressWarnings("unchecked")
   private void checkAlpeNormalImage() {
      List<JpegDecoderStep> steps = new ArrayList<JpegDecoderStep>();

      steps.add(new HuffmanDecodingStep());
      steps.add(new QuantizatonDecoderStep());
      steps.add(new InverseDctDecoderStep());
      steps.add(new BufferedImageExportDecoderStep(new File("./target/out_alpe.png"), "PNG"));

      JpegDecoderContext jpegDecoderContext = executeDecoding("slovenske_alpe_triglav.jpg", steps);
      List<Object>       map                = jpegDecoderContext.getResult(InverseDctDecoderStep.class);
      List<JpegBlock>    list               = (List<JpegBlock>) map.get(0);
      JpegBlock          jpegBlock_00       = list.get(0);
      JpegBlock          jpegBlock_01       = list.get(1);
      JpegBlock          jpegBlock_02       = list.get(2);

      Assert.assertEquals(38d, jpegBlock_00.coefficients[0], 0.0005d);
      Assert.assertEquals(-1d, jpegBlock_01.coefficients[0], 0.0005d);
      Assert.assertEquals(-2d, jpegBlock_02.coefficients[0], 0.0005d);
   }

   /**
    * Method description
    *
    */
   @SuppressWarnings("unchecked")
   private void checkAlpeSmallImage() {
      List<JpegDecoderStep> steps = new ArrayList<JpegDecoderStep>();

      steps.add(new HuffmanDecodingStep());
      steps.add(new QuantizatonDecoderStep());
      steps.add(new InverseDctDecoderStep());
      steps.add(new BufferedImageExportDecoderStep(new File("./target/out_alpe_24_16.png"), "PNG"));

      JpegDecoderContext jpegDecoderContext = executeDecoding("slovenske_alpe_triglav_24_16.jpg", steps);
      List<Object>       map                = jpegDecoderContext.getResult(InverseDctDecoderStep.class);
      List<JpegBlock>    list               = (List<JpegBlock>) map.get(0);
      JpegBlock          jpegBlock_00       = list.get(0);
      JpegBlock          jpegBlock_01       = list.get(1);
      JpegBlock          jpegBlock_02       = list.get(2);

      Assert.assertEquals(50d, jpegBlock_00.coefficients[0], 0.0005d);
      Assert.assertEquals(-11d, jpegBlock_01.coefficients[0], 0.0005d);
      Assert.assertEquals(10d, jpegBlock_02.coefficients[0], 0.0005d);
   }

   /**
    * This image is decoded because it produces some strange behaviour at the last bits
    * For the last two bits no Jpeg block is created which is correct because the bit are leftovers.
    * Check the line "if (bitValue == null)" in JpegBlockFiller. It can happen that an additional block is
    * created which is not there, so therefore this block is ignored.
    *
    */
   @SuppressWarnings("unchecked")
   private void checkAlpsWithStrangeEnd() {
      List<JpegDecoderStep> steps = new ArrayList<JpegDecoderStep>();

      steps.add(new HuffmanDecodingStep());
      steps.add(new QuantizatonDecoderStep());
      steps.add(new InverseDctDecoderStep());
      steps.add(new BufferedImageExportDecoderStep(new File("./target/alps_with_strange_end.png"), "PNG"));

      JpegDecoderContext jpegDecoderContext    = executeDecoding("alps_with_strange_end.jpg", steps);
      List<Object>       map                   = jpegDecoderContext.getResult(InverseDctDecoderStep.class);
      List<JpegBlock>    list                  = (List<JpegBlock>) map.get(0);
      int                listSize              = list.size();
      JpegBlock          lastYLumaJpegBlock    = list.get(listSize - 3);
      JpegBlock          lastCbChromaJpegBlock = list.get(listSize - 2);
      JpegBlock          lastCrChromaJpegBlock = list.get(listSize - 1);

      Assert.assertEquals(-86d, lastYLumaJpegBlock.coefficients[0], 0.0005d);
      Assert.assertEquals(12d, lastCbChromaJpegBlock.coefficients[0], 0.0005d);
      Assert.assertEquals(-1d, lastCrChromaJpegBlock.coefficients[0], 0.0005d);
   }

   /**
    * Method description
    *
    */
   @SuppressWarnings("unchecked")
   private void checkComplexImage() {
      List<JpegDecoderStep> steps = new ArrayList<JpegDecoderStep>();

      steps.add(new HuffmanDecodingStep());
      steps.add(new QuantizatonDecoderStep());
      steps.add(new InverseDctDecoderStep());
      steps.add(new BufferedImageExportDecoderStep(new File("./target/out_complex.png"), "PNG"));

      JpegDecoderContext jpegDecoderContext = executeDecoding("complex_color_8_8.jpg", steps);
      List<Object>       map                = jpegDecoderContext.getResult(InverseDctDecoderStep.class);
      List<JpegBlock>    list               = (List<JpegBlock>) map.get(0);
      JpegBlock          jpegBlock_00       = list.get(0);
      JpegBlock          jpegBlock_01       = list.get(1);
      JpegBlock          jpegBlock_02       = list.get(2);

      Assert.assertEquals(-72d, jpegBlock_00.coefficients[0], 0.0005d);
      Assert.assertEquals(17d, jpegBlock_01.coefficients[0], 0.0005d);
      Assert.assertEquals(1d, jpegBlock_02.coefficients[0], 0.0005d);
   }

   /**
    * Method description
    *
    */
   private void checkGodsEyeImage() {
      List<JpegDecoderStep> steps = new ArrayList<JpegDecoderStep>();

      steps.add(new HuffmanDecodingStep());
      steps.add(new QuantizatonDecoderStep());
      steps.add(new InverseDctDecoderStep());
      steps.add(new BufferedImageExportDecoderStep(new File("./target/out_gods_eye.png"), "PNG"));

      JpegDecoderContext jpegDecoderContext = executeDecoding("gods_eye.jpg", steps);
      List<Object>       map                = jpegDecoderContext.getResult(InverseDctDecoderStep.class);
      List<JpegBlock>    list               = (List<JpegBlock>) map.get(0);
      JpegBlock          jpegBlock_00       = list.get(0);
      JpegBlock          jpegBlock_01       = list.get(1);
      JpegBlock          jpegBlock_02       = list.get(2);
   }

   /**
    * Method description
    *
    */
   @SuppressWarnings("unchecked")
   private void checkSimpleImage() {
      List<JpegDecoderStep> steps = new ArrayList<JpegDecoderStep>();

      steps.add(new HuffmanDecodingStep());
      steps.add(new QuantizatonDecoderStep());
      steps.add(new InverseDctDecoderStep());
      steps.add(new BufferedImageExportDecoderStep(new File("./target/out_simple.png"), "PNG"));

      JpegDecoderContext jpegDecoderContext = executeDecoding("simple_black_white.jpg", steps);
      List<Object>       map                = jpegDecoderContext.getResult(InverseDctDecoderStep.class);
      List<JpegBlock>    list               = (List<JpegBlock>) map.get(0);
      JpegBlock          jpegBlock_00       = list.get(0);
      JpegBlock          jpegBlock_01       = list.get(1);
      JpegBlock          jpegBlock_02       = list.get(2);

      Assert.assertEquals(128d, jpegBlock_00.coefficients[0], 0.0005d);
      Assert.assertEquals(0d, jpegBlock_01.coefficients[0], 0.0005d);
      Assert.assertEquals(0d, jpegBlock_02.coefficients[0], 0.0005d);
   }
}
