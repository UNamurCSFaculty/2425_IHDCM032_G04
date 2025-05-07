import { CheckboxField } from './checkbox-field'
import { ResetButton } from './reset-button'
import { SelectField } from './select-field'
import { SelectLanguageField } from './select-language'
import { SubmitButton } from './submit-button'
import { TextField } from './text-field'
import { TextAreaField } from './textarea-field'
import { createFormHook, createFormHookContexts } from '@tanstack/react-form'

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
