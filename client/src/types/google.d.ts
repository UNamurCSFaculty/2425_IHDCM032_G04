declare global {
  interface Window {
    google?: {
      accounts: {
        id: {
          initialize: (config: GoogleIdInitializeOptions) => void
          prompt: (
            momentListener?: (notification: PromptMomentNotification) => void
          ) => void
        }
      }
    }
  }
}

export interface GoogleIdInitializeOptions {
  client_id: string
  callback: (response: CredentialResponse) => void
  ux_mode?: 'popup' | 'redirect'
  auto_select?: boolean
}

export interface CredentialResponse {
  credential?: string
  clientId?: string
  select_by?:
    | 'auto'
    | 'user'
    | 'user_1tap'
    | 'user_2tap'
    | 'button'
    | 'api_trigger_btn'
    | 'api_trigger_auto'
}

export interface PromptMomentNotification {
  getMomentType: () =>
    | 'display'
    | 'skipped'
    | 'dismissed'
    | 'opt_out_or_no_session'
  getNotDisplayedReason: () =>
    | 'browser_not_supported'
    | 'invalid_client'
    | 'missing_client_id'
    | 'opt_out_or_no_session'
    | 'secure_http_required'
    | 'suppressed_by_user'
    | null
  getSkippedReason: () =>
    | 'auto_cancel'
    | 'user_cancel'
    | 'tap_outside'
    | 'issuing_failed'
    | null
  getDismissedReason: () =>
    | 'credential_returned'
    | 'cancel_called'
    | 'flow_restarted'
    | null
  isDismissedByUser: () => boolean
  isDisplayed: () => boolean
  isNotDisplayed: () => boolean
  isSkippedMoment: () => boolean
}

// Ajoutez un export vide pour que TypeScript traite ce fichier comme un module
// et applique les déclarations globales correctement.
export {}
