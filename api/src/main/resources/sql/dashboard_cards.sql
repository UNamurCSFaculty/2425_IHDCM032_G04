CREATE OR REPLACE VIEW product AS
SELECT id, weight_kg, quality_control_id FROM harvest_product
UNION ALL
SELECT id, weight_kg, quality_control_id FROM transformed_product;

/* ===============================================================
   v_dashboard_cards  :  1 ligne contenant tous les KPI “cards”
   VERSION 3 – ajoute les 10 colonnes “prix moyens par qualité”
   ===============================================================*/
CREATE OR REPLACE VIEW v_dashboard_cards AS
/* ------------ 1) UTILISATEURS (table users) ------------ */
WITH user_counts AS (
    SELECT
        COUNT(*)                                  AS total_users_now,
        COUNT(*) FILTER (WHERE enabled = false)   AS pending_now,

        COUNT(*) FILTER (
            WHERE registration_date < NOW() - INTERVAL '30 days'
        )                                         AS total_users_30,
        COUNT(*) FILTER (
            WHERE registration_date < NOW() - INTERVAL '30 days'
              AND validation_date  > NOW() - INTERVAL '30 days'
        )                                         AS pending_30
    FROM users
),

/* ------------ 2) ENCHÈRES (table auction) --------------- */
auction_totals AS (
    SELECT
        COUNT(*) FILTER (WHERE active = true)     AS total_auctions_now,
        COUNT(*) FILTER (
            WHERE expiration_date > NOW() - INTERVAL '30 days'
              AND creation_date   < NOW() - INTERVAL '30 days'
        )                                         AS total_auctions_30
    FROM auction
),

/* --- 3) LOT WEIGHT & SALES AMOUNT (cumulatif) ----------- */
lot_weights AS (
    SELECT
        SUM(product_weight_kg)                    AS lot_weight_now,
        SUM(bid_winning_amount)                   AS sales_amount_now,
        SUM(product_weight_kg) FILTER (
            WHERE auction_start_date < NOW() - INTERVAL '30 days'
        )                                         AS lot_weight_30,
        SUM(bid_winning_amount) FILTER (
            WHERE auction_start_date < NOW() - INTERVAL '30 days'
        )                                         AS sales_amount_30
    FROM v_auction_bid_analysis
),

/* ---------- 4) Période P1 = [now-30d ; now[ -------------- */
period_1 AS (
    SELECT
        COUNT(*)                AS auctions_concluded_p1,
        SUM(product_weight_kg)  AS sold_weight_p1,
        SUM(bid_winning_amount) AS sales_amount_p1
    FROM v_auction_bid_analysis
    WHERE auction_end_date >= NOW() - INTERVAL '30 days'
      AND auction_end_date <  NOW()
      AND bid_winning_amount IS NOT NULL
),

/* ---------- 5) Période P2 = [now-60d ; now-30d[ ---------- */
period_2 AS (
    SELECT
        COUNT(*)                AS auctions_concluded_p2,
        SUM(product_weight_kg)  AS sold_weight_p2,
        SUM(bid_winning_amount) AS sales_amount_p2
    FROM v_auction_bid_analysis
    WHERE auction_end_date >= NOW() - INTERVAL '60 days'
      AND auction_end_date <  NOW() - INTERVAL '30 days'
      AND bid_winning_amount IS NOT NULL
),

/* ---------- 6) Pré-classification “qualité” -------------- */
price_base AS (
    SELECT
        v.auction_end_date,
		v.product_weight_kg,
        v.bid_winning_amount,
        CASE
            WHEN v.product_quality = 'Grade I'     THEN 'GRADE1'
            WHEN v.product_quality = 'Grade II'    THEN 'GRADE2'
            WHEN v.product_quality = 'Grade III'   THEN 'GRADE3'
            WHEN v.product_quality = 'Hors normes' THEN 'HORS'
            ELSE 'TRANSFORMED'
        END                   AS grade_key
    FROM v_auction_bid_analysis v
    WHERE v.bid_winning_amount IS NOT NULL
      AND v.product_weight_kg  > 0
),

/* ---------- 7) Prix moyens P1 (30 derniers jours) -------- */
period_1_prices AS (
    SELECT
        SUM(bid_winning_amount) FILTER (WHERE grade_key = 'GRADE1')
          / NULLIF(SUM(product_weight_kg) FILTER (WHERE grade_key = 'GRADE1'), 0)
            AS grade1_price_p1,

        SUM(bid_winning_amount) FILTER (WHERE grade_key = 'GRADE2')
          / NULLIF(SUM(product_weight_kg) FILTER (WHERE grade_key = 'GRADE2'), 0)
            AS grade2_price_p1,

        SUM(bid_winning_amount) FILTER (WHERE grade_key = 'GRADE3')
          / NULLIF(SUM(product_weight_kg) FILTER (WHERE grade_key = 'GRADE3'), 0)
            AS grade3_price_p1,

        SUM(bid_winning_amount) FILTER (WHERE grade_key = 'HORS')
          / NULLIF(SUM(product_weight_kg) FILTER (WHERE grade_key = 'HORS'), 0)
            AS hors_price_p1,

        SUM(bid_winning_amount) FILTER (WHERE grade_key = 'TRANSFORMED')
          / NULLIF(SUM(product_weight_kg) FILTER (WHERE grade_key = 'TRANSFORMED'), 0)
            AS transformed_price_p1
    FROM price_base
    WHERE auction_end_date >= NOW() - INTERVAL '30 days'
      AND auction_end_date <  NOW()
),

/* ---------- 8) Prix moyens P2 (30-60 jours) -------------- */
period_2_prices AS (
    SELECT
        SUM(bid_winning_amount) FILTER (WHERE grade_key = 'GRADE1')
          / NULLIF(SUM(product_weight_kg) FILTER (WHERE grade_key = 'GRADE1'), 0)
            AS grade1_price_p2,

        SUM(bid_winning_amount) FILTER (WHERE grade_key = 'GRADE2')
          / NULLIF(SUM(product_weight_kg) FILTER (WHERE grade_key = 'GRADE2'), 0)
            AS grade2_price_p2,

        SUM(bid_winning_amount) FILTER (WHERE grade_key = 'GRADE3')
          / NULLIF(SUM(product_weight_kg) FILTER (WHERE grade_key = 'GRADE3'), 0)
            AS grade3_price_p2,

        SUM(bid_winning_amount) FILTER (WHERE grade_key = 'HORS')
          / NULLIF(SUM(product_weight_kg) FILTER (WHERE grade_key = 'HORS'), 0)
            AS hors_price_p2,

        SUM(bid_winning_amount) FILTER (WHERE grade_key = 'TRANSFORMED')
          / NULLIF(SUM(product_weight_kg) FILTER (WHERE grade_key = 'TRANSFORMED'), 0)
            AS transformed_price_p2
    FROM price_base
    WHERE auction_end_date >= NOW() - INTERVAL '60 days'
      AND auction_end_date <  NOW() - INTERVAL '30 days'
)

/* ---------- 9) Sortie finale : une seule ligne ----------- */
SELECT
    /* ===== UTILISATEURS ===== */
    uc.total_users_now                              AS total_nb_users,
    CASE WHEN uc.total_users_30 = 0 THEN NULL
         ELSE ROUND((uc.total_users_now - uc.total_users_30)::numeric * 100
                        / uc.total_users_30, 2) END        AS total_nb_users_tendency,

    uc.pending_now                                  AS pending_validation,
    CASE WHEN uc.pending_30 = 0 THEN NULL
         ELSE ROUND((uc.pending_now - uc.pending_30)::numeric * 100
                        / uc.pending_30, 2) END            AS pending_validation_tendency,

    /* ===== ENCHÈRES ===== */
    at.total_auctions_now                           AS total_auctions,
    CASE WHEN at.total_auctions_30 = 0 THEN NULL
         ELSE ROUND((at.total_auctions_now - at.total_auctions_30)::numeric * 100
                        / at.total_auctions_30, 2) END     AS total_auctions_tendency,

    /* ===== CONCLUDED AUCTIONS (30 jours) ===== */
    p1.auctions_concluded_p1                        AS auctions_concluded,
    CASE WHEN p2.auctions_concluded_p2 = 0 THEN NULL
         ELSE ROUND((p1.auctions_concluded_p1 - p2.auctions_concluded_p2)::numeric * 100
                        / p2.auctions_concluded_p2, 2) END AS auctions_concluded_tendency,

    /* ===== LOT WEIGHT / SALES AMOUNT (cumulatif) ===== */
    lw.lot_weight_now                               AS total_lot_weight_kg,
    CASE WHEN lw.lot_weight_30 = 0 THEN NULL
         ELSE ROUND((lw.lot_weight_now - lw.lot_weight_30)::numeric * 100
                        / lw.lot_weight_30::numeric, 2) END         AS total_lot_weight_kg_tendency,

    lw.sales_amount_now                             AS total_sales_amount,
    CASE WHEN lw.sales_amount_30 = 0 THEN NULL
         ELSE ROUND((lw.sales_amount_now - lw.sales_amount_30)::numeric * 100
                        / lw.sales_amount_30, 2) END       AS total_sales_amount_tendency,

    /* ===== SOLD WEIGHT / SALES AMOUNT (30 j) ===== */
    p1.sold_weight_p1                               AS total_sold_weight_kg,
    CASE WHEN p2.sold_weight_p2 = 0 THEN NULL
         ELSE ROUND((p1.sold_weight_p1 - p2.sold_weight_p2)::numeric * 100
                        / p2.sold_weight_p2::numeric, 2) END        AS total_sold_weight_kg_tendency,

    p1.sales_amount_p1                              AS monthly_sales_amount,
    CASE WHEN p2.sales_amount_p2 = 0 THEN NULL
         ELSE ROUND((p1.sales_amount_p1 - p2.sales_amount_p2)::numeric * 100
                        / p2.sales_amount_p2, 2) END       AS monthly_sales_amount_tendency,

    /* ====== NOUVEAUX KPI : PRIX MOYENS PAR QUALITÉ ====== */
    ROUND (p1p.grade1_price_p1::numeric, 0)                  AS grade1_price,
    CASE WHEN p2p.grade1_price_p2 IS NULL OR p2p.grade1_price_p2 = 0 THEN NULL
         ELSE ROUND((p1p.grade1_price_p1 - p2p.grade1_price_p2)::numeric * 100
                        / p2p.grade1_price_p2::numeric, 2) END      AS grade1_price_tendency,

    ROUND (p1p.grade2_price_p1::numeric, 0)                  AS grade2_price,
    CASE WHEN p2p.grade2_price_p2 IS NULL OR p2p.grade2_price_p2 = 0 THEN NULL
         ELSE ROUND((p1p.grade2_price_p1 - p2p.grade2_price_p2)::numeric * 100
                        / p2p.grade2_price_p2::numeric, 2) END      AS grade2_price_tendency,

    ROUND (p1p.grade3_price_p1::numeric, 0)                  AS grade3_price,
    CASE WHEN p2p.grade3_price_p2 IS NULL OR p2p.grade3_price_p2 = 0 THEN NULL
         ELSE ROUND((p1p.grade3_price_p1 - p2p.grade3_price_p2)::numeric * 100
                        / p2p.grade3_price_p2::numeric, 2) END      AS grade3_price_tendency,

    ROUND (p1p.hors_price_p1::numeric, 0)                    AS hors_category_price,
    CASE WHEN p2p.hors_price_p2 IS NULL OR p2p.hors_price_p2 = 0 THEN NULL
         ELSE ROUND((p1p.hors_price_p1 - p2p.hors_price_p2)::numeric * 100
                        / p2p.hors_price_p2::numeric, 2) END        AS hors_category_tendency,

    ROUND (p1p.transformed_price_p1::numeric, 0)             AS transformed_price,
    CASE WHEN p2p.transformed_price_p2 IS NULL OR p2p.transformed_price_p2 = 0 THEN NULL
         ELSE ROUND((p1p.transformed_price_p1 - p2p.transformed_price_p2)::numeric * 100
                        / p2p.transformed_price_p2::numeric, 2) END AS transformed_price_tendency

FROM user_counts      uc
         CROSS JOIN auction_totals  at
CROSS JOIN lot_weights     lw
CROSS JOIN period_1        p1
CROSS JOIN period_2        p2
CROSS JOIN period_1_prices p1p
CROSS JOIN period_2_prices p2p;
