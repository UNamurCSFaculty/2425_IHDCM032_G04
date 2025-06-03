package be.labil.anacarde.application.job;

import be.labil.anacarde.application.service.AuctionService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Job Quartz pour la clôture des enchères. Ce job est déclenché par le scheduler pour fermer une
 * enchère spécifique.
 */
@Component
public class CloseAuctionJob implements Job {

	private static final Logger log = LoggerFactory.getLogger(CloseAuctionJob.class);

	@Autowired
	private AuctionService auctionService;

	/**
	 * Méthode exécutée par Quartz pour fermer une enchère. Elle récupère l'ID de l'enchère depuis
	 * le contexte du job et appelle le service d'enchères pour la clôturer.
	 *
	 * @param context
	 *            le contexte d'exécution du job
	 * @throws JobExecutionException
	 *             si une erreur se produit lors de la clôture de l'enchère
	 */
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		Long auctionIdLong = context.getJobDetail().getJobDataMap().getLong("auctionId");
		Integer auctionId = auctionIdLong.intValue();
		log.debug("Exécution du Job de clôture pour l'enchère ID : {}", auctionId);

		try {
			auctionService.closeAuction(auctionId);
			log.debug("Enchère ID : {} clôturée avec succès.", auctionId);
		} catch (Exception e) {
			log.error("Erreur lors de la clôture de l'enchère ID : {}. Message : {}", auctionId,
					e.getMessage());
			// Potentiellement reprogrammer le job ou notifier un administrateur
			throw new JobExecutionException(e);
		}
	}
}