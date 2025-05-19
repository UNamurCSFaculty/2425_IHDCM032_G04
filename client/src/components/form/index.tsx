import { NumberField } from './number-field'
import { AddressField } from '@/components/form/adress-fields'
import { CheckboxField } from '@/components/form/checkbox-field'
import { DateTimePickerField } from '@/components/form/datetime-field'
import { FileUploadField } from '@/components/form/file-upload-field'
import { ResetButton } from '@/components/form/reset-button'
import { SelectField } from '@/components/form/select-field'
import { SubmitButton } from '@/components/form/submit-button'
import { TextField } from '@/components/form/text-field'
import { TextAreaField } from '@/components/form/textarea-field'
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
    FileUploadField,
    AddressField,
    NumberField,
  },
  formComponents: {
    SubmitButton,
    ResetButton,
  },
  fieldContext,
  formContext,
})
