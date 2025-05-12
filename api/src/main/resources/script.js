// scripts/assign-city-ids.js
import fs       from 'fs/promises';
import path     from 'path';

//
// 1. Table fixe des régions → IDs
//
const REGION_IDS = {
  "Alibori":    1,
  "Atacora":    2,
  "Atlantique": 3,
  "Borgou":     4,
  "Collines":   5,
  "Couffo":     6,
  "Donga":      7,
  "Littoral":   8,
  "Mono":       9,
  "Ouémé":     10,
  "Plateau":   11,
  "Zou":       12
};

async function main() {
  // 2. Lire le JSON existant
  const inPath  = path.resolve(process.cwd(), 'data/cities.json');
  const raw     = await fs.readFile(inPath, 'utf-8');
  const cities  = JSON.parse(raw);

  // 3. Réassigner des IDs
  const out = cities.map((c, idx) => {
    const rid = REGION_IDS[c.region];
    if (!rid) {
      console.warn(`Région inconnue pour ${c.city}: "${c.region}"`);
    }
    return {
      id:  idx + 1,        // 1-based
      n:   c.city,
      rid: rid || null,
      lat: parseFloat(String(c.lat).replace(',', '.')),
      lng: parseFloat(String(c.lng).replace(',', '.')),
      pop: c.population
    };
  });

  // 4. Sauver le nouveau JSON
  const outPath = path.resolve(process.cwd(), 'data/cities.json');
  await fs.writeFile(outPath, JSON.stringify(out, null, 2), 'utf-8');
  console.log(`✓ Généré ${out.length} villes dans ${outPath}`);
}

main().catch(err => {
  console.error(err);
  process.exit(1);
});
