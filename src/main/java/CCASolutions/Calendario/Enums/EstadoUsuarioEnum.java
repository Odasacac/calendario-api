package CCASolutions.Calendario.Enums;

public enum EstadoUsuarioEnum {
	ACTIVO,
	/** Más de un mes sin acceder. Vuelve a ACTIVO al hacer login. */
	INACTIVO,
	SUSPENDIDO,
	/** Borrado lógico: el registro se conserva en la base de datos. */
	ELIMINADO
}
