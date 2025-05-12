CREATE OR REPLACE VIEW vw_business_analysis AS
WITH win_bid AS (
    SELECT DISTINCT ON (auction_id)
           auction_id,
           amount,
           trader_id
    FROM bid b
    JOIN bid_status bs ON bs.id = b.status_id
                      AND bs.name = 'WIN' -- Assurez-vous que ce statut est bien celui du gagnant final
    ORDER BY auction_id, b.creation_date -- Ou une autre logique si nécessaire (ex: DESC pour la plus récente)
),
prod AS (
    /* récolte */
    SELECT hp.id,
           hp.delivery_date,
           hp.weight_kg,
           hp.store_id,
           qc.quality_id,
           NULL::int     AS transformed_from_product_id, -- Reste NULL si pas de lien direct
           'HARVEST'     AS type_label,
           hp.field_id   -- Ceci est l'ID numérique du champ
    FROM   harvest_product hp
    LEFT   JOIN quality_control qc ON qc.product_id = hp.id

    UNION ALL

    /* transformé */
    SELECT tp.id,
           tp.delivery_date,
           tp.weight_kg,
           tp.store_id,  -- <<< CORRIGÉ ICI
           qc.quality_id,
           NULL::int     AS transformed_from_product_id, -- Reste NULL si pas de lien direct
           'TRANSFORMED' AS type_label,
           NULL::int     AS field_id
    FROM   transformed_product tp
    LEFT   JOIN quality_control qc ON qc.product_id = tp.id
)
SELECT
    a.id                            AS auction_id,
    a.price                         AS auction_price, -- Renommé pour clarté vs bid price
    a.product_quantity,
    a.expiration_date,
    a.creation_date                 AS auction_creation_date, -- Renommé pour clarté
    s.name                          AS strategy_name, -- Renommé pour clarté
    (s.name = 'IMMEDIATE_BUY')      AS immediate_buy,
    ts.name                         AS auction_status,
    (co.id IS NOT NULL)             AS contract_applied,

    wb.amount                       AS win_bid_amount,
    MAX(b.amount)                   AS max_bid_amount,
    COUNT(b.id)                     AS nb_bids,

    seller.id                       AS seller_id,
    CASE WHEN e.id IS NOT NULL THEN 'EXPORTER'
         WHEN p.id IS NOT NULL THEN 'PRODUCER'
         ELSE 'OTHER_TRADER' END    AS seller_type, -- Ajouté OTHER_TRADER pour plus de clarté
    us.registration_date            AS seller_registration_date,
    sr.name                         AS seller_region, -- Sous réserve que us.address soit city_id
    ls.code                         AS seller_langue,
    coop.id                         AS seller_cooperative_id,
    coop.creation_date              AS seller_cooperative_creation_date,

    wb.trader_id                    AS buyer_id,
    CASE WHEN e2.id IS NOT NULL THEN 'EXPORTER'
         WHEN p2.id IS NOT NULL THEN 'PRODUCER'
         WHEN wb.trader_id IS NOT NULL THEN 'OTHER_TRADER' -- Si c'est un trader mais ni E ni P
         ELSE NULL END              AS buyer_type,
    ub.registration_date            AS buyer_registration_date,
    br.name                         AS buyer_region, -- Sous réserve que ub.address soit city_id
    lb.code                         AS buyer_langue,

    prod.delivery_date              AS product_delivery_date,
    prod.type_label                 AS product_name, -- Ou product_type si vous ajoutez un nom spécifique
    prod.weight_kg                  AS product_weight_kg,
    q.name                          AS product_quality_name,

    ST_AsText(sto.location)         AS store_location,

    f.identifier                    AS field_identifier, -- Nommé pour clarté (était field_id)
    rf.name                         AS field_region,
    prod.transformed_from_product_id

FROM auction                 a
         LEFT JOIN auction_strategy   s    ON s.id = a.strategy_id
         JOIN trade_status            ts   ON ts.id = a.status_id -- Une enchère a toujours un statut
         LEFT JOIN win_bid            wb   ON wb.auction_id = a.id
         LEFT JOIN bid                b    ON b.auction_id = a.id -- Pour MAX et COUNT sur toutes les offres

-- ---- vendeur (trader qui a créé l'enchère) ----
         JOIN users                   us   ON us.id     = a.trader_id -- Un trader est un user
         JOIN trader                  seller ON seller.id = us.id -- Jointure vers la table trader spécifique
         LEFT JOIN exporter           e    ON e.id      = seller.id
         LEFT JOIN producer           p    ON p.id      = seller.id
         LEFT JOIN city               sc   ON sc.id     = us.address::INT -- Attention à cette conversion
LEFT JOIN region             sr   ON sr.id     = sc.region_id
    LEFT JOIN language           ls   ON ls.id     = us.language_id
    LEFT JOIN cooperative        coop ON coop.id   = p.cooperative_id -- Seulement si vendeur est producteur

-- ---- acheteur (trader de la winning bid) ----
    LEFT JOIN users              ub   ON ub.id     = wb.trader_id -- L'acheteur est un user (s'il existe)
    LEFT JOIN trader             buyer ON buyer.id = ub.id     -- Jointure vers la table trader spécifique
    LEFT JOIN exporter           e2   ON e2.id     = buyer.id
    LEFT JOIN producer           p2   ON p2.id     = buyer.id
    LEFT JOIN city               bc   ON bc.id     = ub.address::INT -- Attention à cette conversion
    LEFT JOIN region             br   ON br.id     = bc.region_id
    LEFT JOIN language           lb   ON lb.id     = ub.language_id

-- ---- produit / qualité / magasin / champ ----
    LEFT JOIN prod                      ON prod.id = a.product_id -- Le produit de l'enchère
    LEFT JOIN quality            q      ON q.id    = prod.quality_id
    LEFT JOIN store              sto    ON sto.id  = prod.store_id -- Utilise prod.store_id qui est maintenant corrigé
    LEFT JOIN field              f      ON f.id    = prod.field_id -- prod.field_id est l'ID numérique du champ
    LEFT JOIN region             rf     ON ST_Contains(rf.boundary, f.location) -- Si f.location et rf.boundary existent

-- ---- offre de contrat éventuelle ----
    LEFT JOIN contract_offer     co   ON co.seller_id = seller.id
    AND co.buyer_id  = wb.trader_id -- Contrat entre vendeur et acheteur gagnant
    AND (co.quality_id = prod.quality_id OR co.quality_id IS NULL) -- Et sur la qualité (ou générique)

GROUP BY
    a.id, a.price, a.product_quantity, a.expiration_date, a.creation_date,
    s.name,
    ts.name,
    co.id, -- Pour contract_applied
    wb.amount, wb.trader_id, -- Pour win_bid_amount et buyer_id
    seller.id, us.registration_date, sr.name, ls.code, e.id, p.id, -- Vendeur
    coop.id, coop.creation_date, -- Coopérative vendeur
    ub.registration_date, br.name, lb.code, e2.id, p2.id, -- Acheteur
    prod.delivery_date, prod.type_label, prod.weight_kg, q.name, prod.quality_id, -- Produit (prod.quality_id pour contract_offer)
    sto.location, -- pour ST_AsText
    f.identifier, rf.name, -- Champ
    prod.transformed_from_product_id, prod.store_id, prod.field_id; -- Autres IDs de prod pour jointures/sélections