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
package app.logica.gestores;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.annotation.Resource;
import javax.mail.MessagingException;

import org.springframework.stereotype.Service;

import app.datos.clases.EstadoInmuebleStr;
import app.datos.clases.EstadoStr;
import app.datos.entidades.Cliente;
import app.datos.entidades.Estado;
import app.datos.entidades.Inmueble;
import app.datos.entidades.PDF;
import app.datos.entidades.Reserva;
import app.datos.servicios.ReservaService;
import app.excepciones.GestionException;
import app.excepciones.PersistenciaException;
import app.logica.resultados.ResultadoCrearReserva;
import app.logica.resultados.ResultadoCrearReserva.ErrorCrearReserva;
import app.logica.resultados.ResultadoEliminarReserva;

@Service
/**
 * Gestor que implementa la capa lógica del ABM Reserva y funciones asociadas a una reserva.
 */
public class GestorReserva {

	public static final String ASUNTO_RESERVA_CREADA = "Reserva efectuada con éxito";
	public static final String MENSAJE_RESERVA_CREADA = "Su reserva se ha registrado exitosamente. Puede ver los detalles de la reserva en el documento adjunto.";

	@Resource
	protected ReservaService persistidorReserva;

	@Resource
	protected GestorDatos gestorDatos;

	@Resource
	protected GestorPDF gestorPDF;

	@Resource
	protected GestorEmail gestorEmail;

	/**
	 * Método para crear una reserva. Primero se validan las reglas de negocio y luego se persiste.
	 * Pertenece a la taskcard 25 de la iteración 2 y a la historia 7
	 *
	 * @param reserva
	 *            a guardar
	 * @return resultado de la operación
	 * @throws PersistenciaException
	 *             si falló al persistir
	 * @throws GestionException
	 *             si falló al procesar la reserva
	 */
	public ResultadoCrearReserva crearReserva(Reserva reserva) throws PersistenciaException, GestionException {
		//Inicialización de variables
		Set<ErrorCrearReserva> errores = new HashSet<>();
		boolean reservaEnConflictoEncontrada = false;
		boolean fechaInicioVacia = false;
		boolean fechaFinVacia = false;
		Reserva reservaEnConflicto = null;

		//Validaciones de lógica
		if(reserva.getCliente() == null){
			errores.add(ErrorCrearReserva.Cliente_Vacío);
		}
		else{
			if(reserva.getCliente().getNombre() == null){
				errores.add(ErrorCrearReserva.Nombre_Cliente_Vacío);
			}
			if(reserva.getCliente().getApellido() == null){
				errores.add(ErrorCrearReserva.Apellido_Cliente_Vacío);
			}
			if(reserva.getCliente().getTipoDocumento() == null || reserva.getCliente().getTipoDocumento().getTipo() == null){
				errores.add(ErrorCrearReserva.TipoDocumento_Cliente_Vacío);
			}
			if(reserva.getCliente().getNumeroDocumento() == null){
				errores.add(ErrorCrearReserva.NúmeroDocumento_Cliente_Vacío);
			}
		}
		if(reserva.getInmueble() == null){
			errores.add(ErrorCrearReserva.Inmueble_Vacío);
		}
		else{
			if(reserva.getInmueble().getEstadoInmueble().getEstado().equals(EstadoInmuebleStr.VENDIDO)){
				errores.add(ErrorCrearReserva.Inmueble_Vendido);
			}
			if(reserva.getInmueble().getPropietario() == null){
				errores.add(ErrorCrearReserva.Propietario_Vacío);
			}
			else{
				if(reserva.getInmueble().getPropietario().getNombre() == null){
					errores.add(ErrorCrearReserva.Nombre_Propietario_Vacío);
				}
				if(reserva.getInmueble().getPropietario().getApellido() == null){
					errores.add(ErrorCrearReserva.Apellido_Propietario_Vacío);
				}
			}
			if(reserva.getInmueble().getTipo() == null || reserva.getInmueble().getTipo().getTipo() == null){
				errores.add(ErrorCrearReserva.Tipo_Inmueble_Vacío);
			}
			if(reserva.getInmueble().getDireccion() == null){
				errores.add(ErrorCrearReserva.Dirección_Inmueble_Vacía);
			}
			else{
				if(reserva.getInmueble().getDireccion().getLocalidad() == null){
					errores.add(ErrorCrearReserva.Localidad_Inmueble_Vacía);
				}
				if(reserva.getInmueble().getDireccion().getBarrio() == null){
					errores.add(ErrorCrearReserva.Barrio_Inmueble_Vacío);
				}
				if(reserva.getInmueble().getDireccion().getCalle() == null){
					errores.add(ErrorCrearReserva.Calle_Inmueble_Vacía);
				}
				if(reserva.getInmueble().getDireccion().getNumero() == null){
					errores.add(ErrorCrearReserva.Altura_Inmueble_Vacía);
				}
			}
		}
		if(reserva.getFechaInicio() == null){
			errores.add(ErrorCrearReserva.FechaInicio_vacía);
			fechaInicioVacia = true;
		}

		if(reserva.getFechaFin() == null){
			errores.add(ErrorCrearReserva.FechaFin_vacía);
			fechaFinVacia = true;
		}

		if(!fechaInicioVacia && !fechaFinVacia){
			//Validamos que el rango de fechas no coincida con el de otra reserva
			if(reserva.getFechaInicio().compareTo(reserva.getFechaFin()) > 0){
				errores.add(ErrorCrearReserva.Fecha_Inicio_Posterior_A_Fecha_Fin);
			}
			else if(reserva.getInmueble() != null){
				Set<Reserva> reservasDelInmueble = reserva.getInmueble().getReservas();
				Iterator<Reserva> itRes = reservasDelInmueble.iterator();
				Reserva res = null;
				while(!reservaEnConflictoEncontrada && itRes.hasNext()){
					res = itRes.next();
					if(res.getEstado().getEstado().equals(EstadoStr.ALTA)
							&& res.getFechaFin().compareTo(reserva.getFechaInicio()) > 0
							&& res.getFechaInicio().compareTo(reserva.getFechaFin()) < 0){
						reservaEnConflictoEncontrada = true;
					}
				}
				if(reservaEnConflictoEncontrada){
					reservaEnConflicto = res;
					errores.add(ErrorCrearReserva.Existe_Otra_Reserva_Activa);

				}
			}
		}

		if(reserva.getImporte() == null){
			errores.add(ErrorCrearReserva.Importe_Vacío);
		}
		else if(reserva.getImporte() <= 0){
			errores.add(ErrorCrearReserva.Importe_Menor_O_Igual_A_Cero);
		}

		if(errores.isEmpty()){
			//Si no hay errores generamos el pdf de la reserva
			final PDF pdfReserva = gestorPDF.generarPDF(reserva);
			//se lo seteamos a reserva
			reserva.setArchivoPDF(pdfReserva);
			//también le seteamos el estado
			Estado estadoAlta = null;
			for(Estado estado: gestorDatos.obtenerEstados()){
				if(estado.getEstado().equals(EstadoStr.ALTA)){
					estadoAlta = estado;
				}
			}
			reserva.setEstado(estadoAlta);
			//Mandamos un mail con la reserva (de forma asincrónica)
			new Thread(() -> {
				try{
					if(reserva.getCliente().getCorreo() != null){
						gestorEmail.enviarEmail(reserva.getCliente().getCorreo(), ASUNTO_RESERVA_CREADA, MENSAJE_RESERVA_CREADA, pdfReserva);
					}
				} catch(IOException | MessagingException e){
					e.printStackTrace();
				}
			}).start();
			//Persistimos la reserva
			persistidorReserva.guardarReserva(reserva);
			//Retornamos el PDF generado
			return new ResultadoCrearReserva(pdfReserva);
		}

		//Retornamos los errores
		return new ResultadoCrearReserva(reservaEnConflicto, errores.toArray(new ErrorCrearReserva[0]));
	}

	/**
	 * Método para eliminar una reserva. Se hace una baja lógica.
	 * Pertenece a la taskcard 25 de la iteración 2 y a la historia 7
	 *
	 * @param reserva
	 *            a eliminar
	 * @return resultado de la operación
	 * @throws PersistenciaException
	 *             si falló al persistir
	 */
	public ResultadoEliminarReserva eliminarReserva(Reserva reserva) throws PersistenciaException {
		//Seteamos el estado de la reserva
		ArrayList<Estado> estados = gestorDatos.obtenerEstados();
		Estado estadoBaja = null;
		for(Estado estado: estados){
			if(estado.getEstado().equals(EstadoStr.BAJA)){
				estadoBaja = estado;
			}
		}
		reserva.setEstado(estadoBaja);
		//Persistimos el cambio
		persistidorReserva.modificarReserva(reserva);

		return new ResultadoEliminarReserva();
	}

	/**
	 * Obtiene el listado de reservas de alta de un cliente a la capa de acceso a datos
	 *
	 * @param cliente
	 *            cliente de las reservas a obtener
	 * @return el listado de vendedores solicitados
	 *
	 * @throws PersistenciaException
	 *             se lanza esta excepción al ocurrir un error interactuando con la capa de acceso a datos
	 */
	public ArrayList<Reserva> obtenerReservas(Cliente cliente) throws PersistenciaException {
		return persistidorReserva.obtenerReservas(cliente);
	}

	/**
	 * Obtiene el listado de reservas de alta de un inmueble a la capa de acceso a datos
	 *
	 * @param inmueble
	 *            inmueble de las reservas a obtener
	 * @return el listado de vendedores solicitados
	 *
	 * @throws PersistenciaException
	 *             se lanza esta excepción al ocurrir un error interactuando con la capa de acceso a datos
	 */
	public ArrayList<Reserva> obtenerReservas(Inmueble inmueble) throws PersistenciaException {
		return persistidorReserva.obtenerReservas(inmueble);
	}

}
