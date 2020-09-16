package dominio;

import dominio.repositorio.RepositorioProducto;

//librerías para el manejo de fechas
import java.util.Date;
import java.util.Calendar;

import dominio.excepcion.GarantiaExtendidaException;//importo la clase de excepciones
import dominio.repositorio.RepositorioGarantiaExtendida;

public class Vendedor {

	public static final String EL_PRODUCTO_TIENE_GARANTIA = "El producto ya cuenta con una garantia extendida";
	// creo la excepción para cuando el código del producto tenga 3 vocales.
	public static final String EL_CODIGO_DEL_PRODUCTO_TIENE_3_VOCALES = "Este producto no cuenta con garantía extendida";

	private RepositorioProducto repositorioProducto;
	private RepositorioGarantiaExtendida repositorioGarantia;

	public Vendedor(RepositorioProducto repositorioProducto, RepositorioGarantiaExtendida repositorioGarantia) {
		this.repositorioProducto = repositorioProducto;
		this.repositorioGarantia = repositorioGarantia;

	}

	public void generarGarantia(String codigo, String nombreCliente) {
		// RN2. si el producto tiene más de 1 garantía, se lanza una excepción.
		if (tieneGarantia(codigo)) {
			throw new GarantiaExtendidaException(EL_PRODUCTO_TIENE_GARANTIA);
		}

		// RN3. Si el código del producto tiene 3 vocales debe retornar una
		// excepción.
		String vocales = "aeiou";
		int contVocales = 0;
		// recorro el código, validando que cada carácter sea una vocal y de ser
		// así, la cuento.
		for (int i = 0; i < codigo.length(); i++) {
			for (int j = 0; j < vocales.length(); j++) {
				if (codigo.charAt(i) == vocales.charAt(j)) {
					contVocales++;
				}
			}
		}
		// válido que el contador indique que hayan 3 vocales, de ser así lanzo
		// la excepción.
		if (contVocales == 3) {
			throw new GarantiaExtendidaException(EL_CODIGO_DEL_PRODUCTO_TIENE_3_VOCALES);
		}

		// RN4. agregar garantía
		// obtengo el producto por el código.
		Producto producto = repositorioProducto.obtenerPorCodigo(codigo);
		// Creo la garantía extendida con la fecha actual, para el producto
		// obtenido anteriormente.
		GarantiaExtendida garantiaExtendida = new GarantiaExtendida(producto);

		// en el objeto que creé anteriormente seteo el nombre del cliente.
		// para esto primero voy a GarantiaExtendida y genero el setter para el atributo nombreCliente.
		garantiaExtendida.setNombreCliente(nombreCliente);
		// seteo el precio de la garantia en el objeto creado anteriormente, dandole el valor del precio calculado.
		// para esto primero voy a GarantiaExtendida y genero el setter para el atributo precioGarantia.
		garantiaExtendida.setPrecioGarantia(calcularPrecio(producto.getPrecio()));

		//por último calculo la fecha de finalización para la garantía extendida.
		//1. calculo la fecha de finalización de acuerdo al precio y fecha de solicitud (actual).
		//2. seteo el atributo fechaFinGarantia del objeto y le llevo el valor de la fecha calculada.
		garantiaExtendida.setFechaFinGarantia(calcularFechaFin(garantiaExtendida.getFechaSolicitudGarantia(), producto.getPrecio()));

		//agrego la garantía extendida.
		repositorioGarantia.agregar(garantiaExtendida);
	}

	public boolean tieneGarantia(String codigo) {
		//consulto el producto por el código.
		Producto producto = repositorioGarantia.obtenerProductoConGarantiaPorCodigo(codigo);
		//si el producto es diferente de null, entonces tiene garantía, sino no.
		if (producto != null) {
			return true;
		} else {
			return false;
		}
	}
	
	//método que calcula el precio de acuerdo a la RN5.
	public double calcularPrecio(double precioProducto) {
		if(precioProducto > 500000){
			return precioProducto * 0.2;
		}else{
			return precioProducto * 0.1;
		}
	}
	
	
	//método que calcula la fecha final de la garantía.
	public Date calcularFechaFin(Date fechaSolicitud, double precioProducto) {
		Date fechaFin = null;
		Calendar calFechaFin = Calendar.getInstance();
		calFechaFin.setTime(fechaSolicitud);

		if (precioProducto > 500000) {
			// agrego teniendo en cuenta los 200 días incluyendo el día de la solicitud.
			calFechaFin.add(Calendar.DAY_OF_YEAR, 199);
			//obtengo la instancia del calendario.
			Calendar calFechaSolicitud = Calendar.getInstance();
			calFechaSolicitud.setTime(fechaSolicitud);
			
			// cuento cuántos lunes hay entre las 2 fechas.
			int contadorLunes = 0;
			while (calFechaFin.after(calFechaSolicitud)) {
				if (calFechaSolicitud.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY)
					contadorLunes++;
				calFechaSolicitud.add(Calendar.DAY_OF_YEAR, 1);
			}

			// aumento el número de lunes .
			calFechaFin.add(Calendar.DAY_OF_YEAR, contadorLunes);

			// válido si la fecha en la que finaliza la garantía cae en un domingo, deberá finalizar el siguiente día.
			if (calFechaFin.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
				calFechaFin.add(Calendar.DAY_OF_YEAR, 1);
			}
	
			// obtengo la fecha.
			fechaFin = calFechaFin.getTime();
			
		} else {
			// agrego teniendo en cuenta los 100 días incluyendo el día de la solicitud.
			calFechaFin.add(Calendar.DAY_OF_YEAR, 99);
			fechaFin = calFechaFin.getTime();
		}
		
		//nos retorna la fechaFin que es la fecha de finalización de la garantía extendida.
		return fechaFin;
	}

}
