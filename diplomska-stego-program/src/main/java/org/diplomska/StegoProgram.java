package org.diplomska;

import org.apache.commons.cli.CommandLine;

import org.diplomska.decoder.step.StegoDecodingStep;
import org.diplomska.encoder.step.StegoEncodingStep;
import org.diplomska.jpeg.decoder.JpegDecoder;
import org.diplomska.jpeg.encoder.JpegEncoder;
import org.diplomska.jpeg.encoder.JpegImageContext;
import org.diplomska.jpeg.huffman.JpegHuffmanEncoderWithDefaultTables;
import org.diplomska.jpeg.processor.JpegImageBlockProcessor;
import org.diplomska.jpeg.processor.block.step.CompleteRemainingColorValuesInBlockStep;
import org.diplomska.jpeg.processor.block.step.DctAndQuantizationBlockStep;
import org.diplomska.jpeg.processor.image.step.DifferenceCodingOfDcCoefficientsStep;
import org.diplomska.jpeg.processor.image.step.HuffmanCodingStep;
import org.diplomska.jpeg.processor.image.step.WriteJpegInJfif;
import org.diplomska.jpeg.processor.image.step.ZigZagAndRlcStep;
import org.diplomska.jpeg.steps.JpegDecoderStep;
import org.diplomska.jpeg.steps.huffman.HuffmanDecodingStep;
import org.diplomska.util.JpegImageEncoderUtils;
import org.diplomska.util.JpegImageUtils;
import org.diplomska.util.structs.StegoProgramParameters;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.image.BufferedImage;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import java.net.URL;

import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

//~--- classes ----------------------------------------------------------------

/**
 * Class description
 *
 *
 * @version        Enter version here..., 16/04/12
 * @author         Enter your name here...
 */
public class StegoProgram implements StegoProgramParameters {

   /** LOGGER */
   private static final Logger LOGGER = LoggerFactory.getLogger(StegoProgram.class);

   //~--- fields --------------------------------------------------------------

   /** Field description */
   private CommandLine commandLine;

   //~--- constructors --------------------------------------------------------

   /**
    * Constructs ...
    *
    *
    *
    * @param aCommandLine
    */
   public StegoProgram(CommandLine aCommandLine) {
      commandLine = aCommandLine;
   }

   //~--- methods -------------------------------------------------------------

   /**
    * Method description
    *
    */
   public void process() {
      try {
         if (commandLine.hasOption(ENCODE_SHORT)) {
            encode();
         } else if (commandLine.hasOption(DECODE_SHORT)) {
            decode();
         } else if (commandLine.hasOption(JUST_COMPRESS_SHORT)) {
            compress();
         }
      } catch (Exception e) {
         JpegImageUtils.throwJpegImageExeption(e);
      }
   }

   /**
    * Method description
    *
    *
    * @throws IOException
    */
   private void compress() throws IOException {

      // Program parameters
      String inJpegFilename  = commandLine.getOptionValue(IN_FILE_SHORT);
      String outJpegFilename = commandLine.getOptionValue(OUT_FILE_SHORT);

      // Input jpeg image
      File          inJpegFile = new File(inJpegFilename);
      URL           inJpegUrl  = inJpegFile.toURI().toURL();
      BufferedImage image      = ImageIO.read(inJpegUrl);

      // Output jpeg image
      File                 outJpegFile           = new File(outJpegFilename);
      FileOutputStream     outJpegFileStream     = new FileOutputStream(outJpegFile);
      BufferedOutputStream outJpegBufferedStream = new BufferedOutputStream(outJpegFileStream);

      // Initialize contexts
      JpegImageBlockProcessor processor        = new JpegImageBlockProcessor();
      int[][]                 imageMatrix      = JpegImageEncoderUtils.getImageMatrix(image);
      JpegImageContext        jpegImageContext = new JpegImageContext(imageMatrix, image.getWidth(), image.getHeight());

      try {
         try {
            processor.putBlockStep("CRCV", new CompleteRemainingColorValuesInBlockStep());
            processor.putBlockStep("DCTAndQuantization", new DctAndQuantizationBlockStep());
            processor.putImageStep("Zigzag", new ZigZagAndRlcStep());
            processor.putImageStep("Difference", new DifferenceCodingOfDcCoefficientsStep());
            processor.putImageStep("Huffman", new HuffmanCodingStep(new JpegHuffmanEncoderWithDefaultTables()));
            processor.putImageStep("Write JFIF", new WriteJpegInJfif(outJpegBufferedStream));

            JpegEncoder jpegEncoder = new JpegEncoder(jpegImageContext, processor);

            LOGGER.info("Image width: {} height: {}", imageMatrix[0].length, imageMatrix.length);
            jpegEncoder.encode();
            LOGGER.info("Fin");
         } finally {
            outJpegBufferedStream.close();
         }
      } catch (Exception e) {
         outJpegFile.delete();
         JpegImageUtils.throwJpegImageExeption(e);
      }
   }

   /**
    * Method description
    *
    * @throws IOException
    *
    */
   private void decode() throws IOException {

      // Program parameters
      String                outStegoFilename = commandLine.getOptionValue(OUT_STEGO_FILE_SHORT);
      String                inJpegFilename   = commandLine.getOptionValue(IN_FILE_SHORT);
      File                  outStegoFile     = new File(outStegoFilename);
      File                  inJpegFile       = new File(inJpegFilename);
      InputStream           inStream         = new BufferedInputStream(new FileInputStream(inJpegFile));
      List<JpegDecoderStep> steps            = new ArrayList<JpegDecoderStep>();

      steps.add(new HuffmanDecodingStep());
      steps.add(new StegoDecodingStep(commandLine));

      JpegDecoder jpegDecoder = new JpegDecoder(steps);

      try {
         try {
            jpegDecoder.decode(inStream);
         } finally {
            inStream.close();
         }
      } catch (Exception ioe) {
         outStegoFile.delete();
         JpegImageUtils.throwJpegImageExeption(ioe);
      }
   }

   /**
    * Method description
    *
    *
    * @throws IOException
    */
   private void encode() throws IOException {

      // Program parameters
      String inJpegFilename  = commandLine.getOptionValue(IN_FILE_SHORT);
      String outJpegFilename = commandLine.getOptionValue(OUT_FILE_SHORT);

      // Input jpeg image
      File          inJpegFile = new File(inJpegFilename);
      URL           inJpegUrl  = inJpegFile.toURI().toURL();
      BufferedImage image      = ImageIO.read(inJpegUrl);

      // Output jpeg image
      File                 outJpegFile           = new File(outJpegFilename);
      FileOutputStream     outJpegFileStream     = new FileOutputStream(outJpegFile);
      BufferedOutputStream outJpegBufferedStream = new BufferedOutputStream(outJpegFileStream);

      // Initialize contexts
      JpegImageBlockProcessor processor        = new JpegImageBlockProcessor();
      int[][]                 imageMatrix      = JpegImageEncoderUtils.getImageMatrix(image);
      JpegImageContext        jpegImageContext = new JpegImageContext(imageMatrix, image.getWidth(), image.getHeight());

      try {
         try {
            processor.putBlockStep("CRCV", new CompleteRemainingColorValuesInBlockStep());
            processor.putBlockStep("DCTAndQuantization", new DctAndQuantizationBlockStep());
            processor.putImageStep("Stego", new StegoEncodingStep(commandLine));
            processor.putImageStep("Zigzag", new ZigZagAndRlcStep());
            processor.putImageStep("Difference", new DifferenceCodingOfDcCoefficientsStep());
            processor.putImageStep("Huffman", new HuffmanCodingStep(new JpegHuffmanEncoderWithDefaultTables()));
            processor.putImageStep("Write JFIF", new WriteJpegInJfif(outJpegBufferedStream));

            JpegEncoder jpegEncoder = new JpegEncoder(jpegImageContext, processor);

            LOGGER.info("Image width: {} height: {}", imageMatrix[0].length, imageMatrix.length);
            jpegEncoder.encode();
            LOGGER.info("Fin");
         } finally {
            outJpegBufferedStream.close();
         }
      } catch (Exception e) {
         outJpegFile.delete();
         JpegImageUtils.throwJpegImageExeption(e);
      }
   }
}
