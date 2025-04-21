import { useUserStore } from '@/store/userStore'
import { Link } from '@tanstack/react-router'

export function Header() {
  // on va utiliser user store
  const user = useUserStore(state => state.user)
  const isLoggedIn = Boolean(user)

  return (
    <header>
      <div className="navbar bg-base-100 justify-center shadow-sm">
        <div className="container mx-auto flex items-center justify-between">
          <div className="navbar-start">
            <div className="dropdown">
              <div
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
              </div>
              <ul
                tabIndex={0}
                className="menu menu-sm dropdown-content bg-base-100 rounded-box z-1 mt-3 w-52 p-2 shadow"
              >
                <li>
                  <a>Accueil</a>
                </li>
                <li>
                  <a>Enchères</a>
                  <ul className="p-2">
                    <li>
                      <a>Noix de cajou</a>
                    </li>
                    <li>
                      <a>Autres matières</a>
                    </li>
                  </ul>
                </li>
                <li>
                  <a>À propos</a>
                </li>
              </ul>
            </div>

            <Link to="/">
              <img
                className="w-48"
                src="/public/logo.svg"
                alt="Logo e-Annacarde"
              />
            </Link>
          </div>
          <div className="navbar-center hidden lg:flex">
            <ul className="menu menu-horizontal text-md px-1">
              <li>
                <a>Accueil</a>
              </li>
              <li>
                <details>
                  <summary>Enchères</summary>
                  <ul className="p-2">
                    <li>
                      <a>Noix de cajou</a>
                    </li>
                    <li>
                      <a>Autres matières</a>
                    </li>
                  </ul>
                </details>
              </li>
              <li>
                <a>À propos</a>
              </li>
            </ul>
          </div>
          {!isLoggedIn && (
            <div className="navbar-end">
              <Link to="/login">
                <a className="btn">Login</a>
              </Link>
            </div>
          )}
          {isLoggedIn && (
            <div className="dropdown dropdown-end navbar-end">
              <div
                tabIndex={0}
                role="button"
                className="btn btn-ghost btn-circle avatar"
              >
                <div className="w-10 rounded-full">
                  <img
                    alt="Avatar utilisateur"
                    src="https://img.daisyui.com/images/stock/photo-1534528741775-53994a69daeb.webp"
                  />
                </div>
              </div>
              <ul
                tabIndex={0}
                className="menu menu-sm dropdown-content bg-base-100 rounded-box z-1 mt-3 w-52 p-2 shadow"
              >
                <li>
                  <a className="justify-between">
                    Profil
                    <span className="badge">Nouveau</span>
                  </a>
                </li>
                <li>
                  <a>Paramètres</a>
                </li>
                <li>
                  <a>Déconnexion</a>
                </li>
              </ul>
            </div>
          )}
        </div>
      </div>
    </header>
  )
}
