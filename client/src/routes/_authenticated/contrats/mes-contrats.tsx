import { listContractOffersOptions } from '@/api/generated/@tanstack/react-query.gen'
import { BreadcrumbSection } from '@/components/BreadcrumbSection'
import ContractList from '@/components/contracts/ContractList'
import { useAuthUser } from '@/store/userStore'
import { useSuspenseQuery } from '@tanstack/react-query'
import { createFileRoute } from '@tanstack/react-router'

const listContractsQueryOptions = (userId: number) => ({
  ...listContractOffersOptions({
    query: { traderId: userId },
  }),
  staleTime: 10_000,
})

export const Route = createFileRoute('/_authenticated/contrats/mes-contrats')({
  component: RouteComponent,
  loader: async ({ context: { queryClient, user } }) => {
    await queryClient.ensureQueryData(listContractsQueryOptions(user!.id))
  },
})

export function RouteComponent() {
  const user = useAuthUser()

  const { data: contractsData } = useSuspenseQuery(
    listContractsQueryOptions(user.id)
  )

  return (
    <>
      <BreadcrumbSection
        titleKey="app.contracts_list_contracts.title"
        subtitleKey="app.contracts_list_contracts.subtitle"
        breadcrumbs={[
          { labelKey: 'breadcrumb.contracts' },
          { labelKey: 'breadcrumb.my_contracts' },
        ]}
      />
      <div className="container mx-auto mt-16 mb-16">
        <ContractList contracts={contractsData} />
      </div>
    </>
  )
}
