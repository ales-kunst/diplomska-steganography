package org.diplomska.stego;

import org.apache.commons.cli.CommandLine;

import org.diplomska.jpeg.encoder.JpegImageContext;
import org.diplomska.jpeg.steps.JpegDecoderContext;
import org.diplomska.stego.decode.JpegF4Decode;
import org.diplomska.stego.decode.JpegF5Decode;
import org.diplomska.stego.decode.JpegF5MeDecode;
import org.diplomska.stego.decode.JpegJstegDecode;
import org.diplomska.stego.decode.JpegLsbDecode;
import org.diplomska.stego.encode.JpegF4Encode;
import org.diplomska.stego.encode.JpegF5Encode;
import org.diplomska.stego.encode.JpegF5MeEncode;
import org.diplomska.stego.encode.JpegJstegEncode;
import org.diplomska.stego.encode.JpegLsbEncode;
import org.diplomska.util.structs.StegoProgramParameters;

//~--- classes ----------------------------------------------------------------

/**
 * Class description
 *
 *
 * @version        Enter version here..., 16/04/14
 * @author         Enter your name here...
 */
public class StegoAlgorithmFactory implements StegoProgramParameters {

   /** Field description */
   public static final String LSB = "1LSB";

   /** Field description */
   public static final String JSTEG = "JSTEG";

   /** Field description */
   public static final String F5ME = "F5ME";

   /** Field description */
   public static final String F5 = "F5";

   /** Field description */
   public static final String F4 = "F4";

   //~--- constructors --------------------------------------------------------

   /**
    * Constructs ...
    *
    */
   private StegoAlgorithmFactory() {}

   //~--- get methods ---------------------------------------------------------

   /**
    * Method description
    *
    *
    *
    * @param aCommandLine
    * @param aJpegDecoderContext
    *
    * @return
    */
   public static StegoDecodingAlgorithm getDecoder(CommandLine aCommandLine, JpegDecoderContext aJpegDecoderContext) {
      StegoDecodingAlgorithm resultDecoder = null;
      String                 algorithm     = aCommandLine.getOptionValue(ALGO_SHORT);

      if (LSB.equalsIgnoreCase(algorithm)) {
         resultDecoder = new JpegLsbDecode();
      } else if (JSTEG.equalsIgnoreCase(algorithm)) {
         resultDecoder = new JpegJstegDecode();
      } else if (F4.equalsIgnoreCase(algorithm)) {
         resultDecoder = new JpegF4Decode();
      } else if (F5.equalsIgnoreCase(algorithm)) {
         String password = aCommandLine.getOptionValue(PASSWORD_SHORT);

         resultDecoder = new JpegF5Decode(password.getBytes());
      } else if (F5ME.equalsIgnoreCase(algorithm)) {
         String password = aCommandLine.getOptionValue(PASSWORD_SHORT);

         resultDecoder = new JpegF5MeDecode(password.getBytes());
      }

      return resultDecoder;
   }

   /**
    * Method description
    *
    *
    *
    * @param aCommandLine
    * @param aJpegImageContext
    *
    * @return
    */
   public static StegoEncodingAlgorithm getEncoder(CommandLine aCommandLine, JpegImageContext aJpegImageContext) {
      StegoEncodingAlgorithm resultEncoder = null;
      String                 algorithm     = aCommandLine.getOptionValue(ALGO_SHORT);

      if (LSB.equalsIgnoreCase(algorithm)) {
         resultEncoder = new JpegLsbEncode(aJpegImageContext);
      } else if (JSTEG.equalsIgnoreCase(algorithm)) {
         resultEncoder = new JpegJstegEncode(aJpegImageContext);
      } else if (F4.equalsIgnoreCase(algorithm)) {
         resultEncoder = new JpegF4Encode(aJpegImageContext);
      } else if (F5.equalsIgnoreCase(algorithm)) {
         String password = aCommandLine.getOptionValue(PASSWORD_SHORT);

         resultEncoder = new JpegF5Encode(aJpegImageContext, password.getBytes());
      } else if (F5ME.equalsIgnoreCase(algorithm)) {
         String password = aCommandLine.getOptionValue(PASSWORD_SHORT);

         resultEncoder = new JpegF5MeEncode(aJpegImageContext, password.getBytes());
      }

      return resultEncoder;
   }
}
