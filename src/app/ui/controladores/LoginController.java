package app.ui.controladores;

import java.util.HashSet;
import java.util.Set;

import app.datos.clases.DatosLogin;
import app.datos.entidades.TipoDocumento;
import app.excepciones.PersistenciaException;
import app.logica.resultados.ResultadoAutenticacion;
import app.logica.resultados.ResultadoAutenticacion.ErrorAutenticacion;
import app.ui.controladores.resultado.ResultadoControlador;
import app.ui.controladores.resultado.ResultadoControlador.ErrorControlador;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController extends WindowTitleController {

	public static final String URLVista = "/app/ui/vistas/Login.fxml";

	@FXML
	protected TextField tfNumeroDocumento;

	@FXML
	protected PasswordField pfContra;

	@FXML
	protected ComboBox<TipoDocumento> cbTipoDocumento;

	@FXML
	public void registrar() {

	}

	@FXML
	public ResultadoControlador ingresar() {
		Set<ErrorControlador> erroresControlador = new HashSet<>();
		ResultadoAutenticacion resultado = null;
		Boolean hayErrores;
		DatosLogin datos;
		String errores = "";

		//Toma de datos de la vista
		TipoDocumento tipoDocumento = cbTipoDocumento.getValue();
		String dni = tfNumeroDocumento.getText().trim();
		char[] pass = pfContra.getText().toCharArray();

		if(tipoDocumento == null || dni.isEmpty() || pass.length < 1){
			presentador.presentarError("No se ha podido iniciar sesión", "Campos vacíos.", stage);
			return new ResultadoControlador(ErrorControlador.Campos_Vacios);
		}
		datos = new DatosLogin(tipoDocumento, dni, pass);

		//Inicio transacción al gestor
		try{
			resultado = coordinador.autenticarVendedor(datos);
		} catch(PersistenciaException e){
			presentador.presentarExcepcion(e, stage);
			return new ResultadoControlador(ErrorControlador.Error_Persistencia);
		} catch(Exception e){
			presentador.presentarExcepcionInesperada(e, stage);
			return new ResultadoControlador(ErrorControlador.Error_Desconocido);
		}

		//Tratamiento de errores
		hayErrores = resultado.hayErrores();
		if(hayErrores){
			for(ErrorAutenticacion r: resultado.getErrores()){
				switch(r) {
				case Datos_Incorrectos:
					errores += "Datos inválidos al iniciar sesión.\n";
					erroresControlador.add(ErrorControlador.Datos_Incorrectos);
					break;
				}
			}

			if(!errores.isEmpty()){
				presentador.presentarError("No se ha podido iniciar sesión", errores, stage);
			}
		}
		else{
			//Operacion exitosa
			// Ir otra pantalla
			// ControladorRomano.cambiarScene(MenuAdministracionController.URLVista, apilador, coordinador);
		}
		return new ResultadoControlador(erroresControlador.toArray(new ErrorControlador[0]));
	}

}
