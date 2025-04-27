import { createFormHook, createFormHookContexts } from '@tanstack/react-form'
import { TextField } from './text-field'
import { CheckboxField } from './checkbox-field'
import { SelectField } from './select-field'
import { SubmitButton } from './submit-button'
import { ResetButton } from './reset-button'
import { TextAreaField } from './textarea-field'
import { SelectLanguageField } from './select-language'

export const { fieldContext, useFieldContext, formContext, useFormContext } =
  createFormHookContexts()

export const { useAppForm } = createFormHook({
  fieldComponents: {
    TextField,
    CheckboxField,
    SelectField,
    TextAreaField,
    SelectLanguageField,
  },
  formComponents: {
    SubmitButton,
    ResetButton,
  },
  fieldContext,
  formContext,
})
