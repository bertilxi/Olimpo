package app.logica.gestores;

import java.util.ArrayList;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import app.datos.clases.EstadoStr;
import app.datos.clases.FiltroPropietario;
import app.datos.entidades.Propietario;
import app.datos.servicios.PropietarioService;
import app.excepciones.EntidadExistenteConEstadoBajaException;
import app.excepciones.GestionException;
import app.excepciones.PersistenciaException;
import app.logica.ValidadorFormato;
import app.logica.resultados.ResultadoCrearPropietario;
import app.logica.resultados.ResultadoCrearPropietario.ErrorCrearPropietario;
import app.logica.resultados.ResultadoEliminarPropietario;
import app.logica.resultados.ResultadoEliminarPropietario.ErrorEliminarPropietario;
import app.logica.resultados.ResultadoModificarPropietario;
import app.logica.resultados.ResultadoModificarPropietario.ErrorModificarPropietario;

@Service
public class GestorPropietario {

	@Resource
	protected PropietarioService persistidorPropietario;

	public ResultadoCrearPropietario crearPropietario(Propietario propietario) throws PersistenciaException, GestionException {
		ArrayList<ErrorCrearPropietario> errores = new ArrayList<>();

		validarDatosCrearPropietario(propietario, errores);

		Propietario propietarioAuxiliar;
		try{
			propietarioAuxiliar = persistidorPropietario.obtenerPropietario(new FiltroPropietario(propietario.getTipoDocumento().getTipo(), propietario.getNumeroDocumento()));
		} catch(PersistenciaException e){
			throw e;
		}

		if(null != propietarioAuxiliar){
			if(propietarioAuxiliar.getEstado().getEstado().equals(EstadoStr.ALTA)){
				errores.add(ErrorCrearPropietario.Ya_Existe_Propietario);
			}
			else{
				throw new EntidadExistenteConEstadoBajaException();
			}
		}

		if(errores.isEmpty()){
			try{
				persistidorPropietario.guardarPropietario(propietario);
			} catch(PersistenciaException e){
				throw e;
			}
		}

		return new ResultadoCrearPropietario(errores.toArray(new ErrorCrearPropietario[0]));
	}

	private void validarDatosCrearPropietario(Propietario propietario, ArrayList<ErrorCrearPropietario> errores) {
		if(!ValidadorFormato.validarNombre(propietario.getNombre())){
			errores.add(ErrorCrearPropietario.Formato_Nombre_Incorrecto);
		}

		if(!ValidadorFormato.validarApellido(propietario.getApellido())){
			errores.add(ErrorCrearPropietario.Formato_Apellido_Incorrecto);
		}

		if(!ValidadorFormato.validarDocumento(propietario.getTipoDocumento(), propietario.getNumeroDocumento())){
			errores.add(ErrorCrearPropietario.Formato_Documento_Incorrecto);
		}

		if(!ValidadorFormato.validarTelefono(propietario.getTelefono())){
			errores.add(ErrorCrearPropietario.Formato_Telefono_Incorrecto);
		}

		if(propietario.getEmail() != null && !ValidadorFormato.validarEmail(propietario.getEmail())){
			errores.add(ErrorCrearPropietario.Formato_Email_Incorrecto);
		}

		if(!ValidadorFormato.validarDireccion(propietario.getDireccion())){
			errores.add(ErrorCrearPropietario.Formato_Direccion_Incorrecto);
		}
	}

	public ResultadoModificarPropietario modificarPropietario(Propietario propietario) throws PersistenciaException {
		ArrayList<ErrorModificarPropietario> errores = new ArrayList<>();

		validarDatosModificarPropietario(propietario, errores);

		Propietario propietarioAuxiliar;
		try{
			propietarioAuxiliar = persistidorPropietario.obtenerPropietario(new FiltroPropietario(propietario.getTipoDocumento().getTipo(), propietario.getNumeroDocumento()));
		} catch(PersistenciaException e){
			throw e;
		}
		if(null != propietarioAuxiliar && !propietario.equals(propietarioAuxiliar)){
			errores.add(ErrorModificarPropietario.Ya_Existe_Propietario);
		}

		if(errores.isEmpty()){
			try{
				persistidorPropietario.modificarPropietario(propietario);
			} catch(PersistenciaException e){
				throw e;
			}
		}

		return new ResultadoModificarPropietario(errores.toArray(new ErrorModificarPropietario[0]));
	}

	private void validarDatosModificarPropietario(Propietario propietario, ArrayList<ErrorModificarPropietario> errores) {
		if(!ValidadorFormato.validarNombre(propietario.getNombre())){
			errores.add(ErrorModificarPropietario.Formato_Nombre_Incorrecto);
		}

		if(!ValidadorFormato.validarApellido(propietario.getApellido())){
			errores.add(ErrorModificarPropietario.Formato_Apellido_Incorrecto);
		}

		if(!ValidadorFormato.validarDocumento(propietario.getTipoDocumento(), propietario.getNumeroDocumento())){
			errores.add(ErrorModificarPropietario.Formato_Documento_Incorrecto);
		}

		if(!ValidadorFormato.validarTelefono(propietario.getTelefono())){
			errores.add(ErrorModificarPropietario.Formato_Telefono_Incorrecto);
		}

		if(!ValidadorFormato.validarEmail(propietario.getEmail())){
			errores.add(ErrorModificarPropietario.Formato_Email_Incorrecto);
		}

		if(!ValidadorFormato.validarDireccion(propietario.getDireccion())){
			errores.add(ErrorModificarPropietario.Formato_Direccion_Incorrecto);
		}
	}

	public ResultadoEliminarPropietario eliminarPropietario(Propietario propietario) throws PersistenciaException {
		ArrayList<ErrorEliminarPropietario> errores = new ArrayList<>();

		if(!ValidadorFormato.validarDocumento(propietario.getTipoDocumento(), propietario.getNumeroDocumento())){
			errores.add(ErrorEliminarPropietario.Formato_Documento_Incorrecto);
		}

		Propietario propietarioAuxiliar;
		try{
			propietarioAuxiliar = persistidorPropietario.obtenerPropietario(new FiltroPropietario(propietario.getTipoDocumento().getTipo(), propietario.getNumeroDocumento()));
		} catch(PersistenciaException e){
			throw e;
		}

		if(null == propietarioAuxiliar){
			errores.add(ErrorEliminarPropietario.No_Existe_Propietario);
		}

		if(errores.isEmpty()){
			try{
				persistidorPropietario.eliminarPropietario(propietario);
			} catch(PersistenciaException e){
				throw e;
			}
		}

		return new ResultadoEliminarPropietario(errores.toArray(new ErrorEliminarPropietario[0]));
	}

}
