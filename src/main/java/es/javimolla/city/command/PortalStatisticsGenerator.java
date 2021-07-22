package es.javimolla.city.command;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import es.javimolla.city.domain.Equipment;
import es.javimolla.city.domain.EquipmentLayer;
import es.javimolla.city.entity.PortalSimple;
import es.javimolla.city.entity.PortalStatistic;
import es.javimolla.city.entity.PortalStatisticPK;
import es.javimolla.city.repository.EquipmentRepository;
import es.javimolla.city.repository.PortalSimpleRepository;
import es.javimolla.city.repository.PortalStatisticRepository;

@Component
public class PortalStatisticsGenerator implements CommandLineRunner {
	private Logger logger = LoggerFactory.getLogger(PortalStatisticsGenerator.class.getName());

	@Autowired
	private PortalSimpleRepository portalSimpleRepository;

	@Autowired
	private EquipmentRepository equipmentRepository;

	@Autowired
	private PortalStatisticRepository portalStatisticRepository;

	@Override
	public void run(String... args) throws Exception {
		if (!isThisCommand(args))
			return;

		List<PortalSimple> portals;
		do {
			portals = getPortals();
			for (PortalSimple portal : portals) {
				saveStatistics(portal, getLayersEquipments(getLayers(), portal));
			}
		} while (portals != null && portals.size() != 0);
	}

	private boolean isThisCommand(String[] args) {
		logger.debug("Checking the command to generate the statistics");
		return args.length > 0 && args[0].equalsIgnoreCase("portals_statistics");
	}

	private List<PortalSimple> getPortals() {
		return portalSimpleRepository.findByStatsIsNull();
	}

	private List<EquipmentLayer> getLayers() throws JsonParseException, JsonMappingException, IOException {
		return new ObjectMapper().readValue(getClass().getClassLoader().getResource("layers.json"),
				new TypeReference<List<EquipmentLayer>>() {
				});
	}

	private Map<EquipmentLayer, List<Equipment>> getLayersEquipments(List<EquipmentLayer> layers, PortalSimple portal)
			throws Exception {
		Map<EquipmentLayer, List<Equipment>> results = new LinkedHashMap<>();
		for (EquipmentLayer layer : layers) {
			results.put(layer, equipmentRepository.findAllByGeom(layer, portal.getGeom()));
		}
		return results;
	}

	private void saveStatistics(PortalSimple portal, Map<EquipmentLayer, List<Equipment>> layersEquipments) {
		portalStatisticRepository.deleteByIdGid(portal.getGid());
		layersEquipments.forEach((layer, equipments) -> {
			PortalStatistic linearStatistic = new PortalStatistic();
			linearStatistic.setId(new PortalStatisticPK(portal.getGid(), layer.getName()));
			linearStatistic.setEquipments(equipments.size());
			portalStatisticRepository.save(linearStatistic);
		});
	}
}
