package CCASolutions.Calendario.Enums;

public enum MotivoSuspensionEnum {

	INACTIVIDAD("Más de un año sin acceder"),
	USUARIO("Suspendida por el propio usuario"),
	ADMINISTRADOR("Razones de administrador");

	private final String descripcion;

	MotivoSuspensionEnum(String descripcion) {
		this.descripcion = descripcion;
	}

	public String getDescripcion() {
		return descripcion;
	}
}
