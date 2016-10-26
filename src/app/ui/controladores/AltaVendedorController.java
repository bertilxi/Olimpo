package app.ui.controladores;

import app.datos.entidades.TipoDocumento;
import app.datos.entidades.Vendedor;
import app.excepciones.PersistenciaException;
import app.logica.gestores.GestorDatos;
import app.logica.gestores.GestorVendedor;
import app.logica.resultados.ResultadoCrearVendedor;
import app.ui.componentes.VentanaError;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class AltaVendedorController extends BaseController {

	@FXML
	private TextField textFieldNombre;
	@FXML
	private TextField textFieldApellido;
	@FXML
	private TextField textFieldNumeroDocumento;
	@FXML
	private TextField textFieldContraseña;
	@FXML
	private TextField textFieldRepiteContraseña;
	@FXML
	private ComboBox<TipoDocumento> comboBoxTipoDocumento;

	private ArrayList<TipoDocumento> listaTiposDeDocumento;

	private GestorDatos gestorDatos;

	private GestorVendedor gestorVendedor;

	public ResultadoCrearVendedor acceptAction() {

		StringBuffer error = new StringBuffer("");

		String nombre = textFieldNombre.getText().trim();
		String apellido = textFieldApellido.getText().trim();
		String numeroDocumento = textFieldNumeroDocumento.getText().trim();
		String password1 = textFieldContraseña.getText().trim();
		String password2 = textFieldRepiteContraseña.getText().trim();
		TipoDocumento tipoDoc = comboBoxTipoDocumento.getValue();

		if(nombre.isEmpty()){
			error.append("Inserte un nombre").append("\r\n ");
		}
		if(apellido.isEmpty()){
			error.append("Inserte un apellido").append("\r\n ");
		}
		/*
		 * if (tipoDoc.equals(new TipoDocumento())){
		 * error.append("Elija un tipo de documento").append("\r\n ");
		 * }
		 */
		if(numeroDocumento.isEmpty()){
			error.append("Inserte un numero de documento").append("\r\n ");
		}
		if(password1.isEmpty() && password2.isEmpty()){
			error.append("Inserte su contraseña").append("\r\n ");
		}
		if(!password1.isEmpty() && password2.isEmpty()){
			error.append("Inserte su contraseña nuevamente").append("\r\n ");
		}
		if(!password1.equals(password2)){
			error.append("Sus contraseñas no coinciden, Ingreselas nuevamente").append("\r\n ");
			textFieldContraseña.setText("");
			textFieldRepiteContraseña.setText("");
		}

		if(!error.toString().isEmpty()){
			VentanaError ventanaError = new VentanaError("Revise sus campos", error.toString());
		}
		else{
			Vendedor vendedor = new Vendedor();
			vendedor.setId(null).setNombre(nombre).setApellido(apellido).setNumeroDocumento(numeroDocumento)
					.setTipoDocumento(tipoDoc).setPassword(password1);

			//No está implementado. faltarían parámetros y el catch de excepciones
			//gestorVendedor.crearVendedor();
		}

		return null

	}

    public void cancelAction() {

    }


	@Override
	public void initialize(URL location, ResourceBundle resources) {
		super.initialize(location, resources);

		try {
			listaTiposDeDocumento = gestorDatos.obtenerTiposDeDocumento();
		} catch (PersistenciaException e) {
			// TODO mostrar error inesperado
		}
        comboBoxTipoDocumento.getItems().addAll(listaTiposDeDocumento);
	}
}
