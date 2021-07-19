package es.javimolla.city.command;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import es.javimolla.city.domain.Equipment;
import es.javimolla.city.domain.EquipmentInterval;
import es.javimolla.city.domain.EquipmentLayer;
import es.javimolla.city.entity.Isochrone;
import es.javimolla.city.entity.IsochroneStatistic;
import es.javimolla.city.entity.IsochroneStatisticPK;
import es.javimolla.city.entity.Portal;
import es.javimolla.city.repository.EquipmentRepository;
import es.javimolla.city.repository.IsochroneRepository;
import es.javimolla.city.repository.IsochroneStatisticRepository;
import es.javimolla.city.repository.PortalRepository;

@Component
public class EquipmentReporter implements CommandLineRunner {
	private Logger logger = LoggerFactory.getLogger(EquipmentReporter.class.getName());

	@Autowired
	private PortalRepository portalRepository;

	@Autowired
	private IsochroneRepository isochroneRepository;

	@Autowired
	private EquipmentRepository equipmentRepository;

	@Autowired
	private IsochroneStatisticRepository isochroneStatisticRepository;

	@Override
	public void run(String... args) throws Exception {
		if (!isThisCommand(args))
			return;

		Portal portal = getPortal(args);
		if (portal != null) {
			Map<EquipmentLayer, List<Equipment>> layersEquipments = getLayersEquipments(getLayers(),
					portal.getIsochrone());
			saveStatistics(portal.getIsochrone(), layersEquipments);
			printReport(portal, layersEquipments);
		} else {
			List<Isochrone> isochrones;
			do {
				isochrones = getIsochrones();
				for (Isochrone isochrone : isochrones) {
					saveStatistics(isochrone, getLayersEquipments(getLayers(), isochrone));
				}
			} while (isochrones != null && isochrones.size() != 0);
		}
	}

	private boolean isThisCommand(String[] args) {
		logger.debug("Checking the command to report the equipments");
		return args.length > 0 && args[0].equalsIgnoreCase("equipments");
	}

	private Portal getPortal(String[] args) throws Exception {
		Integer portalId = getPortalId(args);
		if (portalId == null)
			return null;

		logger.debug("Getting the portal information");
		Optional<Portal> portal = portalRepository.findById(portalId);
		if (!portal.isPresent()) {
			System.out.println("No portal found with the identifier " + portalId);
			throw new Exception("No portal found with the identifier " + portalId);
		}
		return portal.get();
	}

	private List<Isochrone> getIsochrones() {
		return isochroneRepository.findByStatsIsNull();
	}

	private Integer getPortalId(String[] args) {
		logger.debug("Checking the portal parameter");
		if (args.length < 2 || !StringUtils.hasLength(args[1]))
			return null;
		try {
			return Integer.parseInt(args[1]);
		} catch (Exception e) {
			System.out.println("Portal parameter must be a number");
			throw e;
		}
	}

	private List<EquipmentLayer> getLayers() throws JsonParseException, JsonMappingException, IOException {
		return new ObjectMapper().readValue(getClass().getClassLoader().getResource("layers.json"),
				new TypeReference<List<EquipmentLayer>>() {
				});
	}

	private Map<EquipmentLayer, List<Equipment>> getLayersEquipments(List<EquipmentLayer> layers, Isochrone isochrone)
			throws Exception {
		Map<EquipmentLayer, List<Equipment>> results = new LinkedHashMap<>();
		for (EquipmentLayer layer : layers) {
			results.put(layer, equipmentRepository.findAllByIsochrone(layer, isochrone));
		}
		return results;
	}

	private void saveStatistics(Isochrone isochrone, Map<EquipmentLayer, List<Equipment>> layersEquipments) {
		isochroneStatisticRepository.deleteByIdGid(isochrone.getGid());
		layersEquipments.forEach((layer, equipments) -> {
			IsochroneStatistic isochroneStatistic = new IsochroneStatistic();
			IsochroneStatisticPK id = new IsochroneStatisticPK();
			id.setGid(isochrone.getGid());
			id.setLayer(layer.getName());
			isochroneStatistic.setId(id);
			isochroneStatistic.setEquipments(equipments.size());
			isochroneStatisticRepository.save(isochroneStatistic);
		});
	}

	private void printReport(Portal portal, Map<EquipmentLayer, List<Equipment>> layersEquipments) {
		System.out.println(String.format("Informe de equipamientos cercanos para \"%s, %s\":",
				portal.getStreet().getName(), portal.getNumber()));
		layersEquipments.forEach((layer, equipments) -> {
			System.out.println(String.format(
					"\t\"%s\". Se dispone de un total de %d equipamientos, lo que corresponde con un nivel \"%s\" dentro de los definidos (\"%s\"). Los datos de los equipamientos son los siguientes:",
					layer.getDescription(), equipments.size(),
					getIntervalNameForEquipments(layer.getIntervals(), equipments.size()),
					getIntervalsNames(layer.getIntervals())));
			equipments.forEach(equipment -> {
				equipment.getFieldsValues().forEach((field, value) -> {
					System.out.println(String.format("\t\t\"%s\": \"%s\"", field.getAlias(), value));
				});
				System.out.println("\t\t---------------------------------------");
			});
		});
	}

	private String getIntervalNameForEquipments(List<EquipmentInterval> intervals, int equipments) {
		for (int interval = 0; interval < intervals.size(); interval++) {
			EquipmentInterval equipmentInterval = intervals.get(interval);
			if (equipmentInterval.getMinimum() == null && equipmentInterval.getMaximum() == null)
				continue;
			if (equipments < equipmentInterval.getMinimum()) {
				return intervals.get(interval - 1).getName();
			}
			if (equipments >= equipmentInterval.getMinimum() && equipments <= equipmentInterval.getMaximum())
				return equipmentInterval.getName();
		}
		return intervals.get(intervals.size() - 1).getName();
	}

	private String getIntervalsNames(List<EquipmentInterval> intervals) {
		return String.join("\", \"", intervals.stream().map(EquipmentInterval::getName).collect(Collectors.toList()));
	}
}
