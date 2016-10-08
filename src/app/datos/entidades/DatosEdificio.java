package app.datos.entidades;

/**
 * Created by fer on 07/10/16.
 */
public class DatosEdificio {
    Integer id;
    Boolean propiedadHorizontal;
    Double superficie; // en metros cuadrados
    Integer antiguedad; // en años
    // dormitorios, baños, garaje/cochera, patio, piscina, agua corriente, cloacas,
    // gas natural, agua caliente, teléfono, lavadero, pavimento;
    Integer dormitorios;
    Integer baños;
    Integer garaje;
    Integer patio;
    Integer piscina;
    Boolean aguaCorriente;
    Boolean cloacas;
    Boolean gasNatural;
    Boolean aguaCaliente;
    String telefono;
    Boolean lavadero;
    Boolean pavimento;
}