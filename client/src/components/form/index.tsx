import { CheckboxField } from './checkbox-field'
import { DateTimePickerField } from './datetime-field'
import { ResetButton } from './reset-button'
import { SelectField } from './select-field'
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
    DateTimePickerField,
  },
  formComponents: {
    SubmitButton,
    ResetButton,
  },
  fieldContext,
  formContext,
})
