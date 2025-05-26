package be.labil.anacarde.application.job;

import be.labil.anacarde.application.service.AuctionService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component // Important pour l'injection de dépendances Spring
public class CloseAuctionJob implements Job {

	private static final Logger log = LoggerFactory.getLogger(CloseAuctionJob.class);

	@Autowired
	private AuctionService auctionService; // Service pour interagir avec la logique métier des
											// enchères

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		Long auctionIdLong = context.getJobDetail().getJobDataMap().getLong("auctionId");
		Integer auctionId = auctionIdLong.intValue();
		log.info("Exécution du Job de clôture pour l'enchère ID : {}", auctionId);

		try {
			auctionService.closeAuction(auctionId);
			log.info("Enchère ID : {} clôturée avec succès.", auctionId);
		} catch (Exception e) {
			log.error("Erreur lors de la clôture de l'enchère ID : {}. Message : {}", auctionId,
					e.getMessage());
			// Potentiellement reprogrammer le job ou notifier un administrateur
			throw new JobExecutionException(e);
		}
	}
}