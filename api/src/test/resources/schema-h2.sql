/**
  Création de la vue de l'export d'auctions dans la base de données H2 de test
 */
CREATE OR REPLACE VIEW v_auction_bid_analysis AS
/* ---------- 1) UNION des produits concrets ---------- */
WITH all_products AS (
    SELECT hp.id,
           hp.weight_kg,
           hp.delivery_date,
           hp.quality_control_id,
           hp.store_id,
           hp.transformed_product_id
    FROM   harvest_product hp

    UNION ALL

    SELECT tp.id,
           tp.weight_kg,
           tp.delivery_date,
           tp.quality_control_id,
           tp.store_id,
           CAST(NULL AS BIGINT) AS transformed_product_id
    FROM   transformed_product tp
),
/* ---------- 2) Offre gagnante = bid dont statut 'Conclu' ---------- */
winner_rn AS (
    SELECT
        b.auction_id,
        b.amount                AS winning_bid_amount,
        b.trader_id             AS winner_trader_id,
        b.status_id,
        ROW_NUMBER() OVER (
            PARTITION BY b.auction_id
            ORDER BY b.amount DESC, b.creation_date DESC
        ) AS rn
    FROM bid b
    WHERE b.status_id = 2
),

winner AS (
    SELECT auction_id,
        winning_bid_amount,
        winner_trader_id,
        status_id
    FROM winner_rn
    WHERE rn = 1
)
/* ---------- 3) Résultat final ---------- */
SELECT
    /* ======== auction ======== */
    a.id                             AS auction_id,
    a.creation_date                  AS auction_start_date,
    a.expiration_date                AS auction_end_date,
    a.price                          AS auction_start_price,
    a.active                         AS auction_ended,
    ts.name                          AS auction_status,

    /* options embarquées */
    ast.name                         AS strategy_name,
    a.min_price_kg                   AS option_min_price_kg,
    a.max_price_kg                   AS option_max_price_kg,
    a.buy_now_price                  AS option_buy_now_price,
    a.show_public                    AS option_show_public,
    a.min_increment                  AS option_min_increment,

    /* ======== produit ======== */
    p.id                             AS product_id,
    p.weight_kg                      AS product_weight_kg,
    p.delivery_date                  AS product_deposit_date,
    p.transformed_product_id         AS transformed_product_id,

    /* -------- quality control -------- */
    qc.quality_inspector_id          AS quality_inspector_id,
    q.name                           AS product_quality,
    qt.name                          AS product_type,

    /* ======== store du produit ======== */
    st.id                            AS store_id,
    st.name                          AS store_name,
    stc.name                         AS store_city,
    str.name                         AS store_region,

    /* ======== vendeur ======== */
    tr.id                            AS seller_id,
    sc.name                          AS seller_city,
    sr.name                          AS seller_region,
    co.name                          AS seller_cooperative,

    /* ======== agrégats bids ======== */
    COUNT(b.id)                      AS bid_count,
    MAX(b.amount)                    AS bid_max,
    MIN(b.amount)                    AS bid_min,
    ROUND(AVG(b.amount), 2)          AS bid_avg,
    SUM(b.amount)                    AS bid_sum,

    /* ======== offre gagnante ======== */
    w.winner_trader_id,
    w.winning_bid_amount             AS bid_winning_amount,
    wc.name                          AS winner_city,
    wr.name                          AS winner_region

FROM   auction a
           LEFT   JOIN auction_strategy ast ON ast.id = a.strategy_id
           JOIN   trade_status ts          ON ts.id = a.status_id

    /* ---------- produit & quality control ---------- */
           JOIN   all_products p           ON p.id = a.product_id
           LEFT   JOIN quality_control qc  ON qc.id = p.quality_control_id
           LEFT   JOIN quality         q   ON q.id  = qc.quality_id
           LEFT   JOIN quality_type    qt  ON qt.id = q.quality_type_id

    /* ---------- store du produit ---------- */
           JOIN   store   st  ON st.id = p.store_id
           JOIN   users   su  ON su.id = st.user_id
           LEFT   JOIN city   stc ON stc.id = st.city_id
           LEFT   JOIN region str ON str.id = st.region_id

    /* ---------- vendeur ---------- */
           JOIN   trader tr ON tr.id = a.trader_id
           JOIN   users  u  ON u.id = tr.id
           LEFT   JOIN producer    pr ON pr.id = tr.id
           LEFT   JOIN cooperative co ON co.id = pr.cooperative_id
           LEFT   JOIN city   sc ON sc.id = u.city_id
           LEFT   JOIN region sr ON sr.id = u.region_id

    /* ---------- bids & gagnant ---------- */
           LEFT   JOIN bid    b  ON b.auction_id = a.id
           LEFT   JOIN winner w  ON w.auction_id = a.id
           LEFT   JOIN trader wt ON wt.id = w.winner_trader_id
           LEFT   JOIN users  wu ON wu.id = wt.id
           LEFT   JOIN city   wc ON wc.id = wu.city_id
           LEFT   JOIN region wr ON wr.id = wu.region_id

GROUP  BY a.id, ts.name,
          a.strategy_id, ast.name,
          a.min_price_kg, a.max_price_kg, a.buy_now_price, a.show_public, a.min_increment,
          p.id, p.weight_kg, p.delivery_date, p.transformed_product_id,
          qc.quality_inspector_id, q.name, qt.name,
          st.id, st.name, su.first_name, su.last_name, stc.name, str.name,
          tr.id, u.first_name, u.last_name, sc.name, sr.name, co.name,
          w.winning_bid_amount, w.winner_trader_id,
          wu.first_name, wu.last_name, wc.name, wr.name;