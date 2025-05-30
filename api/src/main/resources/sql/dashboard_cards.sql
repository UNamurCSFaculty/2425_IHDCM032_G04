/* ===============================================================
   v_dashboard_cards  :  1 ligne contenant tous les KPI “cards”
   VERSION 2 – compatible avec product_quantity et structure Auction
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
                    WHERE enabled = false
                      AND registration_date < NOW() - INTERVAL '30 days'
                )                                         AS pending_30
            FROM users
        ),
        /* ------------ 2) ENCHÈRES (table auction) --------------- */
        auction_totals AS (
            SELECT
                COUNT(*)                                  AS total_auctions_now,
                COUNT(*) FILTER (
                    WHERE creation_date < NOW() - INTERVAL '30 days'
                )                                         AS total_auctions_30
            FROM auction
            WHERE active = true
        ),
        /* --- 3) POIDS/AMOUNT MIS EN VENTE (cumulatif, toutes enchères) -- */
        lot_weights AS (
            SELECT
                SUM(product_weight_kg)                    AS lot_weight_now,
                SUM(bid_winning_amount)                    AS sales_amount_now,
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
                COUNT(*)                              AS auctions_concluded_p1,
                SUM(product_weight_kg)                AS sold_weight_p1,
                SUM(bid_winning_amount)               AS sales_amount_p1
            FROM v_auction_bid_analysis
            WHERE auction_end_date >= NOW() - INTERVAL '30 days'
              AND auction_end_date <  NOW()
              AND bid_winning_amount IS NOT NULL
        ),
        /* ---------- 5) Période P2 = [now-60d ; now-30d[ ---------- */
        period_2 AS (
            SELECT
                COUNT(*)                              AS auctions_concluded_p2,
                SUM(product_weight_kg)                AS sold_weight_p2,
                SUM(bid_winning_amount)               AS sales_amount_p2
            FROM v_auction_bid_analysis
            WHERE auction_end_date >= NOW() - INTERVAL '60 days'
              AND auction_end_date <  NOW() - INTERVAL '30 days'
              AND bid_winning_amount IS NOT NULL
        )
        /* ---------- 6) Sortie finale : une seule ligne ----------- */
        SELECT
            /* ===== USERS ===== */
            uc.total_users_now                              AS total_nb_users,
            CASE
                WHEN uc.total_users_30 = 0 THEN NULL
                ELSE ROUND(
                        (uc.total_users_now - uc.total_users_30)::numeric * 100
                            / uc.total_users_30, 2
                     )
                END                                             AS total_nb_users_tendency,

            uc.pending_now                                  AS pending_validation,
            CASE
                WHEN uc.pending_30 = 0 THEN NULL
                ELSE ROUND(
                        (uc.pending_now - uc.pending_30)::numeric * 100
                            / uc.pending_30, 2
                     )
                END                                             AS pending_validation_tendency,

            /* ===== AUCTIONS ===== */
            at.total_auctions_now                           AS total_auctions,
            CASE
                WHEN at.total_auctions_30 = 0 THEN NULL
                ELSE ROUND(
                        (at.total_auctions_now - at.total_auctions_30)::numeric * 100
                            / at.total_auctions_30, 2
                     )
                END                                             AS total_auctions_tendency,

            /* ===== CONCLUDED AUCTIONS (30 derniers jours) ===== */
            p1.auctions_concluded_p1                        AS auctions_concluded,
            CASE
                WHEN p2.auctions_concluded_p2 = 0 THEN NULL
                ELSE ROUND(
                        (p1.auctions_concluded_p1 - p2.auctions_concluded_p2)::numeric * 100
                            / p2.auctions_concluded_p2, 2
                     )
                END                                             AS auctions_concluded_tendency,

            /* ===== LOT WEIGHT / SALES AMOUNT (cumulatif) ===== */
            lw.lot_weight_now                               AS total_lot_weight_kg,
            CASE
                WHEN lw.lot_weight_30 = 0 THEN NULL
                ELSE ROUND(
                        (lw.lot_weight_now - lw.lot_weight_30)::numeric * 100
                            / lw.lot_weight_30::numeric, 2
                     )
                END                                             AS total_lot_weight_kg_tendency,

            lw.sales_amount_now                               AS total_sales_amount,
            CASE
                WHEN lw.sales_amount_30 = 0 THEN NULL
                ELSE ROUND(
                        (lw.sales_amount_now - lw.sales_amount_30)::numeric * 100
                            / lw.sales_amount_30::numeric, 2
                     )
                END                                             AS total_sales_amount_tendency,

            /* ===== SOLD WEIGHT / SALES AMOUNT (30 derniers jours) ===== */
            p1.sold_weight_p1                               AS total_sold_weight_kg,
            CASE
                WHEN p2.sold_weight_p2 = 0 THEN NULL
                ELSE ROUND(
                        (p1.sold_weight_p1 - p2.sold_weight_p2)::numeric * 100
                            / p2.sold_weight_p2::numeric, 2
                     )
                END                                             AS total_sold_weight_kg_tendency,

            p1.sales_amount_p1                              AS monthly_sales_amount,
            CASE
                WHEN p2.sales_amount_p2 = 0 THEN NULL
                ELSE ROUND(
                        (p1.sales_amount_p1 - p2.sales_amount_p2)::numeric * 100
                            / p2.sales_amount_p2::numeric, 2
                     )
                END                                             AS monthly_sales_amount_tendency
        FROM user_counts   uc
                 CROSS JOIN auction_totals  at
        CROSS JOIN lot_weights     lw
        CROSS JOIN period_1        p1
        CROSS JOIN period_2        p2;
