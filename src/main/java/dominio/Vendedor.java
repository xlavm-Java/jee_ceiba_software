package dominio;

import dominio.repositorio.RepositorioProducto;

//librer�as para el manejo de fechas
import java.util.Date;
import java.util.Calendar;

import dominio.excepcion.GarantiaExtendidaException;//importo la clase de excepciones
import dominio.repositorio.RepositorioGarantiaExtendida;

public class Vendedor {

	public static final String EL_PRODUCTO_TIENE_GARANTIA = "El producto ya cuenta con una garantia extendida";
	// creo la excepci�n para cuando el c�digo del producto tenga 3 vocales.
	public static final String EL_CODIGO_DEL_PRODUCTO_TIENE_3_VOCALES = "Este producto no cuenta con garant�a extendida";

	private RepositorioProducto repositorioProducto;
	private RepositorioGarantiaExtendida repositorioGarantia;

	public Vendedor(RepositorioProducto repositorioProducto, RepositorioGarantiaExtendida repositorioGarantia) {
		this.repositorioProducto = repositorioProducto;
		this.repositorioGarantia = repositorioGarantia;

	}

	public void generarGarantia(String codigo, String nombreCliente) {
		// RN2. si el producto tiene m�s de 1 garant�a, se lanza una excepci�n.
		if (tieneGarantia(codigo)) {
			throw new GarantiaExtendidaException(EL_PRODUCTO_TIENE_GARANTIA);
		}

		// RN3. Si el c�digo del producto tiene 3 vocales debe retornar una
		// excepci�n.
		String vocales = "aeiou";
		int contVocales = 0;
		// recorro el c�digo, validando que cada car�cter sea una vocal y de ser
		// as�, la cuento.
		for (int i = 0; i < codigo.length(); i++) {
			for (int j = 0; j < vocales.length(); j++) {
				if (codigo.charAt(i) == vocales.charAt(j)) {
					contVocales++;
				}
			}
		}
		// v�lido que el contador indique que hayan 3 vocales, de ser as� lanzo
		// la excepci�n.
		if (contVocales == 3) {
			throw new GarantiaExtendidaException(EL_CODIGO_DEL_PRODUCTO_TIENE_3_VOCALES);
		}

		// RN4. agregar garant�a
		// obtengo el producto por el c�digo.
		Producto producto = repositorioProducto.obtenerPorCodigo(codigo);
		// Creo la garant�a extendida con la fecha actual, para el producto
		// obtenido anteriormente.
		GarantiaExtendida garantiaExtendida = new GarantiaExtendida(producto);

		// en el objeto que cre� anteriormente seteo el nombre del cliente.
		// para esto primero voy a GarantiaExtendida y genero el setter para el atributo nombreCliente.
		garantiaExtendida.setNombreCliente(nombreCliente);
		// seteo el precio de la garantia en el objeto creado anteriormente, dandole el valor del precio calculado.
		// para esto primero voy a GarantiaExtendida y genero el setter para el atributo precioGarantia.
		garantiaExtendida.setPrecioGarantia(calcularPrecio(producto.getPrecio()));

		//por �ltimo calculo la fecha de finalizaci�n para la garant�a extendida.
		//1. calculo la fecha de finalizaci�n de acuerdo al precio y fecha de solicitud (actual).
		//2. seteo el atributo fechaFinGarantia del objeto y le llevo el valor de la fecha calculada.
		garantiaExtendida.setFechaFinGarantia(calcularFechaFin(garantiaExtendida.getFechaSolicitudGarantia(), producto.getPrecio()));

		//agrego la garant�a extendida.
		repositorioGarantia.agregar(garantiaExtendida);
	}

	public boolean tieneGarantia(String codigo) {
		//consulto el producto por el c�digo.
		Producto producto = repositorioGarantia.obtenerProductoConGarantiaPorCodigo(codigo);
		//si el producto es diferente de null, entonces tiene garant�a, sino no.
		if (producto != null) {
			return true;
		} else {
			return false;
		}
	}
	
	//m�todo que calcula el precio de acuerdo a la RN5.
	public double calcularPrecio(double precioProducto) {
		if(precioProducto > 500000){
			return precioProducto * 0.2;
		}else{
			return precioProducto * 0.1;
		}
	}
	
	
	//m�todo que calcula la fecha final de la garant�a.
	public Date calcularFechaFin(Date fechaSolicitud, double precioProducto) {
		Date fechaFin = null;
		Calendar calFechaFin = Calendar.getInstance();
		calFechaFin.setTime(fechaSolicitud);

		if (precioProducto > 500000) {
			// agrego teniendo en cuenta los 200 d�as incluyendo el d�a de la solicitud.
			calFechaFin.add(Calendar.DAY_OF_YEAR, 199);
			//obtengo la instancia del calendario.
			Calendar calFechaSolicitud = Calendar.getInstance();
			calFechaSolicitud.setTime(fechaSolicitud);
			
			// cuento cu�ntos lunes hay entre las 2 fechas.
			int contadorLunes = 0;
			while (calFechaFin.after(calFechaSolicitud)) {
				if (calFechaSolicitud.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY)
					contadorLunes++;
				calFechaSolicitud.add(Calendar.DAY_OF_YEAR, 1);
			}

			// aumento el n�mero de lunes .
			calFechaFin.add(Calendar.DAY_OF_YEAR, contadorLunes);

			// v�lido si la fecha en la que finaliza la garant�a cae en un domingo, deber� finalizar el siguiente d�a.
			if (calFechaFin.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
				calFechaFin.add(Calendar.DAY_OF_YEAR, 1);
			}
	
			// obtengo la fecha.
			fechaFin = calFechaFin.getTime();
			
		} else {
			// agrego teniendo en cuenta los 100 d�as incluyendo el d�a de la solicitud.
			calFechaFin.add(Calendar.DAY_OF_YEAR, 99);
			fechaFin = calFechaFin.getTime();
		}
		
		//nos retorna la fechaFin que es la fecha de finalizaci�n de la garant�a extendida.
		return fechaFin;
	}

}
