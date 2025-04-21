import { createFileRoute } from "@tanstack/react-router";
import imgNoix from "@/assets/Noix de cajou bol.webp";
import { useUserStore } from "@/store/userStore";

export const Route = createFileRoute("/")({
  component: RouteComponent,
});

function RouteComponent() {
  const user = useUserStore((state) => state.user);

  return (
    <div className="min-h-screen flex flex-col bg-base-200">
      {/* --- Bandeau de bienvenue si connecté --- */}
      {user && (
        <div className="alert alert-success shadow-lg rounded-none text-lg justify-center">
          <div>
            <span>
              Bienvenue, <strong>{user.nom}</strong> ! Prêt(e) pour vos
              prochaines enchères ?
            </span>
          </div>
        </div>
      )}

      {/* Contenu principal */}
      <main className="flex-grow container mx-auto px-4 py-12 space-y-16">
        {/* Hero */}
        <section className="relative overflow-hidden bg-gradient-to-r from-green-500 to-yellow-400">
          {/* Décorations circulaires */}
          <div className="absolute -top-16 -left-16 w-72 h-72 rounded-full bg-white/20 blur-3xl"></div>
          <div className="absolute -bottom-16 -right-16 w-96 h-96 rounded-full bg-white/10 blur-2xl"></div>

          <div className="container mx-auto px-6 py-24 lg:flex lg:items-center lg:justify-between">
            {/* Texte */}
            <div className="text-center lg:text-left lg:max-w-xl">
              <h1 className="text-6xl font-extrabold leading-tight text-white drop-shadow-lg mb-6">
                e‑Annacarde
                <br />
                Enchères & Marché du Cajou
              </h1>
              <p className="mb-8 text-xl text-white/90">
                Rejoignez la plateforme d’enchères en temps réel pour la noix de
                cajou et ses produits transformés au Bénin. Surveillez,
                surenchérissez et soutenez les producteurs locaux en toute
                confiance.
              </p>
              {!user && (
                <div className="flex flex-col sm:flex-row sm:gap-4 justify-center lg:justify-start">
                  <a
                    href="/login"
                    className="btn btn-lg bg-white text-green-600 hover:bg-white/90 shadow-lg"
                  >
                    Démarrer maintenant
                  </a>
                  <a
                    href="/register"
                    className="btn btn-outline btn-lg border-white text-white hover:bg-white/20 mt-4 sm:mt-0"
                  >
                    Créer un compte
                  </a>
                </div>
              )}
            </div>

            {/* Illustration */}
            <div className="mt-12 lg:mt-0 lg:flex-shrink-0">
              <img
                src={imgNoix}
                alt="Illustration noix de cajou"
                className="w-full max-w-md mx-auto lg:mx-0 drop-shadow-2xl"
              />
            </div>
          </div>
        </section>
        {/* Fonctionnalités */}
        <section>
          <h2 className="text-3xl font-semibold text-center mb-8">
            Pourquoi choisir e‑Annacarde ?
          </h2>
          <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
            <div className="card bg-base-100 shadow-md p-6 text-center">
              <div className="mb-4 text-5xl">🕒</div>
              <h3 className="text-xl font-bold mb-2">Enchères en temps réel</h3>
              <p>
                Suivez chaque enchère et ne manquez aucune opportunité de
                surenchérir.
              </p>
            </div>
            <div className="card bg-base-100 shadow-md p-6 text-center">
              <div className="mb-4 text-5xl">🔒</div>
              <h3 className="text-xl font-bold mb-2">
                Transactions sécurisées
              </h3>
              <p>
                Paiement et livraison gérés de bout en bout pour votre
                tranquillité d’esprit.
              </p>
            </div>
            <div className="card bg-base-100 shadow-md p-6 text-center">
              <div className="mb-4 text-5xl">🤝</div>
              <h3 className="text-xl font-bold mb-2">Soutien à la filière</h3>
              <p>
                Valorisez les producteurs locaux et contribuez au développement
                durable.
              </p>
            </div>
          </div>
        </section>

        {/* Appel à l'action */}
        {!user && (
          <section className="text-center">
            <a href="/register" className="btn btn-secondary btn-lg">
              Créer un compte
            </a>
          </section>
        )}
      </main>
    </div>
  );
}
