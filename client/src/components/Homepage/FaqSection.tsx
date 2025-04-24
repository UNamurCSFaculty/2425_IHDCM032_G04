import {
  Accordion,
  AccordionContent,
  AccordionItem,
  AccordionTrigger,
} from '@/components/ui/accordion'

interface FaqItem {
  question: string
  answer: string
}

interface Faq1Props {
  heading?: string
  items?: FaqItem[]
}

const FaqSection = ({
  heading = 'Questions fréquentes',
  items = [
    {
      question: "Qu'est-ce qu'e-Annacarde ?",
      answer:
        "e-Annacarde est une plateforme d'enchères en ligne dédiée à la noix de cajou au Bénin, qui met en relation directe producteurs et acheteurs pour maximiser la transparence et la valeur des transactions.",
    },
    {
      question: "Comment puis-je m'inscrire en tant que producteur ?",
      answer:
        'Pour vous inscrire, cliquez sur ' +
        '<Link to="/inscription">Inscription</Link>' +
        ', remplissez le formulaire avec vos informations de production et téléchargez les documents requis pour vérification.',
    },
    {
      question: 'Comment participer à une enchère ?',
      answer:
        "Après validation de votre compte, connectez-vous, rendez-vous sur la page d'enchères, sélectionnez le lot de noix de cajou souhaité et placez votre enchère en indiquant le montant que vous êtes prêt à payer.",
    },
    {
      question: 'Quels sont les frais de service ?',
      answer:
        'e-Annacarde applique une commission de 2% sur le montant final de chaque enchère remportée, sans frais cachés pour les producteurs ou acheteurs.',
    },
    {
      question: 'Comment se fait le paiement et la livraison ?',
      answer:
        "Une fois l'enchère remportée, le paiement s'effectue via nos partenaires de paiement mobile (MoMo, Flooz) ou virement bancaire. La livraison est organisée par des transporteurs agréés, aux frais de l'acheteur ou selon l'accord conclu.",
    },
    {
      question: 'Quelles normes qualité dois-je respecter ?',
      answer:
        'Vous devez vous conformer aux nouvelles directives du gouvernement béninois sur le calibrage, la teneur en humidité et le conditionnement de la noix de cajou. Consultez notre article « Nouvelles normes gouvernementales pour la production de cajou » pour tous les détails.',
    },
  ],
}: Faq1Props) => {
  return (
    <section className="py-10">
      <div className="intersect-once intersect-half intersect:scale-100 intersect:opacity-100 container mx-auto h-full max-w-3xl scale-50 transform opacity-0 transition duration-500 ease-out">
        <h1 className="mb-4 text-center text-3xl font-semibold md:mb-11 md:text-4xl">
          {heading}
        </h1>
        <Accordion type="single" collapsible>
          {items.map((item, index) => (
            <AccordionItem key={index} value={`item-${index}`}>
              <AccordionTrigger className="font-semibold hover:no-underline">
                {item.question}
              </AccordionTrigger>
              <AccordionContent className="text-muted-foreground">
                {item.answer}
              </AccordionContent>
            </AccordionItem>
          ))}
        </Accordion>
      </div>
    </section>
  )
}

export default FaqSection
