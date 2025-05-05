import { createFileRoute } from '@tanstack/react-router'

import { Card, CardContent } from '@/components/ui/card'
import { useAppForm } from '@/components/form'
import { useSuspenseQuery, useMutation } from '@tanstack/react-query'
import { useTranslation } from 'react-i18next'
import { useUserStore } from '@/store/userStore'
import { listProductsOptions } from '@/api/generated/@tanstack/react-query.gen'
import type { ProductDtoReadable } from '@/api/generated'

const listProductsQueryOptions = (userId: number) => ({
  ...listProductsOptions({ query: { traderId: userId } }),
  staleTime: 10_000,
});

export const Route = createFileRoute('/_authenticated/ventes/nouvelle-enchere')(
  {
    component: RouteComponent,
    loader: async ({ context: { queryClient } }) => {
      const user = useUserStore.getState().user // cannot use hook here...
      if (!user) {
        throw new Error('L\'utilisateur n\'est pas connecté');
      }
      return queryClient.ensureQueryData(listProductsQueryOptions(user.id!))
    },
  },
)

function RouteComponent() {
  // const submitMutation = useMutation({post});

  const { user } = useUserStore();

  const { data } = useSuspenseQuery(
    listProductsQueryOptions(user!.id!),
  );

  const productsArray = data as ProductDtoReadable[];

  const form = useAppForm({
    defaultValues: { expirationDate: '', product: '', productType: '', quantity: '', quality: '', store: '', deliveryDate: '', price: '' },
    // validators: { onChange: LoginSchema },
    // onSubmit({ value }) {
      // loginMutation.mutate({ body: value as LoginRequest })
    // },
  });

  return (
    <div className="bg-muted flex min-h-svh flex-col items-center justify-center p-6 md:p-10">
    <div className="w-full max-w-sm md:max-w-xl">
    <div className="flex flex-col gap-6">
      <Card className="overflow-hidden">
        <CardContent>
          <h2 className="text-2xl font-bold">Vendre un produit</h2>
          <form
            className="p-6 md:p-8"
            onSubmit={e => {
              e.preventDefault()
              form.handleSubmit()
            }}
          >
            <div className="flex flex-col gap-6">

              {/* Séparateur */}
              <div className="after:border-border relative text-center text-sm after:absolute after:inset-0 after:top-1/2 after:z-0 after:flex after:items-center after:border-t">
                <span className="bg-background text-muted-foreground relative z-10 px-2">
                  Paramètres de votre enchère
                </span>
              </div>

              {/* Produit */}
              <div className="grid gap-2">
                <form.AppField
                  name="product"
                  children={field => (
                    <field.SelectField
                      options={productsArray.map(product => ({
                        value: String(product.id),
                        label: product.type + " " + product.weightKg + " kg @ " + product.deliveryDate,
                      }))}
                      label="Produit"
                      value={field.value}
                    />
                  )}
                />
              </div>

              <div className="flex gap-4">
                {/* Quantité */}
                <div className="grid gap-2">
                  <form.AppField
                    name="quantity"
                    children={field => (
                      <field.TextField
                        label="Quantité (kg)"
                        type="quantity"
                        placeholder="0.0"
                      />
                    )}
                  />
                </div>

                {/* Prix demandé */}
                <div className="grid gap-2">
                <form.AppField
                  name="price"
                  children={field => (
                    <field.TextField
                      label="Prix demandé (CFA)"
                      type="price"
                      placeholder="0.0"
                    />
                  )}
                />
                </div>                
              </div>

              {/* Date d'expiration */}
              <div className="grid gap-2">
              <form.AppField
                name="expirationDate"
                children={field => (
                  <field.TextField
                    label="Expiration de l'enchère"
                    type="expirationDate"
                    placeholder="10/10/2025"
                  />
                )}
              />
              </div>

              {/* Séparateur */}
              <div className="after:border-border relative text-center text-sm after:absolute after:inset-0 after:top-1/2 after:z-0 after:flex after:items-center after:border-t">
                <span className="bg-background text-muted-foreground relative z-10 px-2">
                  Informations sur votre produit
                </span>
              </div>

              <div className="flex gap-4">
                {/* Type */}
                <div className="grid gap-2">
                <form.AppField
                  name="productType"
                  children={field => (
                    <field.TextField
                      label="Type"
                      type="productType"
                      placeholder="Récolte"
                      disabled={true}
                    />
                  )}
                />
                </div>

                {/* Qualité */}
                <div className="grid gap-2">
                <form.AppField
                  name="quality"
                  children={field => (
                    <field.TextField
                      label="Qualité"
                      type="quality"
                      placeholder="WB200"
                      disabled={true}
                    />
                  )}
                />
                </div>
              </div>

              {/* Magasin */}
              <div className="grid gap-2">
              <form.AppField
                name="store"
                children={field => (
                  <field.TextField
                    label="Magasin"
                    type="store"
                    placeholder="Nassara"
                    disabled={true}
                  />
                )}
              />
              </div>

              {/* Date de dépôt */}
              <div className="grid gap-2">
              <form.AppField
                name="deliveryDate"
                children={field => (
                  <field.TextField
                    label="Date de dépôt"
                    type="deliveryDate"
                    placeholder=""
                    disabled={true}
                  />
                )}
              />
              </div>

              {/* Erreur */}
              {/* {submitMutation.error && (
                <p className="text-sm text-red-600">
                  {{t('errors.' + submitMutation.error.code)}}
                </p>
              )} */}

              {/* Bouton submit */}
              <form.AppForm>
                <form.SubmitButton
                  className="w-full"
                  // disabled={isPending || !canSubmit}
                >
                  Ouvrir la vente
                  {/* {isPending ? "Création..." : "Créer l'enchère"} */}
                </form.SubmitButton>
              </form.AppForm>
            </div>
          </form>
        </CardContent>
      </Card>
    </div>
    </div>
    </div>
  )
}
