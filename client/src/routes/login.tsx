import { useState } from "react";
import { createFileRoute, useNavigate } from "@tanstack/react-router";
import { useUserStore } from "@/store/userStore";
import type { User } from "@/types/api";
import logo from "@/assets/logo.svg";

export const Route = createFileRoute("/login")({
  component: RouteComponent,
});

function RouteComponent() {
  const navigate = useNavigate();
  const setUser = useUserStore((state) => state.setUser);
  const logout = useUserStore((state) => state.logout);
  const user = useUserStore((state) => state.user);
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState<string | null>(null);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);

    if (!email || !password) {
      setError("Veuillez remplir tous les champs");
      return;
    }

    // Simulation de la connexion utilisateur
    const user: User = {
      id: 1,
      email,
      nom: email.split("@")[0],
      roles: [],
    };

    setUser(user);
    navigate({ to: "/", replace: true });
  };

  const handleLogout = () => {
    logout();
    navigate({ to: "/", replace: true });
  };

  // Si l'utilisateur est connecté, on affiche un message et le bouton de déconnexion
  if (user) {
    return (
      <div className="flex justify-center items-center min-h-screen bg-base-200">
        <div className="card w-full max-w-sm shadow-xl">
          <div className="card-body text-center">
            <h2 className="card-title mx-auto">
              Vous êtes connecté en tant que{" "}
              <span className="font-semibold">{user.email}</span>
            </h2>
            <button
              onClick={handleLogout}
              className="btn btn-error mt-6 w-full"
            >
              Déconnexion
            </button>
          </div>
        </div>
      </div>
    );
  }

  // Sinon, on affiche le formulaire de connexion
  return (
    <div>
      <div className="card mx-auto mt-20 mb-20 w-full max-w-5xl shadow-xl">
        <div className="bg-base-100 grid grid-cols-1 rounded-xl md:grid-cols-2">
          {/* Section de présentation */}
          <div>
            <div className="hero min-h-full rounded-l-xl bg-neutral-100">
              <div className="hero-content py-12">
                <div className="max-w-md text-center">
                  <img
                    src={logo}
                    className="mr-2 inline-block w-80"
                    alt="Logo e-Annacarde"
                  />
                  <h1 className="mt-8 text-2xl font-bold">
                    Bienvenue sur e‑Annacarde
                  </h1>
                  <ul className="mt-4 space-y-2 text-left">
                    <li>
                      ✓ Plateforme d’enchères de{" "}
                      <span className="font-semibold">noix de cajou</span> et
                      produits transformés
                    </li>
                    <li>
                      ✓ Enchères{" "}
                      <span className="font-semibold">en temps réel</span>
                    </li>
                    <li>
                      ✓ <span className="font-semibold">Suivi</span> facile de
                      vos offres
                    </li>
                    <li>
                      ✓ Transactions{" "}
                      <span className="font-semibold">sécurisées</span>
                    </li>
                    <li>
                      ✓ Interface{" "}
                      <span className="font-semibold">intuitive</span> (Tailwind
                      + DaisyUI)
                    </li>
                  </ul>
                </div>
              </div>
            </div>
          </div>

          {/* Formulaire de connexion */}
          <div className="px-10 py-24">
            <h2 className="mb-6 text-center text-2xl font-semibold">
              Se connecter
            </h2>
            <form onSubmit={handleSubmit}>
              <div className="space-y-6">
                <div className="w-full">
                  <label htmlFor="email" className="label">
                    Adresse e-mail
                  </label>
                  <input
                    id="email"
                    type="email"
                    value={email}
                    onChange={(e) => setEmail(e.currentTarget.value)}
                    placeholder="votre@exemple.com"
                    className="input w-full"
                  />
                </div>

                <div className="w-full">
                  <label htmlFor="password" className="label">
                    Mot de passe
                  </label>
                  <input
                    id="password"
                    type="password"
                    value={password}
                    onChange={(e) => setPassword(e.currentTarget.value)}
                    placeholder="••••••••"
                    className="input w-full"
                  />
                </div>
              </div>

              <div className="mt-2 text-right">
                <a
                  href="/forgot-password"
                  className="hover:text-primary text-sm transition duration-200 hover:underline"
                >
                  Mot de passe oublié ?
                </a>
              </div>

              {error && <p className="text-error mt-4 text-center">{error}</p>}

              <button type="submit" className="btn btn-primary mt-6 w-full">
                Me connecter
              </button>

              <div className="mt-4 text-center">
                Pas encore de compte ?{" "}
                <a
                  href="/register"
                  className="hover:text-primary transition duration-200 hover:underline"
                >
                  Inscrivez‑vous
                </a>
              </div>
            </form>
          </div>
        </div>
      </div>
    </div>
  );
}
