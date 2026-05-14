INSERT INTO calendar_db.festividades (code,descripcion,es_eclipeno,es_equinoccio_otonyo,es_equinoccio_primavera,es_luna_llena,es_luna_nueva,es_metono,es_solsticio_invierno,es_solsticio_verano,hay_eclipse_de_luna,hay_eclipse_de_sol,name,nextsoe,previoussoe) VALUES
	 ('CA','Solsticio de invierno',0,0,0,0,0,0,1,0,0,0,'Cambio de año',2,4),
	 ('MA','Solsticio de verano',0,0,0,0,0,0,0,1,0,0,'Mitad del año',4,2),
	 ('CM','Métono incial nuevo',0,0,0,0,1,1,1,0,1,0,'Cambio de métono',2,4),
	 ('BP','Equinoccio de primavera',0,0,1,0,0,0,0,0,0,0,'Bienvenida de la primavera',3,1),
	 ('PM','Primera luna nueva del año',0,0,0,0,1,0,0,0,0,0,'Inicio del primer mes del año',2,1),
	 ('BO','Equinoccio de otoño',0,1,0,0,0,0,0,0,0,0,'Bienvenida del otoño',1,3),
	 ('CE','Eclípeno inicial nuevo',1,0,0,0,1,1,1,0,0,1,'Cambio de eclípeno',2,4);
