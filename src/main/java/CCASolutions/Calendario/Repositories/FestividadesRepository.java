package CCASolutions.Calendario.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import CCASolutions.Calendario.Entities.FestividadesEntity;

/**
 * EN: The sixteen VAU festivities, each identified by a short code such as "CA" for the
 * change of year or "MSI" for the winter midsison. Read whole and cached.
 * ES: Las dieciséis festividades VAU, identificadas por un código corto como "CA" para el
 * cambio de año o "MSI" para el midsison invernal. Se lee entera y se cachea.
 */
public interface FestividadesRepository extends JpaRepository <FestividadesEntity, Long>{

}
