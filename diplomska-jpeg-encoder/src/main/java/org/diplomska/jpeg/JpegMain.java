package org.diplomska.jpeg;

import org.diplomska.jpeg.encoder.JpegEncoder;
import org.diplomska.jpeg.encoder.JpegImageContext;
import org.diplomska.jpeg.huffman.JpegHuffmanEncoderWithDefaultTables;
import org.diplomska.jpeg.processor.JpegImageBlockProcessor;
import org.diplomska.jpeg.processor.block.step.CompleteRemainingColorValuesInBlockStep;
import org.diplomska.jpeg.processor.block.step.DiscreteCosineTransformBlockStep;
import org.diplomska.jpeg.processor.block.step.QuantizationBlockStep;
import org.diplomska.jpeg.processor.block.step.ZigZagAndRlcBlockStep;
import org.diplomska.jpeg.processor.image.step.DifferenceCodingOfDcCoefficientsStep;
import org.diplomska.jpeg.processor.image.step.HuffmanCodingStep;
import org.diplomska.jpeg.processor.image.step.WriteJpegInJfif;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.image.BufferedImage;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import java.net.URL;

import javax.imageio.ImageIO;

//~--- classes ----------------------------------------------------------------

/**
 * This class contains only main method for starting of jpeg encoder.
 *
 */
public class JpegMain {

   /** LOGGER */
   private static final Logger LOGGER = LoggerFactory.getLogger(JpegMain.class);

   //~--- methods -------------------------------------------------------------

   /**
    * Method description
    *
    *
    * @param args
    *
    * @throws IOException
    */
   public static void main(String[] args) throws IOException {
      File                    inputImageFile          = new File(args[0]);
      URL                     imageUrl                = inputImageFile.toURI().toURL();
      BufferedImage           image                   = ImageIO.read(imageUrl);
      JpegImageContext        jpegImageContext        = new JpegImageContext(image);
      JpegImageBlockProcessor jpegImageBlockProcessor = new JpegImageBlockProcessor();
      FileOutputStream        fileOutputStream        = new FileOutputStream(new File(args[1]));
      BufferedOutputStream    bufferedOutputStream    = new BufferedOutputStream(fileOutputStream);

      try {
         jpegImageBlockProcessor.putJpegImageBlockProcessingStep("CRCV", new CompleteRemainingColorValuesInBlockStep());
         jpegImageBlockProcessor.putJpegImageBlockProcessingStep("DCT", new DiscreteCosineTransformBlockStep());
         jpegImageBlockProcessor.putJpegImageBlockProcessingStep("Quantization", new QuantizationBlockStep());
         jpegImageBlockProcessor.putJpegImageBlockProcessingStep("Zigzag", new ZigZagAndRlcBlockStep());
         jpegImageBlockProcessor.putJpegImageProcessingStep("Difference", new DifferenceCodingOfDcCoefficientsStep());
         jpegImageBlockProcessor.putJpegImageProcessingStep(
             "Huffman", new HuffmanCodingStep(new JpegHuffmanEncoderWithDefaultTables()));
         jpegImageBlockProcessor.putJpegImageProcessingStep("Write JFIF", new WriteJpegInJfif(bufferedOutputStream));

         JpegEncoder jpegEncoder = new JpegEncoder(jpegImageContext, jpegImageBlockProcessor);

         LOGGER.info("Image width: {} height: {}", image.getWidth(), image.getHeight());
         image = null;
         jpegEncoder.encode();
      } finally {
         bufferedOutputStream.close();
      }

      LOGGER.info("Ende!");
   }
}
