
package be.labil.anacarde.application.service;

import be.labil.anacarde.domain.dto.ProductDto;
import java.util.List;

/**
 * Ce service offre des méthodes permettant de créer, récupérer, mettre à jour et supprimer des informations de produit.
 */
public interface ProductService {

	/**
	 * Crée un nouveau produit dans le système en utilisant le ProductDto fourni.
	 *
	 * @param ProductDto
	 *            Le ProductDto contenant les informations du nouveau produit.
	 * @return Un ProductDto représentant le produit créé.
	 */
	ProductDto createProduct(ProductDto ProductDto);

	/**
	 * Retourne le produit correspondant à l'ID fourni.
	 *
	 * @param id
	 *            L'identifiant unique du produit.
	 * @return Un ProductDto représentant le produit avec l'ID spécifié.
	 */
	ProductDto getProductById(Integer id);

	/**
	 * Récupère tous les produits du système.
	 *
	 * @param traderId
	 *            L'identifiant du trader propriétaire des produits.
	 * @return Une List de ProductDto représentant tous les produits.
	 */
	List<ProductDto> listProducts(Integer traderId);

	/**
	 * Met à jour le produit identifié par l'ID donné avec les informations fournies dans le ProductDto.
	 *
	 * @param id
	 *            L'identifiant unique du produit à mettre à jour.
	 * @param ProductDto
	 *            Le ProductDto contenant les informations mises à jour.
	 * @return Un ProductDto représentant le produit mis à jour.
	 */
	ProductDto updateProduct(Integer id, ProductDto ProductDto);

	/**
	 * Supprime le produit identifié par l'ID donné du système.
	 *
	 * @param id
	 *            L'identifiant unique du produit à supprimer.
	 */
	void deleteProduct(Integer id);
}
