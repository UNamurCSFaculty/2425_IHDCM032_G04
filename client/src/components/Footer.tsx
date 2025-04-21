import logo from "@/assets/logo.svg";

export function Footer() {
  return (
    <footer className="bg-neutral text-neutral-content">
      {/* on garde .footer pour la grille DaisyUI, on lui ajoute container + flex */}
      <div className="footer sm:footer-horizontal container mx-auto flex justify-evenly p-10">
        {/* Logo */}
        <nav className="flex-1">
          <img
            src={logo}
            alt="Logo e-Annacarde"
            className="mx-auto w-48 sm:mx-0"
          />
        </nav>

        {/* Enchères */}
        <nav className="flex-1">
          <h6 className="footer-title">Enchères</h6>
          <a className="link link-hover">En cours</a>
          <a className="link link-hover">À venir</a>
          <a className="link link-hover">Mes offres</a>
          <a className="link link-hover">Historique</a>
        </nav>

        {/* Société */}
        <nav className="flex-1">
          <h6 className="footer-title">Société</h6>
          <a className="link link-hover">À propos</a>
          <a className="link link-hover">Contact</a>
          <a className="link link-hover">Recrutement</a>
          <a className="link link-hover">Presse</a>
        </nav>

        {/* Mentions légales */}
        <nav className="flex-1">
          <h6 className="footer-title">Mentions légales</h6>
          <a className="link link-hover">Conditions d’utilisation</a>
          <a className="link link-hover">Politique de confidentialité</a>
          <a className="link link-hover">Politique de cookies</a>
        </nav>
      </div>
    </footer>
  );
}
