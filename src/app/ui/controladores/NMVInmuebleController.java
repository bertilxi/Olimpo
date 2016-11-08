/**
 * Copyright (C) 2016 Fernando Berti - Daniel Campodonico - Emiliano Gioria - Lucas Moretti - Esteban Rebechi - Andres Leonel Rico
 * This file is part of Olimpo.
 *
 * Olimpo is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Olimpo is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Olimpo. If not, see <http://www.gnu.org/licenses/>.
 */
package app.ui.controladores;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javax.imageio.ImageIO;

import app.datos.clases.OrientacionStr;
import app.datos.entidades.Barrio;
import app.datos.entidades.Calle;
import app.datos.entidades.Inmueble;
import app.datos.entidades.Localidad;
import app.datos.entidades.Pais;
import app.datos.entidades.Propietario;
import app.datos.entidades.Provincia;
import app.datos.entidades.TipoInmueble;
import app.excepciones.PersistenciaException;
import app.ui.controladores.resultado.ResultadoControlador;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.util.StringConverter;

/**
 * Controlador de la vista que crea, modifica o muestra un inmueble
 * Pertenece a la taskcard 13 de la iteración 1 y a la historia 3
 */
public class NMVInmuebleController extends OlimpoController {

	public static final String URLVista = "/app/ui/vistas/NMVInmueble.fxml";

	@FXML
	private CheckBox cbAguaCaliente;

	@FXML
	private CheckBox cbAguaCorriente;

	@FXML
	private CheckBox cbCloaca;

	@FXML
	private CheckBox cbGarage;

	@FXML
	private CheckBox cbGasNatural;

	@FXML
	private CheckBox cbLavadero;

	@FXML
	private CheckBox cbPatio;

	@FXML
	private CheckBox cbPavimento;

	@FXML
	private CheckBox cbPiscina;

	@FXML
	private CheckBox cbPropiedadHorizontal;

	@FXML
	private CheckBox cbTelefono;

	@FXML
	private ComboBox<Barrio> cbBarrio;

	@FXML
	private ComboBox<Calle> cbCalle;

	@FXML
	private ComboBox<Localidad> cbLocalidad;

	@FXML
	private ComboBox<OrientacionStr> cbOrientacion;

	@FXML
	private ComboBox<Pais> cbPais;

	@FXML
	private ComboBox<Propietario> cbPropietario;

	@FXML
	private ComboBox<Provincia> cbProvincia;

	@FXML
	private ComboBox<TipoInmueble> cbTipoInmueble;

	@FXML
	private TextArea taObservaciones;

	@FXML
	private TextField tfAltura;

	@FXML
	private TextField tfAntiguedad;

	@FXML
	private TextField tfBaños;

	@FXML
	private TextField tfCodigo;

	@FXML
	private TextField tfDepartamento;

	@FXML
	private TextField tfDormitorios;

	@FXML
	private TextField tfFechaCarga;

	@FXML
	private TextField tfFondo;

	@FXML
	private TextField tfFrente;

	@FXML
	private TextField tfOtros;

	@FXML
	private TextField tfPiso;

	@FXML
	private TextField tfPrecioVenta;

	@FXML
	private TextField tfSuperficie;

	@FXML
	private TextField tfSuperficieEdificio;

	@FXML
	private Pane padre;

	@FXML
	private Pane pantalla1;

	@FXML
	private Pane pantalla2;

	@FXML
	private Pane panelFotos;

	@FXML
	private ImageView imagenSeleccionada;

	@FXML
	private Button btQuitarFoto;

	private StringProperty titulo1 = new SimpleStringProperty();

	private StringProperty titulo2 = new SimpleStringProperty();

	private Inmueble inmueble;

	@Override
	protected void inicializar(URL location, ResourceBundle resources) {
		titulo1.addListener((observable, oldValue, newValue) -> {
			setTitulo(newValue + " - " + titulo2.get());
		});
		titulo2.addListener((observable, oldValue, newValue) -> {
			setTitulo(titulo1.get() + " - " + newValue);
		});
		titulo1.set("Nuevo inmueble");
		atras();
		
		try{
			cbPropietario.getItems().addAll(coordinador.obtenerPropietarios());
		} catch(PersistenciaException e){
			presentador.presentarExcepcion(e, stage);
		}
		
		try{
			cbTipoInmueble.getItems().addAll(coordinador.obtenerTiposInmueble());
		} catch(PersistenciaException e){
			presentador.presentarExcepcion(e, stage);
		}
		
		cbOrientacion.getItems().addAll(OrientacionStr.values());
		
		try{
			cbPais.getItems().addAll(coordinador.obtenerPaises());
		} catch(PersistenciaException e){
			presentador.presentarExcepcion(e, stage);
		}
		
		//se selecciona por defecto a argentina en el combo box país
		for(Pais p: cbPais.getItems()) {
			if(p.getNombre().equals("Argentina")) {
				cbPais.getSelectionModel().select(p);
				break;
			}
		}
		actualizarProvincias(cbPais.getSelectionModel().getSelectedItem());


		cbPais.getSelectionModel().selectedItemProperty().addListener(
				(observable, oldValue, newValue) -> actualizarProvincias(newValue));
		cbProvincia.getSelectionModel().selectedItemProperty().addListener(
				(observable, oldValue, newValue) -> actualizarLocalidades(newValue));
		cbLocalidad.getSelectionModel().selectedItemProperty().addListener(
				(observable, oldValue, newValue) -> actualizarBarriosYCalles(newValue));

		//se setean los converters para cuando se ingrese un item no existente a través
		//del editor de texto de los comboBox editables
		cbPais.setConverter(new StringConverter<Pais>() {

			@Override
			public String toString(Pais object) {
				if(object == null){
					return null;
				}
				return object.toString();
			}

			@Override
			public Pais fromString(String nombre) {
				nombre = nombre.trim();
				if(nombre.isEmpty()){
					return null;
				}
				for(Pais pais: cbPais.getItems()){
					if(nombre.equals(pais.getNombre())){
						return pais;
					}
				}
				Pais pais = new Pais();
				pais.setNombre(nombre);
				return pais;
			}
		});

		cbProvincia.setConverter(new StringConverter<Provincia>() {

			@Override
			public String toString(Provincia object) {
				if(object == null){
					return null;
				}
				return object.toString();
			}

			@Override
			public Provincia fromString(String nombre) {
				nombre = nombre.trim();
				if(nombre.isEmpty()){
					return null;
				}
				for(Provincia prov: cbProvincia.getItems()){
					if(nombre.equals(prov.getNombre())){
						return prov;
					}
				}
				Provincia prov = new Provincia();
				prov.setNombre(nombre);
				prov.setPais(cbPais.getValue());
				return prov;
			}
		});

		cbLocalidad.setConverter(new StringConverter<Localidad>() {

			@Override
			public String toString(Localidad object) {
				if(object == null){
					return null;
				}
				return object.toString();
			}

			@Override
			public Localidad fromString(String nombre) {
				nombre = nombre.trim();
				if(nombre.isEmpty()){
					return null;
				}
				for(Localidad loc: cbLocalidad.getItems()){
					if(nombre.equals(loc.getNombre())){
						return loc;
					}
				}
				Localidad loc = new Localidad();
				loc.setNombre(nombre);
				loc.setProvincia(cbProvincia.getValue());
				return loc;
			}
		});

		cbBarrio.setConverter(new StringConverter<Barrio>() {

			@Override
			public String toString(Barrio object) {
				if(object == null){
					return null;
				}
				return object.toString();
			}

			@Override
			public Barrio fromString(String nombre) {
				nombre = nombre.trim();
				if(nombre.isEmpty()){
					return null;
				}
				for(Barrio bar: cbBarrio.getItems()){
					if(nombre.equals(bar.getNombre())){
						return bar;
					}
				}
				Barrio bar = new Barrio();
				bar.setNombre(nombre);
				bar.setLocalidad(cbLocalidad.getValue());
				return bar;
			}
		});

		cbCalle.setConverter(new StringConverter<Calle>() {

			@Override
			public String toString(Calle object) {
				if(object == null){
					return null;
				}
				return object.toString();
			}

			@Override
			public Calle fromString(String nombre) {
				nombre = nombre.trim();
				if(nombre.isEmpty()){
					return null;
				}
				for(Calle cal: cbCalle.getItems()){
					if(nombre.equals(cal.getNombre())){
						return cal;
					}
				}
				Calle cal = new Calle();
				cal.setNombre(nombre);
				cal.setLocalidad(cbLocalidad.getValue());
				return cal;
			}
		});

		//Cuando el foco sale de los comboBox que estaban siendo editados
		//el texto ingresado se convierte en un item y se lo selecciona
		cbPais.focusedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldPropertyValue, Boolean newPropertyValue) {
				if(!newPropertyValue){
					cbPais.getSelectionModel().select(cbPais.getConverter().fromString(cbPais.getEditor().getText()));

				}
			}
		});

		cbProvincia.focusedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldPropertyValue, Boolean newPropertyValue) {
				if(!newPropertyValue){
					cbProvincia.getSelectionModel().select(cbProvincia.getConverter().fromString(cbProvincia.getEditor().getText()));

				}
			}
		});

		cbLocalidad.focusedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldPropertyValue, Boolean newPropertyValue) {
				if(!newPropertyValue){
					cbLocalidad.getSelectionModel().select(cbLocalidad.getConverter().fromString(cbLocalidad.getEditor().getText()));

				}
			}
		});

		cbBarrio.focusedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldPropertyValue, Boolean newPropertyValue) {
				if(!newPropertyValue){
					cbBarrio.getSelectionModel().select(cbBarrio.getConverter().fromString(cbBarrio.getEditor().getText()));

				}
			}
		});

		cbCalle.focusedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldPropertyValue, Boolean newPropertyValue) {
				if(!newPropertyValue){
					cbCalle.getSelectionModel().select(cbCalle.getConverter().fromString(cbCalle.getEditor().getText()));

				}
			}
		});
	}
	
	/**
	 * Cuando varía la seleccion del comboBox de provincias, se actualiza el comboBox de localidades.
	 * También se delega la tarea de vaciar los comboBox de barrios y calles
	 *
	 * @param provincia
	 * 			provincia que fué seleccionada en el comboBox. Si no hay nada seleccionado, es <code>null</code>
	 */
	private void actualizarLocalidades(Provincia provincia) {
		cbLocalidad.getItems().clear();
		if(provincia != null && provincia.getId() != null){
			try{
				cbLocalidad.getItems().addAll(coordinador.obtenerLocalidadesDe(provincia));
			} catch(PersistenciaException e){
				presentador.presentarExcepcion(e, stage);
			}
		}
		actualizarBarriosYCalles(null);
	}

	/**
	 * Cuando varía la seleccion del comboBox de países, se actualiza el comboBox de provincias.
	 * También se delega la tarea de vaciar el comboBox de localidades
	 *
	 * @param pais
	 * 			país que fué seleccionado en el comboBox. Si no hay nada seleccionado, es <code>null</code>
	 */
	private void actualizarProvincias(Pais pais) {
		cbProvincia.getItems().clear();
		if(pais != null && pais.getId() != null){
			try{
				cbProvincia.getItems().addAll(coordinador.obtenerProvinciasDe(pais));
			} catch(PersistenciaException e){
				presentador.presentarExcepcion(e, stage);
			}
		}
		actualizarLocalidades(null);
	}

	/**
	 * Cuando varía la seleccion del comboBox de localidades, se actualizan los comboBox de barrios y calles.
	 *
	 * @param loc
	 * 			localidad que fué seleccionada en el comboBox. Si no hay nada seleccionado, es <code>null</code>
	 */
	private void actualizarBarriosYCalles(Localidad loc) {
		cbBarrio.getItems().clear();
		cbCalle.getItems().clear();
		if(loc != null && loc.getId() != null){
			try{
				cbBarrio.getItems().addAll(coordinador.obtenerBarriosDe(loc));
				cbCalle.getItems().addAll(coordinador.obtenerCallesDe(loc));
			} catch(PersistenciaException e){
				presentador.presentarExcepcion(e, stage);
			}
		}
		cbBarrio.getEditor().clear();
		cbCalle.getEditor().clear();
	}
	

	@FXML
	public void agregarFoto() {
		File imagen = solicitarArchivo();
		if(imagen == null){
			return;
		}

		try{
			final ImageView imageView = new ImageView(imagen.toURI().toURL().toExternalForm());
			imageView.setPreserveRatio(true);
			imageView.setFitHeight(100);
			imageView.setOnMouseClicked((event) -> {
				cambiarImagen(imageView);
			});
			panelFotos.getChildren().add(imageView);
		} catch(MalformedURLException e){
			presentador.presentarExcepcionInesperada(e, stage);
		}
	}

	private void cambiarImagen(ImageView imageView) {
		if(imagenSeleccionada != null){
			imagenSeleccionada.setOpacity(1);
			if(imagenSeleccionada.equals(imageView)){
				imagenSeleccionada = null;
				btQuitarFoto.setDisable(true);
				return;
			}
		}
		btQuitarFoto.setDisable(false);
		imagenSeleccionada = imageView;
		imagenSeleccionada.setOpacity(0.5);
	}

	private File solicitarArchivo() {
		File retorno = null;
		String tipos = "(";
		ArrayList<String> tiposFiltro = new ArrayList<>();
		for(String formato: ImageIO.getReaderFormatNames()){
			tipos += "*." + formato + ";";
			tiposFiltro.add("*." + formato);
		}
		tipos = tipos.substring(0, tipos.length() - 1);
		tipos += ")";

		ExtensionFilter filtro = new ExtensionFilter("Archivo de imágen " + tipos, tiposFiltro);

		FileChooser archivoSeleccionado = new FileChooser();
		archivoSeleccionado.getExtensionFilters().add(filtro);

		retorno = archivoSeleccionado.showOpenDialog(stage);
		if(retorno != null){
			String nombreArchivo = retorno.toString();
			retorno = new File(nombreArchivo);
		}
		return retorno;
	}

	@FXML
	public void quitarFoto() {
		panelFotos.getChildren().remove(imagenSeleccionada);
		imagenSeleccionada = null;
		if(panelFotos.getChildren().isEmpty()){
			btQuitarFoto.setDisable(true);
		}
	}

	@FXML
	public void cancelar() {
		salir();
	}

	@Override
	public void salir() {
		if(URLVistaRetorno != null){
			cambiarmeAScene(URLVistaRetorno);
		}
		else{
			cambiarmeAScene(AdministrarInmuebleController.URLVista);
		}
	}

	@FXML
	/**
	 * Método que permite guardar los cambios hechos en la vista
	 * Pertenece a la taskcard 13 de la iteración 1 y a la historia 3
	 *
	 * @return ResultadoControlador que resume lo que hizo el controlador
	 */
	public ResultadoControlador aceptar() {
		crearInmueble();
		modificarInmueble();
		return null;
	}

	/**
	 * Método que permite crear un inmueble
	 * Pertenece a la taskcard 13 de la iteración 1 y a la historia 3
	 *
	 * @return ResultadoControlador que resume lo que hizo el controlador
	 */
	private ResultadoControlador crearInmueble() {
		
		return null;
	}

	/**
	 * Método que permite modificar un inmueble
	 * Pertenece a la taskcard 13 de la iteración 1 y a la historia 3
	 *
	 * @return ResultadoControlador que resume lo que hizo el controlador
	 */
	private ResultadoControlador modificarInmueble() {
		return null;
	}

	public void formatearModificarInmueble(Inmueble inmueble) {

	}

	public void formatearVerInmueble(Inmueble inmueble) {

	}

	@FXML
	public void atras() {
		padre.getChildren().clear();
		padre.getChildren().add(pantalla1);
		titulo2.set("Datos inmueble");
	}

	@FXML
	public void siguiente() {
		padre.getChildren().clear();
		padre.getChildren().add(pantalla2);
		titulo2.set("Datos edificio");
	}
}
