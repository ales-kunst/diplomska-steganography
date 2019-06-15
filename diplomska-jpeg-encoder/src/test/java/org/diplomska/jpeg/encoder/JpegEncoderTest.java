
/**
 *
 */
package org.diplomska.jpeg.encoder;

import org.diplomska.jpeg.encoder.JpegEncoder;
import org.diplomska.jpeg.encoder.JpegImageContext;
import org.diplomska.jpeg.processor.JpegImageBlockProcessor;

import org.junit.After;
import org.junit.Before;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.image.BufferedImage;

import java.net.URL;

import javax.imageio.ImageIO;

//~--- classes ----------------------------------------------------------------

/**
 * @author Ales Kunst
 *
 */
public class JpegEncoderTest {

   /** LOGGER */
   private static final Logger LOGGER = LoggerFactory.getLogger(JpegEncoderTest.class);

   //~--- fields --------------------------------------------------------------

   /** Field description */
   private JpegEncoder fJpegEncoder;

   //~--- set methods ---------------------------------------------------------

   /**
    *
    * @throws Exception
    */
   @Before
   public void setUp() throws Exception {
      URL                     desktopUrl              = ClassLoader.getSystemClassLoader().getResource("desktop.png");
      BufferedImage           image                   = ImageIO.read(desktopUrl);
      JpegImageContext        jpegImageContext        = new JpegImageContext(image);
      JpegImageBlockProcessor jpegImageBlockProcessor = new JpegImageBlockProcessor();

      fJpegEncoder = new JpegEncoder(jpegImageContext, jpegImageBlockProcessor);
   }

   //~--- methods -------------------------------------------------------------

   /**
    *
    * @throws Exception
    */
   @After
   public void tearDown() throws Exception {}
}
