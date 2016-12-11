package app.logica.gestores;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;

import javax.imageio.ImageIO;

import org.springframework.stereotype.Service;

import com.lowagie.text.Document;
import com.lowagie.text.Image;
import com.lowagie.text.pdf.PdfWriter;

import app.datos.clases.CatalogoVista;
import app.datos.entidades.PDF;
import app.datos.entidades.Reserva;
import app.datos.entidades.Venta;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.WritableImage;

@Service
/**
 * Gestor que implementa la generación de PDFs a partir de un objeto dado.
 */
public class GestorPDF {

	/**
	 * Método para crear un PDF a partir de una pantalla.
	 *
	 * @param pantallaAPDF
	 *            pantalla que se imprimirá en PDF
	 * @return PDF de una captura de la pantalla pasada
	 */
	private PDF generarPDF(Node pantallaAPDF) throws Exception {
		WritableImage image = pantallaAPDF.snapshot(new SnapshotParameters(), null);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", baos);
		baos.flush();
		byte[] imageInByte = baos.toByteArray();
		baos.close();
		Image imagen = Image.getInstance(imageInByte);

		Document document = new Document();
		FileOutputStream pdf = new FileOutputStream("borrar1234.pdf");
		PdfWriter escritor = PdfWriter.getInstance(document, pdf);
		document.open();
		document.add(imagen);
		document.close();
		escritor.close();
		return null;
	}

	/**
	 * Método para crear un PDF de un catalogo a partir de los datos de un CatalogoVista.
	 * Pertenece a la taskcard 23 de la iteración 2 y a la historia 5
	 *
	 * @param catalogo
	 *            datos que se utilizaran para generar el PDF de un catalogo
	 * @return catalogo en PDF
	 */
	public PDF generarPDF(CatalogoVista catalogo) {
		//TODO hacer
		return null;
	}

	/**
	 * Método para crear un PDF de una reserva a partir de los datos de una Reserva.
	 * Pertenece a la taskcard 25 de la iteración 2 y a la historia 7
	 *
	 * @param reserva
	 *            datos que se utilizaran para generar el PDF de una reserva
	 * @return reserva en PDF
	 */
	public PDF generarPDF(Reserva reserva) {
		//TODO hacer
		return null;
	}

	/**
	 * Método para crear un PDF de una venta a partir de los datos de una Venta.
	 * Pertenece a la taskcard 30 de la iteración 2 y a la historia 8
	 *
	 * @param venta
	 *            datos que se utilizaran para generar el PDF de una venta
	 * @return venta en PDF
	 */
	public PDF generarPDF(Venta venta) {
		//TODO hacer
		return null;
	}
}
