import React, { useMemo, useState } from 'react'
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import {
  listUsersOptions,
  updateUserMutation,
} from '@/api/generated/@tanstack/react-query.gen'
import { Button } from '@/components/ui/button'
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from '@/components/ui/dialog'
import { Separator } from '@/components/ui/separator'
import { Alert, AlertDescription, AlertTitle } from '@/components/ui/alert'
import { Loader2, PlusCircle, MinusCircle, AlertCircle } from 'lucide-react'
import { toast } from 'sonner'
import type {
  CooperativeDto,
  ProducerUpdateDto,
  ProducerListDto,
} from '@/api/generated/types.gen'
import VirtualizedSelect from '@/components/VirtualizedSelect'

interface CooperativeMembersDialogProps {
  open: boolean
  onClose: () => void
  cooperative: CooperativeDto
  allCooperatives?: CooperativeDto[] // New prop
}

export const CooperativeMembersDialog: React.FC<
  CooperativeMembersDialogProps
> = ({ open, onClose, cooperative, allCooperatives }) => {
  const queryClient = useQueryClient()
  const [selectedProducerToAdd, setSelectedProducerToAdd] = useState<
    number | null
  >(null)

  const usersQuery = useQuery(listUsersOptions())

  const updateUserHook = useMutation({
    ...updateUserMutation(),
    onSuccess: (_data, variables) => {
      toast.success(
        `Membre ${variables.body?.type === 'producer' && variables.body.cooperativeId ? 'ajouté' : 'retiré'} avec succès.`
      )
      queryClient.invalidateQueries({ queryKey: listUsersOptions().queryKey })
    },
    onError: error => {
      toast.error('Erreur lors de la mise à jour du membre', {
        description:
          error.errors.map(e => e.message).join(' ,') ||
          'Impossible de mettre à jour le statut du membre.',
      })
    },
  })

  const currentMembers = useMemo(() => {
    if (!usersQuery.data) return []
    return usersQuery.data.filter(
      user =>
        user.type === 'producer' &&
        (user as ProducerListDto).cooperativeId === cooperative.id
    ) as ProducerListDto[]
  }, [usersQuery.data, cooperative.id])

  const availableProducers = useMemo(() => {
    if (!usersQuery.data) return []

    const existingPresidentIds = new Set(
      allCooperatives
        ?.map(c => c.presidentId)
        .filter(id => id !== null && id !== undefined) || []
    )

    return usersQuery.data.filter(user => {
      if (user.type !== 'producer') return false
      if ((user as ProducerListDto).cooperativeId === cooperative.id)
        return false
      if (existingPresidentIds.has(user.id)) return false
      return true
    }) as ProducerListDto[]
  }, [usersQuery.data, cooperative.id, allCooperatives])

  const handleAddMember = () => {
    if (!selectedProducerToAdd) return
    const producerId = selectedProducerToAdd
    const producerToAdd = usersQuery.data?.find(
      u => u.id === producerId && u.type === 'producer'
    ) as ProducerListDto

    if (producerToAdd && producerToAdd.agriculturalIdentifier) {
      const payload: ProducerUpdateDto = {
        type: 'producer',
        agriculturalIdentifier: producerToAdd.agriculturalIdentifier,
        cooperativeId: cooperative.id,
      }
      updateUserHook.mutate({ path: { id: producerToAdd.id }, body: payload })
      setSelectedProducerToAdd(null)
      // Invalider les requêtes tanstack query pour rafraichir les données
      queryClient.invalidateQueries(
        { queryKey: listUsersOptions().queryKey },
        {}
      )
    } else {
      toast.error('Sélection invalide', {
        description:
          "Le producteur sélectionné n'est pas valide ou son identifiant agricole est manquant.",
      })
    }
  }

  const handleRemoveMember = (memberId: number) => {
    const memberToRemove = usersQuery.data?.find(
      u => u.id === memberId && u.type === 'producer'
    ) as ProducerListDto

    if (memberToRemove && memberToRemove.agriculturalIdentifier) {
      const payload: ProducerUpdateDto = {
        type: 'producer',
        cooperativeId: undefined, // Pour retirer de la coopérative
        agriculturalIdentifier: memberToRemove.agriculturalIdentifier,
        firstName: memberToRemove.firstName,
        lastName: memberToRemove.lastName,
        email: memberToRemove.email,
        phone: memberToRemove.phone,
      }
      updateUserHook.mutate({ path: { id: memberToRemove.id }, body: payload })
    } else {
      toast.error('Membre invalide', {
        description:
          "Le membre à retirer n'est pas valide ou son identifiant agricole est manquant.",
      })
    }
  }

  const availableProducerOptions = availableProducers.map(producer => ({
    id: producer.id,
    label: `${producer.firstName} ${producer.lastName} (${producer.email})`,
  }))

  const isLoadingMutation = updateUserHook.isPending

  return (
    <Dialog open={open} onOpenChange={isOpen => !isOpen && onClose()}>
      <DialogContent className="max-h-[90vh] w-full overflow-y-auto sm:max-w-2xl">
        <DialogHeader>
          <DialogTitle>Gérer les Membres de "{cooperative.name}"</DialogTitle>
          <DialogDescription>
            Ajoutez ou retirez des producteurs membres de cette coopérative.
          </DialogDescription>
        </DialogHeader>
        <div className="grid gap-4 py-4">
          {usersQuery.isLoading && (
            <div className="flex items-center justify-center p-4">
              <Loader2 className="text-primary h-8 w-8 animate-spin" />
            </div>
          )}
          {usersQuery.isError && (
            <Alert variant="destructive">
              <AlertCircle className="h-4 w-4" />
              <AlertTitle>Erreur</AlertTitle>
              <AlertDescription>
                Erreur chargement utilisateurs: {usersQuery.error?.message}{' '}
                <Button
                  variant="link"
                  size="sm"
                  onClick={() => usersQuery.refetch()}
                  className="h-auto p-0"
                >
                  Réessayer
                </Button>
              </AlertDescription>
            </Alert>
          )}

          {!usersQuery.isLoading && !usersQuery.isError && usersQuery.data && (
            <>
              <div>
                <h3 className="mb-2 text-lg font-medium">
                  Membres Actuels ({currentMembers.length})
                </h3>
                {currentMembers.length > 0 ? (
                  <div className="max-h-48 space-y-2 overflow-y-auto pr-2">
                    {currentMembers.map(member => (
                      <div
                        key={member.id}
                        className="flex items-center justify-between rounded-md border p-2"
                      >
                        <div>
                          <p className="text-sm font-medium">
                            {member.firstName} {member.lastName}
                          </p>
                          <p className="text-muted-foreground text-xs">
                            ID: {member.id} - {member.email}
                          </p>
                        </div>
                        <Button
                          variant="ghost"
                          size="icon"
                          onClick={() => handleRemoveMember(member.id)}
                          disabled={isLoadingMutation}
                          aria-label="Retirer le membre"
                        >
                          <MinusCircle className="text-destructive h-5 w-5" />
                        </Button>
                      </div>
                    ))}
                  </div>
                ) : (
                  <p className="text-muted-foreground text-sm">
                    Aucun membre actuel.
                  </p>
                )}
              </div>

              <Separator className="my-4" />

              <div>
                <h3 className="mb-2 text-lg font-medium">
                  Ajouter un Producteur
                </h3>
                {availableProducers.length > 0 ? (
                  <div className="flex items-end gap-2">
                    <div className="grid w-full items-center gap-1.5">
                      <VirtualizedSelect
                        value={selectedProducerToAdd}
                        onChange={setSelectedProducerToAdd}
                        disabled={isLoadingMutation}
                        options={availableProducerOptions}
                        placeholder="Sélectionner un producteur"
                        label="  Sélectionner un producteur non assigné"
                        id="select-producer"
                      />
                    </div>
                    <Button
                      onClick={handleAddMember}
                      disabled={!selectedProducerToAdd || isLoadingMutation}
                      aria-label="Ajouter le membre"
                    >
                      <PlusCircle className="mr-2 h-5 w-5" />
                      Ajouter
                    </Button>
                  </div>
                ) : (
                  <p className="text-muted-foreground text-sm">
                    Aucun producteur disponible à ajouter.
                  </p>
                )}
                {isLoadingMutation && (
                  <div className="flex items-center justify-center p-2">
                    <Loader2 className="text-primary h-5 w-5 animate-spin" />
                  </div>
                )}
              </div>
            </>
          )}
        </div>
        <DialogFooter>
          <Button
            variant="outline"
            onClick={onClose}
            disabled={isLoadingMutation}
          >
            Fermer
          </Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  )
}
