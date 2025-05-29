/* ===============================================================
   v_dashboard_graphic  :  1 ligne / jour pour courbe « open vs new »
   ===============================================================*/
CREATE OR REPLACE VIEW v_dashboard_graphic AS
WITH
        -- 1) Liste de tous les jours du système (de la 1ʳᵉ enchère à aujourd’hui)
        days AS (
            SELECT generate_series(
                       date_trunc('day', (SELECT MIN(creation_date) FROM auction)),  -- début
                       date_trunc('day', NOW()),                                     -- fin (aujourd’hui 00 h)
                       interval '1 day'
                   ) AS day_start
        )
        SELECT
            d.day_start                                   AS date,            -- minuit local

            /* ---- A) Enchères toujours ouvertes à minuit ---- */
            (
                SELECT COUNT(*)
                FROM auction a
                JOIN trade_status ts ON ts.id = a.status_id
                WHERE a.creation_date <  d.day_start             -- déjà créées
                  AND a.expiration_date >= d.day_start           -- pas encore expirées
            ) AS total_open_auctions,

            /* ---- B) Nouvelles enchères créées sur les 24 h précédentes ---- */
            (
                SELECT COUNT(*)
                FROM auction a2
                WHERE a2.creation_date >= d.day_start - interval '1 day'
                  AND a2.creation_date <  d.day_start
            ) AS total_new_auctions

        FROM days d
        ORDER BY d.day_start;
