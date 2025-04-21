import { useUserStore } from "@/store/userStore";
import logo from "@/assets/logo.svg";
import { Link, useNavigate } from "@tanstack/react-router";

export function Header() {
  const user = useUserStore((state) => state.user);
  const logout = useUserStore((state) => state.logout);
  const isLoggedIn = Boolean(user);
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate({ to: "/", replace: true });
  };

  return (
    <header>
      <div className="navbar bg-base-100 justify-center shadow-sm">
        <div className="container mx-auto flex items-center justify-between">
          <div className="navbar-start">
            <div className="dropdown">
              <label
                tabIndex={0}
                role="button"
                className="btn btn-ghost lg:hidden"
              >
                <svg
                  xmlns="http://www.w3.org/2000/svg"
                  className="h-5 w-5"
                  fill="none"
                  viewBox="0 0 24 24"
                  stroke="currentColor"
                >
                  <path
                    strokeLinecap="round"
                    strokeLinejoin="round"
                    strokeWidth="2"
                    d="M4 6h16M4 12h8m-8 6h16"
                  />
                </svg>
              </label>
              <ul
                tabIndex={0}
                className="menu menu-sm dropdown-content bg-base-100 rounded-box z-1 mt-3 w-52 p-2 shadow"
              >
                <li>
                  <Link to="/">
                    <a>Accueil</a>
                  </Link>
                </li>
                <li>
                  <a>Enchères</a>
                  <ul className="p-2">
                    <li>
                      <Link to="/enchères/cajou">
                        <a>Noix de cajou</a>
                      </Link>
                    </li>
                    <li>
                      <Link to="/enchères/autres">
                        <a>Autres matières</a>
                      </Link>
                    </li>
                  </ul>
                </li>
                <li>
                  <Link to="/à-propos">
                    <a>À propos</a>
                  </Link>
                </li>
              </ul>
            </div>

            <Link to="/">
              <img className="w-48" src={logo} alt="Logo e‑Annacarde" />
            </Link>
          </div>

          <div className="navbar-center hidden lg:flex">
            <ul className="menu menu-horizontal text-md px-1">
              <li>
                <Link to="/">
                  <a>Accueil</a>
                </Link>
              </li>
              <li>
                <details>
                  <summary>Enchères</summary>
                  <ul className="p-2">
                    <li>
                      <Link to="/enchères/cajou">
                        <a>Noix de cajou</a>
                      </Link>
                    </li>
                    <li>
                      <Link to="/enchères/autres">
                        <a>Autres matières</a>
                      </Link>
                    </li>
                  </ul>
                </details>
              </li>
              <li>
                <Link to="/à-propos">
                  <a>À propos</a>
                </Link>
              </li>
            </ul>
          </div>

          {!isLoggedIn ? (
            <div className="navbar-end">
              <Link to="/login">
                <a className="btn btn-primary">Connexion</a>
              </Link>
            </div>
          ) : (
            <div className="dropdown dropdown-end navbar-end">
              <label tabIndex={0} className="btn btn-ghost btn-circle avatar">
                <div className="w-10 rounded-full">
                  <img
                    alt="Avatar utilisateur"
                    src={
                      user.avatarUrl ??
                      "https://img.daisyui.com/images/stock/photo-1534528741775-53994a69daeb.webp"
                    }
                  />
                </div>
              </label>
              <ul
                tabIndex={0}
                className="menu menu-sm dropdown-content bg-base-100 rounded-box z-1 mt-3 w-52 p-2 shadow"
              >
                <li>
                  <Link to="/profil">
                    <a className="justify-between">
                      Profil
                      <span className="badge">Nouveau</span>
                    </a>
                  </Link>
                </li>
                <li>
                  <Link to="/paramètres">
                    <a>Paramètres</a>
                  </Link>
                </li>
                <li>
                  <button
                    onClick={handleLogout}
                    className="w-full text-left px-4 py-2"
                  >
                    Déconnexion
                  </button>
                </li>
              </ul>
            </div>
          )}
        </div>
      </div>
    </header>
  );
}
