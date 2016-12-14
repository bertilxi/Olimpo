package app.ui.controladores;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import app.datos.clases.CatalogoVista;
import app.datos.entidades.Cliente;
import app.datos.entidades.Imagen;
import app.datos.entidades.Inmueble;
import app.datos.entidades.PDF;
import app.excepciones.GestionException;
import app.excepciones.PersistenciaException;
import app.logica.resultados.ResultadoCrearCatalogo;
import app.logica.resultados.ResultadoCrearCatalogo.ErrorCrearCatalogo;
import app.ui.ScenographyChanger;
import app.ui.controladores.resultado.ResultadoControlador;
import app.ui.controladores.resultado.ResultadoControlador.ErrorControlador;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.Pane;

/**
 * Controlador de la vista para dar de alta un catálogo
 */
public class AltaCatalogoController extends OlimpoController {

	public static final String URLVista = "/app/ui/vistas/altaCatalogo.fxml";

	@FXML
	protected Pane listaInmuebles;

	protected ArrayList<Inmueble> inmuebles = new ArrayList<>();

	@FXML
	protected ComboBox<Cliente> cbCliente;

	@FXML
	protected Pane fondo;

	@FXML
	protected Button btnGenerarCatalogo;
	
	protected Map<Node, RenglonInmuebleController> renglones = new HashMap<>();

	@Override
	protected void inicializar(URL location, ResourceBundle resources) {
		this.agregarScenographyChanger(fondo, new ScenographyChanger(stage, presentador, coordinador, fondo));
		this.setTitulo("Nuevo catalogo");
		
		btnGenerarCatalogo.setDisable(true);
		listaInmuebles.getChildren().addListener(new ListChangeListener<Node>() {
			@Override
			public void onChanged(javafx.collections.ListChangeListener.Change<? extends Node> c) {
				btnGenerarCatalogo.setDisable(c.getList().isEmpty() || cbCliente.getValue() == null);
			}
		});
		
		cbCliente.valueProperty().addListener(new ChangeListener<Cliente>() {
			@Override
			public void changed(ObservableValue<? extends Cliente> observable, Cliente oldValue, Cliente newValue) {
				btnGenerarCatalogo.setDisable(listaInmuebles.getChildren().isEmpty() || newValue == null);
			}
		});
		
		try{
			cbCliente.getItems().addAll(coordinador.obtenerClientes());
		} catch(PersistenciaException e){
			presentador.presentarExcepcion(e, stage);
		}
	}

	@FXML
	public void agregarInmueble() {
		final ArrayList<Inmueble> inmueblesNuevos = new ArrayList<>();
		AdministrarInmuebleController vistaInmuebles = (AdministrarInmuebleController) this.cambiarScene(fondo, AdministrarInmuebleController.URLVista, (Pane) fondo.getChildren().get(0));
		vistaInmuebles.formatearObtenerInmuebles(inmuebles, inmueblesNuevos, () -> {
			agregarInmuebles(inmueblesNuevos);
		}, true);
	}

	private void agregarInmuebles(ArrayList<Inmueble> inmueblesNuevos) {
		try{
			for(Inmueble inmueble: inmueblesNuevos){
				RenglonInmuebleController renglonController = new RenglonInmuebleController(inmueble);
				renglonController.setEliminarInmueble(() -> {
					listaInmuebles.getChildren().remove(renglonController.getRoot());
					renglones.remove(renglonController.getRoot());
					inmuebles.remove(inmueble);
				});
				listaInmuebles.getChildren().add(renglonController.getRoot());
				renglones.put(renglonController.getRoot(), renglonController);
				inmuebles.add(inmueble);
			}
		} catch(Exception e){
			presentador.presentarExcepcionInesperada(e);
		}
	}

	@FXML
	public ResultadoControlador generarCatalogo() {
		ResultadoCrearCatalogo resultado;
		StringBuffer erroresBfr = new StringBuffer();
		CatalogoVista catalogo = null;

		//Toma de datos de la vista
		Map<Inmueble, Imagen> fotos = new HashMap<>();
		for(Node n: listaInmuebles.getChildren()){
			if(renglones.get(n) != null){
				RenglonInmuebleController renglon = renglones.get(n);
				fotos.put(renglon.getInmueble(), renglon.getFotoSeleccionada());
			}
		}
		catalogo = new CatalogoVista(cbCliente.getValue(), fotos);

		//Inicio transacciones al gestor
		try{
			resultado = coordinador.crearCatalogo(catalogo);
		} catch(PersistenciaException | GestionException e){
			presentador.presentarExcepcion(e, stage);
			return new ResultadoControlador(ErrorControlador.Error_Persistencia);
		} catch(Exception e){
			presentador.presentarExcepcionInesperada(e, stage);
			return new ResultadoControlador(ErrorControlador.Error_Desconocido);
		}

		//Tratamiento de errores
		if(resultado.hayErrores()){
			for(ErrorCrearCatalogo e: resultado.getErrores()){
				switch(e) {
				case Barrio_Inmueble_Inexistente:
					erroresBfr.append("El barrio del inmueble está vacío.\n");
					break;
				case Cliente_inexistente:
					erroresBfr.append("No se seleccionó un cliente.\n");
					break;
				case Codigo_Inmueble_Inexistente:
					erroresBfr.append("El código del inmueble está vacío.\n");
					break;
				case Direccion_Inmueble_Inexistente:
					erroresBfr.append("La dirección del inmueble está vacío.\n");
					break;
				case Localidad_Inmueble_Inexistente:
					erroresBfr.append("La localidad del inmueble está vacío.\n");
					break;
				case Precio_Inmueble_Inexistente:
					erroresBfr.append("El precio del inmueble está vacío.\n");
					break;
				case Tipo_Inmueble_Inexistente:
					erroresBfr.append("El tipo del inmueble está vacío.\n");
					break;
				}
			}

			String errores = erroresBfr.toString();
			if(!errores.isEmpty()){
				presentador.presentarError("Error al crear el catálogo", errores, stage);
			}
			return new ResultadoControlador(ErrorControlador.Campos_Vacios);
		}
		else{
			presentador.presentarToast("Se ha creado el catálogo con éxito", stage);
			mostrarPDF(resultado.getCatalogoPDF());
			return new ResultadoControlador();
		}
	}

	protected void mostrarPDF(PDF pdf) {
		String irA = null;
		if(URLVistaRetorno != null){
			irA = URLVistaRetorno;
		}
		else{
			irA = AdministrarClienteController.URLVista;
		}
		VerPDFController visorPDF = (VerPDFController) cambiarmeAScene(VerPDFController.URLVista, irA);
		visorPDF.cargarPDF(pdf);
		visorPDF.setVendedorLogueado(vendedorLogueado);
	}

	public void setCliente(Cliente cliente) {
		Platform.runLater(() -> {
			cbCliente.getSelectionModel().select(cliente);
		});
		cbCliente.setDisable(true);
	}

	public void setInmuebles(ArrayList<Inmueble> inmuebles) {
		agregarInmuebles(inmuebles);
	}

	@Override
	@FXML
	public void salir() {
		if(URLVistaRetorno != null){
			cambiarmeAScene(URLVistaRetorno);
		}
		else{
			cambiarmeAScene(AdministrarClienteController.URLVista);
		}
	}
}
