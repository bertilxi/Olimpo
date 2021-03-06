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

import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.ResourceBundle;

import app.comun.ImpresoraPDF;
import app.datos.entidades.Cliente;
import app.datos.entidades.Inmueble;
import app.datos.entidades.Propietario;
import app.datos.entidades.Venta;
import app.excepciones.GestionException;
import app.excepciones.ImprimirPDFException;
import app.excepciones.PersistenciaException;
import app.logica.resultados.ResultadoCrearVenta;
import app.logica.resultados.ResultadoCrearVenta.ErrorCrearVenta;
import app.ui.componentes.ventanas.VentanaConfirmacion;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Controlador de la pantalla de alta venta
 *
 * Pertenece a la taskcard 29 de la iteración 2 y a la historia 8
 *
 */
public class AltaVentaController extends OlimpoController {

	public static final String URLVista = "/app/ui/vistas/altaVenta.fxml";

	@FXML
	protected Label labelCodigo;

	@FXML
	protected Label labelTipoInmueble;

	@FXML
	protected Label labelLocalidad;

	@FXML
	protected Label labelBarrio;

	@FXML
	protected Label labelCalle;

	@FXML
	protected Label labelAltura;

	@FXML
	protected Label labelPiso;

	@FXML
	protected Label labelDepartamento;

	@FXML
	protected Label labelOtros;

	@FXML
	protected Label labelNombre;

	@FXML
	protected Label labelApellido;

	@FXML
	protected Label labelTipoDocumento;

	@FXML
	protected Label labelDocumento;

	@FXML
	protected ComboBox<Cliente> comboBoxCliente;

	@FXML
	protected TextField textFieldImporte;

	@FXML
	protected TextField textFieldMedioDePago;

	protected Inmueble inmueble;

	protected ImpresoraPDF impresora = new ImpresoraPDF();

	/**
	 * Setea los campos con los datos del inmueble pasado por parámetro y de su propietario.
	 *
	 * @param inmueble
	 *            inmueble del que se obtienen los datos.
	 */
	public void setInmueble(Inmueble inmueble) {
		this.inmueble = inmueble;
		if(inmueble != null){
			Propietario propietario = inmueble.getPropietario();

			//seteo en pantalla los datos del inmueble que se va a vender
			labelAltura.setText(inmueble.getDireccion().getNumero());
			labelApellido.setText(propietario.getApellido());
			labelBarrio.setText(inmueble.getDireccion().getBarrio().getNombre());
			labelCalle.setText(inmueble.getDireccion().getCalle().getNombre());
			labelCodigo.setText(inmueble.getId().toString());
			labelDepartamento.setText(inmueble.getDireccion().getDepartamento());
			labelDocumento.setText(propietario.getNumeroDocumento());
			labelLocalidad.setText(inmueble.getDireccion().getLocalidad().getNombre());
			labelNombre.setText(propietario.getNombre());
			labelOtros.setText(inmueble.getDireccion().getOtros());
			labelPiso.setText(inmueble.getDireccion().getPiso());
			labelTipoDocumento.setText(propietario.getTipoDocumento().toString());
			labelTipoInmueble.setText(inmueble.getTipo().toString());
		}
	}

	@Override
	protected void inicializar(URL location, ResourceBundle resources) {
		this.setTitulo("Nueva Venta");

		try{
			comboBoxCliente.getItems().addAll(coordinador.obtenerClientes());
		} catch(PersistenciaException e){
			presentador.presentarExcepcion(e, stage);
		}
	}

	/**
	 * Acción que se ejecuta al apretar el botón aceptar.
	 *
	 * Valida que se hayan insertado datos, los carga a la venta y deriva la operación a capa lógica.
	 * Si la capa lógica retorna errores, se muestran al usuario.
	 */
	@FXML
	public void acceptAction() {
		StringBuilder errores = new StringBuilder("");

		//obtengo y verifico los datos introducidos
		Cliente cliente = comboBoxCliente.getValue();
		Double importe = null;
		String medioDePago = textFieldMedioDePago.getText().trim();

		if(cliente == null){
			errores.append("Elija un cliente").append("\n");
		}

		try{
			importe = Double.valueOf(textFieldImporte.getText().trim());
		} catch(Exception e){
			errores.append("Importe incorrecto. Introduzca solo números y un punto para decimales.\n");
		}

		if(medioDePago.isEmpty()){
			errores.append("Ingrese un medio de pago").append("\n");
		}

		if(inmueble == null){
			errores.append("No hay inmueble seleccionado").append("\n");
		}
		else if(inmueble.getPropietario() == null){
			errores.append("No se encuentra el propietario del inmueble").append("\n");
		}

		//si hay errores se muestra al usuario
		if(!errores.toString().isEmpty()){
			presentador.presentarError("Revise sus campos", errores.toString(), stage);
		}
		else{ //si no hay errores se muestra un Dialog emergente para que el vendedor confirme su contraseña
			boolean contraseñaCorrecta = showConfirmarContraseñaDialog();
			// si la contraseña es correcta se crea la venta
			if(contraseñaCorrecta){
				Venta venta = new Venta();
				venta.setCliente(cliente);
				venta.setFecha(new Date(System.currentTimeMillis()));
				venta.setImporte(importe);
				venta.setInmueble(inmueble);
				venta.setMedioDePago(medioDePago);
				venta.setPropietario(inmueble.getPropietario());
				venta.setVendedor(vendedorLogueado);

				try{ //se delega la operación a capa lógica
					ResultadoCrearVenta resultado = coordinador.crearVenta(venta);
					if(resultado.hayErrores()){
						//si hay errores los muestro al usuario
						StringBuilder stringErrores = new StringBuilder();
						for(ErrorCrearVenta e: resultado.getErrores()){
							switch(e) {
							case Cliente_Igual_A_Propietario:
								stringErrores.append("El cliente seleccionado es el actual propietario del inmueble.\n");
								break;
							case Cliente_Vacío:
								stringErrores.append("No se ha seleccionado ningún cliente.\n");
								break;
							case Formato_Medio_De_Pago_Incorrecto:
								stringErrores.append("Formato de medio de pago incorrecto.\n");
								break;
							case Importe_vacío:
								stringErrores.append("No se ha introducido importe.\n");
								break;
							case Inmueble_Reservado_Por_Otro_Cliente:
								stringErrores.append("El inmueble está reservado por otro cliente.\n");
								break;
							case Inmueble_Vacío:
								stringErrores.append("No se ha seleccionado ningún inmueble.\n");
								break;
							case Inmueble_Ya_Vendido:
								stringErrores.append("El inmueble ya se encuentra vendido.\n");
								break;
							case Medio_De_Pago_Vacío:
								stringErrores.append("No se ha introducido medio de pago.\n");
								break;
							case Propietario_Vacío:
								stringErrores.append("El inmueble no posee propietario.\n");
								break;
							case Vendedor_Vacío:
								stringErrores.append("No se ha confirmado el vendedor en esta operación.\n");
								break;
							}
						}
						presentador.presentarError("Revise sus campos", stringErrores.toString(), stage);
					}
					else{
						//si no hay errores muestro una notificación
						//y pregunto al usuario si quiere imprimir el documento generado
						presentador.presentarToast("Se ha realizado la venta con éxito", stage);
						VentanaConfirmacion ventana = presentador.presentarConfirmacion("Venta realizada correctamente", "¿Desea imprimir el documento generado?", stage);
						if(ventana.acepta()){
							//si acepta mando a imprimir
							try{
								impresora.imprimirPDF(venta.getArchivoPDF());
							} catch(ImprimirPDFException ex){
								presentador.presentarExcepcion(ex, stage);
							} catch(Exception e){
								presentador.presentarExcepcionInesperada(e);
							}
						}
						//vuelvo a la vista de listar inmuebles
						cambiarmeAScene(AdministrarInmuebleController.URLVista);
					}
				} catch(GestionException e){
					presentador.presentarExcepcion(e, stage);
				} catch(PersistenciaException e){
					presentador.presentarExcepcion(e, stage);
				} catch(Exception e){
					presentador.presentarExcepcionInesperada(e);
				}
			}
		}
	}

	/**
	 * Acción que se ejecuta al presionar el botón cancelar.
	 * Se vuelve a la pantalla administrar inmueble.
	 */
	@FXML
	private void cancelAction() {
		cambiarmeAScene(AdministrarInmuebleController.URLVista);
	}

	/**
	 * Muestra una ventana emergente para que el usuario confirme contraseña
	 *
	 * @return si es correcta la confirmación de contraseña o no
	 */
	protected boolean showConfirmarContraseñaDialog() {
		try{
			//cargo el fxml y creo el stage para el dialog
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource(ConfirmarContraseñaController.URLVista));
			VBox page = (VBox) loader.load();

			// creo el dialog
			Stage dialogStage = new Stage();
			dialogStage.setResizable(false);
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.setTitle("Confirmar contraseña");
			dialogStage.initOwner(stage);
			Scene scene = new Scene(page);
			dialogStage.setScene(scene);

			// cargo y seteo el controlador
			ConfirmarContraseñaController controlador = loader.getController();
			controlador.setDialogStage(dialogStage);
			controlador.setCoordinador(coordinador);
			controlador.setPresentador(presentador);
			controlador.setVendedorLogueado(vendedorLogueado);

			//muestro el dialog y espero hasta que el usuario lo cierre
			dialogStage.showAndWait();

			return controlador.isCorrecto();
		} catch(IOException e){
			presentador.presentarExcepcion(e, stage);
			return false;
		}
	}
}
