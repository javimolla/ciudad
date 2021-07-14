package es.javimolla.city.command;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import es.javimolla.city.entity.Isochrone;
import es.javimolla.city.entity.Portal;
import es.javimolla.city.entity.PortalIsochrone;
import es.javimolla.city.exception.IsochroneExistsException;
import es.javimolla.city.repository.IsochroneRepository;
import es.javimolla.city.repository.PortalIsochroneRepository;
import es.javimolla.city.repository.PortalRepository;

@Component
public class IsochroneCalculator implements CommandLineRunner {
	private Logger logger = LoggerFactory.getLogger(IsochroneCalculator.class.getName());

	@Autowired
	private PortalRepository portalRepository;

	@Autowired
	private IsochroneRepository isochroneRepository;
	
	@Autowired
	private PortalIsochroneRepository portalIsochroneRepository;

	@Override
	public void run(String... args) throws Exception {
		if (!isThisCommand(args))
			return;

		Integer portalId = getPortalId(args);
		if (portalId != null) {
			Portal portal = getPortal(portalId);
			if (portal == null)
				return;
			calculate(portal);

		} else {
			getPortales().forEach(portal -> calculate(portal));
		}
	}

	private boolean isThisCommand(String[] args) {
		logger.debug("Checking the command to calculate the isochrones");
		return args.length > 0 && args[0].equalsIgnoreCase("isochrones");
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

	private Portal getPortal(Integer portalId) {
		logger.debug("Getting the portal information");
		Optional<Portal> portal = portalRepository.findById(portalId);
		if (!portal.isPresent()) {
			System.out.println("No portal found with the identifier " + portalId);
			return null;
		}
		return portal.get();
	}

	private List<Portal> getPortales() {
		logger.debug("Getting all portals");
		return portalRepository.findByIsochroneIsNull();
	}

	@Transactional
	private void calculate(Portal portal) {
		Isochrone isochrone;
		try {
			isochrone = isochroneRepository.findByPortal(portal);
		} catch (IsochroneExistsException e) {
			// If it already exists, do nothing
			return;
		}

		isochroneRepository.save(isochrone);
		portalIsochroneRepository.save(new PortalIsochrone(portal, isochrone));
	}
}
