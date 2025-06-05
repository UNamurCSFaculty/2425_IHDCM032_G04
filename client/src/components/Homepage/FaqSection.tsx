import {
  Accordion,
  AccordionContent,
  AccordionItem,
  AccordionTrigger,
} from '@/components/ui/accordion'
import { useTranslation } from 'react-i18next' // Ajout de l'import

interface FaqItem {
  question: string
  answer: string
}

interface Faq1Props {
  heading?: string
  items?: FaqItem[]
}

const FaqSection = (props: Faq1Props) => {
  const { t } = useTranslation()

  const heading = props.heading ?? t('homepage.faq.heading')

  const defaultItems: FaqItem[] = [
    {
      question: t('homepage.faq.q1_question'),
      answer: t('homepage.faq.q1_answer'),
    },
    {
      question: t('homepage.faq.q2_question'),
      answer: t('homepage.faq.q2_answer_part1'),
    },
    {
      question: t('homepage.faq.q3_question'),
      answer: t('homepage.faq.q3_answer'),
    },
    {
      question: t('homepage.faq.q4_question'),
      answer: t('homepage.faq.q4_answer'),
    },
    {
      question: t('homepage.faq.q5_question'),
      answer: t('homepage.faq.q5_answer'),
    },
    {
      question: t('homepage.faq.q6_question'),
      answer: t('homepage.faq.q6_answer'),
    },
  ]

  const items = props.items ?? defaultItems

  return (
    <section className="py-10">
      <div className="intersect-once intersect-half intersect:scale-100 intersect:opacity-100 container mx-auto h-full max-w-3xl scale-50 transform opacity-0 transition duration-500 ease-out">
        <h2 className="mb-4 text-center text-3xl font-semibold md:mb-11 md:text-4xl">
          {heading}
        </h2>
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
